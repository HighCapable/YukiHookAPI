plugins {
    autowire(libs.plugins.android.application) apply false
    autowire(libs.plugins.android.library) apply false
    autowire(libs.plugins.kotlin.jvm) apply false
    autowire(libs.plugins.kotlin.android) apply false
    autowire(libs.plugins.kotlin.ksp) apply false
}