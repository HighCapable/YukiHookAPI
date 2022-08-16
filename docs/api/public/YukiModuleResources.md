## YukiModuleResources *- class*

```kotlin
class YukiModuleResources private constructor(private val baseInstance: XModuleResources) : Resources
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 对接 `XModuleResources` 的中间层实例。

### fwd *- method*

```kotlin
fun fwd(resId: Int): YukiResForwarder
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 对接 `XModuleResources.fwd` 方法。

创建 `YukiResForwarder` 与 `XResForwarder` 实例。