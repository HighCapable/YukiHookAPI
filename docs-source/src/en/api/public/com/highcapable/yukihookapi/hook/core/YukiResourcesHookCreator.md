---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# YukiResourcesHookCreator <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiResourcesHookCreator(internal val packageParam: PackageParam, internal val hookResources: HookResources)
```

**Change Records**

`v1.0.80` `added`

`v1.1.0` `modified`

修正拼写错误的 **Creater** 命名到 **Creator**

**Function Illustrate**

> `YukiHookAPI` 的 `Resources` 核心 Hook 实现类。

## injectResource <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun injectResource(tag: String, initiate: ResourceHookCreator.() -> Unit): ResourceHookCreator.Result
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 注入要 Hook 的 Resources。

**Function Example**

你可以注入任意 Resources，使用 `injectResource` 即可创建一个 `Hook` 对象。

> The following example

```kotlin
injectResource {
    // Your code here.
}
```

你还可以自定义 `tag`，方便你在调试的时候能够区分你的 `Hook` 对象。

> The following example

```kotlin
injectResource(tag = "KuriharaYuki") {
    // Your code here.
}
```

## ResourcesHookCreator <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class ResourcesHookCreator internal constructor(private val tag: String)
```

**Change Records**

`v1.0.80` `added`

`v1.1.0` `modified`

移除 `packageName`

修正拼写错误的 **Creater** 命名到 **Creator**

**Function Illustrate**

> Hook 核心功能实现类。

查找和处理需要 Hook 的 Resources。

### resourceId <span class="symbol">- field</span>

```kotlin:no-line-numbers
var resourceId: Int
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 直接设置需要替换的 Resources Id。

::: warning

不建议使用此方法设置目标需要 Hook 的 Resources Id，你可以使用 **conditions** 方法。

:::

**Function Example**

你可以直接设置并指定目标 Hook APP 的 Resources Id。

> The following example

```kotlin
injectResource {
    resourceId = 0x7f060001.toInt()
    replaceTo(...)
}
```

### conditions <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun conditions(initiate: ConditionFinder.() -> Unit)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 查找条件。

若你设置了 `resourceId` 则此方法将不会被使用。

**Function Example**

你可参考 [ConditionFinder](#conditionfinder-class) 查看详细用法。

> The following example

```kotlin
injectResource {
    conditions {
        name = "test_string"
        string()
    }
    replaceTo(...)
}
```

### replaceTo <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceTo(any: Any)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 替换指定 Resources 为指定的值。

**Function Example**

你可以替换找到的 Resources 为你想要的值，可以是 `String`、`Drawable` 等。

比如我们要替换一个找到的字符串 Resources。

> The following example

```kotlin
injectResource {
    conditions {
        name = "test_string"
        string()
    }
    replaceTo("replace string")
}
```

或是替换为一个 `Drawable`，你无需对目标 Resources 实现 `fwd` 方法或 `DrawableLoader`。

> The following example

```kotlin
injectResource {
    conditions {
        name = "test_drawable"
        drawable()
    }
    replaceTo(ColorDrawable(Color.RED))
}
```

### replaceToTrue <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceToTrue()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 替换指定 Resources 为 `true`。

::: danger

确保目标替换 Resources 的类型为 **Boolean**。

:::

### replaceToFalse <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceToFalse()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 替换指定 Resources 为 `false`。

::: danger

确保目标替换 Resources 的类型为 **Boolean**。

:::

### replaceToModuleResource <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun replaceToModuleResource(resId: Int)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 替换为当前 Xposed 模块的 Resources。

你可以直接使用模块的 `R.string.xxx`、`R.mipmap.xxx`、`R.drawable.xxx` 替换 Hook APP 的 Resources。

**Function Example**

使用此方法可非常方便地使用当前模块的 Resources 去替换目标 Hook APP 的 Resources。

这个过程你无需对目标 Resources 实现 `fwd` 方法。

比如我们要替换一个字符串。

> The following example

```kotlin
injectResource {
    conditions {
        name = "test_string"
        string()
    }
    replaceToModuleResource(R.string.module_string)
}
```

还可以替换一些复杂的 Resources，比如 `xml` 创建的 `drawable`。

> The following example

```kotlin
injectResource {
    conditions {
        name = "test_drawable"
        drawable()
    }
    replaceToModuleResource(R.drawable.module_drawable)
}
```

### injectAsLayout <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun injectAsLayout(initiate: YukiResources.LayoutInflatedParam.() -> Unit)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 作为装载的布局注入。

**Function Example**

你可以直接注入一个布局监听并修改它的内部 `View`。

> The following example

```kotlin
injectResource {
    conditions {
        name = "activity_main"
        layout()
    }
    injectAsLayout {
        findViewByIdentifier<View>(name = "test_view")?.isVisible = false
        findViewByIdentifier<TextView>(name = "test_text_view")?.text = "Hook this"
    }
}
```

你还可以通过 `currentView` 拿到 `Context`。

> The following example

```kotlin
injectResource {
    conditions {
        name = "activity_main"
        layout()
    }
    injectAsLayout {
        Toast.makeText(currentView.context, "Hook this", Toast.LENGTH_SHORT).show()
    }
}
```

### ConditionFinder <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class ConditionFinder internal constructor()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> Resources 查找条件实现类。

#### name <span class="symbol">- field</span>

```kotlin:no-line-numbers
var name: String
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 名称。

#### anim <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun anim()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为动画。

#### animator <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun animator()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为属性动画。

#### bool <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun bool()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为布朗(Boolean)。

#### color <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun color()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为颜色(Color)。

#### dimen <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun dimen()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为尺寸(Dimention)。

#### drawable <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun drawable()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为 Drawable。

#### integer <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun integer()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为整型(Integer)。

#### layout <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun layout()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为布局(Layout)。

#### plurals <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun plurals()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为 Plurals。

#### string <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun string()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为字符串(String)。

#### xml <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun xml()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为 Xml。

#### mipmap <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun mipmap()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 设置 Resources 类型为位图(Mipmap)。

#### array <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun array()
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置 Resources 类型为数组(Array)。

### Result <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class Result internal constructor()
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 监听全部 Hook 结果实现类，可在这里处理失败事件监听。

#### result <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun result(initiate: Result.() -> Unit): Result
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 创建监听事件方法体。

#### by <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun by(condition: () -> Boolean): Result
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

### onHookingFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onHookingFailure(result: (Throwable) -> Unit): Result
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 监听 Hook 过程发生错误的回调方法。

### ignoredHookingFailure <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun ignoredHookingFailure(): Result
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 忽略 Hook 过程出现的错误。