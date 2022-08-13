## ReflectionFactory [kt]

**变更记录**

`v1.0` `添加`

**功能描述**

> 这是自定义 `Member` 和 `Class` 相关功能的查找匹配以及 `invoke` 的封装类。

### MembersType [class]

```kotlin
enum class MembersType
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 定义一个 `Class` 中的 `Member` 类型

#### ALL [enum]

```kotlin
ALL
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 全部 `Method` 与 `Constructor`。

#### METHOD [enum]

```kotlin
METHOD
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 全部 `Method`。

#### CONSTRUCTOR [enum]

```kotlin
CONSTRUCTOR
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 全部 `Constructor`。

### ~~hookClass [field]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.93` `移除`

`HookClass` 相关功能不再对外开放

### ~~normalClass [field]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.93` `移除`

`HookClass` 相关功能不再对外开放

### ~~hasClass [field]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.93` `移除`

请直接使用 `hasClass()` 无参方法

### hasExtends [field]

```kotlin
val Class<*>.hasExtends: Boolean
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 当前 `Class` 是否有继承关系，父类是 `Any` 将被认为没有继承关系。

### classOf [method]

```kotlin
fun classOf(name: String, loader: ClassLoader?): Class<*>
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 通过字符串使用指定的 `ClassLoader` 转换为实体类。

**功能示例**

你可以直接填写你要查找的目标 `Class`，必须在当前 `ClassLoader` 下存在。

> 示例如下

```kotlin
classOf(name = "com.example.demo.DemoClass")
```

你还可以自定义 `Class` 所在的 `ClassLoader`。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
classOf(name = "com.example.demo.DemoClass", customClassLoader)
```

### classOf [method]

```kotlin
inline fun <reified T> classOf(loader: ClassLoader?): Class<*>
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 通过 `T` 得到其 `Class` 实例并转换为实体类。

**功能示例**

我们要获取一个 `Class` 在 `Kotlin` 下不通过反射时应该这样做。

> 示例如下
> 
```kotlin
DemoClass::class.java
```

现在，你可以直接 `cast` 一个实例并获取它的 `Class` 对象，必须在当前 `ClassLoader` 下存在。

> 示例如下

```kotlin
classOf<DemoClass>()
```

若目标存在的 `Class` 为 `stub`，通过这种方式，你还可以自定义 `Class` 所在的 `ClassLoader`。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
classOf<DemoClass>(customClassLoader)
```

### hasClass [method]

```kotlin
fun String.hasClass(loader: ClassLoader?): Boolean
```

**变更记录**

`v1.0` `添加`

`v1.0.93` `修改`

支持直接使用空参数方法使用默认 `ClassLoader` 进行判断

**功能描述**

> 通过字符串使用指定的 `ClassLoader` 查找类是否存在。

**功能示例**

你可以轻松的使用此方法判断字符串中的类是否存在，效果等同于直接使用 `Class.forName`。

> 示例如下

```kotlin
if("com.example.demo.DemoClass".hasClass()) {
    // Your code here.
}
```

填入方法中的 `loader` 参数可判断指定的 `ClassLoader` 中的 `Class` 是否存在。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
if("com.example.demo.DemoClass".hasClass(customClassloader)) {
    // Your code here.
}
```

### hasField [method]

```kotlin
inline fun Class<*>.hasField(initiate: FieldCondition): Boolean
```

**变更记录**

`v1.0.4` `新增`

`v1.0.67` `修改`

合并到 `FieldFinder`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找变量是否存在。

### hasMethod [method]

```kotlin
inline fun Class<*>.hasMethod(initiate: MethodCondition): Boolean
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

新增 `returnType` 参数

`v1.0.67` `修改`

合并到 `MethodFinder`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找方法是否存在。

### hasConstructor [method]

```kotlin
inline fun Class<*>.hasConstructor(initiate: ConstructorCondition): Boolean
```

**变更记录**

`v1.0.2` `新增`

`v1.0.67` `修改`

合并到 `ConstructorFinder`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找构造方法是否存在。

### hasModifiers [method]

```kotlin
inline fun Member.hasModifiers(initiate: ModifierRules.() -> Unit): Boolean
```

**变更记录**

`v1.0.67` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查询 `Member` 中匹配的描述符。

### ~~obtainStaticFieldAny [method]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `移除`

### ~~obtainFieldAny [method]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `移除`

### ~~modifyStaticField [method]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `移除`

### ~~modifyField [method]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `移除`

### field [method]

```kotlin
inline fun Class<*>.field(initiate: FieldCondition): FieldFinder.Result
```

**变更记录**

`v1.0.2` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找并得到变量。

### method [method]

```kotlin
inline fun Class<*>.method(initiate: MethodCondition): MethodFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

~~`obtainMethod`~~ 更名为 `method`

新增 `returnType` 参数

`v1.0.2` `修改`

合并到 `MethodFinder` 方法体

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找并得到方法。

### constructor [method]

```kotlin
inline fun Class<*>.constructor(initiate: ConstructorCondition): ConstructorFinder.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

~~`obtainConstructor`~~ 更名为 `constructor`

`v1.0.2` `修改`

合并到 `ConstructorFinder` 方法体

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 查找并得到构造方法。

### ~~callStatic [method]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

~~`invokeStatic`~~ 更名为 `callStatic`

`v1.0.2` `移除`

### ~~call [method]~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

~~`invokeAny`~~ 更名为 `call`

`v1.0.2` `移除`

### current [method]

```kotlin
inline fun <reified T : Any> T.current(): CurrentClass
```

```kotlin
inline fun <reified T : Any> T.current(initiate: CurrentClass.() -> Unit): T
```

**变更记录**

`v1.0.70` `新增`

`v1.0.93` `新增`

新增不使用 `current { ... }` 调用域直接使用 `current()` 得到实例的类操作对象

**功能描述**

> 获得当前实例的类操作对象。

### buildOfAny [method]

```kotlin
inline fun Class<*>.buildOfAny(vararg param: Any?, initiate: ConstructorCondition): Any?
```

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 通过构造方法创建新实例，任意类型 `Any`。

### buildOf [method]

```kotlin
inline fun <T> Class<*>.buildOf(vararg param: Any?, initiate: ConstructorCondition): T?
```

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 通过构造方法创建新实例，指定类型 `T`。

### allMethods [method]

```kotlin
inline fun Class<*>.allMethods(result: (index: Int, method: Method) -> Unit)
```

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 遍历当前类中的所有方法。

### allConstructors [method]

```kotlin
inline fun Class<*>.allConstructors(result: (index: Int, constructor: Constructor<*>) -> Unit)
```

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 遍历当前类中的所有构造方法。

### allFields [method]

```kotlin
inline fun Class<*>.allFields(result: (index: Int, field: Field) -> Unit)
```

**变更记录**

`v1.0.70` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 遍历当前类中的所有变量。