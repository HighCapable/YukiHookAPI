# 模块共享文件

> 这是一个在模块与宿主之间共享文件的解决方案。

YukiHook 提供了 `YukiHookSharedFiles`，你可以在模块中写入文件，并在宿主中读取它们。

共享文件存储在 Hook 框架为模块提供的共享目录中，它与模块自身的私有文件目录相互独立。

::: danger

此功能需要 [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) 底座支持。

:::

## 获取实例

在模块中，你可以使用 `YukiHook.Module.sharedFiles` 得到 `YukiHookSharedFiles` 实例。

> 示例如下

```kotlin
val sharedFiles = YukiHook.Module.sharedFiles
```

在宿主中，你可以在 `PackageParam` 中直接使用 `sharedFiles`。

在使用之前，你需要使用 `isAvailable` 判断共享文件当前是否可用，使用 `isWritable` 判断共享文件在当前环境中是否可写。

::: warning

在模块中，建议监听框架服务的可用状态后使用，详情请参考 [模块状态 → Hook 框架服务状态](./module-status.md#hook-框架服务状态)。

:::

## 文件存取

`YukiHookSharedFiles` 提供了一些简单的文件存取方法，这些 API 的设计风格沿用了 Kotlin 的标准库。

### 写入文件

在模块中，你可以使用 `writeText` 或 `writeBytes` 写入文件。

> 示例如下

```kotlin
// 写入文本，默认使用 UTF-8 编码
sharedFiles.writeText("config.json", "{}")
// 写入字节数组
sharedFiles.writeBytes("data.bin", byteArrayOf(1, 2, 3))
```

写入将替换文件原有的全部内容，文件不存在时将会被自动创建。

### 读取文件

你可以使用 `readText` 或 `readBytes` 读取文件的全部内容。

> 示例如下

```kotlin
// 读取文本，默认使用 UTF-8 编码
val config = sharedFiles.readText("config.json")
// 读取字节数组
val data = sharedFiles.readBytes("data.bin")
```

在宿主中读取的文件必须已经存在。

### 使用流

对于较大的文件，你可以使用 `inputStream` 与 `outputStream` 以流的方式读写文件。

> 示例如下

```kotlin
// 读取文件
sharedFiles.inputStream("data.bin").use { stream ->
    // Your code here.
}
// 写入文件，仅在模块中可用
sharedFiles.outputStream("data.bin").use { stream ->
    // Your code here.
}
```

`outputStream` 在打开时会清空文件原有的内容，关闭流时将会同时关闭文件。

### 列出与删除文件

你可以使用 `list` 列出全部文件名，在模块中使用 `delete` 删除文件。

> 示例如下

```kotlin
// 列出全部文件名
val names = sharedFiles.list()
// 删除文件，返回是否删除成功
val isDeleted = sharedFiles.delete("config.json")
```

`list` 返回的是调用时文件名的快照，之后对文件的修改不会反映到已得到的列表中。

::: warning

文件名不能包含路径分隔符，也不能为 `.` 或 `..`，共享文件不支持子目录。

:::

## 使用 ParcelFileDescriptor

如果你需要随机读写文件、获取文件信息或将文件交给其他组件使用，你可以使用 `open` 方法得到文件的 `ParcelFileDescriptor`。

在模块中，文件不存在时将会被自动创建，得到的 `ParcelFileDescriptor` 可读写。

在宿主中，得到的 `ParcelFileDescriptor` 是只读的，文件必须已经存在。

> 示例如下

```kotlin
sharedFiles.open("data.bin").use { descriptor ->
    // 获取文件大小
    val size = descriptor.statSize
    // 从文件开头读取 4 个字节
    val buffer = ByteBuffer.allocate(4)
    FileInputStream(descriptor.fileDescriptor).channel.read(buffer, 0)
}
```

在模块中，你还可以从指定位置写入文件，与 `outputStream` 不同，这种方式不会清空文件原有的内容。

> 示例如下

```kotlin
sharedFiles.open("data.bin").use { descriptor ->
    // 从文件开头写入 4 个字节，覆盖原有的内容
    val buffer = ByteBuffer.wrap(byteArrayOf(1, 2, 3, 4))
    FileOutputStream(descriptor.fileDescriptor).channel.write(buffer, 0)
}
```

::: tip

如果你需要将文件交给原生层使用，你可以通过 `fd` 得到原始的文件描述符，此时文件描述符仍然由 `ParcelFileDescriptor` 管理，若需要转移它的所有权，请使用 `detachFd`。

:::

::: danger

`open` 方法返回的 `ParcelFileDescriptor` 需要由你自行关闭，我们推荐像上方示例一样使用 `use` 方法。

:::