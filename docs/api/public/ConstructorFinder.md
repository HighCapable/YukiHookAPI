## ConstructorFinder [class]

```kotlin
class ConstructorFinder(override val hookInstance: YukiHookCreater.MemberHookCreater?, override val classSet: Class<*>) : BaseFinder()
```

<b>变更记录</b>

`v1.0` `添加`

`v1.0.2` `修改`

合并到 `BaseFinder`

<b>功能描述</b>

> `Constructor` 查找类。

### paramCount [field]

```kotlin
var paramCount: Int
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> `Constructor` 参数个数。

你可以不使用 `param` 指定参数类型而是仅使用此变量指定参数个数。

若参数个数小于零则忽略并使用 `param`。

### modifiers [method]

```kotlin
fun modifiers(initiate: ModifierRules.() -> Unit): IndexTypeCondition
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> `Constructor` 筛选条件。

可不设置筛选条件，默认模糊查找并取第一个匹配的 `Constructor`。

!> 存在多个 `IndexTypeCondition` 时除了 `order` 只会生效最后一个。

### param [method]

```kotlin
fun param(vararg paramType: Any): IndexTypeCondition
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> `Constructor` 参数。

如果同时使用了 `paramCount` 则 `paramTypes` 的数量必须与 `paramCount` 完全匹配。

!> 无参 `Constructor` 不要使用此方法。

!> 有参 `Constructor` 必须使用此方法设定参数或使用 `paramCount` 指定个数。

!> 存在多个 `IndexTypeCondition` 时除了 `order` 只会生效最后一个。

### RemedyPlan [class]

```kotlin
inner class RemedyPlan
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> `Constructor` 重查找实现类，可累计失败次数直到查找成功。

#### constructor [method]

```kotlin
fun constructor(initiate: ConstructorFinder.() -> Unit)
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 创建需要重新查找的 `Constructor`。

你可以添加多个备选构造方法，直到成功为止，若最后依然失败，将停止查找并输出错误日志。

#### Result [class]

```kotlin
inner class Result
```

<b>变更记录</b>

`v1.0.1` `新增`

<b>功能描述</b>

> `RemedyPlan` 结果实现类。

##### onFind [method]

```kotlin
fun onFind(initiate: Constructor<*>.() -> Unit)
```

<b>变更记录</b>

`v1.0.1` `新增`

<b>功能描述</b>

> 当在 `RemedyPlan` 中找到结果时。

<b>功能示例</b>

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
inner class Result(internal val isNoSuch: Boolean, private val e: Throwable?)
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> `Constructor` 查找结果实现类。

#### result [method]

```kotlin
fun result(initiate: Result.() -> Unit): Result
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 创建监听结果事件方法体。

<b>功能示例</b>

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

<b>变更记录</b>

`v1.0.2` `新增`

<b>功能描述</b>

> 获得 `Constructor` 实例处理类。

!> 若你设置了 `remedys` 请使用 `wait` 回调结果方法。

<b>功能示例</b>

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

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 得到构造方法本身。

#### wait [method]

```kotlin
fun wait(initiate: Instance.() -> Unit)
```

<b>变更记录</b>

`v1.0.2` `新增`

<b>功能描述</b>

> 获得 `Constructor` 实例处理类，配合 `RemedyPlan` 使用。

!> 若你设置了 `remedys` 必须使用此方法才能获得结果。

!> 若你没有设置 `remedys` 此方法将不会被回调。

#### remedys [method]

```kotlin
fun remedys(initiate: RemedyPlan.() -> Unit): Result
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 创建构造方法重查找功能。

<b>功能示例</b>

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
fun onNoSuchConstructor(initiate: (Throwable) -> Unit): Result
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 监听找不到构造方法时。

只会返回第一次的错误信息，不会返回 `RemedyPlan` 的错误信息。

#### ignoredError [method]

```kotlin
fun ignoredError(): Result
```

<b>变更记录</b>

`v1.0.3` `新增`

<b>功能描述</b>

> 忽略任何错误发出的警告。

若 `isNotIgnoredHookingFailure` 为 `false` 则自动忽略。

#### Instance [class]

```kotlin
inner class Instance
```

<b>变更记录</b>

`v1.0.2` `新增`

<b>功能描述</b>

> `Constructor` 实例处理类。

##### call [method]

```kotlin
fun call(vararg param: Any?): Any?
```

<b>变更记录</b>

`v1.0.2` `新增`

<b>功能描述</b>

> 执行构造方法创建目标实例，不指定目标实例类型。

##### newInstance [method]

```kotlin
fun <T> newInstance(vararg param: Any?): T?
```

<b>变更记录</b>

`v1.0.2` `新增`

<b>功能描述</b>

> 执行构造方法创建目标实例 ，指定 `T` 目标实例类型。