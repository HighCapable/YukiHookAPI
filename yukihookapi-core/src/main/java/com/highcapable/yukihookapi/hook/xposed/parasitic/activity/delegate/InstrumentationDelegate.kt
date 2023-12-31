/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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
 * This file is created by fankes on 2022/8/8.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.app.UiAutomation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.os.PersistableBundle
import android.os.TestLooperManager
import android.view.KeyEvent
import android.view.MotionEvent
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.processName
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics

/**
 * 代理当前 [Instrumentation]
 * @param baseInstance 原始实例
 */
internal class InstrumentationDelegate private constructor(private val baseInstance: Instrumentation) : Instrumentation() {

    internal companion object {

        /**
         * 从 [Instrumentation] 创建 [InstrumentationDelegate] 实例
         * @param baseInstance [Instrumentation] 实例
         * @return [InstrumentationDelegate]
         */
        internal fun wrapper(baseInstance: Instrumentation) = InstrumentationDelegate(baseInstance)
    }

    /**
     * 注入当前 [Activity] 生命周期
     * @param icicle [Bundle]
     */
    private fun Activity.injectLifecycle(icicle: Bundle?) {
        if (icicle != null && current().name.startsWith(YukiXposedModule.modulePackageName))
            icicle.classLoader = AppParasitics.baseClassLoader
        if (current().name.startsWith(YukiXposedModule.modulePackageName)) injectModuleAppResources()
    }

    override fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity? = try {
        baseInstance.newActivity(cl, className, intent)
    } catch (e: Throwable) {
        if (className?.startsWith(YukiXposedModule.modulePackageName) == true)
            className.toClass().buildOf<Activity>() ?: throw e
        else throw e
    }

    override fun onCreate(arguments: Bundle?) {
        baseInstance.onCreate(arguments)
    }

    override fun start() {
        baseInstance.start()
    }

    override fun onStart() {
        baseInstance.onStart()
    }

    override fun onException(obj: Any?, e: Throwable?) = baseInstance.onException(obj, e)

    override fun sendStatus(resultCode: Int, results: Bundle?) {
        baseInstance.sendStatus(resultCode, results)
    }

    override fun addResults(results: Bundle?) {
        if (Build.VERSION.SDK_INT >= 26) baseInstance.addResults(results)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        baseInstance.finish(resultCode, results)
    }

    override fun setAutomaticPerformanceSnapshots() {
        baseInstance.setAutomaticPerformanceSnapshots()
    }

    override fun startPerformanceSnapshot() {
        baseInstance.startPerformanceSnapshot()
    }

    override fun endPerformanceSnapshot() {
        baseInstance.endPerformanceSnapshot()
    }

    override fun onDestroy() {
        baseInstance.onDestroy()
    }

    override fun getContext(): Context? = baseInstance.context

    override fun getComponentName(): ComponentName? = baseInstance.componentName

    override fun getTargetContext(): Context? = baseInstance.targetContext

    override fun getProcessName(): String? =
        if (Build.VERSION.SDK_INT >= 26) baseInstance.processName else AppParasitics.systemContext.processName

    override fun isProfiling() = baseInstance.isProfiling

    override fun startProfiling() {
        baseInstance.startProfiling()
    }

    override fun stopProfiling() {
        baseInstance.stopProfiling()
    }

    override fun setInTouchMode(inTouch: Boolean) {
        baseInstance.setInTouchMode(inTouch)
    }

    override fun waitForIdle(recipient: Runnable?) {
        baseInstance.waitForIdle(recipient)
    }

    override fun waitForIdleSync() {
        baseInstance.waitForIdleSync()
    }

    override fun runOnMainSync(runner: Runnable?) {
        baseInstance.runOnMainSync(runner)
    }

    override fun startActivitySync(intent: Intent?): Activity? = baseInstance.startActivitySync(intent)

    override fun startActivitySync(intent: Intent, options: Bundle?): Activity =
        if (Build.VERSION.SDK_INT >= 28) baseInstance.startActivitySync(intent, options) else error("Operating system not supported")

    override fun addMonitor(monitor: ActivityMonitor?) {
        baseInstance.addMonitor(monitor)
    }

    override fun addMonitor(cls: String?, result: ActivityResult?, block: Boolean): ActivityMonitor? =
        baseInstance.addMonitor(cls, result, block)

    override fun addMonitor(filter: IntentFilter?, result: ActivityResult?, block: Boolean): ActivityMonitor? =
        baseInstance.addMonitor(filter, result, block)

    override fun checkMonitorHit(monitor: ActivityMonitor?, minHits: Int) = baseInstance.checkMonitorHit(monitor, minHits)

    override fun waitForMonitor(monitor: ActivityMonitor?): Activity? = baseInstance.waitForMonitor(monitor)

    override fun waitForMonitorWithTimeout(monitor: ActivityMonitor?, timeOut: Long): Activity? =
        baseInstance.waitForMonitorWithTimeout(monitor, timeOut)

    override fun removeMonitor(monitor: ActivityMonitor?) {
        baseInstance.removeMonitor(monitor)
    }

    override fun invokeContextMenuAction(targetActivity: Activity?, id: Int, flag: Int) =
        baseInstance.invokeContextMenuAction(targetActivity, id, flag)

    override fun invokeMenuActionSync(targetActivity: Activity?, id: Int, flag: Int) =
        baseInstance.invokeMenuActionSync(targetActivity, id, flag)

    override fun sendCharacterSync(keyCode: Int) {
        baseInstance.sendCharacterSync(keyCode)
    }

    override fun sendKeyDownUpSync(key: Int) {
        baseInstance.sendKeyDownUpSync(key)
    }

    override fun sendKeySync(event: KeyEvent?) {
        baseInstance.sendKeySync(event)
    }

    override fun sendPointerSync(event: MotionEvent?) {
        baseInstance.sendPointerSync(event)
    }

    override fun sendStringSync(text: String?) {
        baseInstance.sendStringSync(text)
    }

    override fun sendTrackballEventSync(event: MotionEvent?) {
        baseInstance.sendTrackballEventSync(event)
    }

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application? =
        baseInstance.newApplication(cl, className, context)

    override fun callApplicationOnCreate(app: Application?) {
        baseInstance.callApplicationOnCreate(app)
    }

    override fun newActivity(
        clazz: Class<*>?, context: Context?,
        token: IBinder?, application: Application?,
        intent: Intent?, info: ActivityInfo?,
        title: CharSequence?, parent: Activity?,
        id: String?, lastNonConfigurationInstance: Any?
    ): Activity? = baseInstance.newActivity(
        clazz, context,
        token, application,
        intent, info, title,
        parent, id, lastNonConfigurationInstance
    )

    override fun callActivityOnCreate(activity: Activity, icicle: Bundle?, persistentState: PersistableBundle?) {
        activity.injectLifecycle(icicle)
        baseInstance.callActivityOnCreate(activity, icicle, persistentState)
    }

    override fun callActivityOnCreate(activity: Activity, icicle: Bundle?) {
        activity.injectLifecycle(icicle)
        baseInstance.callActivityOnCreate(activity, icicle)
    }

    override fun callActivityOnDestroy(activity: Activity?) {
        baseInstance.callActivityOnDestroy(activity)
    }

    override fun callActivityOnRestoreInstanceState(activity: Activity, savedInstanceState: Bundle) {
        baseInstance.callActivityOnRestoreInstanceState(activity, savedInstanceState)
    }

    override fun callActivityOnRestoreInstanceState(activity: Activity, savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        baseInstance.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState)
    }

    override fun callActivityOnPostCreate(activity: Activity, savedInstanceState: Bundle?) {
        baseInstance.callActivityOnPostCreate(activity, savedInstanceState)
    }

    override fun callActivityOnPostCreate(activity: Activity, savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        baseInstance.callActivityOnPostCreate(activity, savedInstanceState, persistentState)
    }

    override fun callActivityOnNewIntent(activity: Activity?, intent: Intent?) {
        baseInstance.callActivityOnNewIntent(activity, intent)
    }

    override fun callActivityOnStart(activity: Activity?) {
        baseInstance.callActivityOnStart(activity)
    }

    override fun callActivityOnRestart(activity: Activity?) {
        baseInstance.callActivityOnRestart(activity)
    }

    override fun callActivityOnPause(activity: Activity?) {
        baseInstance.callActivityOnPause(activity)
    }

    override fun callActivityOnResume(activity: Activity?) {
        baseInstance.callActivityOnResume(activity)
    }

    override fun callActivityOnStop(activity: Activity?) {
        baseInstance.callActivityOnStop(activity)
    }

    override fun callActivityOnUserLeaving(activity: Activity?) {
        baseInstance.callActivityOnUserLeaving(activity)
    }

    override fun callActivityOnSaveInstanceState(activity: Activity, outState: Bundle) {
        baseInstance.callActivityOnSaveInstanceState(activity, outState)
    }

    override fun callActivityOnSaveInstanceState(activity: Activity, outState: Bundle, outPersistentState: PersistableBundle) {
        baseInstance.callActivityOnSaveInstanceState(activity, outState, outPersistentState)
    }

    override fun callActivityOnPictureInPictureRequested(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 30) baseInstance.callActivityOnPictureInPictureRequested(activity)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun startAllocCounting() {
        baseInstance.startAllocCounting()
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun stopAllocCounting() {
        baseInstance.stopAllocCounting()
    }

    override fun getAllocCounts(): Bundle? = baseInstance.allocCounts

    override fun getBinderCounts(): Bundle? = baseInstance.binderCounts

    override fun getUiAutomation(): UiAutomation? = baseInstance.uiAutomation

    override fun getUiAutomation(flags: Int): UiAutomation? =
        if (Build.VERSION.SDK_INT >= 24) baseInstance.getUiAutomation(flags) else error("Operating system not supported")

    override fun acquireLooperManager(looper: Looper?): TestLooperManager? =
        if (Build.VERSION.SDK_INT >= 26) baseInstance.acquireLooperManager(looper) else error("Operating system not supported")
}