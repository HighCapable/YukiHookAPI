/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
}

/**
 * 类名常量定义类
 */
object ClassName {
    const val YukiHookAPI_Impl = "YukiHookAPI_Impl"
    const val ModuleApplication_Impl = "ModuleApplication_Impl"
    const val YukiXposedModuleStatus_Impl = "YukiXposedModuleStatus_Impl"
    const val YukiXposedModuleStatus_Impl_Impl = "YukiXposedModuleStatus_Impl_Impl"
    const val HandlerDelegateImpl_Impl = "HandlerDelegateImpl_Impl"
    const val HandlerDelegateClass = "HandlerDelegate"
    const val IActivityManagerProxyImpl_Impl = "IActivityManagerProxyImpl_Impl"
    const val IActivityManagerProxyClass = "IActivityManagerProxy"
    const val XposedInit = "xposed_init"
    const val XposedInit_Impl = "xposed_init_Impl"
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
      
    """.trimIndent() + "\n\n" + createCommentContent(ClassName.YukiXposedModuleStatus_Impl) + "\n" + """
      object ${ClassName.YukiXposedModuleStatus_Impl} {

          val className get() = "${PackageName.YukiXposedModuleStatus_Impl}.${tailPackageName(ClassName.YukiXposedModuleStatus_Impl_Impl)}"
      }
    """.trimIndent(),
    ClassName.YukiXposedModuleStatus_Impl_Impl to """
      @file:Suppress("ClassName")
      
      package ${PackageName.YukiXposedModuleStatus_Impl}
      
      import android.util.Log
      import androidx.annotation.Keep
    """.trimIndent() + "\n\n" + createCommentContent(ClassName.YukiXposedModuleStatus_Impl) + "\n" + """
      @Keep
      object ${tailPackageName(ClassName.YukiXposedModuleStatus_Impl_Impl)} {
      
          @JvmStatic
          @JvmName("${YukiXposedModuleStatusJvmName.IS_ACTIVE_METHOD_NAME}")
          fun function${(1000..99999).random()}(): Boolean {
              phe()
              return false
          }
      
          @JvmStatic
          @JvmName("${YukiXposedModuleStatusJvmName.IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME}")
          fun function${(1000..99999).random()}(): Boolean {
              phe()
              return false
          }
      
          @JvmStatic
          @JvmName("${YukiXposedModuleStatusJvmName.GET_EXECUTOR_NAME_METHOD_NAME}")
          fun function${(1000..99999).random()}(): String {
              phe()
              return "unknown"
          }
      
          @JvmStatic
          @JvmName("${YukiXposedModuleStatusJvmName.GET_EXECUTOR_API_LEVEL_METHOD_NAME}")
          fun function${(1000..99999).random()}(): Int {
              phe()
              return -1
          }
      
          @JvmStatic
          @JvmName("${YukiXposedModuleStatusJvmName.GET_EXECUTOR_VERSION_NAME_METHOD_NAME}")
          fun function${(1000..99999).random()}(): String {
              phe()
              return "unknown"
          }
      
          @JvmStatic
          @JvmName("${YukiXposedModuleStatusJvmName.GET_EXECUTOR_VERSION_CODE_METHOD_NAME}")
          fun function${(1000..99999).random()}(): Int {
              phe()
              return -1
          }
      
          @JvmStatic
          @JvmName("_${(1000..99999).random()}")
          private fun phe() {
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
    """.trimIndent() + "\n\n" + createCommentContent("Xposed Init Impl") + "\n" + """
      object ${entryClassName}_Impl {
      
          private const val MODULE_PACKAGE_NAME = "${customMPackageName.ifBlank { modulePackageName }}"
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
                      ${ExternalCallerName.YukiXposedModuleCaller.second}.callLogError("You cannot load a hooker in \"onInit\" or \"onXposedEvent\" method! Aborted")
                      return
                  }
                  hookEntry.onHook()
                  ${ExternalCallerName.YukiXposedModuleCaller.second}.callOnFinishLoadModule()
              }.onFailure { ${ExternalCallerName.YukiXposedModuleCaller.second}.callLogError("YukiHookAPI try to load hook entry class failed", it) }
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
                  ${ExternalCallerName.YukiXposedModuleCaller.second}.callOnStartLoadModule(MODULE_PACKAGE_NAME, sparam.modulePath)
                  callOnXposedModuleLoaded(isZygoteLoaded = true)
                  isZygoteCalled = true
              }.onFailure { ${ExternalCallerName.YukiXposedModuleCaller.second}.callLogError("An exception occurred when YukiHookAPI loading Xposed Module", it) }
          }
      
          fun callHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
              if (lpparam != null && isZygoteCalled) callOnXposedModuleLoaded(lpparam = lpparam)
          }
      
          fun callHandleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
              if (resparam != null && isZygoteCalled) callOnXposedModuleLoaded(resparam = resparam)
          }
      }
    """.trimIndent()
)