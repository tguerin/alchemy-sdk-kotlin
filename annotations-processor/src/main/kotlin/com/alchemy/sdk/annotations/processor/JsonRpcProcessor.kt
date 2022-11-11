package com.alchemy.sdk.annotations.processor

import com.google.devtools.ksp.closestClassDeclaration
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
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class JsonRpcProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    private val jsonRpcClientInterface = ClassName(
        "com.alchemy.sdk.json.rpc.client",
        "JsonRpcClient"
    )
    private val jsonRpcRequestInterface = ClassName(
        "com.alchemy.sdk.json.rpc.client.model",
        "JsonRpcRequest"
    )
    private val idGeneratorInterface = ClassName(
        "com.alchemy.sdk.json.rpc.client.generator",
        "IdGenerator"
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

    private fun addFunctionsFromTheApi(
        jsonRpcMethodsForClass: List<KSFunctionDeclaration>,
        classSpecBuilder: TypeSpec.Builder
    ) {
        for (jsonRpcMethod in jsonRpcMethodsForClass) {
            val rpcMethodName = jsonRpcMethod.simpleName.asString()
            val rpcMethodNameFromAnnotation = jsonRpcMethod.annotations
                .firstOrNull()
                ?.arguments?.first()
                ?.value as String?
                ?: error("Should have rpc annotation")
            logger.info("[JsonRpcProcessor] Processing $rpcMethodName method")
            val functionBuilder = FunSpec.builder(rpcMethodName)
                .addModifiers(KModifier.SUSPEND)
                .returns(
                    jsonRpcMethod.returnType?.toTypeName()
                        ?: error("No return type for function $rpcMethodName")
                )
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
                    it.name?.asString()
                        ?: error("Parameter of function $rpcMethodName has no name")
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
                        Any::class.asTypeName().copy(true),
                        KModifier.VARARG
                    )
                        .build()
                )
                .returns(
                    ClassName.bestGuess("Result")
                        .parameterizedBy(t)
                )
                .addCode(
                    CodeBlock.of(
                        """
                        |return try {
                        |     val request = JsonRpcRequest(
                        |         id = idGenerator.generateId(),
                        |         method = rpcMethodName,
                        |         params = args.toList()
                        |     )
                        |     jsonRpcClient.call(request,  T::class.java)
                        |} catch (e: Exception) {
                        |     Result.failure(e)
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
                    .addParameter("idGenerator", idGeneratorInterface)
                    .addParameter("jsonRpcClient", jsonRpcClientInterface)
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    "jsonRpcClient",
                    jsonRpcClientInterface,
                    KModifier.PRIVATE
                )
                    .initializer("jsonRpcClient")
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
    }

    private fun writeToFile(
        classDeclaration: KSClassDeclaration,
        classSpecBuilder: TypeSpec.Builder
    ) {
        FileSpec.builder(
            classDeclaration.packageName.asString(),
            classDeclaration.simpleName.asString() + "Impl"
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
                classSpecBuilder.build()
            )
            .build()
            .writeTo(codeGenerator, true)
    }
}