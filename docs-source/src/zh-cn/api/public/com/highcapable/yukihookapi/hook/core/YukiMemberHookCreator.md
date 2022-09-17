---
pageClass: code-page
---

# YukiMemberHookCreator <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiMemberHookCreator(internal val packageParam: PackageParam, internal val hookClass: HookClass)
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

对 `hookClass` 进行 inline 处理

`v1.1.0` `修改`

修正拼写错误的 **Creater** 命名到 **Creator**

**功能描述**

> `YukiHookAPI` 的 `Member` 核心 Hook 实现类。

## PRIORITY_DEFAULT <span class="symbol">- field</span>

```kotlin:no-line-numbers
val PRIORITY_DEFAULT: Int
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 默认 Hook 回调优先级。

## PRIORITY_LOWEST <span class="symbol">- field</span>

```kotlin:no-line-numbers
val PRIORITY_LOWEST: Int
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 延迟回调 Hook 方法结果。

## PRIORITY_HIGHEST <span class="symbol">- field</span>

```kotlin:no-line-numbers
val PRIORITY_HIGHEST: Int
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 更快回调 Hook 方法结果。

## instanceClass <span class="symbol">- field</span>

```kotlin:no-line-numbers
val instanceClass: Class<*>
```

**变更记录**

`v1.0` `添加`

`v1.0.2` `修改`

~~`thisClass`~~ 更名为 `instanceClass`

**功能描述**

> 得到当前被 Hook 的 `Class`。

::: danger

不推荐直接使用，万一得不到 **Class** 对象则会无法处理异常导致崩溃。

:::

## injectMember <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun injectMember(priority: Int, tag: String, initiate: MemberHookCreator.() -> Unit): MemberHookCreator.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

增加 `priority` Hook 优先级

**功能描述**

> 注入要 Hook 的 `Method`、`Constructor`。

**功能示例**

你可以注入任意 `Method` 与 `Constructor`，使用 `injectMember` 即可创建一个 `Hook` 对象。

> 示例如下

```kotlin
injectMember {
    // Your code here.
}
```

你还可以自定义 `tag`，方便你在调试的时候能够区分你的 `Hook` 对象。

> 示例如下

```kotlin
injectMember(tag = "KuriharaYuki") {
    // Your code here.
}
```

你还可以自定义 `priority`，以控制当前 Hook 对象并列执行的优先级速度。

> 示例如下

```kotlin
injectMember(priority = PRIORITY_HIGHEST) {
    // Your code here.
}
```

## useDangerousOperation <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun useDangerousOperation(option: String)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 允许 Hook 过程中的所有危险行为。

请在 `option` 中键入 `Yes do as I say!` 代表你同意允许所有危险行为。

你还需要在整个调用域中声明注解 `CauseProblemsApi` 以消除警告。

若你只需要 Hook `ClassLoader` 的 `loadClass` 方法，请参考 [ClassLoader.onLoadClass](../factory/ReflectionFactory#classloader-onloadclass-ext-method)。

::: danger

若你不知道允许此功能会带来何种后果，请勿使用。

:::

## MemberHookCreator <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class MemberHookCreator internal constructor(private val priority: Int, internal val tag: String)
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

增加 `priority` Hook 优先级

`v1.0.81` `修改`

增加 `packageName` 当前 Hook 的 APP 包名

`v1.1.0` `修改`

移除 `packageName`

修正拼写错误的 **Creater** 命名到 **Creator**

**功能描述**

> Hook 核心功能实现类，查找和处理需要 Hook 的 `Method`、`Constructor`。

<h3 class="deprecated">member - field</h3>

**变更记录**

`v1.0` `添加`

`v1.1.0` `移除`

请转移到 `members`

### members <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun members(vararg member: Member?)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 手动指定要 Hook 的 `Method`、`Constructor`。

::: warning

不建议使用此方法设置目标需要 Hook 的 **Member** 对象，你可以使用 **method** 或 **constructor** 方法。

:::

**功能示例**

你可以调用 `instanceClass` 来手动查询要 Hook 的 `Method`、`Constructor`。

> 示例如下

```kotlin
injectMember {
    members(instanceClass.getDeclaredMethod("test", StringType))
    beforeHook {}
    afterHook {}
}
```

同样地，你也可以传入一组 `Member` 同时进行 Hook。

> 示例如下

```kotlin
injectMember {
    members(
        instanceClass.getDeclaredMethod("test1", StringType),
        instanceClass.getDeclaredMethod("test2", StringType),
        instanceClass.getDeclaredMethod("test3", StringType)
    )
    beforeHook {}
    afterHook {}
}
```

<h3 class="deprecated">allMethods - method</h3>

**变更记录**

`v1.0` `添加`

`v1.1.0` `作废`

请使用 `method { name = /** name */ }.all()` 来取代它

<h3 class="deprecated">allConstructors - method</h3>

**变更记录**

`v1.0` `添加`

`v1.1.0` `作废`

请使用 `allMembers(MembersType.CONSTRUCTOR)` 来取代它

### allMembers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun allMembers(type: MembersType)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 查找并 Hook `hookClass` 中的全部 `Method`、`Constructor`。

::: warning

无法准确处理每个 **Member** 的返回值和 **param**，建议使用 **method** or **constructor** 对每个 **Member** 单独 Hook。

:::

### method <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun method(initiate: MethodConditions): MethodFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找当前 `Class` 需要 Hook 的 `Method` 。

**功能示例**

你可参考 [MethodFinder](finder/members/MethodFinder) 查看详细用法。

> 示例如下

```kotlin
injectMember {
    method {
        name = "test"
        param(StringType)
        returnType = UnitType
    }
    beforeHook {}
    afterHook {}
}
```

若想 Hook 当前查询 `method { ... }` 条件的全部结果，你只需要在最后加入 `all` 即可。

> 示例如下

```kotlin
injectMember {
    method {
        name = "test"
        paramCount(1..5)
    }.all()
    beforeHook {}
    afterHook {}
}
```

此时 `beforeHook` 与 `afterHook` 会在每个查询到的结果中多次回调 Hook 方法体。

::: warning

若没有 **all**，默认只会 Hook 当前条件查询到的数组下标结果第一位。

:::

### constructor <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun constructor(initiate: ConstructorConditions): ConstructorFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找当前 `Class` 需要 Hook 的 `Constructor`。

**功能示例**

你可参考 [ConstructorFinder](finder/members/ConstructorFinder) 查看详细用法。

> 示例如下

```kotlin
injectMember {
    constructor { param(StringType) }
    beforeHook {}
    afterHook {}
}
```

若想 Hook 当前查询 `constructor { ... }` 条件的全部结果，你只需要在最后加入 `all` 即可。

> 示例如下

```kotlin
injectMember {
    constructor { paramCount(1..5) }.all()
    beforeHook {}
    afterHook {}
}
```

此时 `beforeHook` 与 `afterHook` 会在每个查询到的结果中多次回调 Hook 方法体。

::: warning

若没有 **all**，默认只会 Hook 当前条件查询到的数组下标结果第一位。

:::

### HookParam.field <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
inline fun HookParam.field(initiate: FieldConditions): FieldFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 使用当前 `hookClass` 查找并得到 `Field`。

**功能示例**

你可参考 [FieldFinder](finder/members/FieldFinder) 查看详细用法。

> 示例如下

```kotlin
injectMember {
    method {
        name = "test"
        param(StringType)
        returnType = UnitType
    }
    afterHook {
        // 这里不需要再调用 instanceClass.field 进行查询
        field {
            name = "isSweet"
            type = BooleanType
        }.get(instance).setTrue()
    }
}
```

### HookParam.method <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
inline fun HookParam.method(initiate: MethodConditions): MethodFinder.Result
```

**变更记录**

`v1.0.2` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 使用当前 `hookClass` 查找并得到 `Method` 。

### HookParam.constructor <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
inline fun HookParam.constructor(initiate: ConstructorConditions): ConstructorFinder.Result
```

**变更记录**

`v1.0.2` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 使用当前 `hookClass` 查找并得到 `Constructor`。

### HookParam.injectMember <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
inline fun HookParam.injectMember(priority: Int, tag: String, initiate: MemberHookCreator.() -> Unit): MemberHookCreator.Result
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 注入要 Hook 的 `Method`、`Constructor` (嵌套 Hook)。

### beforeHook <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun beforeHook(initiate: HookParam.() -> Unit): HookCallback
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

新增 `HookCallback` 返回类型

**功能描述**

> 在 `Member` 执行完成前 Hook。

### afterHook <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun afterHook(initiate: HookParam.() -> Unit): HookCallback
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

新增 `HookCallback` 返回类型

**功能描述**

> 在 `Member` 执行完成后 Hook。

### replaceAny <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceAny(initiate: HookParam.() -> Any?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换此 `Member` 内容，给出返回值。

### replaceUnit <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceUnit(initiate: HookParam.() -> Unit)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换此 `Member` 内容，没有返回值，可以称为 `Void`。

### replaceTo <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceTo(any: Any?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替 `Member` 返回值。

### replaceToTrue <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceToTrue()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换 `Member` 返回值为 `true`。

::: danger

确保替换 **Member** 的返回对象为 **Boolean**。

:::

### replaceToFalse <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceToFalse()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换 `Member` 返回值为 `false`。

::: danger

确保替换 **Member** 的返回对象为 **Boolean**。

:::

### intercept <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun intercept()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截此 `Member` 。

这将会禁止此 `Member` 执行并返回 `null`。

::: danger

例如 **Int**、**Long**、**Boolean** 常量返回值的 **Member** 一旦被设置为 **null** 可能会造成 Hook APP 抛出异常。

:::

### removeSelf <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun removeSelf(result: (Boolean) -> Unit)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 移除当前注入的 Hook `Method`、`Constructor` (解除 Hook)。

::: danger

你只能在 Hook 回调方法中使用此功能。

:::

### HookCallback <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class HookCallback internal constructor()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> Hook 方法体回调实现类。

#### onFailureThrowToApp <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onFailureThrowToApp()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 当回调方法体内发生异常时将异常抛出给当前 Hook APP。

### Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听 Hook 结果实现类。

#### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Result.() -> Unit): Result
```

**变更记录**

`v1.0` `添加`

`v1.0.5` `修改`

~~`failures`~~ 修改为 `result`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 创建监听失败事件方法体。

**功能示例**

你可以使用此方法为 `Result` 类创建 `lambda` 方法体。

> 示例如下

```kotlin
injectMember {
    // Your code here.
}.result {
    onHooked {}
    onAlreadyHooked {}
    ignoredConductFailure()
    onHookingFailure {}
    // ...
}
```

#### by <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun by(condition: () -> Boolean): Result
```

**变更记录**

`v1.0.5` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

#### onHooked <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHooked(result: (Member) -> Unit): Result
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 监听 `members` Hook 成功的回调方法。

在首次 Hook 成功后回调。

在重复 Hook 时会回调 `onAlreadyHooked`。

#### onAlreadyHooked <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onAlreadyHooked(result: (Member) -> Unit): Result
```

**变更记录**

`v1.0.89` `新增`

**功能描述**

> 监听 `members` 重复 Hook 的回调方法。

::: warning

同一个 **hookClass** 中的同一个 **members** 不会被 API 重复 Hook，若由于各种原因重复 Hook 会回调此方法。

:::

#### onNoSuchMemberFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onNoSuchMemberFailure(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 监听 `members` 不存在发生错误的回调方法。

#### onConductFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onConductFailure(result: (HookParam, Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听 Hook 进行过程中发生错误的回调方法。

#### onHookingFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHookingFailure(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听 Hook 开始时发生的错误的回调方法。

#### onAllFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onAllFailure(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听全部 Hook 过程发生错误的回调方法。

#### ignoredNoSuchMemberFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredNoSuchMemberFailure(): Result
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 忽略 `members` 不存在发生的错误。

#### ignoredConductFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredConductFailure(): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 忽略 Hook 进行过程中发生的错误。

#### ignoredHookingFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredHookingFailure(): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 忽略 Hook 开始时发生的错误。

#### ignoredAllFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredAllFailure(): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 忽略全部 Hook 过程发生的错误。

#### remove <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun remove(result: (Boolean) -> Unit)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 移除当前注入的 Hook `Method`、`Constructor` (解除 Hook)。

::: warning

你只能在 Hook 成功后才能解除 Hook，可监听 **onHooked** 事件。

:::

## Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor()
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 监听全部 Hook 结果实现类。

### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Result.() -> Unit): Result
```

**变更记录**

`v1.0.3` `新增`

`v1.0.5` `修改`

~~`failures`~~ 修改为 `result`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 创建监听事件方法体。

### by <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun by(condition: () -> Boolean): Result
```

**变更记录**

`v1.0.5` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

### onPrepareHook <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onPrepareHook(callback: () -> Unit): Result
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 监听 `hookClass` 存在时准备开始 Hook 的操作。

### onHookClassNotFoundFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHookClassNotFoundFailure(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 监听 `hookClass` 找不到时发生错误的回调方法。

### ignoredHookClassNotFoundFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredHookClassNotFoundFailure(): Result
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 忽略 `hookClass` 找不到时出现的错误。