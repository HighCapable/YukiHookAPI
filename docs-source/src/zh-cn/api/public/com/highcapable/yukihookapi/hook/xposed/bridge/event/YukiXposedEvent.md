---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# YukiXposedEvent <span class="symbol">- object</span>

```kotlin:no-line-numbers
object YukiXposedEvent
```

**变更记录**

`v1.0.80` `添加`

**功能描述**

> 实现对原生 Xposed API 的装载事件监听。

## events <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun events(initiate: YukiXposedEvent.() -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 对 `YukiXposedEvent` 创建一个方法体。

## onInitZygote <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onInitZygote(result: (StartupParam) -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 initZygote 事件监听。

## onHandleLoadPackage <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHandleLoadPackage(result: (LoadPackageParam) -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 handleLoadPackage 事件监听。

## onHandleInitPackageResources <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHandleInitPackageResources(result: (InitPackageResourcesParam) -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 handleInitPackageResources 事件监听。