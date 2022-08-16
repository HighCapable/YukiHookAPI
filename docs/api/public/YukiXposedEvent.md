## YukiXposedEvent *- object*

```kotlin
object YukiXposedEvent
```

**变更记录**

`v1.0.80` `添加`

**功能描述**

> 实现对原生 Xposed API 的装载事件监听。

### events *- method*

```kotlin
inline fun events(initiate: YukiXposedEvent.() -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 对 `YukiXposedEvent` 创建一个方法体。

### onInitZygote *- method*

```kotlin
fun onInitZygote(result: (StartupParam) -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 initZygote 事件监听。

### onHandleLoadPackage *- method*

```kotlin
fun onHandleLoadPackage(result: (LoadPackageParam) -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 handleLoadPackage 事件监听。

### onHandleInitPackageResources *- method*

```kotlin
fun onHandleInitPackageResources(result: (InitPackageResourcesParam) -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 handleInitPackageResources 事件监听。