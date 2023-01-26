package com.alchemy.sdk.annotations.processor

import com.alchemy.sdk.annotations.JsonRpc
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getAnnotationsByType
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
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class JsonRpcProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val jsonRpcClient = ClassName(
        "io.ktor.client",
        "HttpClient"
    )
    private val jsonRpcRequestInterface = ClassName(
        "com.alchemy.sdk.rpc.model",
        "JsonRpcRequest"
    )

    private val idGeneratorInterface = ClassName(
        "com.alchemy.sdk.util.generator",
        "IdGenerator"
    )

    private val json = ClassName(
        "kotlinx.serialization.json",
        "Json"
    )

    private val jsonElement = ClassName(
        "kotlinx.serialization.json",
        "JsonElement"
    )

    private val kmLog = ClassName(
        "org.lighthousegames.logging",
        "KmLog"
    )

    private val t = TypeVariableName("T")

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("[JsonRpcProcessor] Processing JsonRpc methods")
        val jsonRpcMethods = resolver
            .getSymbolsWithAnnotation("com.alchemy.sdk.annotations.JsonRpc")
            .filterIsInstance<KSFunctionDeclaration>()

        if (!jsonRpcMethods.iterator().hasNext()) {
            logger.info("[JsonRpcProcessor] No symbol found")
            return emptyList()
        }
        val jsonRpcMethodsPerClass = jsonRpcMethods.groupBy {
            it.closestClassDeclaration() ?: error("Class should have qualified name")
        }
        for (jsonRpcMethodsEntry in jsonRpcMethodsPerClass) {
            val classDeclaration = jsonRpcMethodsEntry.key
            val classSpecBuilder = createClassBuilder(classDeclaration)
            addFunctionsFromTheApi(jsonRpcMethodsEntry.value, classSpecBuilder)
            addInvokeFunction(classSpecBuilder)
            writeToFile(classDeclaration, classSpecBuilder)
        }
        return emptyList()
    }

    @OptIn(KspExperimental::class)
    private fun addFunctionsFromTheApi(
        jsonRpcMethodsForClass: List<KSFunctionDeclaration>,
        classSpecBuilder: TypeSpec.Builder
    ) {
        for (jsonRpcMethod in jsonRpcMethodsForClass) {
            val rpcMethodName = jsonRpcMethod.simpleName.asString()
            val rpcMethodNameFromAnnotation = jsonRpcMethod.getAnnotationsByType(JsonRpc::class).firstOrNull()?.method
                ?: error("Should have rpc annotation")
            logger.info("[JsonRpcProcessor] Processing $rpcMethodName method")
            val returnType =
                jsonRpcMethod.returnType?.toTypeName() ?: error("No return type for function $rpcMethodName")
            val functionBuilder = FunSpec.builder(rpcMethodName)
                .addModifiers(KModifier.SUSPEND)
                .returns(returnType)
            jsonRpcMethod.parameters.forEach { parameter ->
                functionBuilder.addParameter(
                    ParameterSpec.builder(
                        parameter.name?.asString()
                            ?: error("Parameter of function $rpcMethodName has no name"),
                        parameter.type.toTypeName()
                    )
                        .build()
                )
            }
            val allParameters = jsonRpcMethod.parameters
                .joinToString(
                    separator = ",·",
                ) {
                    val paramName = it.name?.asString() ?: error("Parameter of function $rpcMethodName has no name")
                    "json.encodeToJsonElement($paramName)"
                }
            val separator = if (allParameters.isEmpty()) {
                ""
            } else {
                ",·"
            }
            classSpecBuilder.addFunction(
                functionBuilder
                    .addModifiers(KModifier.OVERRIDE)
                    .addCode(
                        CodeBlock.of(
                            "return·invoke(%S$separator$allParameters)",
                            rpcMethodNameFromAnnotation
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
                    ParameterSpec.builder("rpcMethodName", String::class)
                        .build()
                )
                .addParameter(
                    ParameterSpec.builder(
                        "args",
                        jsonElement,
                        KModifier.VARARG
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
                        |     val request = JsonRpcRequest(
                        |         id = idGenerator.generateId(),
                        |         method = rpcMethodName,
                        |         params = JsonArray(args.toList())
                        |     )
                        |     jsonRpcClient.call(url, request, logger)
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
                    .addParameter("idGenerator", idGeneratorInterface)
                    .addParameter("jsonRpcClient", jsonRpcClient)
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
                    "idGenerator",
                    idGeneratorInterface,
                    KModifier.PRIVATE
                )
                    .initializer("idGenerator")
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    "jsonRpcClient",
                    jsonRpcClient,
                    KModifier.PRIVATE
                )
                    .initializer("jsonRpcClient")
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
                "com.alchemy.sdk.rpc.http.call",
                ""
            )
            .addImport(
                "kotlinx.serialization.json.encodeToJsonElement",
                ""
            )
            .addImport(
                "org.lighthousegames.logging.logging",
                ""
            )
            .addImport(
                "kotlinx.serialization.json",
                "JsonArray"
            )
            .addImport(
                jsonRpcRequestInterface.packageName,
                jsonRpcRequestInterface.simpleName
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