---
pageClass: code-page
---

# IYukiHookXposedInit <span class="symbol">- interface</span>

```kotlin:no-line-numbers
interface IYukiHookXposedInit
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改` `作废`

作废了 ~~`YukiHookXposedInitProxy`~~ 名称但保留接口

转移到 `IYukiHookXposedInit` 新名称

**功能描述**

> YukiHookAPI 的 Xposed 装载 API 调用接口。

## onInit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onInit()
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 配置 `YukiHookAPI.Configs` 的初始化方法。

::: danger

在这里只能进行初始化配置，不能进行 Hook 操作。

:::

此方法可选，你也可以选择不对 [YukiHookAPI.Configs](../../../YukiHookAPI#configs-object) 进行配置。

## onHook <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHook()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> Xposed API 的模块装载调用入口方法。

## onXposedEvent <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onXposedEvent()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 监听 Xposed 原生装载事件。

若你的 Hook 事件中存在需要兼容的原生 Xposed 功能，可在这里实现。

请在这里使用 [YukiXposedEvent](../bridge/event/YukiXposedEvent) 创建回调事件监听。

可监听的事件如下：

`YukiXposedEvent.onInitZygote`

`YukiXposedEvent.onHandleLoadPackage`

`YukiXposedEvent.onHandleInitPackageResources`

::: danger

此接口仅供监听和实现原生 Xposed API 的功能，请不要在这里操作 **YukiHookAPI**。

:::

<h1 class="deprecated">YukiHookXposedInitProxy - interface</h1>

**变更记录**

`v1.0` `添加`

`v1.0.80` `作废`

请转移到 `IYukiHookXposedInit`