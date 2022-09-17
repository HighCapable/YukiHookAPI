---
pageClass: code-page
---

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