# 用法示例

> 这里介绍了 `YukiHookAPI` 的基本工作方式以及列举了简单的 Hook 例子和常用功能。

## 结构图解

> 下方的结构描述了 `YukiHookAPI` 的基本工作方式和原理。

```
Host Environment
└── YukiMemberHookCreater
    └── Class
        └── MemberHookCreater
            └── Member
                ├── Before
                └── After
            MemberHookCreater
            └── Member
                ├── Before
                └── After
            ...
    YukiResourcesHookCreater
    └── Resources
        └── ResourcesHookCreater
            └── Drawable
                └── Replace
            ResourcesHookCreater
            └── Layout
                └── Inject
            ...
```

> 上方的结构换做代码将可写为如下形式。

```kotlin
TargetClass.hook { 
    injectMember { 
        method { 
            // Your code here.
        }
        beforeHook {
            // Your code here.
        }
        afterHook {
            // Your code here.
        }
    }
}
resources().hook {
    injectResource {
        conditions {
            // Your code here.
        }
        replaceTo(...)
    }
}
```

## Demo

> 你可以在下方找到 API 提供的 Demo 来学习 `YukiHookAPI` 的使用方法。

- 宿主 APP Demo [点击这里查看](https://github.com/fankes/YukiHookAPI/tree/master/demo-app)

- 模块 APP Demo [点击这里查看](https://github.com/fankes/YukiHookAPI/tree/master/demo-module)

同时安装宿主和模块 Demo，通过激活模块来测试宿主中被 Hook 的功能。

## 一个简单的 Hook 例子

> 这里给出了 Hook APP、Hook 系统框架与 Hook Resources 的例子，可供参考。

### Hook APP

假设，我们要 Hook `com.android.browser` 中的 `onCreate` 方法并弹出一个对话框。

在 `encase` 方法体中添加代码。

> 示例如下

```kotlin
loadApp(name = "com.android.browser") {
    ActivityClass.hook { 
        injectMember { 
            method { 
                name = "onCreate"
                param(BundleClass)
                returnType = UnitType
            }
            afterHook {
                AlertDialog.Builder(instance())
                    .setTitle("Hooked")
                    .setMessage("I am hook!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
```

至此，`onCreate` 方法将被成功 Hook 并在 `com.android.browser` 中的每个 `Activity` 启动时弹出此对话框。

那么，我想继续 Hook `onStart` 方法要怎么做呢？

在刚刚的代码中，继续插入一个 `injectMember` 方法体即可。

> 示例如下

```kotlin
loadApp(name = "com.android.browser") {
    ActivityClass.hook { 
        injectMember { 
            method { 
                name = "onCreate"
                param(BundleClass)
                returnType = UnitType
            }
            afterHook {
                AlertDialog.Builder(instance())
                    .setTitle("Hooked")
                    .setMessage("I am hook!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
        injectMember { 
            method { 
                name = "onStart"
                emptyParam()
                returnType = UnitType
            }
            afterHook {
                // Your code here.
            }
        }
    }
}
```

对于当前项目下没有的 `Class`，你可以使用 `stub` 方式或 `findClass` 方法来得到需要 Hook 的类。

比如，我要得到 `com.example.demo.TestClass`。

> 示例如下

```kotlin
findClass(name = "com.example.demo.TestClass").hook {
    injectMember {
        // Your code here.
    }
}
```

若 `com.example.demo` 是你要 Hook 的 APP，那么写法可以更简单。

> 示例如下

```kotlin
findClass(name = "$packageName.TestClass").hook {
    injectMember {
        // Your code here.
    }
}
```

到这里有些同学可能就开始说了，在某些场景下 `findClass` 显得有些繁琐。

因为可能有些同学有如下需求。

> 示例如下

```kotlin
const val TestClass = "com.example.demo.TestClass"

TestClass.hook {
    injectMember {
        // Your code here.
    }
}
```

没关系，你还可以使用字符串类名直接创建一个 Hook。

> 示例如下

```kotlin
("$packageName.TestClass").hook {
    injectMember {
        // Your code here.
    }
}
```

### Hook Zygote

在 APP 启动时，新的进程被 fork 后的第一个事件 `initZygote`。

假设我们要全局 Hook 一个 APP `Activity` 的 `onCreate` 事件

在 `encase` 方法体中添加代码。

> 示例如下

```kotlin
loadZygote {
    ActivityClass.hook { 
        injectMember { 
            method { 
                name = "onCreate"
                param(BundleClass)
                returnType = UnitType
            }
            afterHook {
                // Your code here.
            }
        }
    }
}
```

### Hook 系统框架

在 `YukiHookAPI` 中，Hook 系统框架的实现非常简单。

假设，你要得到 `ApplicationInfo` 与 `PackageInfo` 并对它们进行一些操作。

在 `encase` 方法体中添加代码。

> 示例如下

```kotlin
loadSystem {
    ApplicationInfoClass.hook {
        // Your code here.
    }
    PackageInfoClass.hook {
        // Your code here.
    }
}
```

!> `loadZygote` 与 `loadSystem` 有直接性区别，`loadZygote` 会在 `initZygote` 中装载，系统框架被视为一个单独的 APP 而存在，若要 Hook 系统框架，请使用 `loadSystem`。

### Hook Resources

假设，我们要 Hook `com.android.browser` 中 `string` 类型的 `app_name` 内容替换为 `123`。

在 `encase` 方法体中添加代码。

> 示例如下

```kotlin
loadApp(name = "com.android.browser") {
    resources().hook {
        injectResource {
            conditions {
                name = "app_name"
                string()
            }
            replaceTo("123")
        }
    }
}
```

若当前 APP 使用 `app_name` 设置了标题栏文本，则它就会变成我们的 `123`。

你还可以使用当前 Xposed 模块的 Resources 替换 Hook APP 的 Resources。

假设，我们要继续 Hook `com.android.browser` 中 `mipmap` 类型的 `ic_launcher`。

> 示例如下

```kotlin
loadApp(name = "com.android.browser") {
    resources().hook {
        injectResource {
            conditions {
                name = "ic_launcher"
                mipmap()
            }
            replaceToModuleResource(R.mipmap.ic_launcher)
        }
    }
}
```

至此目标 APP 的图标将会被替换为我们设置的图标。

若你想替换系统框架的资源，同样也可以这样实现，只需要把 `loadApp` 换成 `loadZygote` 即可。

> 示例如下

```kotlin
loadZygote {
    resources().hook {
        // Your code here.
    }
}
```

更多功能请参考 [ResourcesHookCreater](api/document?id=resourceshookcreater-class)。

## 异常处理

> `YukiHookAPI` 重新设计了对异常的监听，任何异常都不会在 Hook 过程中抛出，避免打断下一个 Hook 流程导致 Hook 进程“死掉”。

你可以处理 Hook 方法过程发生的异常。

> 示例如下

```kotlin
injectMember {
    // Your code here.
}.result {
    // 处理 Hook 开始时的异常
    onHookingFailure {}
    // 处理 Hook 过程中的异常
    onConductFailure { param, throwable -> }
    // 处理全部异常
    onAllFailure {}
    // ...
}
```

在 Resources Hook 时此方法同样适用。

> 示例如下

```kotlin
injectResource {
    // Your code here.
}.result {
    // 处理 Hook 时的任意异常
    onHookingFailure {}
    // ...
}
```

你还可以处理 Hook 的 `Class` 不存在时发生的异常。

> 示例如下

```kotlin
TargetClass.hook {
    injectMember {
        // Your code here.
    }
}.onHookClassNotFoundFailure {
    // Your code here.
}
```

你还可以处理查找方法时的异常。

> 示例如下

```kotlin
method {
    // Your code here.
}.onNoSuchMethod {
    // Your code here.
}
```

这里介绍了可能发生的常见异常，若要了解更多请参考 [API 异常处理](config/api-exception.md)。

## 状态监听

在使用 `XposedHelper` 的同学往往会在 Hook 后打印 `UnHook` 的方法确定是否 Hook 成功。

在 `YukiHookAPI` 中，你可以用以下方法方便地重新实现这个功能。

首先我们可以监听 Hook 已经准备开始。

> 示例如下

```kotlin
YourClass.hook {
    // Your code here.
}.onPrepareHook {
    loggerD(msg = "$instanceClass hook start")
}
```

!> 请注意 `instanceClass` 建议只在 `onPrepareHook` 中使用，万一被 Hook 的 `Class` 不存在将会抛出无法拦截的异常导致 Hook 进程“死掉”。

然后，我们还可以对 Hook 的方法结果进行监听是否成功。

> 示例如下

```kotlin
injectMember {
    // Your code here.
}.onHooked { member ->
    loggerD(msg = "$member has hooked")
}
```

## 扩展用法

> 你可以在 Hook 过程中使用下面的方法方便地实现各种判断和功能。

### 多个宿主

如果你的模块需要同时处理多个 APP 的 Hook 事件，你可以使用 `loadApp` 方法体来区分你要 Hook 的 APP。

> 示例如下

```kotlin
loadApp(name = "com.android.browser") {
    // Your code here.
}
loadApp(name = "com.android.phone") {
    // Your code here.
}
```

详细用法可 [点击这里](api/document?id=loadapp-method) 进行查看。

### 多个进程

如果你 Hook 的宿主 APP 有多个进程，你可以使用 `withProcess` 方法体来对它们分别进行 Hook。

> 示例如下

```kotlin
withProcess(mainProcessName) {
    // Your code here.
}
withProcess(name = "$packageName:tool") {
    // Your code here.
}
```

详细用法可 [点击这里](api/document?id=withprocess-method) 进行查看。

## 写法优化

为了使代码更加简洁，你可以删去 `YukiHookAPI` 的名称，将你的 `onHook` 入口写作 `lambda` 形式。

> 示例如下

```kotlin
override fun onHook() = encase {
    // Your code here.
}
```

## Xposed 模块判断自身激活状态

通常情况下，我们会选择写一个方法，使其返回 `false`，然后 Hook 掉这个方法使其返回 `true` 来证明 Hook 已经生效。

在 `YukiHookAPI` 中你完全不需要再这么做了，`YukiHookAPI` 已经帮你封装好了这个操作，你可以直接进行使用。

现在，你可以直接使用 `isXposedModuleActive` 在模块中判断自身是否被激活。

> 示例如下

```kotlin
if(isXposedModuleActive) {
    // Your code here.
}
```

由于一些特殊原因，在太极、无极中的模块无法使用标准方法检测激活状态。

此时你可以在 `Activity` 中使用 `isTaiChiModuleActive` 判断自身是否被激活。

> 示例如下

```kotlin
if(isTaiChiModuleActive) {
    // Your code here.
}
```

若你想使用两者得兼的判断方案，`YukiHookAPI` 同样为你封装了便捷的方式。

此时你可以在 `Activity` 中使用 `isModuleActive` 判断自身是否在 Xposed 或太极、无极中被激活。

> 示例如下

```kotlin
if(isModuleActive) {
    // Your code here.
}
```

若要了解更多可 [点击这里](api/document?id=ismoduleactive-field) 进行查看。

!> 除了提供标准 API 的 Hook 框架之外，其它情况下模块可能都将无法判断自己是否被激活。