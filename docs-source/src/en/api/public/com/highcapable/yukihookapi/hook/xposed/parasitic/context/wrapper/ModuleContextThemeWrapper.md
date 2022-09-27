---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# ModuleContextThemeWrapper <span class="symbol">- class</span>

```kotlin:no-line-numbers
class ModuleContextThemeWrapper private constructor(baseContext: Context, theme: Int, configuration: Configuration?) : ContextThemeWrapper
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 代理 `ContextThemeWrapper`。

通过包装，你可以轻松在 (Xposed) 宿主环境使用来自模块的主题资源。

## applyConfiguration <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun applyConfiguration(initiate: Configuration.() -> Unit): ModuleContextThemeWrapper
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置当前 `ModuleContextThemeWrapper` 的 `Configuration`。

设置后会自动调用 `Resources.updateConfiguration`。