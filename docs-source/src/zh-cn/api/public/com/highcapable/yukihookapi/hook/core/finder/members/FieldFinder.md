---
pageClass: code-page
---

# FieldFinder <span class="symbol">- class</span>

```kotlin:no-line-numbers
class FieldFinder internal constructor(override val classSet: Class<*>?) : MemberBaseFinder
```

**变更记录**

`v1.0` `添加`

`v1.0.2` `修改`

合并到 `BaseFinder`

`v1.1.0` `修改`

合并到 `MemberBaseFinder`

`v1.1.8` `修改`

移动 `hookInstance` 参数到 `MemberBaseFinder.MemberHookerManager`

**功能描述**

> `Field` 查找类。

可通过指定类型查找指定 `Field` 或一组 `Field`。

<h2 class="deprecated">classSet - field</h2>

**变更记录**

`v1.0` `添加`

`v1.0.2` `移除`

## name <span class="symbol">- field</span>

```kotlin:no-line-numbers
var name: String
```

**变更记录**

`v1.0` `添加`

`v1.0.70` `修改`

允许不填写名称

**功能描述**

> 设置 `Field` 名称。

::: danger

若不填写名称则必须存在一个其它条件。

:::

## type <span class="symbol">- field</span>

```kotlin:no-line-numbers
var type: Any?
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置 `Field` 类型。

可不填写类型。

## modifiers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun modifiers(conditions: ModifierConditions): IndexTypeCondition
```

**变更记录**

`v1.0.67` `新增`

`v1.0.80` `修改`

将方法体进行 inline

`v1.1.0` `修改`

合并到 `ModifierConditions`

**功能描述**

> 设置 `Field` 标识符筛选条件。

可不设置筛选条件。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## order <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun order(): IndexTypeCondition
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 顺序筛选字节码的下标。

## name <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun name(value: String): IndexTypeCondition
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置 `Field` 名称。

::: danger

若不填写名称则必须存在一个其它条件。

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## name <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun name(conditions: NameConditions): IndexTypeCondition
```

**变更记录**

`v1.0.88` `新增`

`v1.1.0` `修改`

合并到 `NameConditions`

**功能描述**

> 设置 `Field` 名称条件。

::: danger

若不填写名称则必须存在一个其它条件。

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## type <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun type(value: Any): IndexTypeCondition
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置 `Field` 类型。

可不填写类型。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## type <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun type(conditions: ObjectConditions): IndexTypeCondition
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 设置 `Field` 类型条件。

可不填写类型。

::: danger

存在多个 **IndexTypeCondition** 时除了 **order** 只会生效最后一个。

:::

## superClass <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun superClass(isOnlySuperClass: Boolean)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置在 `classSet` 的所有父类中查找当前 `Field`。

::: warning

若当前 **classSet** 的父类较多可能会耗时，API 会自动循环到父类继承是 **Any** 前的最后一个类。

:::

## RemedyPlan <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class RemedyPlan internal constructor()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> `Field` 重查找实现类，可累计失败次数直到查找成功。

### field <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun field(initiate: FieldConditions): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 创建需要重新查找的 `Field`。

你可以添加多个备选 `Field`，直到成功为止，若最后依然失败，将停止查找并输出错误日志。

### Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> `RemedyPlan` 结果实现类。

#### onFind <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onFind(initiate: HashSet<Field>.() -> Unit)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 当在 `RemedyPlan` 中找到结果时。

**功能示例**

你可以方便地对重查找的 `Field` 实现 `onFind` 方法。

> 示例如下

```kotlin
field {
    // Your code here.
}.onFind {
    // Your code here.
}
```

## Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor(internal val isNoSuch: Boolean, private val throwable: Throwable?) : BaseResult
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

继承到接口 `BaseResult`

**功能描述**

> `Field` 查找结果实现类。

### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
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
field {
    // Your code here.
}.result {
    get(instance).set("something")
    get(instance).string()
    get(instance).cast<CustomClass>()
    get().boolean()
    all(instance)
    give()
    giveAll()
    onNoSuchField {}
}
```

### get <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun get(instance: Any?): Instance
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获得 `Field` 实例处理类。

若有多个 `Field` 结果只会返回第一个。

**功能示例**

你可以轻松地得到 `Field` 的实例以及使用它进行设置实例。

> 示例如下

```kotlin
field {
    // Your code here.
}.get(instance).set("something")
```

如果你取到的是静态 `Field`，可以不需要设置实例。

> 示例如下

```kotlin
field {
    // Your code here.
}.get().set("something")
```

### all <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun all(instance: Any?): ArrayList<Instance>
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获得 `Field` 实例处理类数组。

返回全部查找条件匹配的多个 `Field` 实例结果。

**功能示例**

你可以通过此方法来获得当前条件结果中匹配的全部 `Field`，其 `Field` 所在实例用法与 `get` 相同。

> 示例如下

```kotlin
field {
    // Your code here.
}.all(instance).forEach { instance ->
    instance.self
}
```

### give <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun give(): Field?
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 得到 `Field` 本身。

若有多个 Field 结果只会返回第一个。

在查找条件找不到任何结果的时候将返回 `null`。

### giveAll <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun giveAll(): HashSet<Field>
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 得到 `Field` 本身数组。

返回全部查找条件匹配的多个 `Field` 实例。

在查找条件找不到任何结果的时候将返回空的 `HashSet`。

### wait <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun wait(instance: Any?, initiate: Instance.() -> Unit)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获得 `Field` 实例处理类，配合 `RemedyPlan` 使用。

若有多个 `Field` 结果只会返回第一个。

::: danger

若你设置了 **remedys** 必须使用此方法才能获得结果。

若你没有设置 **remedys** 此方法将不会被回调。

:::

### waitAll <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun waitAll(instance: Any?, initiate: ArrayList<Instance>.() -> Unit)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获得 `Field` 实例处理类数组，配合 `RemedyPlan` 使用。

返回全部查找条件匹配的多个 `Field` 实例结果。

::: danger

若你设置了 **remedys** 必须使用此方法才能获得结果。

若你没有设置 **remedys** 此方法将不会被回调。

:::

### remedys <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun remedys(initiate: RemedyPlan.() -> Unit): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 创建 `Field` 重查找功能。

**功能示例**

当你遇到一种 `Field` 可能存在不同形式的存在时，可以使用 `RemedyPlan` 重新查找它，而没有必要使用 `onNoSuchField` 捕获异常二次查找 `Field`。

若第一次查找失败了，你还可以在这里继续添加此方法体直到成功为止。

> 示例如下

```kotlin
field {
    // Your code here.
}.remedys {
    field {
        // Your code here.
    }
    field {
        // Your code here.
    }
}
```

### onNoSuchField <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onNoSuchField(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听找不到 `Field` 时。

### ignored <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignored(): Result
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 忽略异常并停止打印任何错误日志。

若 `MemberBaseFinder.MemberHookerManager.isNotIgnoredNoSuchMemberFailure` 为 `false` 则自动忽略。

::: warning

此时若要监听异常结果，你需要手动实现 **onNoSuchField** 方法。

:::

<h3 class="deprecated">ignoredError - method</h3>

**变更记录**

`v1.0.3` `新增`

`v1.1.0` `作废`

请转移到新方法 `ignored()`

### Instance <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Instance internal constructor(private val instance: Any?, private val field: Field?)
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

新增 `field` 参数

不再对外公开 `self` 参数

**功能描述**

> `Field` 实例变量处理类。

<h4 class="deprecated">self - field</h4>

**变更记录**

`v1.0` `添加`

`v1.1.0` `移除`

请直接使用 `any` 方法得到 `Field` 自身的实例化对象

#### current <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun current(ignored: Boolean): CurrentClass?
```

```kotlin:no-line-numbers
inline fun current(ignored: Boolean, initiate: CurrentClass.() -> Unit): Any?
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获得当前 `Field` 自身 `self` 实例的类操作对象 `CurrentClass`。

#### cast <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun <T> cast(): T?
```

**变更记录**

`v1.0` `添加`

`v1.0.68` `修改`

修改 ~~`of`~~ 为 `cast`

移动方法到 `Instance`

**功能描述**

> 得到当前 `Field` 实例。

#### byte <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun byte(): Byte?
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到当前 `Field` Byte 实例。

#### int <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun int(): Int
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofInt`~~ 为 `int`

移动方法到 `Instance`

**功能描述**

> 得到当前 `Field` Int 实例。

#### long <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun long(): Long
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofLong`~~ 为 `long`

移动方法到 `Instance`

**功能描述**

> 得到当前 `Field` Long 实例。

#### short <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun short(): Short
```
**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofShort`~~ 为 `short`

移动方法到 `Instance`

**功能描述**

> 得到当前 `Field` Short 实例。

#### double <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun double(): Double
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofDouble`~~ 为 `double`

移动方法到 `Instance`

**功能描述**

> 得到当前 `Field` Double 实例。

#### float <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun float(): Float
```
**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofFloat`~~ 为 `float`

移动方法到 `Instance`

**功能描述**

> 得到当前 `Field` Float 实例。

#### string <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun string(): String
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofString`~~ 为 `string`

移动方法到 `Instance`

**功能描述**

> 得到当前 `Field` String 实例。

#### char <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun char(): Char
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到当前 `Field` Char 实例。

#### boolean <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun boolean(): Boolean
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofBoolean`~~ 为 `boolean`

移动方法到 `Instance`

**功能描述**

> 得到当前 `Field` Boolean 实例。

#### any <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun any(): Any?
```
**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofAny`~~ 为 `any`

移动方法到 `Instance`

**功能描述**

> 得到当前 `Field` Any 实例。

#### array <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> array(): Array<T>
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到当前 `Field` Array 实例。

#### list <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T> list(): List<T>
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到当前 `Field` List 实例。

#### set <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun set(any: Any?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置当前 `Field` 实例。

#### setTrue <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun setTrue()
```
**变更记录**

`v1.0` `添加`

**功能描述**

> 设置当前 `Field` 实例为 `true`。

::: danger

请确保实例对象类型为 **Boolean**。

:::

#### setFalse <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun setFalse()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置当前 `Field` 实例为 `false`。

::: danger

请确保实例对象类型为 **Boolean**。

:::

#### setNull <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun setNull()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置当前 `Field` 实例为 `null`。