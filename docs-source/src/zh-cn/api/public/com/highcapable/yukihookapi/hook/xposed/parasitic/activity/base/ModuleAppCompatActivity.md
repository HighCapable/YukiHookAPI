---
pageClass: code-page
---

# ModuleAppCompatActivity <span class="symbol">- class</span>

```kotlin:no-line-numbers
open class ModuleAppCompatActivity : AppCompatActivity()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 代理 `AppCompatActivity`。

继承于此类的 `Activity` 可以同时在宿主与模块中启动。

在 (Xposed) 宿主环境需要在宿主启动时调用 `Context.registerModuleAppActivities` 进行注册。

在 (Xposed) 宿主环境需要重写 `moduleTheme` 设置 AppCompat 主题，否则会无法启动。

## moduleTheme <span class="symbol">- field</span>

```kotlin:no-line-numbers
open val moduleTheme: Int
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置当前代理的 `Activity` 主题。

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