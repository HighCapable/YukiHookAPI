---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# IYukiHookXposedInit <span class="symbol">- interface</span>

```kotlin:no-line-numbers
interface IYukiHookXposedInit
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified` `deprecated`

作废了 ~~`YukiHookXposedInitProxy`~~ 名称但保留接口

转移到 `IYukiHookXposedInit` 新名称

**Function Illustrate**

> YukiHookAPI 的 Xposed 装载 API 调用接口。

## onInit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onInit()
```

**Change Records**

`v1.0.5` `added`

**Function Illustrate**

> 配置 `YukiHookAPI.Configs` 的初始化方法。

::: danger

在这里只能进行初始化配置，不能进行 Hook 操作。

:::

此方法可选，你也可以选择不对 [YukiHookAPI.Configs](../../../YukiHookAPI#configs-object) 进行配置。

## onHook <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHook()
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> Xposed API 的模块装载调用入口方法。

## onXposedEvent <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onXposedEvent()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

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

**Change Records**

`v1.0` `first`

`v1.0.80` `deprecated`

请转移到 `IYukiHookXposedInit`