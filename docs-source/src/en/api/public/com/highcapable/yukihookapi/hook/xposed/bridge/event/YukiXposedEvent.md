---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# YukiXposedEvent <span class="symbol">- object</span>

```kotlin:no-line-numbers
object YukiXposedEvent
```

**Change Records**

`v1.0.80` `first`

**Function Illustrate**

> 实现对原生 Xposed API 的装载事件监听。

## events <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun events(initiate: YukiXposedEvent.() -> Unit)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 对 `YukiXposedEvent` 创建一个方法体。

## onInitZygote <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onInitZygote(result: (StartupParam) -> Unit)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 initZygote 事件监听。

## onHandleLoadPackage <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHandleLoadPackage(result: (LoadPackageParam) -> Unit)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 handleLoadPackage 事件监听。

## onHandleInitPackageResources <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHandleInitPackageResources(result: (InitPackageResourcesParam) -> Unit)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 handleInitPackageResources 事件监听。