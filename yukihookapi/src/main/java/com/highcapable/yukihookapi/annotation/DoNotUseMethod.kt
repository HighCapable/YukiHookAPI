@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package com.highcapable.yukihookapi.annotation

@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@MustBeDocumented
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS
)
@Retention(AnnotationRetention.BINARY)
/**
 * ⚠️ 警告方法外部调用声明
 * 此方法除继承和接口外不应该在这里被调用
 * 如果调用此方法可能会出现错误或 APP 发生异常
 */
annotation class DoNotUseMethod