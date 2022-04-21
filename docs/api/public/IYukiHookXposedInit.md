## IYukiHookXposedInit [interface]

```kotlin
interface IYukiHookXposedInit
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改` `作废`

作废了 ~~`YukiHookXposedInitProxy`~~ 名称但保留接口

转移到 `IYukiHookXposedInit` 新名称

**功能描述**

> YukiHookAPI 的 Xposed 装载 API 调用接口。

### onInit [method]

```kotlin
fun onInit()
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 配置 `YukiHookAPI.Configs` 的初始化方法。

!> 在这里只能进行初始化配置，不能进行 Hook 操作。

此方法可选，你也可以选择不对 `YukiHookAPI.Configs` 进行配置。

### onHook [method]

```kotlin
fun onHook()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> Xposed API 的模块装载调用入口方法。