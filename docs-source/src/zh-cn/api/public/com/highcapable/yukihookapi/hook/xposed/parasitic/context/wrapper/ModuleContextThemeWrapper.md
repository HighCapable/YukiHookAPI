---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# ModuleContextThemeWrapper <span class="symbol">- class</span>

```kotlin:no-line-numbers
class ModuleContextThemeWrapper private constructor(baseContext: Context, theme: Int, configuration: Configuration?) : ContextThemeWrapper
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 代理 `ContextThemeWrapper`。

通过包装，你可以轻松在 (Xposed) 宿主环境使用来自模块的主题资源。

## applyConfiguration <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun applyConfiguration(initiate: Configuration.() -> Unit): ModuleContextThemeWrapper
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置当前 `ModuleContextThemeWrapper` 的 `Configuration`。

设置后会自动调用 `Resources.updateConfiguration`。