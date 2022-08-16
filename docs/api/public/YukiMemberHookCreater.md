## YukiMemberHookCreater *- class*

```kotlin
class YukiMemberHookCreater(internal val packageParam: PackageParam, internal val hookClass: HookClass)
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

对 `hookClass` 进行 inline 处理

**功能描述**

> `YukiHookAPI` 的 `Member` 核心 Hook 实现类。

### PRIORITY_DEFAULT *- field*

```kotlin
val PRIORITY_DEFAULT: Int
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 默认 Hook 回调优先级。

### PRIORITY_LOWEST *- field*

```kotlin
val PRIORITY_LOWEST: Int
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 延迟回调 Hook 方法结果。

### PRIORITY_HIGHEST *- field*

```kotlin
val PRIORITY_HIGHEST: Int
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 更快回调 Hook 方法结果。

### instanceClass *- field*

```kotlin
val instanceClass: Class<*>
```

**变更记录**

`v1.0` `添加`

`v1.0.2` `修改`

~~`thisClass`~~ 更名为 `instanceClass`

**功能描述**

> 得到当前被 Hook 的 `Class`。

!> 不推荐直接使用，万一得不到 `Class` 对象则会无法处理异常导致崩溃。

### injectMember *- method*

```kotlin
inline fun injectMember(priority: Int, tag: String, initiate: MemberHookCreater.() -> Unit): MemberHookCreater.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

增加 `priority` Hook 优先级

**功能描述**

> 注入要 Hook 的方法、构造方法。

**功能示例**

你可以注入任意方法与构造方法，使用 `injectMember` 即可创建一个 `Hook` 对象。

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

### useDangerousOperation *- method*

```kotlin
fun useDangerousOperation(option: String)
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 允许 Hook 过程中的所有危险行为。

请在 `option` 中键入 `Yes do as I say!` 代表你同意允许所有危险行为。

你还需要在整个作用域中声明注解 `CauseProblemsApi` 以消除警告。

!> 若你不知道允许此功能会带来何种后果，请勿使用。

### MemberHookCreater *- class*

```kotlin
inner class MemberHookCreater internal constructor(private val priority: Int, internal val tag: String)
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

增加 `priority` Hook 优先级

`v1.0.81` `修改`

增加 `packageName` 当前 Hook 的 APP 包名

`v1.0.93` `修改`

移除 `packageName`

**功能描述**

> Hook 核心功能实现类，查找和处理需要 Hook 的方法、构造方法。

#### ~~member *- field*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.93` `移除`

请转移到 `members`

#### members *- method*

```kotlin
fun members(vararg member: Member?)
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 手动指定要 Hook 的方法、构造方法。

!> 不建议使用此方法设置目标需要 Hook 的 `Member` 对象，你可以使用 `method` 或 `constructor` 方法。

**功能示例**

你可以调用 `instanceClass` 来手动查询要 Hook 的方法。

> 示例如下

```kotlin
injectMember {
    members(instanceClass.getMethod("test", StringType))
    beforeHook {}
    afterHook {}
}
```

同样地，你也可以传入一组方法同时进行 Hook。

> 示例如下

```kotlin
injectMember {
    members(
        instanceClass.getMethod("test1", StringType),
        instanceClass.getMethod("test2", StringType),
        instanceClass.getMethod("test3", StringType)
    )
    beforeHook {}
    afterHook {}
}
```

#### ~~allMethods *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.93` `作废`

请使用 `method { name = /** name */ }.all()` 来取代它

#### ~~allConstructors *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.93` `作废`

请使用 `allMembers(MembersType.CONSTRUCTOR)` 来取代它

#### allMembers *- method*

```kotlin
fun allMembers(type: MembersType)
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 查找并 Hook `hookClass` 中的全部方法、构造方法。

!> 警告：无法准确处理每个方法的返回值和 `param`，建议使用 `method` or `constructor` 对每个方法单独 Hook。

#### method *- method*

```kotlin
inline fun method(initiate: MethodCondition): MethodFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找当前 `Class` 需要 Hook 的方法。

**功能示例**

你可参考 [MethodFinder](#methodfinder-class) 查看详细用法。

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

#### constructor *- method*

```kotlin
inline fun constructor(initiate: ConstructorCondition): ConstructorFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找当前 `Class` 需要 Hook 的构造方法。

**功能示例**

你可参考 [ConstructorFinder](#constructorfinder-class) 查看详细用法。

> 示例如下

```kotlin
injectMember {
    constructor { param(StringType) }
    beforeHook {}
    afterHook {}
}
```

#### HookParam.field *- i-ext-method*

```kotlin
inline fun HookParam.field(initiate: FieldCondition): FieldFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 使用当前 `hookClass` 查找并得到 `Field`。

**功能示例**

你可参考 [FieldFinder](#fieldfinder-class) 查看详细用法。

> 示例如下

```kotlin
injectMember {
    method {
        name = "test"
        param(StringType)
        returnType = UnitType
    }
    afterHook {
        field {
            name = "isSweet"
            type = BooleanType
        }.get(instance).setTrue()
    }
}
```

#### HookParam.method *- i-ext-method*

```kotlin
inline fun HookParam.method(initiate: MethodCondition): MethodFinder.Result
```

**变更记录**

`v1.0.2` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 使用当前 `hookClass` 查找并得到方法。

#### HookParam.constructor *- i-ext-method*

```kotlin
inline fun HookParam.constructor(initiate: ConstructorCondition): ConstructorFinder.Result
```

**变更记录**

`v1.0.2` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 使用当前 `hookClass` 查找并得到构造方法。

#### HookParam.injectMember *- i-ext-method*

```kotlin
inline fun HookParam.injectMember(priority: Int, tag: String, initiate: MemberHookCreater.() -> Unit): MemberHookCreater.Result
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 注入要 Hook 的方法、构造方法 (嵌套 Hook)。

#### beforeHook *- method*

```kotlin
fun beforeHook(initiate: HookParam.() -> Unit): HookCallback
```

**变更记录**

`v1.0` `添加`

`v1.0.93` `修改`

新增 `HookCallback` 返回类型

**功能描述**

> 在方法执行完成前 Hook。

#### afterHook *- method*

```kotlin
fun afterHook(initiate: HookParam.() -> Unit): HookCallback
```

**变更记录**

`v1.0` `添加`

`v1.0.93` `修改`

新增 `HookCallback` 返回类型

**功能描述**

> 在方法执行完成后 Hook。

#### replaceAny *- method*

```kotlin
fun replaceAny(initiate: HookParam.() -> Any?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换此方法内容，给出返回值。

#### replaceUnit *- method*

```kotlin
fun replaceUnit(initiate: HookParam.() -> Unit)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换此方法内容，没有返回值，可以称为 `Void`。

#### replaceTo *- method*

```kotlin
fun replaceTo(any: Any?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换方法返回值。

#### replaceToTrue *- method*

```kotlin
fun replaceToTrue()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换方法返回值为 `true`。

!> 确保替换方法的返回对象为 `Boolean`。

#### replaceToFalse *- method*

```kotlin
fun replaceToFalse()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换方法返回值为 `false`。

!> 确保替换方法的返回对象为 `Boolean`。

#### intercept *- method*

```kotlin
fun intercept()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截此方法。

!> 这将会禁止此方法执行并返回 `null`。

!> 注意：例如 `Int`、`Long`、`Boolean` 常量返回值的方法一旦被设置为 null 可能会造成 Hook APP 抛出异常。

#### removeSelf *- method*

```kotlin
fun removeSelf(result: (Boolean) -> Unit)
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 移除当前注入的 Hook 方法、构造方法 (解除 Hook)。

!> 你只能在 Hook 回调方法中使用此功能。

#### HookCallback *- class*

```kotlin
inner class HookCallback internal constructor()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> Hook 方法体回调实现类。

##### onFailureThrowToApp *- method*

```kotlin
fun onFailureThrowToApp()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 当回调方法体内发生异常时将异常抛出给当前 Hook APP。

#### Result *- class*

```kotlin
inner class Result internal constructor()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听 Hook 结果实现类。

##### result *- method*

```kotlin
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

##### by *- method*

```kotlin
inline fun by(condition: () -> Boolean): Result
```

**变更记录**

`v1.0.5` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

##### onHooked *- method*

```kotlin
fun onHooked(result: (Member) -> Unit): Result
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 监听 `members` Hook 成功的回调方法。

在首次 Hook 成功后回调。

在重复 Hook 时会回调 `onAlreadyHooked`。

##### onAlreadyHooked *- method*

```kotlin
fun onAlreadyHooked(result: (Member) -> Unit): Result
```

**变更记录**

`v1.0.89` `新增`

**功能描述**

> 监听 `members` 重复 Hook 的回调方法。

!> 同一个 `hookClass` 中的同一个 `members` 不会被 API 重复 Hook，若由于各种原因重复 Hook 会回调此方法。

##### onNoSuchMemberFailure *- method*

```kotlin
fun onNoSuchMemberFailure(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 监听 `members` 不存在发生错误的回调方法。

##### onConductFailure *- method*

```kotlin
fun onConductFailure(result: (HookParam, Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听 Hook 进行过程中发生错误的回调方法。

##### onHookingFailure *- method*

```kotlin
fun onHookingFailure(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听 Hook 开始时发生的错误的回调方法。

##### onAllFailure *- method*

```kotlin
fun onAllFailure(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听全部 Hook 过程发生错误的回调方法。

##### ignoredNoSuchMemberFailure *- method*

```kotlin
fun ignoredNoSuchMemberFailure(): Result
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 忽略 `members` 不存在发生的错误。

##### ignoredConductFailure *- method*

```kotlin
fun ignoredConductFailure(): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 忽略 Hook 进行过程中发生的错误。

##### ignoredHookingFailure *- method*

```kotlin
fun ignoredHookingFailure(): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 忽略 Hook 开始时发生的错误。

##### ignoredAllFailure *- method*

```kotlin
fun ignoredAllFailure(): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 忽略全部 Hook 过程发生的错误。

##### remove *- method*

```kotlin
fun remove(result: (Boolean) -> Unit)
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 移除当前注入的 Hook 方法、构造方法 (解除 Hook)。

!> 你只能在 Hook 成功后才能解除 Hook，可监听 `onHooked` 事件。

### Result *- class*

```kotlin
inner class Result internal constructor()
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 监听全部 Hook 结果实现类。

#### result *- method*

```kotlin
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

#### by *- method*

```kotlin
inline fun by(condition: () -> Boolean): Result
```

**变更记录**

`v1.0.5` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

#### onPrepareHook *- method*

```kotlin
fun onPrepareHook(callback: () -> Unit): Result
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 监听 `hookClass` 存在时准备开始 Hook 的操作。

#### onHookClassNotFoundFailure *- method*

```kotlin
fun onHookClassNotFoundFailure(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 监听 `hookClass` 找不到时发生错误的回调方法。

#### ignoredHookClassNotFoundFailure *- method*

```kotlin
fun ignoredHookClassNotFoundFailure(): Result
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 忽略 `hookClass` 找不到时出现的错误。