## ConstructorFinder [class]

```kotlin
class ConstructorFinder(override val hookInstance: YukiMemberHookCreater.MemberHookCreater?, override val classSet: Class<*>) : BaseFinder()
```

**变更记录**

`v1.0` `添加`

`v1.0.2` `修改`

合并到 `BaseFinder`

**功能描述**

> `Constructor` 查找类。

### paramCount [field]

```kotlin
var paramCount: Int
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 设置 `Constructor` 参数个数。

你可以不使用 `param` 指定参数类型而是仅使用此变量指定参数个数。

若参数个数小于零则忽略并使用 `param`。

### modifiers [method]

```kotlin
inline fun modifiers(initiate: ModifierRules.() -> Unit): IndexTypeCondition
```

**变更记录**

`v1.0.67` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 设置 `Constructor` 标识符筛选条件。

可不设置筛选条件，默认模糊查找并取第一个匹配的 `Constructor`。

!> 存在多个 `IndexTypeCondition` 时除了 `order` 只会生效最后一个。

### emptyParam [method]

```kotlin
fun emptyParam(): IndexTypeCondition
```

**变更记录**

`v1.0.75` `新增`

**功能描述**

> 设置 `Constructor` 空参数、无参数。

### param [method]

```kotlin
fun param(vararg paramType: Any): IndexTypeCondition
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置 `Constructor` 参数。

如果同时使用了 `paramCount` 则 `paramTypes` 的数量必须与 `paramCount` 完全匹配。

!> 无参 `Constructor` 请使用 `emptyParam` 设置查询条件。

!> 有参 `Constructor` 必须使用此方法设定参数或使用 `paramCount` 指定个数。

!> 存在多个 `IndexTypeCondition` 时除了 `order` 只会生效最后一个。

### paramCount [method]

```kotlin
fun paramCount(num: Int): IndexTypeCondition
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置 `Constructor` 参数个数。

你可以不使用 `param` 指定参数类型而是仅使用此方法指定参数个数。

若参数个数小于零则忽略并使用 `param`。

!> 存在多个 `IndexTypeCondition` 时除了 `order` 只会生效最后一个。

### superClass [method]

```kotlin
fun superClass(isOnlySuperClass: Boolean)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置在 `classSet` 的所有父类中查找当前 `Constructor`。

!> 若当前 `classSet` 的父类较多可能会耗时，API 会自动循环到父类继承是 `Any` 前的最后一个类。

### RemedyPlan [class]

```kotlin
inner class RemedyPlan
```

**变更记录**

`v1.0` `添加`

**功能描述**

> `Constructor` 重查找实现类，可累计失败次数直到查找成功。

#### constructor [method]

```kotlin
inline fun constructor(initiate: ConstructorFinder.() -> Unit)
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 创建需要重新查找的 `Constructor`。

你可以添加多个备选构造方法，直到成功为止，若最后依然失败，将停止查找并输出错误日志。

#### Result [class]

```kotlin
inner class Result
```

**变更记录**

`v1.0.1` `新增`

**功能描述**

> `RemedyPlan` 结果实现类。

##### onFind [method]

```kotlin
fun onFind(initiate: Constructor<*>.() -> Unit)
```

**变更记录**

`v1.0.1` `新增`

**功能描述**

> 当在 `RemedyPlan` 中找到结果时。

**功能示例**

你可以方便地对重查找的 `Constructor` 实现 `onFind` 方法。

> 示例如下

```kotlin
constructor {
    // Your code here.
}.onFind {
    // Your code here.
}
```

### Result [class]

```kotlin
inner class Result(internal val isNoSuch: Boolean, internal val e: Throwable?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> `Constructor` 查找结果实现类。

#### result [method]

```kotlin
inline fun result(initiate: Result.() -> Unit): Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 创建监听结果事件方法体。

**功能示例**

你可以使用 `lambda` 形式创建 `Result` 类。

> 示例如下

```kotlin
constructor {
    // Your code here.
}.result {
    get().call()
    remedys {}
    onNoSuchConstructor {}
}
```

#### get [method]

```kotlin
fun get(): Instance
```

**变更记录**

`v1.0.2` `新增`

**功能描述**

> 获得 `Constructor` 实例处理类。

!> 若你设置了 `remedys` 请使用 `wait` 回调结果方法。

**功能示例**

你可以通过获得方法所在实例来执行构造方法创建新的实例对象。

> 示例如下

```kotlin
constructor {
    // Your code here.
}.get().call()
```

你可以 `cast` 构造方法为指定类型的实例对象。

> 示例如下

```kotlin
constructor {
    // Your code here.
}.get().newInstance<TestClass>()
```

!> 若构造方法含有参数则后方参数必填。

> 示例如下

```kotlin
constructor {
    // Your code here.
}.get().newInstance<TestClass>("param1", "param2")
```

#### give [method]

```kotlin
fun give(): Constructor<*>?
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 得到构造方法本身。

#### wait [method]

```kotlin
fun wait(initiate: Instance.() -> Unit)
```

**变更记录**

`v1.0.2` `新增`

**功能描述**

> 获得 `Constructor` 实例处理类，配合 `RemedyPlan` 使用。

!> 若你设置了 `remedys` 必须使用此方法才能获得结果。

!> 若你没有设置 `remedys` 此方法将不会被回调。

#### remedys [method]

```kotlin
inline fun remedys(initiate: RemedyPlan.() -> Unit): Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 创建构造方法重查找功能。

**功能示例**

当你遇到一种构造方法可能存在不同形式的存在时，可以使用 `RemedyPlan` 重新查找它，而没有必要使用 `onNoSuchConstructor` 捕获异常二次查找构造方法。

若第一次查找失败了，你还可以在这里继续添加此方法体直到成功为止。

> 示例如下

```kotlin
constructor {
    // Your code here.
}.remedys {
    constructor {
        // Your code here.
    }
    constructor {
        // Your code here.
    }
}
```

#### onNoSuchConstructor [method]

```kotlin
inline fun onNoSuchConstructor(initiate: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 监听找不到构造方法时。

只会返回第一次的错误信息，不会返回 `RemedyPlan` 的错误信息。

#### ignoredError [method]

```kotlin
fun ignoredError(): Result
```

**变更记录**

`v1.0.3` `新增`

**功能描述**

> 忽略任何错误发出的警告。

若 `isNotIgnoredHookingFailure` 为 `false` 则自动忽略。

#### Instance [class]

```kotlin
inner class Instance
```

**变更记录**

`v1.0.2` `新增`

**功能描述**

> `Constructor` 实例处理类。

##### call [method]

```kotlin
fun call(vararg param: Any?): Any?
```

**变更记录**

`v1.0.2` `新增`

**功能描述**

> 执行构造方法创建目标实例，不指定目标实例类型。

##### newInstance [method]

```kotlin
fun <T> newInstance(vararg param: Any?): T?
```

**变更记录**

`v1.0.2` `新增`

**功能描述**

> 执行构造方法创建目标实例 ，指定 `T` 目标实例类型。