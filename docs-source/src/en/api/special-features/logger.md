# Debug Logs

> Log is the most important part of the debugging process, `YukiHookAPI` encapsulates a set of stable and efficient debugging log functions for developers.

## Normal Logs

You can call `loggerD`, `loggerI`, `loggerW` to print normal logs to the console.

The usage method is as follows.

> The following example

```kotlin
loggerD(msg = "This is a log")
```

At this point, `YukiHookAPI` will call `android.util.Log` and `XposedBridge.log` to print this log at the same time.

The default `TAG` of the log is the value you set in `YukiHookLogger.Configs.tag`.

You can also customize this value dynamically, but it is not recommended to modify `TAG` easily to prevent logs from being filtered.

> The following example

```kotlin
loggerD(tag = "YukiHookAPI", msg = "This is a log")
```

The printed result is as shown below.

> The following example

```:no-line-numbers
[YukiHookAPI][D][host package name]--> This is a log
```

You can also use `LoggerType` to customize the type of log printing.

You can choose to use `android.util.Log` or `XposedBridge.log` to print logs.

The default type is `LoggerType.BOTH`, which means that both methods are used to print logs.

For example we only use `android.util.Log` to print logs.

> The following example

```kotlin
loggerD(tag = "YukiHookAPI", msg = "This is a log", type = LoggerType.LOGD)
```

Or just use `XposedBridge.log` to print the log, this method can only be used in the (Xposed) Host environment.

> The following example

```kotlin
loggerD(tag = "YukiHookAPI", msg = "This is a log", type = LoggerType.XPOSEDBRIDGE)
```

If you want to intelligently distinguish the (Xposed) Host environment from the Module environment, you can write it in the following form.

> The following example

```kotlin
loggerD(tag = "YukiHookAPI", msg = "This is a log", type = LoggerType.SCOPE)
```

In this way, the API will intelligently select the specified method type to print this log in different environments.

::: tip

For more functions, please refer to [loggerD](../public/com/highcapable/yukihookapi/hook/log/LoggerFactory#loggerd-method), [loggerI](../public/com/highcapable/yukihookapi/hook/log/LoggerFactory#loggeri-method) and [loggerW](../public/com/highcapable/yukihookapi/hook/log/LoggerFactory#loggerw-method) methods.

:::

## Error Logs

You can call `loggerE` to print `E` level logs to the console.

The usage method is as follows.

> The following example

```kotlin
loggerE(msg = "This is an error")
```

The error log is the highest level, regardless of whether you have filtered only `E` level logs.

For error-level logging, you can also append an exception stack.

```kotlin
// Assume this is the exception that was thrown
val throwable = Throwable(...)
// Print log
loggerE(msg = "This is an error", e = throwable)
```

The printed result is as shown below.

> The following example

```:no-line-numbers
[YukiHookAPI][E][host package name]--> This is an error
```

At the same time, the log will help you print the entire exception stack.

> The following example

```:no-line-numbers
java.lang.Throwable
        at com.demo.Test.<init>(...)
        at com.demo.Test.doTask(...)
        at com.demo.Test.stop(...)
        at com.demo.Test.init(...)
        at a.a.a(...)
        ... 3 more
```

In the error log, you can also use `LoggerType` to specify the method type currently used to print the log.

::: tip

For more functions, please refer to the [loggerE](../public/com/highcapable/yukihookapi/hook/log/LoggerFactory#loggere-method) method.

:::

## Save Logs and Custom Elements

You can save all currently printed logs directly to a file using the `YukiHookLogger.saveToFile` method.

> The following example

```kotlin
// Please note
// The saved file path must have read and write permissions
// Otherwise an exception will be thrown
YukiHookLogger.saveToFile("/sdcard/Documents/debug_log.log")
```

You can also use `YukiHookLogger.contents` to get all the log file contents that have been printed so far.

> The following example

```kotlin
// Get the contents of all log files that have been printed so far
val fileContent = YukiHookLogger.contents
```

If you need an array of real-time log data structures, you can directly get the content of `YukiHookLogger.inMemoryData`.

> The following example

```kotlin
// Get the currently printed real-time log data structure array
YukiHookLogger.inMemoryData.forEach {
     it.timestamp // Get timestamp
     it.time // Get UTC time
     it.priority // Get priority
     it.msg // Get message
     it.throwable // Get exception
     // ...
}
```

The above features require `YukiHookLogger.Configs.isRecord` to be enabled.

You can also use `YukiHookLogger.Configs.elements` to customize the elements that debug logs display externally.

This function requires `YukiHookAPI.Configs` to be configured in `onInit` of the Hook entry class.

> The following example

```kotlin
override fun onInit() = configs {
    debugLog {
        // ...
        elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
    }
    // ...
}
```

::: tip

For more functions, please refer to [YukiHookLogger.inMemoryData](../public/com/highcapable/yukihookapi/hook/log/LoggerFactory#inmemorydata-field), [YukiHookLogger.contents](../public/com/highcapable/yukihookapi/hook/log/LoggerFactory#contents-field), [YukiHookLogger.saveToFile](../public/com/highcapable/yukihookapi/hook/log/LoggerFactory#savetofile-method) methods and [YukiHookLogger.Configs](../public/com/highcapable/yukihookapi/hook/log/LoggerFactory#configs-object).

:::