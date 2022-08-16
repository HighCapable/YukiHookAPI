## YukiResourcesHookCreater *- class*

```kotlin
class YukiResourcesHookCreater(internal val packageParam: PackageParam, internal val hookResources: HookResources)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> `YukiHookAPI` 的 `Resources` 核心 Hook 实现类。

### injectResource *- method*

```kotlin
inline fun injectResource(tag: String, initiate: ResourceHookCreater.() -> Unit): ResourceHookCreater.Result
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 注入要 Hook 的 Resources。

**功能示例**

你可以注入任意 Resources，使用 `injectResource` 即可创建一个 `Hook` 对象。

> 示例如下

```kotlin
injectResource {
    // Your code here.
}
```

你还可以自定义 `tag`，方便你在调试的时候能够区分你的 `Hook` 对象。

> 示例如下

```kotlin
injectResource(tag = "KuriharaYuki") {
    // Your code here.
}
```

### ResourcesHookCreater *- class*

```kotlin
inner class ResourcesHookCreater internal constructor(private val tag: String)
```

**变更记录**

`v1.0.80` `新增`

`v1.0.93` `修改`

移除 `packageName`

**功能描述**

> Hook 核心功能实现类。

查找和处理需要 Hook 的 Resources。

#### resourceId *- field*

```kotlin
var resourceId: Int
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 直接设置需要替换的 Resources Id。

!> 不建议使用此方法设置目标需要 Hook 的 Resources Id，你可以使用 `conditions` 方法。

**功能示例**

你可以直接设置并指定目标 Hook APP 的 Resources Id。

> 示例如下

```kotlin
injectResource {
    resourceId = 0x7f060001.toInt()
    replaceTo(...)
}
```

#### conditions *- method*

```kotlin
inline fun conditions(initiate: ConditionFinder.() -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 查找条件。

若你设置了 `resourceId` 则此方法将不会被使用。

**功能示例**

你可参考 [ConditionFinder](#conditionfinder-class) 查看详细用法。

> 示例如下

```kotlin
injectResource {
    conditions {
        name = "test_string"
        string()
    }
    replaceTo(...)
}
```

#### replaceTo *- method*

```kotlin
fun replaceTo(any: Any)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 替换指定 Resources 为指定的值。

**功能示例**

你可以替换找到的 Resources 为你想要的值，可以是 `String`、`Drawable` 等。

比如我们要替换一个找到的字符串 Resources。

> 示例如下

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

> 示例如下

```kotlin
injectResource {
    conditions {
        name = "test_drawable"
        drawable()
    }
    replaceTo(ColorDrawable(Color.RED))
}
```

#### replaceToTrue *- method*

```kotlin
fun replaceToTrue()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 替换指定 Resources 为 `true`。

!> 确保目标替换 Resources 的类型为 `Boolean`。

#### replaceToFalse *- method*

```kotlin
fun replaceToFalse()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 替换指定 Resources 为 `false`。

!> 确保目标替换 Resources 的类型为 `Boolean`。

#### replaceToModuleResource *- method*

```kotlin
fun replaceToModuleResource(resId: Int)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 替换为当前 Xposed 模块的 Resources。

你可以直接使用模块的 `R.string.xxx`、`R.mipmap.xxx`、`R.drawable.xxx` 替换 Hook APP 的 Resources。

**功能示例**

使用此方法可非常方便地使用当前模块的 Resources 去替换目标 Hook APP 的 Resources。

这个过程你无需对目标 Resources 实现 `fwd` 方法。

比如我们要替换一个字符串。

> 示例如下

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

> 示例如下

```kotlin
injectResource {
    conditions {
        name = "test_drawable"
        drawable()
    }
    replaceToModuleResource(R.drawable.module_drawable)
}
```

#### injectAsLayout *- method*

```kotlin
fun injectAsLayout(initiate: YukiResources.LayoutInflatedParam.() -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 作为装载的布局注入。

**功能示例**

你可以直接注入一个布局监听并修改它的内部 `View`。

> 示例如下

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

> 示例如下

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

#### ConditionFinder *- class*

```kotlin
inner class ConditionFinder internal constructor()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> Resources 查找条件实现类。

##### name *- field*

```kotlin
var name: String
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 名称。

##### anim *- method*

```kotlin
fun anim()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为动画。

##### animator *- method*

```kotlin
fun animator()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为属性动画。

##### bool *- method*

```kotlin
fun bool()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为布朗(Boolean)。

##### color *- method*

```kotlin
fun color()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为颜色(Color)。

##### dimen *- method*

```kotlin
fun dimen()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为尺寸(Dimention)。

##### drawable *- method*

```kotlin
fun drawable()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为 Drawable。

##### integer *- method*

```kotlin
fun integer()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为整型(Integer)。

##### layout *- method*

```kotlin
fun layout()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为布局(Layout)。

##### plurals *- method*

```kotlin
fun plurals()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为 Plurals。

##### string *- method*

```kotlin
fun string()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为字符串(String)。

##### xml *- method*

```kotlin
fun xml()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为 Xml。

##### mipmap *- method*

```kotlin
fun mipmap()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 设置 Resources 类型为位图(Mipmap)。

##### array *- method*

```kotlin
fun array()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 设置 Resources 类型为数组(Array)。

#### Result *- class*

```kotlin
inner class Result internal constructor()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 监听全部 Hook 结果实现类，可在这里处理失败事件监听。

##### result *- method*

```kotlin
inline fun result(initiate: Result.() -> Unit): Result
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 创建监听事件方法体。

##### by *- method*

```kotlin
inline fun by(condition: () -> Boolean): Result
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 添加执行 Hook 需要满足的条件，不满足条件将直接停止 Hook。

#### onHookingFailure *- method*

```kotlin
fun onHookingFailure(result: (Throwable) -> Unit): Result
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 监听 Hook 过程发生错误的回调方法。

#### ignoredHookingFailure *- method*

```kotlin
fun ignoredHookingFailure(): Result
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 忽略 Hook 过程出现的错误。