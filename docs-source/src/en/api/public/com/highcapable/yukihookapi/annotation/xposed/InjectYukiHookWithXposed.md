---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# InjectYukiHookWithXposed <span class="symbol">- annotation</span>

```kotlin:no-line-numbers
annotation class InjectYukiHookWithXposed(
    val sourcePath: String,
    val modulePackageName: String,
    val entryClassName: String,
    val isUsingXposedModuleStatus: Boolean,
    val isUsingResourcesHook: Boolean
)
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

新增 `entryClassName` 参数

`v1.0.92` `modified`

新增 `isUsingResourcesHook` 参数

`v1.2.0` `modified`

新增 `isUsingXposedModuleStatus` 参数

**Function Illustrate**

> 标识 `YukiHookAPI` 注入 Xposed 入口的类注解。

**Function Example**

详情请参考 [InjectYukiHookWithXposed Annotation](../../../../../../../config/xposed-using#injectyukihookwithxposed-annotation)。