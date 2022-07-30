# 特色功能

> 除了基本的 Hook 功能之外，`YukiHookAPI` 还为开发者提供了大量的语法糖和扩展用法。

## 字节码扩展功能

假设有一个这样的 `Class`。

> 示例如下

```java
package com.demo;

public class BaseTest {

    public BaseTest() {
        // ...
    }

    public BaseTest(boolean isInit) {
        // ...
    }

    private void doBaseTask(String taskName) {
        // ...
    }
}
```

```java
package com.demo;

public class Test extends BaseTest {

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

假设我们要得到 `Test`(以下统称“当前 `Class`”)的 `doTask` 方法并执行，通常情况下，我们可以使用标准的反射 API 去查询这个方法。

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
    emptyParam()
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
    emptyParam()
}.get(instance).string() // 得到方法的结果
```

是的，对于确切不会变化的方法，你可以精简查询条件，**`YukiHookAPI` 会默认按照字节码顺序匹配第一个查询到的结果**。

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

### 在父类查询

你会注意到 `Test` 继承于 `BaseTest`，现在我们想得到 `BaseTest` 的 `doBaseTask` 方法，在不知道父类名称的情况下，要怎么做呢？

参照上面的查询条件，我们只需要在查询条件中加入一个 `superClass` 即可实现这个功能。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "doBaseTask"
    param(StringType)
    // 只需要添加这个条件
    superClass()
}.get(instance).call("task_name")
```

这个时候我们就可以在父类中取到这个方法了。

`superClass` 有一个参数为 `isOnlySuperClass`，设置为 `true` 后，可以跳过当前 `Class` 仅查询当前 `Class` 的父类。

由于我们现在已知 `doBaseTask` 方法只存在于父类，可以加上这个条件节省查询时间。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name = "doBaseTask"
    param(StringType)
    // 加入一个查询条件
    superClass(isOnlySuperClass = true)
}.get(instance).call("task_name")
```

这个时候我们同样可以得到父类中的这个方法。

`superClass` 一旦设置就会自动循环向后查找全部继承的父类中是否有这个方法，直到查询到目标没有父类(继承关系为 `java.lang.Object`)为止。

更多用法可参考 [superClass 方法](api/document?id=superclass-method)。

!> 当前查询的 `Method` 除非指定 `superClass` 条件，否则只能查询到当前 `Class` 的 `Method`。

### 模糊查询

如果我们想查询一个方法名称，但是又不确定它在每个版本中是否发生变化，此时我们就可以使用模糊查询功能。

假设我们要得到 `Class` 中的 `doTask` 方法，可以使用如下实现。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name {
        // 设置名称不区分大小写
        equalsOf(other = "dotask", isIgnoreCase = true)
    }
    param(StringType)
}.get(instance).call("task_name")
```

已知当前 `Class` 中仅有一个 `doTask` 方法，我们还可以判断方法名称仅包含其中指定的字符。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name {
        // 仅包含 oTas
        contains(other = "oTas")
    }
    param(StringType)
}.get(instance).call("task_name")
```

我们还可以根据首尾字符串进行判断。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name {
        // 开头包含 do
        startsWith(prefix = "do")
        // 结尾包含 Task
        endsWith(suffix = "Task")
    }
    param(StringType)
}.get(instance).call("task_name")
```

通过观察发现这个方法名称中只包含字母，我们还可以再增加一个精确的查询条件。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 使用 YukiHookAPI 调用并执行
Test::class.java.method {
    name {
        // 开头包含 do
        startsWith(prefix = "do")
        // 结尾包含 Task
        endsWith(suffix = "Task")
        // 仅包含字母
        onlyLetters()
    }
    param(StringType)
}.get(instance).call("task_name")
```

更多用法可参考 [NameConditions](api/document?id=nameconditions-class)。

### 静态字节码

有些方法和变量在 `Class` 中是静态的实现，这个时候，我们不需要传入实例就可以调用它们。

假设我们这次要得到静态变量 `TAG` 的内容。

> 示例如下

```kotlin
Test::class.java.field {
    name = "TAG"
    type = StringType
}.get().string() // Field 的类型是字符串，可直接进行 cast
```

假设 `Class` 中存在同名的非静态 `TAG` 变量，这个时候怎么办呢？

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
    emptyParam()
}.get().call()
```

同样地，你可以标识它是一个静态。

> 示例如下

```kotlin
Test::class.java.method {
    name = "init"
    emptyParam()
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
        emptyParam()
    }.call()
    // 得到 name
    val name = method { name = "getName" }.string()
}
```

我们还可以用 `superClass` 调用当前 `Class` 父类的方法。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val instance = Test()
// 假设这个 Class 是不能被直接得到的
instance.current {
    // 执行父类的 doBaseTask 方法
    superClass().method {
        name = "doBaseTask"
        param(StringType)
    }.call("task_name")
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
    emptyParam()
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
    emptyParam()
}.get().call()
```

感觉这种方案非常的不优雅且繁琐，那么此时 `YukiHookAPI` 就为你提供了一个非常方便的 `VariousClass` 专门来解决这个问题。

现在，你可以直接使用以下方式获取到这个 `Class`。

> 示例如下

```kotlin
VariousClass("com.demo.ATest", "com.demo.BTest").get().method {
    name = "doTask"
    emptyParam()
}.get().call()
```

若当前 `Class` 在指定的 `ClassLoader` 中存在，你可以在 `get` 中填入你的 `ClassLoader`。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
VariousClass("com.demo.ATest", "com.demo.BTest").get(customClassLoader).method {
    name = "doTask"
    emptyParam()
}.get().call()
```

若你正在 `PackageParam` 中操作 (Xposed) 宿主环境的 `Class`，可以直接使用 `clazz` 进行设置。

> 示例如下

```kotlin
VariousClass("com.demo.ATest", "com.demo.BTest").clazz.method {
    name = "doTask"
    emptyParam()
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

#### 必要的查询条件

!> 在普通方法查询条件中，<u>**即使是无参的方法也需要设置查询条件**</u>。

假设我们有如下的 `Class`。

> 示例如下

```java
public class TestFoo {

    public void foo(String string) {
        // ...
    }

    public void foo() {
        // ...
    }
}
```

我们要得到其中的 `public void foo()` 方法，可以写作如下形式。

> 示例如下

```kotlin
TestFoo::class.java.method {
    name = "foo"
}
```

但是，上面的例子<u>**是错误的**</u>。

你会发现这个 `Class` 中有两个 `foo` 方法，其中一个带有方法参数。

由于上述例子没有设置 `param` 的查询条件，得到的结果将会是匹配名称且匹配字节码顺序的第一个方法 `public void foo(String string)`，而不是我们需要的最后一个方法。

这是一个**经常会出现的错误**，**没有方法参数就会丢失方法参数查询条件**的使用问题。

正确的使用方法如下。

> 示例如下

```kotlin
TestFoo::class.java.method {
    name = "foo"
    // ✅ 正确的使用方法，添加详细的筛选条件
    emptyParam()
}
```

至此，上述的示例将可以完美地匹配到 `public void foo()` 方法。

> PS：在较旧的 API 版本中是允许匹配不写默认匹配无参方法的做法的，但是最新版本更正了这一问题，请确保你使用的是最新的 API 版本。

#### 可简写查询条件

> 在构造方法查询条件中，<u>**无参的构造方法可以不需要填写查询条件**</u>。

假设我们有如下的 `Class`。

> 示例如下

```java
public class TestFoo {

    public TestFoo() {
        // ...
    }
}
```

我们要得到其中的 `public TestFoo()` 构造方法，可以写作如下形式。

> 示例如下

```kotlin
TestFoo::class.java.constructor { emptyParam() }
```

上面的例子可以成功获取到 `public TestFoo()` 构造方法，但是感觉有一些繁琐。

与普通方法不同，由于构造方法不需要考虑 `name` 名称，当构造方法没有参数的时候，我们可以省略 `emptyParam` 参数。

> 示例如下

```kotlin
TestFoo::class.java.constructor()
```

!> PS：在旧的 API 版本中构造方法不填写任何查询参数会直接找不到构造方法，<u>**这是一个 BUG，最新版本已经进行修复**</u>，请确保你使用的是最新的 API 版本。

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

在 Java 中常见的基本类型都已被封装为 **类型 + Type** 的方式，例如 `IntType`、`FloatType`。

相应地，数组类型也有方便的使用方法，假设我们要获得 `String[]` 类型的数组。

需要写做 `java.lang.reflect.Array.newInstance(String::class.java, 0).javaClass` 才能得到这个类型。

感觉是不是很麻烦，这个时候我们可以使用扩展方法 `ArrayClass(StringType)` 来得到这个类型。

同时由于 `String` 是常见类型，所以还可以直接使用 `StringArrayClass` 来得到这个类型。

一些常见的 Hook 中查询的方法，都有其对应的封装类型以供使用，格式为 **类型 + Class**。

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

你还可以使用 `LoggerType` 自定义日志打印的类型，可选择使用 `android.util.Log` 还是 `XposedBridge.log` 来打印日志。

默认类型为 `LoggerType.BOTH`，含义为同时使用这两个方法来打印日志。

比如我们仅使用 `android.util.Log` 来打印日志。

> 示例如下

```kotlin
loggerD(tag = "YukiHookAPI", msg = "This is a log", type = LoggerType.LOGD)
```

或又仅使用 `XposedBridge.log` 来打印日志，此方法仅可在 (Xposed) 宿主环境使用。

> 示例如下

```kotlin
loggerD(tag = "YukiHookAPI", msg = "This is a log", type = LoggerType.XPOSEDBRIDGE)
```

若你想智能区分 (Xposed) 宿主环境与模块环境，可以写为如下形式。

> 示例如下

```kotlin
loggerD(tag = "YukiHookAPI", msg = "This is a log", type = LoggerType.SCOPE)
```

这样 API 就会在不同环境智能选择指定的方法类型去打印这条日志。

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
val throwable = Throwable(...)
// 打印日志
loggerE(msg = "This is an error", e = throwable)
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

在错误日志中，你同样也可以使用 `LoggerType` 来指定当前打印日志所用到的方法类型。

更多用法可参考 [loggerE](api/document?id=loggere-method) 方法。

## Xposed 模块数据存储功能

> 这是一个自动对接 `SharedPreferences` 和 `XSharedPreferences` 的高效模块数据存储解决方案。

我们需要存储模块的数据，以供宿主调用，这个时候会遇到原生 `Sp` 存储的数据互通阻碍。

原生的 `Xposed` 给我们提供了一个 `XSharedPreferences` 用于读取模块的 `Sp` 数据。

### 在 Activity 中使用

> 这里描述了在 `Activity` 中装载 `YukiHookModulePrefs` 的场景。

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

### 在 PreferenceFragment 中使用

> 这里描述了在 `PreferenceFragment` 中装载 `YukiHookModulePrefs` 的场景。

若你的模块使用了 `PreferenceFragmentCompat`，你现在可以将其继承类开始迁移到 `ModulePreferenceFragment`。

!> 你必须继承 `ModulePreferenceFragment` 才能实现 `YukiHookModulePrefs` 的模块存储功能。

详情请参考 [ModulePreferenceFragment](api/document?id=modulepreferencefragment-class)。

## Xposed 模块与宿主通讯桥功能

> 这是一个使用系统无序广播在模块与宿主之间发送和接收数据的解决方案。

!> 需要满足的条件：模块与宿主需要保持存活状态，否则无法建立通讯。

### 基本用法

> 这里描述了 `wait` 与 `put` 方法的基本使用方法。

通过使用 `dataChannel` 来实现模块与宿主之间的通讯桥，原理为发送接收系统无序广播。

> 模块示例如下

```kotlin
// 从指定包名的宿主获取
dataChannel(packageName = "com.example.demo").wait<String>(key = "key_from_host") { value ->
    // Your code here.
}
// 发送给指定包名的宿主
dataChannel(packageName = "com.example.demo").put(key = "key_from_module", value = "I am module")
```

> 宿主示例如下

```kotlin
// 从模块获取
dataChannel.wait<String>(key = "key_from_module") { value ->
    // Your code here.
}
// 发送给模块
dataChannel.put(key = "key_from_host", value = "I am host")
```

你可以不设置 `dataChannel` 的 `value` 来达到仅通知模块或宿主回调 `wait` 方法。

> 模块示例如下

```kotlin
// 从指定包名的宿主获取
dataChannel(packageName = "com.example.demo").wait(key = "listener_from_host") {
    // Your code here.
}
// 发送给指定包名的宿主
dataChannel(packageName = "com.example.demo").put(key = "listener_from_module")
```

> 宿主示例如下

```kotlin
// 从模块获取
dataChannel.wait(key = "listener_from_module") {
    // Your code here.
}
// 发送给模块
dataChannel.put(key = "listener_from_host")
```

!> 接收方需要保持存活状态才能收到通讯数据。

详情请参考 [YukiHookDataChannel](api/document?id=yukihookdatachannel-class)。

### 判断模块与宿主版本是否匹配

> 通过通讯桥功能，`YukiHookAPI` 还为你提供了在用户更新模块后，判断模块是否与宿主版本匹配的解决方案。

我们只需要调用 `checkingVersionEquals` 方法，即可实现这个功能。

在模块与宿主中可进行双向判断。

你可以在模块中判断指定包名的宿主是否与当前模块的版本匹配。

> 示例如下

```kotlin
// 从指定包名的宿主获取
dataChannel(packageName = "com.example.demo").checkingVersionEquals { isEquals ->
    // Your code here.
}
```

你还可以在宿主中判断是否自身与当前模块的版本匹配。

> 示例如下

```kotlin
// 从模块获取
dataChannel.checkingVersionEquals { isEquals ->
    // Your code here.
}
```

!> 方法回调的条件为宿主、模块保持存活状态，并在激活模块后重启了作用域中的 Hook 目标宿主对象。

详情请参考 [YukiHookDataChannel](api/document?id=yukihookdatachannel-class)。

### 回调事件响应的规则

!> 在模块和宿主中，每一个 `dataChannel` 对应的 `key` 的回调事件**都不允许重复创建**，若重复，之前的回调事件会被新增加的回调事件替换，若在模块中使用，在同一个 `Activity` 中不可以重复，不同的 `Activity` 中相同的 `key` 允许重复。

这里只列出了在模块中使用的例子，在宿主中相同的 `key` 始终不允许重复创建。

> 示例如下

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 回调事件 A
        dataChannel(packageName = "com.example.demo").wait(key = "test_key") {
            // Your code here.
        }
        // 回调事件 B
        dataChannel(packageName = "com.example.demo").wait(key = "test_key") {
            // Your code here.
        }
        // 回调事件 C
        dataChannel(packageName = "com.example.demo").wait(key = "other_test_key") {
            // Your code here.
        }
    }
}

class OtherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 回调事件 D
        dataChannel(packageName = "com.example.demo").wait(key = "test_key") {
            // Your code here.
        }
    }
}
```

在上述示例中，回调事件 A 会被回调事件 B 替换掉，回调事件 C 的 `key` 不与其它重复，回调事件 D 在另一个 Activity 中，所以最终回调事件 B、C、D 都可被创建成功。

!> 一个相同 `key` 的回调事件只会回调当前模块正在显示的 `Activity` 中注册的回调事件，例如上述中的 `test_key`，如果 `OtherActivity` 正在显示，那么 `MainActivity` 中的 `test_key` 就不会被回调。

!> 请特别注意，相同的 `key` 在同一个 `Activity` 不同的 `Fragment` 中注册 `dataChannel`，它们依然会在当前 `Activity` 中同时被回调。 

!> 在模块中，你只能使用 `Activity` 的 `Context` 注册 `dataChannel`，你不能在 `Application` 以及 `Service` 等地方使用 `dataChannel`，若要在 `Fragment` 中使用 `dataChannel`，请使用 `activity?.dataChannel(...)`。

## 宿主生命周期扩展功能
 
> 这是一个自动 Hook 宿主 APP 生命周期的扩展功能。

### 监听生命周期

> 通过自动化 Hook 宿主 APP 的生命周期方法，来实现监听功能。

我们需要监听宿主 `Application` 的启动和生命周期方法，只需要使用以下方式实现。

> 示例如下

```kotlin
loadApp(name = "com.example.demo") {
    // 注册生命周期监听
    onAppLifecycle {
        // 你可以在这里实现 Application 中的生命周期方法监听
        attachBaseContext { baseContext, hasCalledSuper ->
            // 通过判断 hasCalledSuper 来确定是否已执行 super.attachBaseContext(base) 方法
            // ...
        }
        onCreate {
            // 通过 this 得到当前 Application 实例
            // ...
        }
        onTerminate {
            // 通过 this 得到当前 Application 实例
            // ...
        }
        onLowMemory {
            // 通过 this 得到当前 Application 实例
            // ...
        }
        onTrimMemory { self, level ->
            // 可在这里判断 APP 是否已切换到后台
            if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
                // ...
            }
            // ...
        }
        onConfigurationChanged { self, config ->
            // ...
        }
    }
}
```

详情请参考 [AppLifecycle](api/document?id=applifecycle-class)。

### 注册系统广播

> 通过 `Application.onCreate` 方法注册系统广播，来实现对系统广播的监听。

我们还可以在宿主 `Application` 中注册系统广播。

> 示例如下

```kotlin
loadApp(name = "com.example.demo") {
    // 注册生命周期监听
    onAppLifecycle {
        // 注册用户解锁时的广播监听
        registerReceiver(Intent.ACTION_USER_PRESENT) { context, intent ->
            // ...
        }
        // 注册多个广播监听 - 会同时回调多次
        registerReceiver(Intent.ACTION_PACKAGE_CHANGED, Intent.ACTION_TIME_TICK) { context, intent ->
            // ...
        }
    }
}
```

详情请参考 [AppLifecycle](api/document?id=applifecycle-class)。