# 特色功能

> 除了基本的 Hook 功能之外，`YukiHookAPI` 还为开发者提供了大量的语法糖和扩展用法。

## 字节码扩展功能

假设有一个这样的 `Class`。

> 示例如下

```java
package com.demo;

public class Test {

    public Test() {
        // ...
    }

    public Test(boolean isInit) {
        // ...
    }

    private static TAG = "Test";

    private String a;

    private boolean a;

    private boolean isTaskRunning = false;

    private static void init() {
        // ...
    }

    private void doTask(String taskName) {
        // ...
    }

    private void release(Release release, Function<boolean, String> function, Task task) {
        // ...
    }

    private void stop() {
        // ...
    }

    private String getName() {
        // ...
    }

    private void b() {
        // ...
    }

    private void b(String a) {
        // ...
    }
}
```

### 查询与反射调用

假设我们要得到 `doTask` 方法并执行，通常情况下，我们可以使用标准的反射 API 去查询这个方法。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用反射 API 调用并执行
Test::class.java.getDeclaredMethod("doTask", String::class.java).apply { isAccessible = true }.invoke(instance, "task_name")
```

这种写法大概不是很友好，此时 `YukiHookAPI` 就为你提供了一个可在任意地方使用的语法糖。

以上写法换做 `YukiHookAPI` 可写作如下形式。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "doTask"
    param(StringType)
}.get(instance).call("task_name")
```

更多用法可参考 [MethodFinder](api/document?id=methodfinder-class)。

同样地，我们需要得到 `isTaskRunning` 变量也可以写作如下形式。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.field {
    name = "isTaskRunning"
    type = BooleanType
}.get(instance).self // self 为 Field 的实例对象
```

更多用法可参考 [FieldFinder](api/document?id=fieldfinder-class)。

也许你还想得到当前 `Class` 的构造方法，同样可以实现。

> 示例如下

```kotlin
Test::class.java.constructor {
    param(BooleanType)
}.get().call(true) // 可创建一个新的实例
```

若想得到的是 `Class` 的无参构造方法，可写作如下形式。

> 示例如下

```kotlin
Test::class.java.constructor().get().call() // 可创建一个新的实例
```

更多用法可参考 [ConstructorFinder](api/document?id=constructorfinder-class)。

### 可选的查询条件

假设我们要得到 `Class` 中的 `getName` 方法，可以使用如下实现。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "getName"
    returnType = StringType
}.get(instance).string() // 得到方法的结果
```

通过观察发现，这个 `Class` 中只有一个名为 `getName` 的方法，那我们可不可以再简单一点呢？

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "getName"
}.get(instance).string() // 得到方法的结果
```

是的，对于确切不会变化的方法，你可以精简查询条件，<b>`YukiHookAPI` 会默认按照字节码顺序匹配第一个查询到的结果</b>。

问题又来了，这个 `Class` 中有一个 `release` 方法，但是它的方法参数好长，而且很多的类型都无法直接得到。

通常情况下我们会使用 `param(...)` 来查询这个方法，但是有没有更简单的方法呢。

此时，在确定方法唯一性后，你可以使用 `paramCount` 来查询到这个方法。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "release"
    // 此时我们不必确定方法参数具体类型，写个数就好
    paramCount = 3
}.get(instance) // 得到这个方法
```

### 静态字节码

有些方法和变量在类中是静态的实现，这个时候，我们不需要传入实例就可以调用它们。

假设我们这次要得到静态变量 `TAG` 的内容。

> 示例如下

```kotlin
Test::class.java.field {
    name = "TAG"
    type = StringType
}.get().string() // Field 的类型是字符串，可直接进行 cast
```

假设类中存在同名的非静态 `TAG` 变量，这个时候怎么办呢？

加入一个筛选条件即可。

> 示例如下

```kotlin
Test::class.java.field {
    name = "TAG"
    type = StringType
    modifiers {
        // 标识查询的这个变量需要是静态
        asStatic()
    }
}.get().string() // Field 的类型是字符串，可直接进行 cast
```

更多用法可参考 [ModifierRules](api/document?id=modifierrules-class)。

我们还可以调用名为 `init` 的静态方法。

> 示例如下

```kotlin
Test::class.java.method {
    name = "init"
}.get().call()
```

同样地，你可以标识它是一个静态。

> 示例如下

```kotlin
Test::class.java.method {
    name = "init"
    modifiers {
        // 标识查询的这个方法需要是静态
        asStatic()
    }
}.get().call()
```

### 混淆的字节码

你可能已经注意到了，这里给出的示例 `Class` 中有两个混淆的变量名称，它们都是 `a`，这个时候我们要怎么得到它们呢？

有两种方案。

第一种方案，确定变量的名称和类型。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.field {
    name = "a"
    type = BooleanType
}.get(instance).self // 得到名称为 a 类型为 Boolean 的变量
```

第二种方案，确定变量的类型所在的位置。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.field {
    type(BooleanType).index().first()
}.get(instance).self // 得到第一个类型为 Boolean 的变量
```

以上两种情况均可得到对应的变量 `private boolean a`。

同样地，这个 `Class` 中也有两个混淆的方法名称，它们都是 `b`。

你也可以有两种方案来得到它们。

第一种方案，确定方法的名称和方法参数。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "b"
    param(StringType)
}.get(instance).call("test_string") // 得到名称为 b 方法参数为 [String] 的方法
```

第二种方案，确定方法的参数所在的位置。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    param(StringType).index().first()
}.get(instance).call("test_string") // 得到第一个方法参数为 [String] 的方法
```

由于观察到这个方法在 `Class` 的最后一个，那我们还有一个备选方案。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    order().index().last()
}.get(instance).call("test_string") // 得到当前 Class 的最后一个方法
```

!> 请尽量不要使用 `order` 来筛选字节码的下标，它们可能是不确定的，除非你确定它在这个 `Class` 中的位置一定不会变。

### 直接调用

上面介绍的调用字节码的方法都需要使用 `get(instance)` 才能调用对应的方法，有没有简单一点的办法呢？

此时，你可以在任意实例上使用 `current` 方法来创建一个调用空间。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 假设这个 Class 是不能被直接得到的
instance.current {
    // 执行 doTask 方法
    method {
        name = "doTask"
        param(StringType)
    }.call("task_name")
    // 执行 stop 方法
    method {
        name = "stop"
    }.call()
    // 得到 name
    val name = method { name = "getName" }.string()
}
```

问题又来了，我想使用反射的方式创建如下的实例并调用其中的方法，该怎么做呢？

> 示例如下

```kotlin
Test(true).doTask("task_name")
```

通常情况下，我们可以使用标准的反射 API 来调用。

> 示例如下

```kotlin
classOf("com.demo.Test")
    .getDeclaredConstructor(Boolean::class.java)
    .apply { isAccessible = true }
    .newInstance(true)
    .apply {
        javaClass
            .getDeclaredMethod("doTask", String::class.java)
            .apply { isAccessible = true }
            .invoke(this, "task_name")
    }
```

但是感觉这种做法好麻烦，有没有更简洁的调用方法呢？

这个时候，我们还可以借助 `buildOf` 和 `buildOfAny` 方法来创建一个实例。

> 示例如下

```kotlin
classOf("com.demo.Test").buildOfAny(true) { param(BooleanType) }?.current {
    method {
        name = "doTask"
        param(StringType)
    }.call("task_name")
}
```

更多用法可参考 [CurrentClass](api/document?id=currentclass-class) 以及 [buildOf](api/document?id=buildof-method) 方法。

### 再次查询

假设有三个不同版本的 `Class`，它们都是这个宿主不同版本相同的 `Class`。

这里面同样都有一个方法 `doTask`，假设它们的功能是一样的。

> 版本 A 示例如下

```java
public class Test {

    public void doTask() {
        // ...
    }
}
```

> 版本 B 示例如下

```java
public class Test {

    public void doTask(String taskName) {
        // ...
    }
}
```

> 版本 C 示例如下

```java
public class Test {

    public void doTask(String taskName, int type) {
        // ...
    }
}
```

我们需要在不同的版本中得到这个相同功能的 `doTask` 方法，要怎么做呢？

此时，你可以使用 `RemedyPlan` 完成你的需求。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "doTask"
}.remedys {
    method {
        name = "doTask"
        param(StringType)
    }.onFind {
        // 可在这里实现找到的逻辑
    }
    method {
        name = "doTask"
        param(StringType, IntType)
    }.onFind {
        // 可在这里实现找到的逻辑
    }
}.wait(instance) {
    // 得到方法的结果
}
```

!> 特别注意使用了 `RemedyPlan` 的方法查询结果不能再使用 `get` 的方式得到方法实例，应当使用 `wait` 方法。

更多用法可参考 [Method RemedyPlan](api/document?id=remedyplan-class) 以及 [Constructor RemedyPlan](api/document?id=remedyplan-class-1)。

### 相对匹配

假设宿主中不同版本中存在功能相同的 `Class` 但仅有 `Class` 的名称不一样。

> 版本 A 示例如下

```java
public class ATest {

    public static void doTask() {
        // ...
    }
}
```

> 版本 B 示例如下

```java
public class BTest {

    public static void doTask() {
        // ...
    }
}
```

这个时候我们想在每个版本都调用这个 `Class` 里的 `doTask` 方法该怎么做呢？

通常做法是判断 `Class` 是否存在。

> 示例如下

```kotlin
// 首先查询到这个 Class
val currentClass = if("com.demo.ATest".hasClass) classOf("com.demo.ATest") else classOf("com.demo.BTest")
// 然后再查询这个方法并调用
currentClass.method {
    name = "doTask"
}.get().call()
```

感觉这种方案非常的不优雅且繁琐，那么此时 `YukiHookAPI` 就为你提供了一个非常方便的 `VariousClass` 专门来解决这个问题。

现在，你可以直接使用以下方式获取到这个 `Class`。

> 示例如下

```kotlin
VariousClass("com.demo.ATest", "com.demo.BTest").get().method {
    name = "doTask"
}.get().call()
```

更多用法可参考 [VariousClass](api/document?id=variousclass-class)。

若在创建 Hook 的时候使用，可以更加方便，还可以自动拦截找不到 `Class` 的异常。

> 示例如下

```kotlin
findClass("com.demo.ATest", "com.demo.BTest").hook {
    // Your code here.
}
```

你还可以把这个 `Class` 定义为一个常量类型来使用。

> 示例如下

```kotlin
// 定义常量类型
val ABTestClass = VariousClass("com.demo.ATest", "com.demo.BTest")
// 直接使用
ABTestClass.hook {
    // Your code here.
}
```

更多用法可参考 [findClass](api/document?id=findclass-method) 方法。

### 注意误区

> 这里列举了使用时可能会遇到的误区部分，可供参考。

#### 限制性查询条件

!> 在查询条件中，除了 `order` 你只能使用一次 `index` 功能。

> 示例如下

```kotlin
method {
    name = "test"
    param(BooleanType).index(num = 2)
    // ❗错误的使用方法，请仅保留一个 index 方法
    returnType(StringType).index(num = 1)
}
```

以下查询条件的使用是没有任何问题的。

> 示例如下

```kotlin
method {
    name = "test"
    param(BooleanType).index(num = 2)
    order().index(num = 1)
}
```

#### 字节码类型

!> 在字节码调用结果中，`cast` 方法只能指定字节码对应的类型。

例如我们想得到一个 `Boolean` 类型的变量，把他转换为 `String`。

以下是错误的使用方法。

> 示例如下

```kotlin
field {
    name = "test"
    type = BooleanType
}.get().string() // ❗错误的使用方法，必须 cast 为字节码目标类型
```

以下是正确的使用方法。

> 示例如下

```kotlin
field {
    name = "test"
    type = BooleanType
}.get().boolean().toString() // ✅ 正确的使用方法，得到类型后再进行转换
```

## 常用类型扩展功能

在查询方法、变量的时候我们通常需要指定所查询的类型。

> 示例如下

```kotlin
field {
    name = "test"
    type = Boolean::class.java
}
```

在 `Kotlin` 中表达出 `Boolean::class.java` 这个类型的写法很长，感觉并不方便。

因此，`YukiHookAPI` 为开发者封装了常见的类型调用，其中包含了 Android 的基本类型和 Java 的基本类型。

这个时候上面的类型就可以写作如下形式了。

> 示例如下

```kotlin
field {
    name = "test"
    type = BooleanType
}
```

在 Java 中常见的基本类型都已被封装为 <b>类型 + Type</b> 的方式，例如 `IntType`、`FloatType`。

相应地，数组类型也有方便的使用方法，假设我们要获得 `String[]` 类型的数组。

需要写做 `java.lang.reflect.Array.newInstance(String::class.java, 0).javaClass` 才能得到这个类型。

感觉是不是很麻烦，这个时候我们可以使用扩展方法 `ArrayClass(StringType)` 来得到这个类型。

同时由于 `String` 是常见类型，所以还可以直接使用 `StringArrayClass` 来得到这个类型。

一些常见的 Hook 中查询的方法，都有其对应的封装类型以供使用，格式为 <b>类型 + Class</b>。

例如 Hook `onCreate` 方法需要查询 `Bundle::class.java` 类型。

> 示例如下

```kotlin
method {
    name = "onCreate"
    param(BundleClass)
}
```

更多类型请 [点击这里](api/document?id=graphicstypefactory-kt) 前往查看，也欢迎你能贡献更多的常用类型。

## 调试日志功能

> 日志是调试过程最重要的一环，`YukiHookAPI` 为开发者封装了一套稳定高效的调试日志功能。

### 普通日志

你可以调用 `loggerD`、`loggerI`、`loggerW` 来向控制台打印普通日志。

使用方法如下所示。

> 示例如下

```kotlin
loggerD(msg = "This is a log")
```

此时，`YukiHookAPI` 会调用 `android.util.Log` 与 `XposedBridge.log` 同时打印这条日志。

日志默认的 `TAG` 为你在 `YukiHookAPI.Configs.debugTag` 中设置的值。

你也可以动态自定义这个值，但是不建议轻易修改 `TAG` 防止过滤不到日志。

> 示例如下

```kotlin
loggerD(tag = "YukiHookAPI", msg = "This is a log")
```

打印的结果为如下所示。

> 示例如下

```
[YukiHookAPI][D]--> This is a log
```

更多用法可参考 [loggerD](api/document?id=loggerd-method)、[loggerI](api/document?id=loggeri-method) 及 [loggerW](api/document?id=loggerw-method) 方法。

### 错误日志

你可以调用 `loggerE` 来向控制台打印 `E` 级别的日志。

使用方法如下所示。

> 示例如下

```kotlin
loggerE(msg = "This is an error")
```

错误日志的级别是最高的，无论你有没有过滤仅为 `E` 级别的日志。

对于错误级别的日志，你还可以在后面加上一个异常堆栈。

```kotlin
// 假设这就是被抛出的异常
val e = Throwable(...)
// 打印日志
loggerE(msg = "This is an error", throwable = e)
```

打印的结果为如下所示。

> 示例如下

```
[YukiHookAPI][E]--> This is an error
```

同时，日志会帮你打印整个异常堆栈。

> 示例如下

```
java.lang.Throwable
        at com.demo.Test.<init>(...) 
        at com.demo.Test.doTask(...) 
        at com.demo.Test.stop(...) 
        at com.demo.Test.init(...) 
        at a.a.a(...) 
        ... 3 more
```

更多用法可参考 [loggerE](api/document?id=loggere-method) 方法。

## Xposed 模块数据存储功能

> 这是一个自动对接 `SharedPreferences` 和 `XSharedPreferences` 的高效模块数据存储解决方案。

我们需要存储模块的数据，以供宿主调用，这个时候会遇到原生 `Sp` 存储的数据互通阻碍。

原生的 `Xposed` 给我们提供了一个 `XSharedPreferences` 用于读取模块的 `Sp` 数据。

通常情况下我们可以这样在 Hook 内对其进行初始化。

> 示例如下

```kotlin
XSharedPreferences(BuildConfig.APPLICATION_ID)
```

有没有方便快捷的解决方案呢，此时你就可以使用 `YukiHookAPI` 的扩展能力快速实现这个功能。

当你在模块中存储数据的时候，若当前处于 `Activity` 内，可以使用如下方法。

> 示例如下

```kotlin
modulePrefs.putString("test_name", "saved_value")
```

当你在 Hook 中读取数据时，可以使用如下方法。

> 示例如下

```kotlin
val testName = prefs.getString("test_name", "default_value")
```

你不需要考虑传入模块的包名以及一系列复杂的权限配置，一切都交给 `YukiHookModulePrefs` 来处理。

若要实现存储的区域划分，你可以指定每个 `prefs` 文件的名称。

在模块的 `Activity` 中这样使用。

> 示例如下

```kotlin
// 推荐用法
modulePrefs("specify_file_name").putString("test_name", "saved_value")
// 也可以这样用
modulePrefs.name("specify_file_name").putString("test_name", "saved_value")
```

在 Hook 中这样读取。

> 示例如下

```kotlin
// 推荐用法
val testName = prefs("specify_file_name").getString("test_name", "default_value")
// 也可以这样用
val testName = prefs.name("specify_file_name").getString("test_name", "default_value")
```

若你的项目中有大量的固定数据需要存储和读取，推荐使用 `PrefsData` 来创建模板，详细用法可参考 [PrefsData](api/document?id=prefsdata-class)。

更多用法可参考 [YukiHookModulePrefs](api/document?id=yukihookmoduleprefs-class)。