# R8 & Proguard Obfuscate

> In most scenarios, the Xposed Module can be compressed by native obfuscation.
> 
> Here is the configuration method of obfuscation.

## R8

> If you are using `R8` then you don't need any special configuration for `YukiHookAPI`.

## Proguard

> ~~If you are still using `Proguard`, you need to do some rule configuration.~~

::: danger

Proguard rules have been deprecated, please don't use them anymore.

Since Android Gradle Plugin 4.2, the obfuscator with the latest version of the Android Jetpack default is **R8**, and you no longer need to consider obfuscation.

:::

To enable `R8` in any version, please add the following rules to the `gradle.properties` file, no configuration is required for Android Gradle Plugin 7.0 and above.

```groovy
android.enableR8=true
```