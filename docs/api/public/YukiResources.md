## YukiResources *- class*

```kotlin
class YukiResources private constructor(private val baseInstance: XResources) : Resources
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 对接 `XResources` 的中间层实例。

### LayoutInflatedParam *- class*

```kotlin
class LayoutInflatedParam(internal val baseParam: XC_LayoutInflated.LayoutInflatedParam)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 装载 Hook APP 的目标布局 Resources 实现类。

#### variantName *- field*

```kotlin
val variantName: String
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获取当前被 Hook 的布局装载目录名称。

例如：`layout`、`layout-land`、`layout-sw600dp`。

#### currentView *- field*

```kotlin
val currentView: View
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获取当前被 Hook 的布局实例。

#### findViewByIdentifier *- method*

```kotlin
inline fun <reified T : View> View.findViewByIdentifier(name: String): T?
```

```kotlin
inline fun <reified T : View> findViewByIdentifier(name: String): T?
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 使用 Identifier 查找 Hook APP 指定 Id 的 `View`。

扩展方法可以使用 Identifier 查找 Hook APP 当前装载布局中指定 Id 的 `View`。