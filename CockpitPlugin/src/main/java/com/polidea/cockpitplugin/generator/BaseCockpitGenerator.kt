package com.polidea.cockpitplugin.generator

import com.polidea.cockpitplugin.model.*
import com.squareup.javapoet.*
import java.io.File
import javax.lang.model.element.Modifier

abstract class BaseCockpitGenerator {

    abstract fun generate(params: List<Param<*>>, file: File?)

    protected val cockpitPackage = "com.polidea.cockpit.cockpit"
    protected val cockpitManagerPackage = "com.polidea.cockpit.manager"
    protected val androidContentPackage = "android.content"
    protected val cockpitActivityPackage = "com.polidea.cockpit.activity"
    protected val javaUtilPackage = "java.util"

    protected val cockpit = "Cockpit"
    protected val cockpitManager = "CockpitManager"
    protected val cockpitParam = "CockpitParam"
    protected val intent = "Intent"
    protected val context = "Context"
    protected val cockpitActivity = "CockpitActivity"
    protected val list = "List"
    protected val arrayList = "ArrayList"

    protected val cockpitManagerClassName = ClassName.get(cockpitManagerPackage, cockpitManager)
    protected val cockpitParamClassName = ClassName.get(cockpitManagerPackage, cockpitParam)
    protected val androidIntentClassName = ClassName.get(androidContentPackage, intent)
    protected val androidContextClassName = ClassName.get(androidContentPackage, context)
    protected val cockpitActivityClassName = ClassName.get(cockpitActivityPackage, cockpitActivity)
    protected val listClassName = ClassName.get(javaUtilPackage, list)
    protected val arrayListClassName = ClassName.get(javaUtilPackage, arrayList)

    protected fun generate(file: File?, configurator: (TypeSpec.Builder) -> TypeSpec.Builder) {

        val cockpitClass = configurator(TypeSpec.classBuilder(cockpit)
                .addModifiers(Modifier.PUBLIC))
                .build()

        val cockpitFile = JavaFile.builder(cockpitPackage, cockpitClass).build()

        if (file == null) {
            cockpitFile.writeTo(System.out)
        } else {
            cockpitFile.writeTo(file)
        }
    }

    inline protected fun createGetterMethodSpecForParamAndConfigurator(param: Param<*>,
                                                                     configurator: (MethodSpec.Builder) -> MethodSpec.Builder): MethodSpec {
        return configurator(MethodSpec.methodBuilder("get${param.name}")
                .returns(mapToTypeClass(param))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC))
                .build()
    }

    inline protected fun createGetAllCockpitParamsMethodForConfigurator(configurator: (MethodSpec.Builder) -> MethodSpec.Builder): MethodSpec {
        val parametrizedListClass: TypeName = ParameterizedTypeName.get(listClassName, cockpitParamClassName)
        return configurator(MethodSpec.methodBuilder("getAllCockpitParams")
                .returns(parametrizedListClass)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC))
                .build()
    }

    protected fun createNewCockpitParamStatementForParam(it: Param<*>) =
            "new $cockpitParam(\"${it.name}\", ${it.value.javaClass.simpleName}.class, ${createWrappedValueForParam(it)})"

    protected fun mapToTypeClass(param: Param<*>): Class<*> {
        return when (param) {
            is BooleanParam -> Boolean::class.java
            is DoubleParam -> Double::class.java
            is IntegerParam -> Int::class.java
            is StringParam -> String::class.java
            else -> throw IllegalArgumentException("Param type undefined: $param!")
        }
    }

    protected fun createWrappedValueForParam(param: Param<*>): Any {
        return when (param.value.javaClass) {
            String::class.java -> "\"${param.value}\""
            else -> param.value
        }
    }

}