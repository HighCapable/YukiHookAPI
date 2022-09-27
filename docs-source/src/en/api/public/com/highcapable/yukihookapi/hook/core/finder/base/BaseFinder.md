---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# BaseFinder <span class="symbol">- class</span>

```kotlin:no-line-numbers
abstract class BaseFinder
```

**Change Records**

`v1.0.70` `added`

`v1.1.0` `modified`

分离原始命名 `BaseFinder` 中的部分方法与参数到 `MemberBaseFinder`

**Function Illustrate**

> 这是 `Class` 与 `Member` 查找类功能的基本类实现。

## BaseFinder.IndexTypeCondition <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class IndexTypeCondition internal constructor(private val type: IndexConfigType)
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 字节码下标筛选实现类。

### index <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun index(num: Int)
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 设置下标。

若 `index` 小于零则为倒序，此时可以使用 `IndexTypeConditionSort.reverse` 方法实现。

可使用 `IndexTypeConditionSort.first` 和 `IndexTypeConditionSort.last` 设置首位和末位筛选条件。

### index <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun index(): IndexTypeConditionSort
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 得到下标。

### IndexTypeConditionSort <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class IndexTypeConditionSort internal constructor()
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 字节码下标排序实现类。

#### first <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun first()
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 设置满足条件的第一个。

#### last <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun last()
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 设置满足条件的最后一个。

#### reverse <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun reverse(num: Int)
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 设置倒序下标。