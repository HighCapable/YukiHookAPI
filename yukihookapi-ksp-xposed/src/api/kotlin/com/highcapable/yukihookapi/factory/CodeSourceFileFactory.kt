/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/fankes/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is created by fankes on 2022/9/20.
 */
@file:Suppress("ConstPropertyName")

package com.highcapable.yukihookapi.factory

import com.highcapable.yukihookapi.bean.GenerateData
import com.highcapable.yukihookapi.generated.YukiHookAPIProperties
import java.text.SimpleDateFormat
import java.util.Date

/**
 * 包名常量定义类
 */
object PackageName {
    const val YukiHookAPI_Impl = "com.highcapable.yukihookapi"
    const val ModuleApplication_Impl = "com.highcapable.yukihookapi.hook.xposed.application"
    const val YukiXposedModuleStatus_Impl = "com.highcapable.yukihookapi.hook.xposed.bridge.status"
    const val HandlerDelegateImpl_Impl = "com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.impl"
    const val HandlerDelegateClass = "com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate"
    const val IActivityManagerProxyImpl_Impl = "com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.impl"
    const val IActivityManagerProxyClass = "com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate"
    const val BootstrapReflectionClass = "com.highcapable.yukihookapi.thirdparty.me.weishu.reflection"
}

/**
 * 类名常量定义类
 */
object ClassName {
    const val YukiHookAPI_Impl = "YukiHookAPI_Impl"
    const val ModuleApplication_Impl = "ModuleApplication_Impl"
    const val YukiXposedModuleStatus_Impl = "YukiXposedModuleStatus_Impl"
    const val HandlerDelegateImpl_Impl = "HandlerDelegateImpl_Impl"
    const val HandlerDelegateClass = "HandlerDelegate"
    const val IActivityManagerProxyImpl_Impl = "IActivityManagerProxyImpl_Impl"
    const val IActivityManagerProxyClass = "IActivityManagerProxy"
    const val XposedInit = "xposed_init"
    const val XposedInit_Impl = "xposed_init_Impl"
    const val BootstrapClass = "BootstrapClass"
    const val Reflection = "Reflection"
}

/**
 * 外部调用者包名和类名定义类
 */
object ExternalCallerName {
    val HandlerDelegateCaller = Pair(
        "com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.caller.HandlerDelegateCaller",
        "HandlerDelegateCaller"
    )
    val IActivityManagerProxyCaller = Pair(
        "com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.caller.IActivityManagerProxyCaller",
        "IActivityManagerProxyCaller"
    )
    val YukiXposedEventCaller = Pair(
        "com.highcapable.yukihookapi.hook.xposed.bridge.event.caller.YukiXposedEventCaller",
        "YukiXposedEventCaller"
    )
    val YukiXposedModuleCaller = Pair(
        "com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiXposedModuleCaller",
        "YukiXposedModuleCaller"
    )
    val YukiXposedResourcesCaller = Pair(
        "com.highcapable.yukihookapi.hook.xposed.bridge.resources.caller.YukiXposedResourcesCaller",
        "YukiXposedResourcesCaller"
    )
}

/**
 * YukiXposedModuleStatus 方法名称定义类
 */
object YukiXposedModuleStatusJvmName {
    const val IS_ACTIVE_METHOD_NAME = "__--"
    const val IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME = "_--_"
    const val GET_EXECUTOR_NAME_METHOD_NAME = "_-_-"
    const val GET_EXECUTOR_API_LEVEL_METHOD_NAME = "-__-"
    const val GET_EXECUTOR_VERSION_NAME_METHOD_NAME = "-_-_"
    const val GET_EXECUTOR_VERSION_CODE_METHOD_NAME = "___-"
}

/**
 * 创建尾部包名名称
 * @param name 前置名称
 * @return [String]
 */
private fun GenerateData.tailPackageName(name: String) = "${name}_${modulePackageName.replace(".", "_")}"

/**
 * 创建文件注释
 * @param currrentClassTag 当前注入类标签
 * @return [String]
 */
private fun createCommentContent(currrentClassTag: String) =
    """
      /**
       * $currrentClassTag Class
       *
       * Compiled from YukiHookXposedProcessor
       *
       * Generate Date: ${SimpleDateFormat.getDateTimeInstance().format(Date())}
       *
       * Powered by YukiHookAPI (C) HighCapable 2019-2023
       *
       * Project URL: [${YukiHookAPIProperties.PROJECT_NAME}](${YukiHookAPIProperties.PROJECT_URL})
       */
    """.trimIndent()

/**
 * 获得注入文件代码内容
 * @return [Map]<[String], [String]>
 */
fun GenerateData.sources() = mapOf(
    ClassName.YukiHookAPI_Impl to """
      @file:Suppress("ClassName")
      
      package ${PackageName.YukiHookAPI_Impl}
    """.trimIndent() + "\n\n" + createCommentContent(ClassName.YukiHookAPI_Impl) + "\n" + """
      object ${ClassName.YukiHookAPI_Impl} {
      
          val compiledTimestamp get() = ${System.currentTimeMillis()}
      }
    """.trimIndent(),
    ClassName.ModuleApplication_Impl to """
      @file:Suppress("ClassName")
      
      package ${PackageName.ModuleApplication_Impl}
      
      import $entryPackageName.$entryClassName
    """.trimIndent() + "\n\n" + createCommentContent(ClassName.ModuleApplication_Impl) + "\n" + """
      object ${ClassName.ModuleApplication_Impl} {
      
          fun callHookEntryInit() = try {
              ${if (isEntryClassKindOfObject) "$entryClassName.onInit()" else "$entryClassName().onInit()"}
          } catch (_: Throwable) {
          }
      }
    """.trimIndent(),
    ClassName.YukiXposedModuleStatus_Impl to """
      @file:Suppress("ClassName")
      
      package ${PackageName.YukiXposedModuleStatus_Impl}
      
      import android.util.Log
      import androidx.annotation.Keep
    """.trimIndent() + "\n\n" + createCommentContent(ClassName.YukiXposedModuleStatus_Impl) + "\n" + """
      @Keep
      object ${ClassName.YukiXposedModuleStatus_Impl} {
      
          @JvmName("${YukiXposedModuleStatusJvmName.IS_ACTIVE_METHOD_NAME}")
          fun isActive(): Boolean {
              placeholderExecution()
              return false
          }
      
          @JvmName("${YukiXposedModuleStatusJvmName.IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME}")
          fun isSupportResourcesHook(): Boolean {
              placeholderExecution()
              return false
          }
      
          @JvmName("${YukiXposedModuleStatusJvmName.GET_EXECUTOR_NAME_METHOD_NAME}")
          fun getExecutorName(): String {
              placeholderExecution()
              return "unknown"
          }
      
          @JvmName("${YukiXposedModuleStatusJvmName.GET_EXECUTOR_API_LEVEL_METHOD_NAME}")
          fun getExecutorApiLevel(): Int {
              placeholderExecution()
              return -1
          }
      
          @JvmName("${YukiXposedModuleStatusJvmName.GET_EXECUTOR_VERSION_NAME_METHOD_NAME}")
          fun getExecutorVersionName(): String {
              placeholderExecution()
              return "unknown"
          }
      
          @JvmName("${YukiXposedModuleStatusJvmName.GET_EXECUTOR_VERSION_CODE_METHOD_NAME}")
          fun getExecutorVersionCode(): Int {
              placeholderExecution()
              return -1
          }
      
          private fun placeholderExecution() {
              /** Consume a long method body */
              if (System.currentTimeMillis() == 0L) Log.d("${(1000..9999).random()}", "${(100000..999999).random()}")
          }
      }
    """.trimIndent(),
    ClassName.HandlerDelegateImpl_Impl to """
      @file:Suppress("ClassName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
      
      package ${PackageName.HandlerDelegateImpl_Impl}
      
      import android.os.Handler
      import ${PackageName.HandlerDelegateClass}.${tailPackageName(ClassName.HandlerDelegateClass)}
    """.trimIndent() + "\n\n" + createCommentContent(ClassName.HandlerDelegateImpl_Impl) + "\n" + """
      object ${ClassName.HandlerDelegateImpl_Impl} {
      
          val wrapperClassName get() = "${PackageName.HandlerDelegateClass}.${tailPackageName(ClassName.HandlerDelegateClass)}"
      
          fun createWrapper(baseInstance: Handler.Callback? = null): Handler.Callback = ${tailPackageName(ClassName.HandlerDelegateClass)}(baseInstance)
      }
    """.trimIndent(),
    ClassName.HandlerDelegateClass to """
      @file:Suppress("ClassName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
      
      package ${PackageName.HandlerDelegateClass}
      
      import android.os.Handler
      import android.os.Message
      import androidx.annotation.Keep
      import ${ExternalCallerName.HandlerDelegateCaller.first}
    """.trimIndent() + "\n\n" + createCommentContent(ClassName.HandlerDelegateClass) + "\n" + """
      @Keep
      class ${tailPackageName(ClassName.HandlerDelegateClass)}(private val baseInstance: Handler.Callback?) : Handler.Callback {
      
          override fun handleMessage(msg: Message) = ${ExternalCallerName.HandlerDelegateCaller.second}.callHandleMessage(baseInstance, msg)
      }
    """.trimIndent(),
    ClassName.IActivityManagerProxyImpl_Impl to """
      @file:Suppress("ClassName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
      
      package ${PackageName.IActivityManagerProxyImpl_Impl}
      
      import android.os.Handler
      import ${ExternalCallerName.IActivityManagerProxyCaller.first}
      import ${PackageName.IActivityManagerProxyClass}.${tailPackageName(ClassName.IActivityManagerProxyClass)}
      import java.lang.reflect.Proxy
    """.trimIndent() + "\n\n" + createCommentContent(ClassName.IActivityManagerProxyImpl_Impl) + "\n" + """
      object ${ClassName.IActivityManagerProxyImpl_Impl} {
      
          fun createWrapper(clazz: Class<*>?, instance: Any) = 
              Proxy.newProxyInstance(${ExternalCallerName.IActivityManagerProxyCaller.second}.currentClassLoader, arrayOf(clazz), ${
        tailPackageName(
            ClassName.IActivityManagerProxyClass
        )
    }(instance))
      }
    """.trimIndent(),
    ClassName.IActivityManagerProxyClass to """
      @file:Suppress("ClassName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
      
      package ${PackageName.IActivityManagerProxyClass}
      
      import androidx.annotation.Keep
      import ${ExternalCallerName.IActivityManagerProxyCaller.first}
      import java.lang.reflect.InvocationHandler
      import java.lang.reflect.Method
      import java.lang.reflect.Proxy
    """.trimIndent() + "\n\n" + createCommentContent(ClassName.IActivityManagerProxyClass) + "\n" + """
      @Keep
      class ${tailPackageName(ClassName.IActivityManagerProxyClass)}(private val baseInstance: Any) : InvocationHandler {
      
          override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?) = ${ExternalCallerName.IActivityManagerProxyCaller.second}.callInvoke(baseInstance, method, args)
      }
    """.trimIndent(),
    ClassName.XposedInit to """
      @file:Suppress("ClassName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
      
      package $entryPackageName
      
      import androidx.annotation.Keep
      import ${ExternalCallerName.YukiXposedEventCaller.first}
      ${if (isUsingResourcesHook) "import de.robv.android.xposed.IXposedHookInitPackageResources" else ""}
      import de.robv.android.xposed.IXposedHookLoadPackage
      import de.robv.android.xposed.IXposedHookZygoteInit
      ${if (isUsingResourcesHook) "import de.robv.android.xposed.callbacks.XC_InitPackageResources" else ""}
      import de.robv.android.xposed.callbacks.XC_LoadPackage
    """.trimIndent() + "\n\n" + createCommentContent("Xposed Init") + "\n" + """
      @Keep
      class $xInitClassName : IXposedHookZygoteInit, IXposedHookLoadPackage${if (isUsingResourcesHook) ", IXposedHookInitPackageResources" else ""} {
      
          override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam?) {
              ${entryClassName}_Impl.callInitZygote(sparam)
              ${ExternalCallerName.YukiXposedEventCaller.second}.callInitZygote(sparam)
          }
      
          override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
              ${entryClassName}_Impl.callHandleLoadPackage(lpparam)
              ${ExternalCallerName.YukiXposedEventCaller.second}.callHandleLoadPackage(lpparam)
          }
    """.trimIndent() +
        (if (isUsingResourcesHook)
            "\n" + """
              
                  override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
                      ${entryClassName}_Impl.callHandleInitPackageResources(resparam)
                      ${ExternalCallerName.YukiXposedEventCaller.second}.callHandleInitPackageResources(resparam)
                  }
              }
            """.trimIndent()
        else "\n}"),
    ClassName.XposedInit_Impl to """
      @file:Suppress("ClassName", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
      
      package $entryPackageName
      
      import ${ExternalCallerName.YukiXposedModuleCaller.first}
      import ${ExternalCallerName.YukiXposedResourcesCaller.first}
      import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
      import de.robv.android.xposed.IXposedHookZygoteInit
      import de.robv.android.xposed.XposedBridge
      import de.robv.android.xposed.callbacks.XC_InitPackageResources
      import de.robv.android.xposed.callbacks.XC_LoadPackage
      ${if (customMPackageName.isBlank()) "import $modulePackageName.BuildConfig" else ""}
    """.trimIndent() + "\n\n" + createCommentContent("Xposed Init Impl") + "\n" + """
      object ${entryClassName}_Impl {
      
          private const val modulePackageName = ${if (customMPackageName.isNotBlank()) "\"$customMPackageName\"" else "BuildConfig.APPLICATION_ID"}
          private var isZygoteCalled = false
          private val hookEntry = ${if (isEntryClassKindOfObject) entryClassName else "$entryClassName()"}
      
          private fun callOnXposedModuleLoaded(
              isZygoteLoaded: Boolean = false,
              lpparam: XC_LoadPackage.LoadPackageParam? = null,
              resparam: XC_InitPackageResources.InitPackageResourcesParam? = null
          ) {
              if (isZygoteCalled.not()) runCatching {
                  hookEntry.onXposedEvent()
                  hookEntry.onInit()
                  if (${ExternalCallerName.YukiXposedModuleCaller.second}.isXposedCallbackSetUp) {
                      ${ExternalCallerName.YukiXposedModuleCaller.second}.internalLoggerE("You cannot load a hooker in \"onInit\" or \"onXposedEvent\" method! Aborted")
                      return
                  }
                  hookEntry.onHook()
                  ${ExternalCallerName.YukiXposedModuleCaller.second}.callOnFinishLoadModule()
              }.onFailure { ${ExternalCallerName.YukiXposedModuleCaller.second}.internalLoggerE("YukiHookAPI try to load hook entry class failed", it) }
              ${ExternalCallerName.YukiXposedModuleCaller.second}.callOnPackageLoaded(
                  type = when {
                      isZygoteLoaded -> HookEntryType.ZYGOTE
                      lpparam != null -> HookEntryType.PACKAGE
                      resparam != null -> HookEntryType.RESOURCES
                      else -> HookEntryType.ZYGOTE
                  },
                  packageName = lpparam?.packageName ?: resparam?.packageName,
                  processName = lpparam?.processName,
                  appClassLoader = lpparam?.classLoader ?: runCatching { XposedBridge.BOOTCLASSLOADER }.getOrNull(),
                  appInfo = lpparam?.appInfo,
                  appResources = ${ExternalCallerName.YukiXposedResourcesCaller.second}.createYukiResourcesFromXResources(resparam?.res)
              )
          }
      
          fun callInitZygote(sparam: IXposedHookZygoteInit.StartupParam?) {
              if (sparam == null) return
              runCatching {
                  ${ExternalCallerName.YukiXposedModuleCaller.second}.callOnStartLoadModule(modulePackageName, sparam.modulePath)
                  callOnXposedModuleLoaded(isZygoteLoaded = true)
                  isZygoteCalled = true
              }.onFailure { ${ExternalCallerName.YukiXposedModuleCaller.second}.internalLoggerE("An exception occurred when YukiHookAPI loading Xposed Module", it) }
          }
      
          fun callHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
              if (lpparam != null && isZygoteCalled) callOnXposedModuleLoaded(lpparam = lpparam)
          }
      
          fun callHandleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
              if (resparam != null && isZygoteCalled) callOnXposedModuleLoaded(resparam = resparam)
          }
      }
    """.trimIndent(),
    ClassName.BootstrapClass to ("package ${PackageName.BootstrapReflectionClass};\n" +
        "\n" +
        "import static android.os.Build.VERSION.SDK_INT;\n" +
        "\n" +
        "import android.os.Build;\n" +
        "import android.util.Log;\n" +
        "\n" +
        "import androidx.annotation.Keep;\n" +
        "\n" +
        "import java.lang.reflect.Method;\n" +
        "\n" +
        createCommentContent(currrentClassTag = ClassName.BootstrapClass) +
        "@Keep\n" +
        "public final class BootstrapClass {\n" +
        "\n" +
        "    private static final String TAG = \"BootstrapClass\";\n" +
        "    private static Object sVmRuntime;\n" +
        "    private static Method setHiddenApiExemptions;\n" +
        "\n" +
        "    static {\n" +
        "        if (SDK_INT >= Build.VERSION_CODES.P) {\n" +
        "            try {\n" +
        "                Method forName = Class.class.getDeclaredMethod(\"forName\", String.class);\n" +
        "                Method getDeclaredMethod = Class.class.getDeclaredMethod(\"getDeclaredMethod\", String.class, Class[].class);\n" +
        "                Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, \"dalvik.system.VMRuntime\");\n" +
        "                Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, \"getRuntime\", null);\n" +
        "                setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, \"setHiddenApiExemptions\", new Class[]{String[].class});\n" +
        "                sVmRuntime = getRuntime.invoke(null);\n" +
        "            } catch (Throwable e) {\n" +
        "                Log.w(TAG, \"reflect bootstrap failed:\", e);\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    public static boolean exempt(String method) {\n" +
        "        return exempt(new String[]{method});\n" +
        "    }\n" +
        "\n" +
        "    public static boolean exempt(String... methods) {\n" +
        "        if (sVmRuntime == null || setHiddenApiExemptions == null) {\n" +
        "            return false;\n" +
        "        }\n" +
        "        try {\n" +
        "            setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{methods});\n" +
        "            return true;\n" +
        "        } catch (Throwable e) {\n" +
        "            return false;\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    public static boolean exemptAll() {\n" +
        "        return exempt(new String[]{\"L\"});\n" +
        "    }\n" +
        "}"),
    ClassName.Reflection to ("package ${PackageName.BootstrapReflectionClass};\n" +
        "\n" +
        "import static android.os.Build.VERSION.SDK_INT;\n" +
        "import static com.highcapable.yukihookapi.thirdparty.me.weishu.reflection.BootstrapClass.exemptAll;\n" +
        "\n" +
        "import android.content.Context;\n" +
        "import android.text.TextUtils;\n" +
        "import android.util.Base64;\n" +
        "\n" +
        "import androidx.annotation.Keep;\n" +
        "\n" +
        "import java.io.File;\n" +
        "import java.io.FileOutputStream;\n" +
        "import java.lang.reflect.Method;\n" +
        "\n" +
        "import dalvik.system.DexFile;\n" +
        "\n" +
        createCommentContent(currrentClassTag = ClassName.Reflection) +
        "@Keep\n" +
        "public class Reflection {\n" +
        "\n" +
        "    private static final String TAG = \"Reflection\";\n" +
        "    private static final String DEX = \"ZGV4CjAzNQCXDT0vQ44GJqsrjm32y0qlQmxUevbk56r0CwAAcAAAAHhWNBIAAAAAAAAAADwLAABDAAAAcAAAABMAAAB8AQAACwAAAMgBAAAMAAAATAIAAA8AAACsAgAAAwAAACQDAABwCAAAhAMAAIQDAACGAwAAiwMAAJUDAACdAwAArQMAALkDAADJAwAA3gMAAPADAAD3AwAA/wMAAAIEAAAGBAAACgQAABAEAAATBAAAGAQAADMEAABZBAAAdQQAAIkEAADYBAAAJgUAAHAFAACDBQAAmQUAAK0FAADBBQAA1QUAAOwFAAAIBgAAFAYAACUGAAAuBgAAMwYAADYGAABEBgAAUgYAAFYGAABZBgAAXQYAAHEGAACGBgAAmwYAAKQGAAC9BgAAwAYAAMgGAADTBgAA3AYAAO0GAAABBwAAFAcAACAHAAAoBwAANQcAAE8HAABXBwAAYAcAAHsHAACEBwAAkAcAAKgHAAC6BwAAwgcAANAHAAALAAAAEQAAABIAAAATAAAAFAAAABUAAAAWAAAAFwAAABgAAAAaAAAAGwAAABwAAAAdAAAAHgAAACMAAAAnAAAAKQAAACoAAAArAAAADAAAAAAAAAD4BwAADQAAAAAAAAAMCAAADgAAAAAAAAAACAAADwAAAAIAAAAAAAAAEAAAAAkAAAAUCAAAEAAAAA0AAADoBwAAIwAAAA4AAAAAAAAAJgAAAA4AAADgBwAAJwAAAA8AAAAAAAAAKAAAAA8AAADgBwAAKAAAAA8AAADwBwAAAgAAAD8AAAADAAAAIQAAAAUACgAEAAAABQAKAAUAAAAFAA8ACQAAAAUACgAKAAAABQAAACQAAAAFAAoAJQAAAAYACgAiAAAABgAJAD0AAAAGAA0APgAAAAcACgAiAAAAAQADADMAAAAEAAIALgAAAAUABgADAAAABgAGAAIAAAAGAAYAAwAAAAYACQAvAAAABgAKAC8AAAAGAAgAMAAAAAcABgADAAAABwABAEAAAAAHAAAAQQAAAAgABQA0AAAACQAGAAMAAAALAAcANwAAAA0ABAA2AAAABQAAABEAAAAJAAAAAAAAAAgAAAAAAAAA7AoAAB8IAAAGAAAAEQAAAAkAAAAAAAAABwAAAAAAAAACCwAAHAgAAAcAAAABAAAACQAAAAAAAAAgAAAAAAAAACULAAArCAAAAAADMS4wAAg8Y2xpbml0PgAGPGluaXQ+AA5BUFBMSUNBVElPTl9JRAAKQlVJTERfVFlQRQAOQm9vdHN0cmFwQ2xhc3MAE0Jvb3RzdHJhcENsYXNzLmphdmEAEEJ1aWxkQ29uZmlnLmphdmEABURFQlVHAAZGTEFWT1IAAUkAAklJAAJJTAAESUxMTAABTAADTExMABlMYW5kcm9pZC9jb250ZW50L0NvbnRleHQ7ACRMYW5kcm9pZC9jb250ZW50L3BtL0FwcGxpY2F0aW9uSW5mbzsAGkxhbmRyb2lkL29zL0J1aWxkJFZFUlNJT047ABJMYW5kcm9pZC91dGlsL0xvZzsATUxjb20vaGlnaGNhcGFibGUveXVraWhvb2thcGkvdGhpcmRwYXJ0eS9tZS93ZWlzaHUvZnJlZXJlZmxlY3Rpb24vQnVpbGRDb25maWc7AExMY29tL2hpZ2hjYXBhYmxlL3l1a2lob29rYXBpL3RoaXJkcGFydHkvbWUvd2Vpc2h1L3JlZmxlY3Rpb24vQm9vdHN0cmFwQ2xhc3M7AEhMY29tL2hpZ2hjYXBhYmxlL3l1a2lob29rYXBpL3RoaXJkcGFydHkvbWUvd2Vpc2h1L3JlZmxlY3Rpb24vUmVmbGVjdGlvbjsAEUxqYXZhL2xhbmcvQ2xhc3M7ABRMamF2YS9sYW5nL0NsYXNzPCo+OwASTGphdmEvbGFuZy9PYmplY3Q7ABJMamF2YS9sYW5nL1N0cmluZzsAEkxqYXZhL2xhbmcvU3lzdGVtOwAVTGphdmEvbGFuZy9UaHJvd2FibGU7ABpMamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kOwAKUmVmbGVjdGlvbgAPUmVmbGVjdGlvbi5qYXZhAAdTREtfSU5UAANUQUcAAVYADFZFUlNJT05fQ09ERQAMVkVSU0lPTl9OQU1FAAJWTAABWgACWkwAEltMamF2YS9sYW5nL0NsYXNzOwATW0xqYXZhL2xhbmcvT2JqZWN0OwATW0xqYXZhL2xhbmcvU3RyaW5nOwAHY29udGV4dAAXZGFsdmlrLnN5c3RlbS5WTVJ1bnRpbWUAAWUABmV4ZW1wdAAJZXhlbXB0QWxsAAdmb3JOYW1lAA9mcmVlLXJlZmxlY3Rpb24AEmdldEFwcGxpY2F0aW9uSW5mbwARZ2V0RGVjbGFyZWRNZXRob2QACmdldFJ1bnRpbWUABmludm9rZQALbG9hZExpYnJhcnkAGG1lLndlaXNodS5mcmVlcmVmbGVjdGlvbgAGbWV0aG9kAAdtZXRob2RzABlyZWZsZWN0IGJvb3RzdHJhcCBmYWlsZWQ6AAdyZWxlYXNlAApzVm1SdW50aW1lABZzZXRIaWRkZW5BcGlFeGVtcHRpb25zABB0YXJnZXRTZGtWZXJzaW9uAAZ1bnNlYWwADHVuc2VhbE5hdGl2ZQAOdm1SdW50aW1lQ2xhc3MAAQAAAAoAAAACAAAACgAQAAEAAAASAAAAAQAAAAAAAAADAAAACgAKAAwAAAABAAAAAQAAAAIAAAAJABEAARcGBhc4FzwfFwAEARcBARcfAAAAAAAABgAHDgAWAAcOav8DATIOARUQAwI1DvAEBEMJGgESDwMDNg4BGw+pBQIFAwUEGR4DAC8NAA4ABw4ALAE6Bw4ANgE7ByydGuIBAQMALw0eAEgABw4ADQAHDgATAS0HHXIZa1oAAAEAAQABAAAANAgAAAQAAABwEAwAAAAOAAoAAAADAAEAOQgAAHsAAABgBQEAEwYcADRlbQAcBQgAGgYxABIXI3cQABIIHAkKAE0JBwhuMAsAZQcMARwFCAAaBjQAEicjdxAAEggcCQoATQkHCBIYHAkQAE0JBwhuMAsAZQcMAhIFEhYjZhEAEgcaCC0ATQgGB24wDgBRBgwEHwQIABIlI1URABIGGgc1AE0HBQYSFhIHTQcFBm4wDgBCBQwDHwMNABIlI1URABIGGgc+AE0HBQYSFhIXI3cQABIIHAkSAE0JBwhNBwUGbjAOAEIFDAUfBQ0AaQUKABIFEgYjZhEAbjAOAFMGDAVpBQkADgANABoFBgAaBjsAcTABAGUAKPcAAAYAAABrAAEAAQEMcgEAAQABAAAAaAgAAAQAAABwEAwAAAAOAAMAAQABAAAAbQgAAAsAAAASECMAEgASAU0CAAFxEAYAAAAKAA8AAAAIAAEAAwABAHMIAAAdAAAAEhESAmIDCQA4AwYAYgMKADkDBAABIQ8BYgMKAGIECQASFSNVEQASBk0HBQZuMA4AQwUo8g0AASEo7wAADAAAAA0AAQABAQwaAwAAAAEAAACDCAAADQAAABIQIwASABIBGgIPAE0CAAFxEAYAAAAKAA8AAAABAAEAAQAAAIgIAAAEAAAAcBAMAAAADgAEAAEAAQAAAI0IAAAeAAAAEgBgAQEAEwIcADUhAwAPAHEABwAAAAoBOQH7/xoAMgBxEA0AAABuEAAAAwAMAFIAAABxEAoAAAAKACjqBgABAAIZARkBGQEZARkBGQKBgASYEQMABQAIGgEKAQoDiIAEsBEBgYAExBMBCdwTAYkBhBQBCdwUAQADAAsaCIGABIgVAQmgFQGKAgAAAAAPAAAAAAAAAAEAAAAAAAAAAQAAAEMAAABwAAAAAgAAABMAAAB8AQAAAwAAAAsAAADIAQAABAAAAAwAAABMAgAABQAAAA8AAACsAgAABgAAAAMAAAAkAwAAAiAAAEMAAACEAwAAARAAAAcAAADgBwAABSAAAAMAAAAcCAAAAxAAAAEAAAAwCAAAAyAAAAgAAAA0CAAAASAAAAgAAACYCAAAACAAAAMAAADsCgAAABAAAAEAAAA8CwAA\";\n" +
        "\n" +
        "    private static native int unsealNative(int targetSdkVersion);\n" +
        "\n" +
        "    public static int unseal(Context context) {\n" +
        "        if (SDK_INT < 28) {\n" +
        "            // Below Android P, ignore\n" +
        "            return 0;\n" +
        "        }\n" +
        "        // try exempt API first.\n" +
        "        if (exemptAll()) {\n" +
        "            return 0;\n" +
        "        }\n" +
        "        if (unsealByDexFile(context)) {\n" +
        "            return 0;\n" +
        "        }\n" +
        "        return -1;\n" +
        "    }\n" +
        "\n" +
        "    @SuppressWarnings({\"deprecation\", \"ResultOfMethodCallIgnored\"})\n" +
        "    private static boolean unsealByDexFile(Context context) {\n" +
        "        byte[] bytes = Base64.decode(DEX, Base64.NO_WRAP);\n" +
        "        File codeCacheDir = getCodeCacheDir(context);\n" +
        "        if (codeCacheDir == null) {\n" +
        "            return false;\n" +
        "        }\n" +
        "        File code = new File(codeCacheDir, \"__temp_\" + System.currentTimeMillis() + \".dex\");\n" +
        "        try {\n" +
        "            try (FileOutputStream fos = new FileOutputStream(code)) {\n" +
        "                fos.write(bytes);\n" +
        "            }\n" +
        "            DexFile dexFile = new DexFile(code);\n" +
        "            // This class is hardcoded in the dex, Don't use BootstrapClass.class to reference it\n" +
        "            // it maybe obfuscated!!\n" +
        "            Class<?> bootstrapClass = dexFile.loadClass(\"com.highcapable.yukihookapi.thirdparty.me.weishu.reflection.BootstrapClass\", null);\n" +
        "            Method exemptAll = bootstrapClass.getDeclaredMethod(\"exemptAll\");\n" +
        "            return (boolean) exemptAll.invoke(null);\n" +
        "        } catch (Throwable e) {\n" +
        "            e.printStackTrace();\n" +
        "            return false;\n" +
        "        } finally {\n" +
        "            if (code.exists()) {\n" +
        "                code.delete();\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    private static File getCodeCacheDir(Context context) {\n" +
        "        if (context != null) {\n" +
        "            return context.getCodeCacheDir();\n" +
        "        }\n" +
        "        String tmpDir = System.getProperty(\"java.io.tmpdir\");\n" +
        "        if (TextUtils.isEmpty(tmpDir)) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        File tmp = new File(tmpDir);\n" +
        "        if (!tmp.exists()) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        return tmp;\n" +
        "    }\n" +
        "}")
)