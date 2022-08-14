## ModuleAppActivity [class]

```kotlin
open class ModuleAppActivity : Activity()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 代理 `Activity`。

继承于此类的 `Activity` 可以同时在宿主与模块中启动。

在 (Xposed) 宿主环境需要在宿主启动时调用 `Context.registerModuleAppActivities` 进行注册。