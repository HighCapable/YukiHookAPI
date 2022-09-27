---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# NameRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
class NameRules private constructor()
```

**Change Records**

`v1.0.88` `added`

`v1.1.0` `modified`

`NameConditions` 更名为 `NameRules`

作为 lambda 整体判断条件使用

移动到 base 包名

私有化构造方法

**Function Illustrate**

> 这是一个模糊 `Class`、`Member` 名称条件实现类。

可对 R8 混淆后的 `Class`、`Member` 进行更加详细的定位。

## String.isSynthetic <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.isSynthetic(index: Int): Boolean
```

**Change Records**

`v1.0.88` `added`

`v1.1.0` `modified`

统一合并到扩展方法并改名

**Function Illustrate**

> 是否为匿名类的主类调用对象。

## String.isOnlySymbols <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.isOnlySymbols(): Boolean
```

**Change Records**

`v1.0.88` `added`

`v1.1.0` `modified`

统一合并到扩展方法并改名

**Function Illustrate**

> 是否只有符号。

## String.isOnlyLetters <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.isOnlyLetters(): Boolean
```

**Change Records**

`v1.0.88` `added`

`v1.1.0` `modified`

统一合并到扩展方法并改名

**Function Illustrate**

> 是否只有字母。

## String.isOnlyNumbers <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.isOnlyNumbers(): Boolean
```

**Change Records**

`v1.0.88` `added`

`v1.1.0` `modified`

统一合并到扩展方法并改名

**Function Illustrate**

> 是否只有数字。

## String.isOnlyLettersNumbers <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.isOnlyLettersNumbers(): Boolean
```

**Change Records**

`v1.0.88` `added`

`v1.1.0` `modified`

统一合并到扩展方法并改名

**Function Illustrate**

> 是否只有字母或数字。

## String.isOnlyLowercase <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.isOnlyLowercase(): Boolean
```

**Change Records**

`v1.0.88` `added`

`v1.1.0` `modified`

统一合并到扩展方法并改名

**Function Illustrate**

> 是否只有小写字母。

在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符。

## String.isOnlyUppercase <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.isOnlyUppercase(): Boolean
```

**Change Records**

`v1.0.88` `added`

`v1.1.0` `modified`

统一合并到扩展方法并改名

**Function Illustrate**

> 是否只有大写字母。

在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符。