## YukiHookModuleStatus [class]

```kotlin
object YukiHookModuleStatus
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 这是一个 Xposed 模块 Hook 状态类。

### executorName [field]

```kotlin
val executorName: String
```

<b>变更记录</b>

`v1.0.5` `新增`

<b>功能描述</b>

> 获取当前 Hook 框架的名称。

模块未激活会返回 `unknown`，获取过程发生错误会返回 `invalid`。

### executorVersion [field]

```kotlin
val executorVersion: Int
```

<b>变更记录</b>

`v1.0.5` `新增`

<b>功能描述</b>

> 获取当前 Hook 框架的版本。

模块未激活会返回 `-1`。

### ~~isActive [method]~~ <!-- {docsify-ignore} -->

<b>变更记录</b>

`v1.0` `添加`

`v1.0.6` `作废` 

请使用 `isModuleActive`、`isXposedModuleActive` 或 `isTaiChiModuleActive`