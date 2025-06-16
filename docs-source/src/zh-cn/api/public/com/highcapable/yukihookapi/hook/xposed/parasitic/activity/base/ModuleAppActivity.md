---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# ModuleAppActivity <span class="symbol">- class</span>

```kotlin:no-line-numbers
open class ModuleAppActivity : Activity()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 代理 `Activity`。

继承于此类的 `Activity` 可以同时在宿主与模块中启动。

在 (Xposed) 宿主环境需要在宿主启动时调用 `Context.registerModuleAppActivities` 进行注册。

## proxyClassName <span class="symbol">- field</span>

```kotlin:no-line-numbers
open val proxyClassName: String
```

**变更记录**

`v1.1.10` `新增`

**功能描述**

> 设置当前代理的 `Activity` 类名。

留空则使用 `Context.registerModuleAppActivities` 时设置的类名

::: danger

代理的 **Activity** 类名必须存在于宿主的 AndroidMainifest 清单中。

:::