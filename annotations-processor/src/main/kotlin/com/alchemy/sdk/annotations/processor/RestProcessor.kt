package com.alchemy.sdk.annotations.processor

import com.alchemy.sdk.annotations.GET
import com.alchemy.sdk.annotations.Headers
import com.alchemy.sdk.annotations.Query
import com.alchemy.sdk.annotations.QueryMap
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class RestProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val httpClient = ClassName(
        "io.ktor.client",
        "HttpClient"
    )

    private val json = ClassName(
        "kotlinx.serialization.json",
        "Json"
    )

    private val kmLog = ClassName(
        "org.lighthousegames.logging",
        "KmLog"
    )

    private val t = TypeVariableName("T")

    private val mapData = Map::class.asClassName().parameterizedBy(STRING, STRING.copy(nullable = true))

    private val mapHeaders = Map::class.asClassName().parameterizedBy(STRING, STRING)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("[RestProcessor] Processing Rest methods")
        val getMethods = resolver
            .getSymbolsWithAnnotation("com.alchemy.sdk.annotations.GET")
            .filterIsInstance<KSFunctionDeclaration>()

        if (!getMethods.iterator().hasNext()) {
            logger.info("[RestProcessor] No symbol found")
            return emptyList()
        }
        val restMethodsPerClass = getMethods.groupBy {
            it.closestClassDeclaration() ?: error("Class should have qualified name")
        }
        for (restMethodsEntry in restMethodsPerClass) {
            val classDeclaration = restMethodsEntry.key
            val classSpecBuilder = createClassBuilder(classDeclaration)
            addFunctionsFromTheApi(restMethodsEntry.value, classSpecBuilder)
            addInvokeFunction(classSpecBuilder)
            writeToFile(classDeclaration, classSpecBuilder)
        }
        return emptyList()
    }

    @OptIn(KspExperimental::class)
    private fun addFunctionsFromTheApi(
        restMethodsForClass: List<KSFunctionDeclaration>,
        classSpecBuilder: TypeSpec.Builder
    ) {
        for (restMethod in restMethodsForClass) {
            val restMethodName = restMethod.simpleName.asString()
            val restMethodNameFromAnnotation = restMethod.getAnnotationsByType(GET::class)
                .firstOrNull()
                ?.value
                ?: error("Should have GET annotation for method $restMethodName")
            logger.info("[RestProcessor] Processing $restMethodName method")
            val functionBuilder = FunSpec.builder(restMethodName)
                .addModifiers(KModifier.SUSPEND)
                .returns(
                    restMethod.returnType?.toTypeName()
                        ?: error("No return type for function $restMethodName")
                )
            restMethod.parameters.forEach { parameter ->
                functionBuilder.addParameter(
                    ParameterSpec.builder(
                        parameter.name?.asString()
                            ?: error("Parameter of function $restMethodName has no name"),
                        parameter.type.toTypeName()
                    )
                        .build()
                )
            }
            val addAllParametersToMap = restMethod.parameters
                .joinToString(
                    separator = "\n",
                ) {
                    val paramName = it.name?.asString() ?: error("Parameter of function $restMethodName has no name")
                    if (it.isAnnotationPresent(QueryMap::class)) {
                        "entriesMap.putAll($paramName.encode(json))"
                    } else {
                        val query = it.getAnnotationsByType(Query::class).singleOrNull()
                            ?: error("At least a @Query annotation should be added")
                        if (it.type.toTypeName().isNullable) {
                            "entriesMap[\"${query.value}\"] = if ($paramName == null) null else json.encodeToString($paramName)"
                        } else {
                            "entriesMap[\"${query.value}\"] = json.encodeToString($paramName)"
                        }
                    }
                }

            val headersFromAnnotation = restMethod.getAnnotationsByType(Headers::class)
                .firstOrNull()
                ?.value
                ?.toList() ?: emptyList()
            val addAllHeadersFromAnnotation = headersFromAnnotation
                .map { it.split(": ") }
                .joinToString("\n") {
                    "headersMap[\"${it[0]}\"] = \"${it[1]}\""
                }
            classSpecBuilder.addFunction(
                functionBuilder
                    .addModifiers(KModifier.OVERRIDE)
                    .addCode(
                        CodeBlock.of(
                            """
                            |val entriesMap = mutableMapOf<String, String?>()
                            |$addAllParametersToMap
                            |val headersMap = mutableMapOf<String, String>()
                            |$addAllHeadersFromAnnotation
                            |headersMap["Alchemy-Ethers-Sdk-Version"] = "2.0.3"
                            |return invoke("$restMethodNameFromAnnotation", headersMap, entriesMap)
                            """.trimMargin(),
                            restMethodNameFromAnnotation
                        )
                    )
                    .build()
            )
        }
    }

    private fun addInvokeFunction(classSpecBuilder: TypeSpec.Builder) {
        classSpecBuilder.addFunction(
            FunSpec.builder("invoke")
                .addTypeVariable(t.copy(reified = true))
                .addModifiers(KModifier.PRIVATE, KModifier.SUSPEND, KModifier.INLINE)
                .addParameter(
                    ParameterSpec.builder("restMethodName", String::class)
                        .build()
                )
                .addParameter(
                    ParameterSpec.builder(
                        "headers",
                        mapHeaders
                    )
                        .build()
                )
                .addParameter(
                    ParameterSpec.builder(
                        "params",
                        mapData
                    )
                        .build()
                )
                .returns(
                    ClassName.bestGuess("SdkResult")
                        .parameterizedBy(t)
                )
                .addCode(
                    CodeBlock.of(
                        """
                        |return try {
                        |     httpClient.executeGet("${'$'}url/${'$'}restMethodName", headers, params, logger)
                        |} catch (e: Exception) {
                        |     SdkResult.failure(e)
                        |}
                        """.trimMargin()
                    )
                )
                .build()
        )
    }

    private fun createClassBuilder(classDeclaration: KSClassDeclaration): TypeSpec.Builder {
        return TypeSpec.classBuilder(classDeclaration.simpleName.asString() + "Impl")
            .addSuperinterface(classDeclaration.asType(emptyList()).toTypeName())
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("url", String::class)
                    .addParameter("httpClient", httpClient)
                    .addParameter("json", json)
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    "url",
                    String::class,
                    KModifier.PRIVATE
                )
                    .initializer("url")
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    "httpClient",
                    httpClient,
                    KModifier.PRIVATE
                )
                    .initializer("httpClient")
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    "json",
                    json,
                    KModifier.PRIVATE
                )
                    .initializer("json")
                    .build()
            )
    }

    private fun writeToFile(
        classDeclaration: KSClassDeclaration,
        classSpecBuilder: TypeSpec.Builder
    ) {
        val companion = TypeSpec.companionObjectBuilder()
            .addProperty(
                PropertySpec.builder("logger", kmLog)
                    .initializer("logging()")
                    .build()
            )
            .build()

        FileSpec.builder(
            classDeclaration.packageName.asString(),
            classDeclaration.simpleName.asString() + "Impl"
        )
            .addImport(
                "kotlinx.serialization.encodeToString",
                ""
            )
            .addImport(
                "com.alchemy.sdk.nft.http.executeGet",
                ""
            )
            .addImport(
                "org.lighthousegames.logging.logging",
                ""
            )
            .addAnnotation(
                AnnotationSpec.builder(ClassName("", "Suppress"))
                    .addMember("%S", "RedundantVisibilityModifier")
                    .build()
            )
            .addType(
                classSpecBuilder
                    .addType(companion)
                    .build()
            )
            .build()
            .writeTo(codeGenerator, true)
    }
}