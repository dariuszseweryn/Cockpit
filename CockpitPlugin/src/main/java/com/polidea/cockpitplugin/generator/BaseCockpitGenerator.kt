package com.polidea.cockpitplugin.generator

import com.polidea.cockpit.core.CockpitParam
import com.polidea.cockpit.core.type.CockpitColor
import com.polidea.cockpit.core.type.CockpitRange
import com.squareup.javapoet.*
import java.io.File
import javax.lang.model.element.Modifier

internal abstract class BaseCockpitGenerator {

    abstract fun generate(params: List<CockpitParam<*>>, file: File?)

    private val cockpitPackage = "com.polidea.cockpit.cockpit"
    private val cockpitManagerPackage = "com.polidea.cockpit.manager"
    private val androidxPackage = "androidx.fragment.app"
    private val androidxLifecyclePackage = "androidx.lifecycle"
    private val cockpitDialogPackage = "com.polidea.cockpit.paramsedition"
    private val cockpitEventPackage = "com.polidea.cockpit.event"
    private val cockpitMapperPackage = "com.polidea.cockpit.mapper"
    private val cockpitCoreTypePackage = "com.polidea.cockpit.core.type"

    private val javaUtilPackage = "java.util"

    private val cockpit = "Cockpit"
    private val cockpitManager = "CockpitManager"
    private val fragmentManager = "FragmentManager"
    private val lifecycleOwner = "LifecycleOwner"
    private val cockpitDialog = "CockpitDialog"
    private val propertyChangeListener = "PropertyChangeListener"
    private val actionRequestCallback = "ActionRequestCallback"
    private val cockpitColor = "CockpitColor"
    private val cockpitRange = "CockpitRange"
    private val cockpitStep = "CockpitStep"
    private val cockpitReadOnlyMapper = "CockpitReadOnlyMapper"
    private val cockpitColorMapper = "CockpitColorMapper"
    private val cockpitRangeMapper = "CockpitRangeMapper"
    private val cockpitStepMapper = "CockpitStepMapper"
    private val mappingPropertyChangeListener = "MappingPropertyChangeListener"

    protected val map = "Map"
    protected val hashMap = "HashMap"

    protected val cockpitManagerClassName = ClassName.get(cockpitManagerPackage, cockpitManager)
    protected val androidFragmentManagerClassName = ClassName.get(androidxPackage, fragmentManager)
    protected val androidLifecycleOwnerClassName = ClassName.get(androidxLifecyclePackage, lifecycleOwner)
    protected val cockpitDialogClassName = ClassName.get(cockpitDialogPackage, cockpitDialog)
    protected val propertyChangeListenerClassName = ClassName.get(cockpitEventPackage, propertyChangeListener)
    protected val actionRequestCallbackClassName = ClassName.get(cockpitEventPackage, actionRequestCallback)
    protected val cockpitColorClassName = ClassName.get(cockpitCoreTypePackage, cockpitColor)
    protected val cockpitRangeClassName = ClassName.get(cockpitCoreTypePackage, cockpitRange)
    protected val cockpitStepClassName = ClassName.get(cockpitCoreTypePackage, cockpitStep)
    protected val cockpitReadOnlyMapperClassName = ClassName.get(cockpitMapperPackage, cockpitReadOnlyMapper)
    protected val cockpitColorMapperClassName = ClassName.get(cockpitMapperPackage, cockpitColorMapper)
    protected val cockpitRangeMapperClassName = ClassName.get(cockpitMapperPackage, cockpitRangeMapper)
    protected val cockpitStepMapperClassName = ClassName.get(cockpitMapperPackage, cockpitStepMapper)
    protected val mappingPropertyChangeListenerClassName = ClassName.get(cockpitMapperPackage, mappingPropertyChangeListener)

    protected val mapClassName = ClassName.get(javaUtilPackage, map)
    protected val hashMapClassName = ClassName.get(javaUtilPackage, hashMap)

    protected fun getParametrizedCockpitPropertyChangeListenerClassName(clazz: Class<*>) =
            getParametrizedCockpitPropertyChangeListenerClassName(TypeName.get(clazz))

    protected fun getParametrizedCockpitPropertyChangeListenerClassName(typeName: TypeName) =
            ParameterizedTypeName.get(propertyChangeListenerClassName, typeName)

    protected fun getParametrizedCockpitRangeClassName(clazz: Class<*>) =
            ParameterizedTypeName.get(cockpitRangeClassName, TypeName.get(clazz))

    protected fun getParametrizedCockpitRangeMapperClassName(clazz: Class<*>) =
            ParameterizedTypeName.get(cockpitRangeMapperClassName, TypeName.get(clazz))

    protected fun getParametrizedCockpitStepClassName(clazz: Class<*>) =
            ParameterizedTypeName.get(cockpitStepClassName, TypeName.get(clazz))

    protected fun getParametrizedCockpitStepMapperClassName(clazz: Class<*>) =
            ParameterizedTypeName.get(cockpitStepMapperClassName, TypeName.get(clazz))

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

    protected fun <T : Any> createGetterMethodSpecForParamAndConfigurator(paramName: String, value: T,
                                                                          configurator: (MethodSpec.Builder) -> MethodSpec.Builder): MethodSpec {
        val prefix = when (value) {
            is Boolean -> "is"
            else -> "get"
        }
        val returnedTypeClass = when (value) {
            is CockpitColor -> String::class.java
            is CockpitRange<*> -> mapToTypeClass(value.value)
            else -> mapToTypeClass(value)
        }
        return configurator(MethodSpec.methodBuilder("$prefix${paramName.capitalize()}")
                .returns(returnedTypeClass)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC))
                .build()
    }

    protected fun mapToTypeClass(value: Any): Class<*> {
        return when (value) {
            is Boolean -> Boolean::class.java
            is Double -> Double::class.java
            is Int -> Int::class.java
            else -> value::class.java
        }
    }

    protected fun mapToJavaObjectTypeClass(value: Any) = value::class.javaObjectType
}