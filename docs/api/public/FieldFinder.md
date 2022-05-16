## FieldFinder [class]

```kotlin
class FieldFinder(override val hookInstance: YukiMemberHookCreater.MemberHookCreater?, override val classSet: Class<*>?) : BaseFinder()
```

**变更记录**

`v1.0` `添加`

`v1.0.2` `修改`

合并到 `BaseFinder`

**功能描述**

> `Field` 查找类。

### ~~classSet [field]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.2` `移除`

### name [field]

```kotlin
var name: String
```

**变更记录**

`v1.0` `添加`

`v1.0.70` `修改`

允许不填写名称

**功能描述**

> 设置 `Field` 名称。

!> 若不填写名称则必须存在一个其它条件。

### type [field]

```kotlin
var type: Any?
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置 `Field` 类型。

可不填写类型，默认模糊查找并取第一个匹配的 `Field`。

### modifiers [method]

```kotlin
inline fun modifiers(initiate: ModifierRules.() -> Unit): IndexTypeCondition
```

**变更记录**

`v1.0.67` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 设置 `Field` 标识符筛选条件。

可不设置筛选条件，默认模糊查找并取第一个匹配的 `Field`。

!> 存在多个 `IndexTypeCondition` 时除了 `order` 只会生效最后一个。

### order [method]

```kotlin
fun order(): IndexTypeCondition
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 顺序筛选字节码的下标。

### name [method]

```kotlin
fun name(value: String): IndexTypeCondition
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置 `Field` 名称。

!> 若不填写名称则必须存在一个其它条件，默认模糊查找并取第一个匹配的 `Field`。

!> 存在多个 `IndexTypeCondition` 时除了 `order` 只会生效最后一个。

### name [method]

```kotlin
inline fun name(initiate: NameConditions.() -> Unit): IndexTypeCondition
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 设置 `Field` 名称条件。

!> 若不填写名称则必须存在一个其它条件，默认模糊查找并取第一个匹配的 `Field`。

!> 存在多个 `IndexTypeCondition` 时除了 `order` 只会生效最后一个。

### type [method]

```kotlin
fun type(value: Any): IndexTypeCondition
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置 `Field` 类型。

!> 可不填写类型，默认模糊查找并取第一个匹配的 `Field`。

!> 存在多个 `IndexTypeCondition` 时除了 `order` 只会生效最后一个。

### superClass [method]

```kotlin
fun superClass(isOnlySuperClass: Boolean)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置在 `classSet` 的所有父类中查找当前 `Field`。

!> 若当前 `classSet` 的父类较多可能会耗时，API 会自动循环到父类继承是 `Any` 前的最后一个类。

### Result [class]

```kotlin
inner class Result internal constructor(internal val isNoSuch: Boolean, private val e: Throwable?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> `Field` 查找结果实现类。

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
field {
    // Your code here.
}.result {
    get(instance).set("something")
    get(instance).string()
    get(instance).cast<CustomClass>()
    get().boolean()
    give()
    onNoSuchField {}
}
```

#### get [method]
```kotlin
fun get(instance: Any?): Instance
```
**变更记录**

`v1.0` `添加`

**功能描述**

> 得到变量实例处理类。

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

#### give [method]

```kotlin
fun give(): Field?
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 得到变量本身。

#### onNoSuchField [method]

```kotlin
fun onNoSuchField(initiate: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 监听找不到变量时。

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
inner class Instance internal constructor(private val instance: Any?, val self: Any?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> `Field` 实例变量处理类。

##### cast [method]

```kotlin
fun <T> cast(): T?
```

**变更记录**

`v1.0` `添加`

`v1.0.68` `修改`

修改 ~~`of`~~ 为 `cast`

移动方法到 `Instance`

**功能描述**

> 得到变量实例。

##### byte [method]

```kotlin
fun byte(): Byte?
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到变量 Byte 实例。

##### int [method]

```kotlin
fun int(): Int
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofInt`~~ 为 `int`

移动方法到 `Instance`

**功能描述**

> 得到变量 Int 实例。

##### long [method]

```kotlin
fun long(): Long
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofLong`~~ 为 `long`

移动方法到 `Instance`

**功能描述**

> 得到变量 Long 实例。

##### short [method]

```kotlin
fun short(): Short
```
**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofShort`~~ 为 `short`

移动方法到 `Instance`

**功能描述**

> 得到变量 Short 实例。

##### double [method]

```kotlin
fun double(): Double
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofDouble`~~ 为 `double`

移动方法到 `Instance`

**功能描述**

> 得到变量 Double 实例。

##### float [method]

```kotlin
fun float(): Float
```
**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofFloat`~~ 为 `float`

移动方法到 `Instance`

**功能描述**

> 得到变量 Float 实例。

##### string [method]

```kotlin
fun string(): String
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofString`~~ 为 `string`

移动方法到 `Instance`

**功能描述**

> 得到变量 String 实例。

##### char [method]

```kotlin
fun char(): Char
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到变量 Char 实例。

##### boolean [method]

```kotlin
fun boolean(): Boolean
```

**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofBoolean`~~ 为 `boolean`

移动方法到 `Instance`

**功能描述**

> 得到变量 Boolean 实例。

##### any [method]

```kotlin
fun any(): Any?
```
**变更记录**

`v1.0.65` `新增`

`v1.0.68` `修改`

修改 ~~`ofAny`~~ 为 `any`

移动方法到 `Instance`

**功能描述**

> 得到变量 Any 实例。

##### array [method]

```kotlin
inline fun <reified T> array(): Array<T>
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到变量 Array 实例。

##### list [method]

```kotlin
inline fun <reified T> list(): List<T>
```

**变更记录**

`v1.0.68` `新增`

**功能描述**

> 得到变量 List 实例。

##### set [method]

```kotlin
fun set(any: Any?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置变量实例。

##### setTrue [method]

```kotlin
fun setTrue()
```
**变更记录**

`v1.0` `添加`

**功能描述**

> 设置变量实例为 `true`。

!> 请确保实例对象类型为 `Boolean`。

##### setFalse [method]

```kotlin
fun setFalse()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置变量实例为 `false`。

!> 请确保实例对象类型为 `Boolean`。

##### setNull [method]

```kotlin
fun setNull()
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 设置变量实例为 `null`。