## YukiHookModuleStatus [class]

```kotlin
object YukiHookModuleStatus
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 这是一个 Xposed 模块 Hook 状态类。

### executorName [field]

```kotlin
val executorName: String
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 获取当前 Hook 框架的名称。

模块未激活会返回 `unknown`，获取过程发生错误会返回 `invalid`。

### executorVersion [field]

```kotlin
val executorVersion: Int
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 获取当前 Hook 框架的版本。

模块未激活会返回 `-1`。

### ~~isActive [method]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.6` `作废` 

请使用 `isModuleActive`、`isXposedModuleActive` 或 `isTaiChiModuleActive`