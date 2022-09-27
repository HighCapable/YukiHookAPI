---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# ModuleAppCompatActivity <span class="symbol">- class</span>

```kotlin:no-line-numbers
open class ModuleAppCompatActivity : AppCompatActivity()
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 代理 `AppCompatActivity`。

继承于此类的 `Activity` 可以同时在宿主与模块中启动。

在 (Xposed) 宿主环境需要在宿主启动时调用 `Context.registerModuleAppActivities` 进行注册。

在 (Xposed) 宿主环境需要重写 `moduleTheme` 设置 AppCompat 主题，否则会无法启动。

## moduleTheme <span class="symbol">- field</span>

```kotlin:no-line-numbers
open val moduleTheme: Int
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置当前代理的 `Activity` 主题。