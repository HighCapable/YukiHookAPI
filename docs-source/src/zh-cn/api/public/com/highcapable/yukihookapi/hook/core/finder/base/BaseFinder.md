---
pageClass: code-page
---

# BaseFinder <span class="symbol">- class</span>

```kotlin:no-line-numbers
abstract class BaseFinder
```

**变更记录**

`v1.0.70` `新增`

`v1.1.0` `修改`

分离原始命名 `BaseFinder` 中的部分方法与参数到 `MemberBaseFinder`

**功能描述**

> 这是 `Class` 与 `Member` 查找类功能的基本类实现。

## BaseFinder.IndexTypeCondition <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class IndexTypeCondition internal constructor(private val type: IndexConfigType)
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 字节码下标筛选实现类。

### index <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun index(num: Int)
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置下标。

若 `index` 小于零则为倒序，此时可以使用 `IndexTypeConditionSort.reverse` 方法实现。

可使用 `IndexTypeConditionSort.first` 和 `IndexTypeConditionSort.last` 设置首位和末位筛选条件。

### index <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun index(): IndexTypeConditionSort
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 得到下标。

### IndexTypeConditionSort <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class IndexTypeConditionSort internal constructor()
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 字节码下标排序实现类。

#### first <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun first()
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置满足条件的第一个。

#### last <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun last()
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置满足条件的最后一个。

#### reverse <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun reverse(num: Int)
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 设置倒序下标。