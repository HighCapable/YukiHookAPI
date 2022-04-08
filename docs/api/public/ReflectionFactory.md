## ReflectionFactory [kt]

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 这是自定义 `Member` 和 `Class` 相关功能的查找匹配以及 `invoke` 的封装类。

### hasClass [field]

```kotlin
val String.hasClass: Boolean
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 通过字符串查找类是否存在。

<b>功能示例</b>

你可以轻松的使用此方法判断字符串中的类是否存在。

!> 此查找仅限使用当前的 `ClassLoader`，若要指定 `ClassLoader` 请使用下方的 `hasClass` 同名方法。

> 示例如下

```kotlin
if("com.example.demo.DemoClass".hasClass) {
    // Your code here.
}
```

### hookClass [field]

```kotlin
val Class<*>.hookClass: HookClass
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 将 `Class` 转换为 `HookClass`。

### normalClass [field]

```kotlin
val HookClass.normalClass: Class<*>?
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 将 `HookClass` 转换为 `Class`。

### classOf [method]

```kotlin
fun classOf(name: String, loader: ClassLoader?): Class<*>
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 通过字符串使用指定的 `ClassLoader` 转换为实体类。

<b>功能示例</b>

你可以直接填写你要查找的目标 `Class`，必须在当前 `ClassLoader` 下存在。

> 示例如下

```kotlin
classOf(name = "com.example.demo.DemoClass")
```

你还可以自定义 `Class` 所在的 `ClassLoader`。

> 示例如下

```kotlin
classOf(name = "com.example.demo.DemoClass", classLoader)
```

### hasClass [method]

```kotlin
fun String.hasClass(loader: ClassLoader?): Boolean
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 通过字符串使用指定的 `ClassLoader` 查找类是否存在。

### hasField [method]

```kotlin
fun Class<*>.hasField(initiate: FieldFinder.() -> Unit): Boolean
```

<b>变更记录</b>

`v1.0.4` `新增`

`v1.0.67` `修改`

合并到 `FieldFinder`

<b>功能描述</b>

> 查找变量是否存在。

### hasMethod [method]

```kotlin
fun Class<*>.hasMethod(initiate: MethodFinder.() -> Unit): Boolean
```

<b>变更记录</b>

`v1.0` `添加`

`v1.0.1` `修改`

新增 `returnType` 参数

`v1.0.67` `修改`

合并到 `MethodFinder`

<b>功能描述</b>

> 查找方法是否存在。

### hasConstructor [method]

```kotlin
fun Class<*>.hasConstructor(initiate: ConstructorFinder.() -> Unit): Boolean
```

<b>变更记录</b>

`v1.0.2` `新增`

`v1.0.67` `修改`

合并到 `ConstructorFinder`

<b>功能描述</b>

> 查找构造方法是否存在。

### hasModifiers [method]

```kotlin
fun Member.hasModifiers(initiate: ModifierRules.() -> Unit): Boolean
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 查询 `Member` 中匹配的描述符。

### ~~obtainStaticFieldAny [method]~~ <!-- {docsify-ignore} -->

<b>变更记录</b>

`v1.0` `添加`

`v1.0.1` `移除`

### ~~obtainFieldAny [method]~~ <!-- {docsify-ignore} -->

<b>变更记录</b>

`v1.0` `添加`

`v1.0.1` `移除`

### ~~modifyStaticField [method]~~ <!-- {docsify-ignore} -->

<b>变更记录</b>

`v1.0` `添加`

`v1.0.1` `移除`

### ~~modifyField [method]~~ <!-- {docsify-ignore} -->

<b>变更记录</b>

`v1.0` `添加`

`v1.0.1` `移除`

### field [method]

```kotlin
fun Class<*>.field(initiate: FieldFinder.() -> Unit): FieldFinder.Result
```

<b>变更记录</b>

`v1.0.2` `新增`

<b>功能描述</b>

> 查找并得到变量。

### method [method]

```kotlin
fun Class<*>.method(initiate: MethodFinder.() -> Unit): MethodFinder.Result
```

<b>变更记录</b>

`v1.0` `添加`

`v1.0.1` `修改`

~~`obtainMethod`~~ 更名为 `method`

新增 `returnType` 参数

`v1.0.2` `修改`

合并到 `MethodFinder` 方法体。

<b>功能描述</b>

> 查找并得到方法。

### constructor [method]

```kotlin
fun Class<*>.constructor(initiate: ConstructorFinder.() -> Unit): ConstructorFinder.Result
```

<b>变更记录</b>

`v1.0` `添加`

`v1.0.1` `修改`

~~`obtainConstructor`~~ 更名为 `constructor`

`v1.0.2` `修改`

合并到 `ConstructorFinder` 方法体。

<b>功能描述</b>

> 查找并得到构造类。

### ~~callStatic [method]~~ <!-- {docsify-ignore} -->

<b>变更记录</b>

`v1.0` `添加`

`v1.0.1` `修改`

~~`invokeStatic`~~ 更名为 `callStatic`

`v1.0.2` `移除`

### ~~call [method]~~ <!-- {docsify-ignore} -->

<b>变更记录</b>

`v1.0` `添加`

`v1.0.1` `修改`

~~`invokeAny`~~ 更名为 `call`

`v1.0.2` `移除`

### current [method]

```kotlin
inline fun <reified T : Any> T.current(initiate: CurrentClass.() -> Unit): T
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 获得当前实例的类操作对象。

### buildOf [method]

```kotlin
fun <T> Class<*>.buildOf(vararg param: Any?, initiate: ConstructorFinder.() -> Unit): T?
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 通过构造方法创建新实例，指定类型 `T`。

### buildOfAny [method]

```kotlin
fun Class<*>.buildOfAny(vararg param: Any?, initiate: ConstructorFinder.() -> Unit): Any?
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 通过构造方法创建新实例，任意类型 `Any`。

### allMethods [method]

```kotlin
fun Class<*>.allMethods(callback: (index: Int, method: Method) -> Unit)
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 遍历当前类中的所有方法。

### allConstructors [method]

```kotlin
fun Class<*>.allConstructors(callback: (index: Int, constructor: Constructor<*>) -> Unit)
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 遍历当前类中的所有构造方法。

### allFields [method]

```kotlin
fun Class<*>.allFields(callback: (index: Int, field: Field) -> Unit)
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 遍历当前类中的所有变量。