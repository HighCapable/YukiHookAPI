# Module Shared Files

> This is a solution for sharing files between the module and hosts.

YukiHook provides `YukiHookSharedFiles`, you can write files in the module and read them in hosts.

Shared files are stored in the shared directory provided by the hook framework for the module, they are independent of the private file directory of the module itself.

::: danger

This feature requires the [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) base module.

:::

## Get the Instance

In the module, you can get the `YukiHookSharedFiles` instance with `YukiHook.Module.sharedFiles`.

> The following example

```kotlin
val sharedFiles = YukiHook.Module.sharedFiles
```

In hosts, you can use `sharedFiles` directly in `PackageParam`.

Before using it, you need to use `isAvailable` to check whether shared files are currently available, and `isWritable` to check whether shared files are writable in the current environment.

::: warning

In the module, it is recommended to use it after observing the availability of the framework service, for details please refer to [Module Status → Hook Framework Service Status](./module-status.md#hook-framework-service-status).

:::

## File Access

`YukiHookSharedFiles` provides some simple file access functions, the design style of these APIs follows the Kotlin standard library.

### Write Files

In the module, you can use `writeText` or `writeBytes` to write files.

> The following example

```kotlin
// Write text, UTF-8 is used by default.
sharedFiles.writeText("config.json", "{}")
// Write a byte array.
sharedFiles.writeBytes("data.bin", byteArrayOf(1, 2, 3))
```

Writing replaces all the existing content of the file, and the file is created automatically when it does not exist.

### Read Files

You can use `readText` or `readBytes` to read the whole content of a file.

> The following example

```kotlin
// Read text, UTF-8 is used by default.
val config = sharedFiles.readText("config.json")
// Read a byte array.
val data = sharedFiles.readBytes("data.bin")
```

Files read in hosts must already exist.

### Use Streams

For larger files, you can use `inputStream` and `outputStream` to read and write files as streams.

> The following example

```kotlin
// Read the file.
sharedFiles.inputStream("data.bin").use { stream ->
    // Your code here.
}
// Write the file, only available in the module.
sharedFiles.outputStream("data.bin").use { stream ->
    // Your code here.
}
```

`outputStream` clears the existing content of the file when it is opened, and closing the stream also closes the file.

### List and Delete Files

You can use `list` to list all file names, and `delete` in the module to delete files.

> The following example

```kotlin
// List all file names.
val names = sharedFiles.list()
// Delete a file, returns whether it was deleted.
val isDeleted = sharedFiles.delete("config.json")
```

`list` returns a snapshot of the file names at the time of the call, later changes to the files are not reflected in the list you got.

::: warning

File names must not contain path separators and must not be `.` or `..`, shared files do not support subdirectories.

:::

## Use ParcelFileDescriptor

If you need to read and write a file randomly, get file information or hand the file over to other components, you can use `open` to get the `ParcelFileDescriptor` of the file.

In the module, the file is created automatically when it does not exist, and the `ParcelFileDescriptor` you get is readable and writable.

In hosts, the `ParcelFileDescriptor` you get is read-only, and the file must already exist.

> The following example

```kotlin
sharedFiles.open("data.bin").use { descriptor ->
    // Get the file size.
    val size = descriptor.statSize
    // Read 4 bytes from the start of the file.
    val buffer = ByteBuffer.allocate(4)
    FileInputStream(descriptor.fileDescriptor).channel.read(buffer, 0)
}
```

In the module, you can also write the file from a given position. Unlike `outputStream`, this does not clear the existing content of the file.

> The following example

```kotlin
sharedFiles.open("data.bin").use { descriptor ->
    // Write 4 bytes from the start of the file, overwriting the existing content.
    val buffer = ByteBuffer.wrap(byteArrayOf(1, 2, 3, 4))
    FileOutputStream(descriptor.fileDescriptor).channel.write(buffer, 0)
}
```

::: tip

If you need to hand the file over to the native layer, you can get the raw file descriptor with `fd`. The file descriptor is still managed by the `ParcelFileDescriptor` in this case, to transfer its ownership, please use `detachFd`.

:::

::: danger

You need to close the `ParcelFileDescriptor` returned by `open` yourself, we recommend using `use` as in the examples above.

:::