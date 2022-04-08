## YukiHookXposedInitProxy [interface]

```kotlin
interface YukiHookXposedInitProxy
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> YukiHookAPI 的 Xposed 装载 API 调用接口。

### onInit [method]

```kotlin
fun onInit()
```

<b>变更记录</b>

`v1.0.5` `新增`

<b>功能描述</b>

> 配置 `YukiHookAPI.Configs` 的初始化方法。

!> 在这里只能进行初始化配置，不能进行 Hook 操作。

此方法可选，你也可以选择不对 `YukiHookAPI.Configs` 进行配置。

### onHook [method]

```kotlin
fun onHook()
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> Xposed API 的模块装载调用入口方法。