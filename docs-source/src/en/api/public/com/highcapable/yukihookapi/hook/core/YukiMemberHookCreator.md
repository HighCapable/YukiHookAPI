---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# YukiMemberHookCreator <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiMemberHookCreator internal constructor(private val packageParam: PackageParam, private val hookClass: HookClass)
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

对 `hookClass` 进行 inline 处理

`v1.1.0` `modified`

修正拼写错误的 **Creater** 命名到 **Creator**

`v1.1.5` `modified`

私有化构造方法

**Function Illustrate**

> `YukiHookAPI` 的 `Member` 核心 Hook 实现类。

<h2 class="deprecated">PRIORITY_DEFAULT - field</h2>

**Change Records**

`v1.0.80` `added`

`v1.2.0` `deprecated`

请迁移到 `YukiHookPriority`

<h2 class="deprecated">PRIORITY_LOWEST - field</h2>

**Change Records**

`v1.0.80` `added`

`v1.2.0` `deprecated`

请迁移到 `YukiHookPriority`

<h2 class="deprecated">PRIORITY_HIGHEST - field</h2>

**Change Records**

`v1.0.80` `added`

`v1.2.0` `deprecated`

请迁移到 `YukiHookPriority`

<h2 class="deprecated">instanceClass - field</h2>

**Change Records**

`v1.0` `first`

`v1.0.2` `modified`

~~`thisClass`~~ 更名为 `instanceClass`

`v1.2.0` `deprecated`

不再推荐使用

<h2 class="deprecated">injectMember - method</h2>

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

增加 `priority` Hook 优先级

`v1.2.0` `deprecated`

请迁移到另一个 `injectMember`

## injectMember <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun injectMember(priority: YukiHookPriority, initiate: MemberHookCreator.LegacyCreator.() -> Unit): MemberHookCreator.Result
```

**Change Records**

`v1.2.0` `added`

**Function Illustrate**

> 注入要 Hook 的 `Method`、`Constructor`。

<h2 class="deprecated">useDangerousOperation - method</h2>

**Change Records**

`v1.1.0` `added`

`v1.2.0` `deprecated`

此功能已被弃用

## MemberHookCreator <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class MemberHookCreator internal constructor(private val priority: YukiHookPriority, private val hookMode: HookMode)
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

增加 `priority` Hook 优先级

`v1.0.81` `modified`

增加 `packageName` 当前 Hook 的 APP 包名

`v1.1.0` `modified`

移除 `packageName`

修正拼写错误的 **Creater** 命名到 **Creator**

`v1.2.0` `modified`

移除 `tag`

`priority` 类型由 `Int` 变更为 `YukiHookPriority`

增加 `hookMode` Hook 模式

**Function Illustrate**

> Hook 核心功能实现类，查找和处理需要 Hook 的 `Method`、`Constructor`。

### before <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun before(initiate: HookParam.() -> Unit): HookCallback
```

**Change Records**

`v1.2.0` `added`

**Function Illustrate**

> 在 `Member` 执行完成前 Hook。

### after <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun after(initiate: HookParam.() -> Unit): HookCallback
```

**Change Records**

`v1.2.0` `added`

**Function Illustrate**

> 在 `Member` 执行完成后 Hook。

### replaceAny <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceAny(initiate: HookParam.() -> Any?)
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 拦截并替换此 `Member` 内容，给出返回值。

### replaceUnit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceUnit(initiate: HookParam.() -> Unit)
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 拦截并替换此 `Member` 内容，没有返回值，可以称为 `Void`。

### replaceTo <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceTo(any: Any?)
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 拦截并替 `Member` 返回值。

### replaceToTrue <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceToTrue()
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 拦截并替换 `Member` 返回值为 `true`。

::: danger

确保替换 **Member** 的返回对象为 **Boolean**。

:::

### replaceToFalse <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceToFalse()
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 拦截并替换 `Member` 返回值为 `false`。

::: danger

确保替换 **Member** 的返回对象为 **Boolean**。

:::

### intercept <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun intercept()
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 拦截此 `Member` 。

这将会禁止此 `Member` 执行并返回 `null`。

::: danger

例如 **Int**、**Long**、**Boolean** 常量返回值的 **Member** 一旦被设置为 **null** 可能会造成 Hook APP 抛出异常。

:::

### removeSelf <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun removeSelf(result: (Boolean) -> Unit)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 移除当前注入的 Hook `Method`、`Constructor` (解除 Hook)。

::: danger

你只能在 Hook 回调方法中使用此功能。

:::

### LegacyCreator <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class LegacyCreator internal constructor()
```

**Change Records**

`v1.2.0` `added`

**Function Illustrate**

> 使用 `injectMember` 创建的 Hook 核心功能实现类 (旧版本)。

::: warning

大部分旧版 API 已被迁移至此处，将不再特殊说明其中包含的旧版 API。

:::

### HookCallback <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class HookCallback internal constructor()
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> Hook 方法体回调实现类。

#### onFailureThrowToApp <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onFailureThrowToApp()
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 当回调方法体内发生异常时将异常抛出给当前 Hook APP。

### Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor()
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 监听 Hook 结果实现类。

#### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Result.() -> Unit): Result
```

**Change Records**

`v1.0` `first`

`v1.0.5` `modified`

~~`failures`~~ 修改为 `result`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 创建监听失败事件方法体。

#### by <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun by(condition: () -> Boolean): Result
```

**Change Records**

`v1.0.5` `added`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

#### onHooked <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHooked(result: (Member) -> Unit): Result
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 监听 `members` Hook 成功的回调方法。

在首次 Hook 成功后回调。

在重复 Hook 时会回调 `onAlreadyHooked`。

#### onAlreadyHooked <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onAlreadyHooked(result: (Member) -> Unit): Result
```

**Change Records**

`v1.0.89` `added`

**Function Illustrate**

> 监听 `members` 重复 Hook 的回调方法。

::: warning

同一个 **hookClass** 中的同一个 **members** 不会被 API 重复 Hook，若由于各种原因重复 Hook 会回调此方法。

:::

#### onNoSuchMemberFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onNoSuchMemberFailure(result: (Throwable) -> Unit): Result
```

**Change Records**

`v1.0.5` `added`

**Function Illustrate**

> 监听 `members` 不存在发生错误的回调方法。

#### onConductFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onConductFailure(result: (HookParam, Throwable) -> Unit): Result
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 监听 Hook 进行过程中发生错误的回调方法。

#### onHookingFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHookingFailure(result: (Throwable) -> Unit): Result
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 监听 Hook 开始时发生的错误的回调方法。

#### onAllFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onAllFailure(result: (Throwable) -> Unit): Result
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 监听全部 Hook 过程发生错误的回调方法。

#### ignoredNoSuchMemberFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredNoSuchMemberFailure(): Result
```

**Change Records**

`v1.0.5` `added`

**Function Illustrate**

> 忽略 `members` 不存在发生的错误。

#### ignoredConductFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredConductFailure(): Result
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 忽略 Hook 进行过程中发生的错误。

#### ignoredHookingFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredHookingFailure(): Result
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 忽略 Hook 开始时发生的错误。

#### ignoredAllFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredAllFailure(): Result
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 忽略全部 Hook 过程发生的错误。

#### remove <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun remove(result: (Boolean) -> Unit)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 移除当前注入的 Hook `Method`、`Constructor` (解除 Hook)。

::: warning

你只能在 Hook 成功后才能解除 Hook，可监听 **onHooked** 事件。

:::

## Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor()
```

**Change Records**

`v1.0.3` `added`

**Function Illustrate**

> 监听全部 Hook 结果实现类。

### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Result.() -> Unit): Result
```

**Change Records**

`v1.0.3` `added`

`v1.0.5` `modified`

~~`failures`~~ 修改为 `result`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 创建监听事件方法体。

### by <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun by(condition: () -> Boolean): Result
```

**Change Records**

`v1.0.5` `added`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

### onPrepareHook <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onPrepareHook(callback: () -> Unit): Result
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 监听 `hookClass` 存在时准备开始 Hook 的操作。

### onHookClassNotFoundFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHookClassNotFoundFailure(result: (Throwable) -> Unit): Result
```

**Change Records**

`v1.0.3` `added`

**Function Illustrate**

> 监听 `hookClass` 找不到时发生错误的回调方法。

### ignoredHookClassNotFoundFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredHookClassNotFoundFailure(): Result
```

**Change Records**

`v1.0.3` `added`

**Function Illustrate**

> 忽略 `hookClass` 找不到时出现的错误。