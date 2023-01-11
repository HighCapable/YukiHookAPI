/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
 * Copyright (C) 2019-2022 HighCapable
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
 * This file is Created by fankes on 2022/9/20.
 */
package com.highcapable.yukihookapi_ksp_xposed.factory

import com.highcapable.yukihookapi_ksp_xposed.bean.GenerateData
import java.text.SimpleDateFormat
import java.util.*

/**
 * 包名常量定义类
 */
object PackageName {
    const val YukiHookAPI_Impl = "com.highcapable.yukihookapi"
    const val ModuleApplication_Impl = "com.highcapable.yukihookapi.hook.xposed.application"
    const val YukiXposedModuleStatus_Impl = "com.highcapable.yukihookapi.hook.xposed.bridge.status"
    const val BootstrapReflectionClass = "com.highcapable.yukihookapi.thirdparty.me.weishu.reflection"
}

/**
 * 类名常量定义类
 */
object ClassName {
    const val YukiHookAPI_Impl = "YukiHookAPI_Impl"
    const val ModuleApplication_Impl = "ModuleApplication_Impl"
    const val YukiXposedModuleStatus_Impl = "YukiXposedModuleStatus_Impl"
    const val XposedInit = "xposed_init"
    const val XposedInit_Impl = "xposed_init_Impl"
    const val BootstrapClass = "BootstrapClass"
    const val Reflection = "Reflection"
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
 * 创建文件注释
 * @param entryClassName 入口类名 - 空则不生成
 * @param currrentClassTag 当前注入类标签
 * @return [String]
 */
private fun createCommentContent(entryClassName: String = "", currrentClassTag: String) =
    ("/**\n" +
            " * $currrentClassTag Class\n" +
            " *\n" +
            " * Compiled from YukiHookXposedProcessor\n" +
            " *\n" +
            (if (entryClassName.isNotBlank()) " * HookEntryClass: [$entryClassName]\n *\n" else "") +
            " * Generate Date: ${SimpleDateFormat.getDateTimeInstance().format(Date())}\n" +
            " *\n" +
            " * Powered by YukiHookAPI (C) HighCapable 2019-2022\n" +
            " *\n" +
            " * Project Address: [YukiHookAPI](https://github.com/fankes/YukiHookAPI)\n" +
            " */\n")

/**
 * 获得注入文件代码内容
 * @return [Map]<[String],[String]>
 */
fun GenerateData.sources() = mapOf(
    ClassName.YukiHookAPI_Impl to ("@file:Suppress(\"ClassName\")\n" +
            "\n" +
            "package ${PackageName.YukiHookAPI_Impl}\n" +
            "\n" +
            createCommentContent(currrentClassTag = ClassName.YukiHookAPI_Impl) +
            "object ${ClassName.YukiHookAPI_Impl} {\n" +
            "\n" +
            "    val compiledTimestamp get() = ${System.currentTimeMillis()}\n" +
            "}"),
    ClassName.ModuleApplication_Impl to ("@file:Suppress(\"ClassName\")\n" +
            "\n" +
            "package ${PackageName.ModuleApplication_Impl}\n" +
            "\n" +
            "import $entryPackageName.$entryClassName\n" +
            "\n" +
            createCommentContent(entryClassName, ClassName.ModuleApplication_Impl) +
            "object ${ClassName.ModuleApplication_Impl} {\n" +
            "\n" +
            "    fun callHookEntryInit() = try {\n" +
            "        " + (if (isEntryClassKindOfObject) "$entryClassName.onInit()\n" else "$entryClassName().onInit()\n") +
            "    } catch (_: Throwable) {\n" +
            "    }\n" +
            "}"),
    ClassName.YukiXposedModuleStatus_Impl to ("@file:Suppress(\"ClassName\")\n" +
            "\n" +
            "package ${PackageName.YukiXposedModuleStatus_Impl}\n" +
            "\n" +
            "import android.util.Log\n" +
            "import androidx.annotation.Keep\n" +
            "\n" +
            createCommentContent(currrentClassTag = ClassName.YukiXposedModuleStatus_Impl) +
            "@Keep\n" +
            "object ${ClassName.YukiXposedModuleStatus_Impl} {\n" +
            "\n" +
            "    @JvmName(\"${YukiXposedModuleStatusJvmName.IS_ACTIVE_METHOD_NAME}\")\n" +
            "    fun isActive(): Boolean {\n" +
            "        placeholderExecution()\n" +
            "        return false\n" +
            "    }\n" +
            "\n" +
            "    @JvmName(\"${YukiXposedModuleStatusJvmName.IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME}\")\n" +
            "    fun isSupportResourcesHook(): Boolean {\n" +
            "        placeholderExecution()\n" +
            "        return false\n" +
            "    }\n" +
            "\n" +
            "    @JvmName(\"${YukiXposedModuleStatusJvmName.GET_EXECUTOR_NAME_METHOD_NAME}\")\n" +
            "    fun getExecutorName(): String {\n" +
            "        placeholderExecution()\n" +
            "        return \"unknown\"\n" +
            "    }\n" +
            "\n" +
            "    @JvmName(\"${YukiXposedModuleStatusJvmName.GET_EXECUTOR_API_LEVEL_METHOD_NAME}\")\n" +
            "    fun getExecutorApiLevel(): Int {\n" +
            "        placeholderExecution()\n" +
            "        return -1\n" +
            "    }\n" +
            "\n" +
            "    @JvmName(\"${YukiXposedModuleStatusJvmName.GET_EXECUTOR_VERSION_NAME_METHOD_NAME}\")\n" +
            "    fun getExecutorVersionName(): String {\n" +
            "        placeholderExecution()\n" +
            "        return \"unknown\"\n" +
            "    }\n" +
            "\n" +
            "    @JvmName(\"${YukiXposedModuleStatusJvmName.GET_EXECUTOR_VERSION_CODE_METHOD_NAME}\")\n" +
            "    fun getExecutorVersionCode(): Int {\n" +
            "        placeholderExecution()\n" +
            "        return -1\n" +
            "    }\n" +
            "\n" +
            "    private fun placeholderExecution() {\n" +
            "        /** Consume a long method body */\n" +
            "        if (System.currentTimeMillis() == 0L) Log.d(\"${(1000..9999).random()}\", \"${(100000..999999).random()}\")\n" +
            "    }\n" +
            "}"),
    ClassName.XposedInit to ("@file:Suppress(\"ClassName\")\n" +
            "\n" +
            "package $entryPackageName\n" +
            "\n" +
            "import androidx.annotation.Keep\n" +
            "import com.highcapable.yukihookapi.hook.xposed.bridge.event.caller.YukiXposedEventCaller\n" +
            "import com.highcapable.yukihookapi.annotation.YukiGenerateApi\n" +
            (if (isUsingResourcesHook) "import de.robv.android.xposed.IXposedHookInitPackageResources\n" else "") +
            "import de.robv.android.xposed.IXposedHookLoadPackage\n" +
            "import de.robv.android.xposed.IXposedHookZygoteInit\n" +
            (if (isUsingResourcesHook) "import de.robv.android.xposed.callbacks.XC_InitPackageResources\n" else "") +
            "import de.robv.android.xposed.callbacks.XC_LoadPackage\n" +
            "\n" +
            createCommentContent(entryClassName, currrentClassTag = "Xposed Init") +
            "@Keep\n" +
            "@YukiGenerateApi\n" +
            "class $xInitClassName : IXposedHookZygoteInit, IXposedHookLoadPackage" +
            "${if (isUsingResourcesHook) ", IXposedHookInitPackageResources" else ""} {\n" +
            "\n" +
            "    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam?) {\n" +
            "        ${entryClassName}_Impl.callInitZygote(sparam)\n" +
            "        YukiXposedEventCaller.callInitZygote(sparam)\n" +
            "    }\n" +
            "\n" +
            "    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {\n" +
            "        ${entryClassName}_Impl.callHandleLoadPackage(lpparam)\n" +
            "        YukiXposedEventCaller.callHandleLoadPackage(lpparam)\n" +
            "    }\n" +
            (if (isUsingResourcesHook)
                ("\n    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {\n" +
                        "        ${entryClassName}_Impl.callHandleInitPackageResources(resparam)\n" +
                        "        YukiXposedEventCaller.callHandleInitPackageResources(resparam)\n" +
                        "    }\n") else "") +
            "}"),
    ClassName.XposedInit_Impl to ("@file:Suppress(\"ClassName\")\n" +
            "\n" +
            "package $entryPackageName\n" +
            "\n" +
            "import com.highcapable.yukihookapi.annotation.YukiGenerateApi\n" +
            "import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiXposedModuleCaller\n" +
            "import com.highcapable.yukihookapi.hook.xposed.bridge.resources.caller.YukiXposedResourcesCaller\n" +
            "import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType\n" +
            "import de.robv.android.xposed.IXposedHookZygoteInit\n" +
            "import de.robv.android.xposed.XposedBridge\n" +
            "import de.robv.android.xposed.callbacks.XC_InitPackageResources\n" +
            "import de.robv.android.xposed.callbacks.XC_LoadPackage\n" +
            (if (customMPackageName.isBlank()) "import $modulePackageName.BuildConfig\n" else "") +
            "\n" +
            createCommentContent(entryClassName, currrentClassTag = "Xposed Init Impl") +
            "@YukiGenerateApi\n" +
            "object ${entryClassName}_Impl {\n" +
            "\n" +
            "    private const val modulePackageName = " +
            (if (customMPackageName.isNotBlank()) "\"$customMPackageName\"" else "BuildConfig.APPLICATION_ID") + "\n" +
            "    private var isZygoteCalled = false\n" +
            "    private val hookEntry = " + (if (isEntryClassKindOfObject) "$entryClassName\n" else "$entryClassName()\n") +
            "\n" +
            "    private fun callOnXposedModuleLoaded(\n" +
            "        isZygoteLoaded: Boolean = false,\n" +
            "        lpparam: XC_LoadPackage.LoadPackageParam? = null,\n" +
            "        resparam: XC_InitPackageResources.InitPackageResourcesParam? = null\n" +
            "    ) {\n" +
            "        if (isZygoteCalled.not()) runCatching {\n" +
            "            hookEntry.onXposedEvent()\n" +
            "            hookEntry.onInit()\n" +
            "            if (YukiXposedModuleCaller.isXposedCallbackSetUp) {\n" +
            "                YukiXposedModuleCaller.internalLoggerE(\"You cannot load a hooker in \\\"onInit\\\" or \\\"onXposedEvent\\\" method! Aborted\")\n" +
            "                return\n" +
            "            }\n" +
            "            hookEntry.onHook()\n" +
            "            YukiXposedModuleCaller.callOnFinishLoadModule()\n" +
            "        }.onFailure { YukiXposedModuleCaller.internalLoggerE(\"YukiHookAPI try to load HookEntryClass failed\", it) }\n" +
            "        YukiXposedModuleCaller.callOnPackageLoaded(\n" +
            "            type = when {\n" +
            "                isZygoteLoaded -> HookEntryType.ZYGOTE\n" +
            "                lpparam != null -> HookEntryType.PACKAGE\n" +
            "                resparam != null -> HookEntryType.RESOURCES\n" +
            "                else -> HookEntryType.ZYGOTE\n" +
            "            },\n" +
            "            packageName = lpparam?.packageName ?: resparam?.packageName,\n" +
            "            processName = lpparam?.processName,\n" +
            "            appClassLoader = lpparam?.classLoader ?: runCatching { XposedBridge.BOOTCLASSLOADER }.getOrNull(),\n" +
            "            appInfo = lpparam?.appInfo,\n" +
            "            appResources = YukiXposedResourcesCaller.createYukiResourcesFromXResources(resparam?.res)\n" +
            "        )\n" +
            "    }\n" +
            "\n" +
            "    fun callInitZygote(sparam: IXposedHookZygoteInit.StartupParam?) {\n" +
            "        if (sparam == null) return\n" +
            "        runCatching {\n" +
            "            YukiXposedModuleCaller.callOnStartLoadModule(modulePackageName, sparam.modulePath)\n" +
            "            callOnXposedModuleLoaded(isZygoteLoaded = true)\n" +
            "            isZygoteCalled = true\n" +
            "        }.onFailure { YukiXposedModuleCaller.internalLoggerE(\"An exception occurred when YukiHookAPI loading Xposed Module\", it) }\n" +
            "    }\n" +
            "\n" +
            "    fun callHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {\n" +
            "        if (lpparam != null && isZygoteCalled) callOnXposedModuleLoaded(lpparam = lpparam)\n" +
            "    }\n" +
            "\n" +
            "    fun callHandleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {\n" +
            "        if (resparam != null && isZygoteCalled) callOnXposedModuleLoaded(resparam = resparam)\n" +
            "    }\n" +
            "}"),
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