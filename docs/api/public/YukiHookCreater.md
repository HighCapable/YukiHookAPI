## YukiHookCreater [class]

```kotlin
class YukiHookCreater(private val packageParam: PackageParam, internal val hookClass: HookClass)
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

对 `hookClass` 进行 inline 处理

**功能描述**

> `YukiHookAPI` 核心 Hook 实现类。

### instanceClass [field]

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

### injectMember [method]

```kotlin
inline fun injectMember(tag: String, initiate: MemberHookCreater.() -> Unit): MemberHookCreater.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 注入要 Hook 的方法、构造类。

**功能示例**

你可以注入任意方法与构造类，使用 `injectMember` 即可创建一个 `Hook` 对象。

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

### MemberHookCreater [class]

```kotlin
inner class MemberHookCreater(var tag: String)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> Hook 核心功能实现类，查找和处理需要 Hook 的方法、构造类。

#### member [field]

```kotlin
var member: Member?
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 手动指定要 Hook 的方法、构造类。

!> 不建议使用此方法设置目标需要 Hook 的 `Member` 对象，你可以使用 `method` 或 `constructor` 方法。

**功能示例**

你可以调用 `instanceClass` 来手动查询要 Hook 的方法。

> 示例如下

```kotlin
injectMember {
    member = instanceClass.getMethod("test", StringType)
    beforeHook {}
    afterHook {}
}
```

#### allMethods [method]

```kotlin
fun allMethods(name: String)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 查找并 Hook 当前 `Class` 中指定 `name` 的全部方法。

**功能示例**

使用此方法可将当前类的全部同名方法进行批量 Hook。

!> 无法准确处理每个方法的 `param`，建议使用 `method` 对每个方法单独 Hook。

> 示例如下

```kotlin
injectMember {
    allMethods(name = "test")
    beforeHook {}
    afterHook {}
}
```

#### allConstructors [method]

```kotlin
fun allConstructors()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 查找并 Hook 当前 `Class` 中的全部构造方法。

**功能示例**

使用此方法可将当前类的全部构造方法进行批量 Hook。

!> 无法准确处理每个构造方法的 `param`，建议使用 `constructor` 对每个构造方法单独 Hook。

> 示例如下

```kotlin
injectMember {
    allConstructors()
    beforeHook {}
    afterHook {}
}
```

#### method [method]

```kotlin
inline fun method(initiate: MethodFinder.() -> Unit): MethodFinder.Result
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

#### constructor [method]

```kotlin
inline fun constructor(initiate: ConstructorFinder.() -> Unit): ConstructorFinder.Result
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

#### field [method]

```kotlin
inline fun HookParam.field(initiate: FieldFinder.() -> Unit): FieldFinder.Result
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

#### method [method]

```kotlin
inline fun HookParam.method(initiate: MethodFinder.() -> Unit): MethodFinder.Result
```

**变更记录**

`v1.0.2` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 使用当前 `hookClass` 查找并得到方法。

#### constructor [method]

```kotlin
inline fun HookParam.constructor(initiate: ConstructorFinder.() -> Unit): ConstructorFinder.Result
```

**变更记录**

`v1.0.2` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 使用当前 `hookClass` 查找并得到构造方法。

#### beforeHook [method]

```kotlin
fun beforeHook(initiate: HookParam.() -> Unit)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 在方法执行完成前 Hook。

#### afterHook [method]

```kotlin
fun afterHook(initiate: HookParam.() -> Unit)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 在方法执行完成后 Hook。

#### replaceAny [method]

```kotlin
fun replaceAny(initiate: HookParam.() -> Any?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换此方法内容，给出返回值。

#### replaceUnit [method]

```kotlin
fun replaceUnit(initiate: HookParam.() -> Unit)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换此方法内容，没有返回值，可以称为 `Void`。

#### replaceTo [method]

```kotlin
fun replaceTo(any: Any?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换方法返回值。

#### replaceToTrue [method]

```kotlin
fun replaceToTrue()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换方法返回值为 `true`。

!> 确保替换方法的返回对象为 `Boolean`。

#### replaceToFalse [method]

```kotlin
fun replaceToFalse()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截并替换方法返回值为 `false`。

!> 确保替换方法的返回对象为 `Boolean`。

#### intercept [method]

```kotlin
fun intercept()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 拦截此方法。

!> 这将会禁止此方法执行并返回 `null`。

#### Result [class]

```kotlin
inner class Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听 Hook 结果实现类。

##### result [method]

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
    ignoredConductFailure()
    onHookingFailure {}
    // ...
}
```

##### by [method]

```kotlin
inline fun by(initiate: () -> Boolean): Result
```

**变更记录**

`v1.0.5` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

##### onHooked [method]

```kotlin
fun onHooked(initiate: (Member) -> Unit): Result
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 监听 `member` Hook 成功的回调方法。

在首次 Hook 成功后回调。

##### onNoSuchMemberFailure [method]

```kotlin
fun onNoSuchMemberFailure(initiate: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 监听 `member` 不存在发生错误的回调方法。

##### onConductFailure [method]

```kotlin
fun onConductFailure(initiate: (HookParam, Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听 Hook 进行过程中发生错误的回调方法。

##### onHookingFailure [method]

```kotlin
fun onHookingFailure(initiate: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听 Hook 开始时发生的错误的回调方法。

##### onAllFailure [method]

```kotlin
fun onAllFailure(initiate: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听全部 Hook 过程发生错误的回调方法。

##### ignoredNoSuchMemberFailure [method]

```kotlin
fun ignoredNoSuchMemberFailure(): Result
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 忽略 `member` 不存在发生的错误。

##### ignoredConductFailure [method]

```kotlin
fun ignoredConductFailure(): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 忽略 Hook 进行过程中发生的错误。

##### ignoredHookingFailure [method]

```kotlin
fun ignoredHookingFailure(): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 忽略 Hook 开始时发生的错误。

##### ignoredAllFailure [method]

```kotlin
fun ignoredAllFailure(): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 忽略全部 Hook 过程发生的错误。

### Result [class]

```kotlin
inner class Result
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 监听全部 Hook 结果实现类。

#### result [method]

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

#### by [method]

```kotlin
inline fun by(initiate: () -> Boolean): Result
```

**变更记录**

`v1.0.5` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

#### onPrepareHook [method]

```kotlin
fun onPrepareHook(initiate: () -> Unit): Result
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 监听 `hookClass` 存在时准备开始 Hook 的操作。

#### onHookClassNotFoundFailure [method]

```kotlin
fun onHookClassNotFoundFailure(initiate: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 监听 `hookClass` 找不到时发生错误的回调方法。

#### ignoredHookClassNotFoundFailure [method]

```kotlin
fun ignoredHookClassNotFoundFailure(): Result
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 忽略 `hookClass` 找不到时出现的错误。