## ModuleAppCompatActivity [class]

```kotlin
open class ModuleAppCompatActivity : AppCompatActivity()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 代理 `AppCompatActivity`。

继承于此类的 `Activity` 可以同时在宿主与模块中启动。

在 (Xposed) 宿主环境需要在宿主启动时调用 `Context.registerModuleAppActivities` 进行注册。

在 (Xposed) 宿主环境需要重写 `moduleTheme` 设置 AppCompat 主题，否则会无法启动。

### moduleTheme [field]

```kotlin
open val moduleTheme: Int
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 设置当前代理的 `Activity` 主题。