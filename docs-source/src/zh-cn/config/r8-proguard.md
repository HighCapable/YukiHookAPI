# R8 与 Proguard 混淆

> 大部分场景下 Android 应用程序安装包可通过混淆压缩体积，这里介绍了混淆规则的配置方法。

自动生成的入口类会使用 `androidx.annotation.Keep` 注解与底座内置规则保留，在 R8 下你无需对 YukiHook 进行任何特殊配置。