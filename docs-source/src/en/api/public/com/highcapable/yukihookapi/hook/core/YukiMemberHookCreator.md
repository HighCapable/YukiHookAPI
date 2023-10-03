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

**变更记录**

`v1.0.80` `added`

`v1.2.0` `deprecated`

请迁移到 `YukiHookPriority`

<h2 class="deprecated">PRIORITY_LOWEST - field</h2>

**变更记录**

`v1.0.80` `added`

`v1.2.0` `deprecated`

请迁移到 `YukiHookPriority`

<h2 class="deprecated">PRIORITY_HIGHEST - field</h2>

**变更记录**

`v1.0.80` `added`

`v1.2.0` `deprecated`

请迁移到 `YukiHookPriority`

## instanceClass <span class="symbol">- field</span>

```kotlin:no-line-numbers
val instanceClass: Class<*>
```

**Change Records**

`v1.0` `first`

`v1.0.2` `modified`

~~`thisClass`~~ 更名为 `instanceClass`

**Function Illustrate**

> 得到当前被 Hook 的 `Class`。

::: danger

不推荐直接使用，万一得不到 **Class** 对象则会无法处理异常导致崩溃。

:::

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
inline fun injectMember(priority: YukiHookPriority, initiate: MemberHookCreator.() -> Unit): MemberHookCreator.Result
```

**Change Records**

`v1.2.0` `added`

**Function Illustrate**

> 注入要 Hook 的 `Method`、`Constructor`。

## useDangerousOperation <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun useDangerousOperation(option: String)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 允许 Hook 过程中的所有危险行为。

请在 `option` 中键入 `Yes do as I say!` 代表你同意允许所有危险行为。

你还需要在整个调用域中声明注解 `DangerousHookOperation` 以消除警告。

若你只需要 Hook `ClassLoader` 的 `loadClass` 方法，请参考 [ClassLoader.onLoadClass](../factory/ReflectionFactory#classloader-onloadclass-ext-method)。

::: danger

若你不知道允许此功能会带来何种后果，请勿使用。

:::

## MemberHookCreator <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class MemberHookCreator internal constructor(private val priority: YukiHookPriority)
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

**Function Illustrate**

> Hook 核心功能实现类，查找和处理需要 Hook 的 `Method`、`Constructor`。

<h3 class="deprecated">member - field</h3>

**Change Records**

`v1.0` `first`

`v1.1.0` `removed`

请迁移到 `members`

### members <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun members(vararg member: Member?)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 手动指定要 Hook 的 `Method`、`Constructor`。

::: warning

不建议使用此方法设置目标需要 Hook 的 **Member** 对象，你可以使用 **method** 或 **constructor** 方法。

:::

<h3 class="deprecated">allMethods - method</h3>

**Change Records**

`v1.0` `first`

`v1.1.0` `deprecated`

请使用 `method { name = /** name */ }.all()` 来取代它

<h3 class="deprecated">allConstructors - method</h3>

**Change Records**

`v1.0` `first`

`v1.1.0` `deprecated`

请使用 `allMembers(MembersType.CONSTRUCTOR)` 来取代它

### allMembers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun allMembers(type: MembersType)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 查找并 Hook `hookClass` 中的全部 `Method`、`Constructor`。

::: warning

无法准确处理每个 **Member** 的返回值和 **param**，建议使用 **method** or **constructor** 对每个 **Member** 单独 Hook。

:::

### method <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun method(initiate: MethodConditions): MethodFinder.Result
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 查找当前 `Class` 需要 Hook 的 `Method`。

### constructor <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun constructor(initiate: ConstructorConditions): ConstructorFinder.Result
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 查找当前 `Class` 需要 Hook 的 `Constructor`。

<h3 class="deprecated">HookParam.field - i-ext-method</h3>

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

`v1.2.0` `removed`

<h3 class="deprecated">HookParam.method - i-ext-method</h3>

**Change Records**

`v1.0.2` `added`

`v1.0.80` `modified`

将方法体进行 inline

`v1.2.0` `removed`

<h3 class="deprecated">HookParam.constructor - i-ext-method</h3>

**Change Records**

`v1.0.2` `added`

`v1.0.80` `modified`

将方法体进行 inline

`v1.2.0` `removed`

<h3 class="deprecated">HookParam.injectMember - i-ext-method</h3>

**Change Records**

`v1.0.88` `added`

`v1.2.0` `deprecated`

嵌套 Hook 功能已弃用

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

<h3 class="deprecated">beforeHook - method</h3>

**Change Records**

`v1.0` `first`

`v1.1.0` `modified`

新增 `HookCallback` 返回类型

`v1.2.0` `deprecated`

请迁移到 `before`

<h3 class="deprecated">afterHook - method</h3>

**Change Records**

`v1.0` `first`

`v1.1.0` `modified`

新增 `HookCallback` 返回类型

`v1.2.0` `deprecated`

请迁移到 `after`

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