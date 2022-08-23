## ModuleContextThemeWrapper *- class*

```kotlin
class ModuleContextThemeWrapper private constructor(baseContext: Context, theme: Int, configuration: Configuration?) : ContextThemeWrapper
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 代理 `ContextThemeWrapper`。

通过包装，你可以轻松在 (Xposed) 宿主环境使用来自模块的主题资源。

### applyConfiguration *- method*

```kotlin
fun applyConfiguration(initiate: Configuration.() -> Unit): ModuleContextThemeWrapper
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 设置当前 `ModuleContextThemeWrapper` 的 `Configuration`。

设置后会自动调用 `Resources.updateConfiguration`。