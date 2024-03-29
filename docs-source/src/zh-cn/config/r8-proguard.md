# R8 与 Proguard 混淆

> 大部分场景下 Xposed 模块可通过原生混淆压缩体积，这里介绍了混淆的配置方法。

## R8

> 如果你使用的是 `R8`，那么你无需对 `YukiHookAPI` 进行任何特殊配置。

## Proguard

> ~~如果你仍然在使用 `Proguard`，你需要做一些规则配置。~~

::: danger

Proguard 规则已被弃用，请不要再使用，自从 Android Gradle Plugin 4.2 后，拥有 Android Jetpack 套件最新版本的混淆处理程序默认均为 **R8**，不再需要考虑混淆的问题。

:::

若要在任何版本下启用 `R8`，请在 `gradle.properties` 文件中加入如下规则，Android Gradle Plugin 7.0 及以上版本无需任何配置。

```groovy
android.enableR8=true
```