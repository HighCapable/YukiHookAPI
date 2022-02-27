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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "KDocUnresolvedReference", "DEPRECATION", "NewApi")

package com.highcapable.yukihookapi.hook.type.android

import android.app.*
import android.content.*
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.*
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.*
import android.service.notification.StatusBarNotification
import android.text.SpannableStringBuilder
import android.util.*
import android.view.Display
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.highcapable.yukihookapi.hook.factory.classOf

/**
 * 获得 [android.R] 类型
 * @return [Class]
 */
val AndroidRClass get() = android.R::class.java

/**
 * 获得 [Context] 类型
 * @return [Class]
 */
val ContextClass get() = Context::class.java

/**
 * 获得 [ContextImpl] 类型
 * @return [Class]
 */
val ContextImplClass get() = classOf(name = "android.app.ContextImpl")

/**
 * 获得 [ContextWrapper] 类型
 * @return [Class]
 */
val ContextWrapperClass get() = ContextWrapper::class.java

/**
 * 获得 [Application] 类型
 * @return [Class]
 */
val ApplicationClass get() = Application::class.java

/**
 * 获得 [ApplicationPackageManager] 类型
 * @return [Class]
 */
val ApplicationPackageManagerClass get() = classOf(name = "android.app.ApplicationPackageManager")

/**
 * 获得 [ActivityThread] 类型
 * @return [Class]
 */
val ActivityThreadClass get() = classOf(name = "android.app.ActivityThread")

/**
 * 获得 [IPackageManager] 类型
 * @return [Class]
 */
val IPackageManagerClass get() = classOf(name = "android.content.pm.IPackageManager")

/**
 * 获得 [LoadedApk] 类型
 * @return [Class]
 */
val LoadedApkClass get() = classOf(name = "android.app.LoadedApk")

/**
 * 获得 [Activity] 类型
 * @return [Class]
 */
val ActivityClass get() = Activity::class.java

/**
 * 获得 [Looper] 类型
 * @return [Class]
 */
val LooperClass get() = Looper::class.java

/**
 * 获得 [Fragment] 类型 - Support
 * @return [Class]
 */
val FragmentClass_AndroidSupport get() = classOf(name = "android.support.v4.app.Fragment")

/**
 * 获得 [Fragment] 类型 - AndroidX
 * @return [Class]
 */
val FragmentClass_AndroidX get() = classOf(name = "androidx.fragment.app.Fragment")

/**
 * 获得 [FragmentActivity] 类型 - Support
 * @return [Class]
 */
val FragmentActivityClass_AndroidSupport get() = classOf(name = "android.support.v4.app.FragmentActivity")

/**
 * 获得 [FragmentActivity] 类型 - AndroidX
 * @return [Class]
 */
val FragmentActivityClass_AndroidX get() = classOf(name = "androidx.fragment.app.FragmentActivity")

/**
 * 获得 [DocumentFile] 类型 - AndroidX
 * @return [Class]
 */
val DocumentFileClass get() = classOf(name = "androidx.documentfile.provider.DocumentFile")

/**
 * 获得 [Service] 类型
 * @return [Class]
 */
val ServiceClass get() = Service::class.java

/**
 * 获得 [Binder] 类型
 * @return [Class]
 */
val BinderClass get() = Binder::class.java

/**
 * 获得 [IBinder] 类型
 * @return [Class]
 */
val IBinderClass get() = IBinder::class.java

/**
 * 获得 [BroadcastReceiver] 类型
 * @return [Class]
 */
val BroadcastReceiverClass get() = BroadcastReceiver::class.java

/**
 * 获得 [Bundle] 类型
 * @return [Class]
 */
val BundleClass get() = Bundle::class.java

/**
 * 获得 [BaseBundle] 类型
 * @return [Class]
 */
val BaseBundleClass get() = BaseBundle::class.java

/**
 * 获得 [Resources] 类型
 * @return [Class]
 */
val ResourcesClass get() = Resources::class.java

/**
 * 获得 [ContentResolver] 类型
 * @return [Class]
 */
val ContentResolverClass get() = ContentResolver::class.java

/**
 * 获得 [ContentProvider] 类型
 * @return [Class]
 */
val ContentProviderClass get() = ContentProvider::class.java

/**
 * 获得 [ArrayMap] 类型
 * @return [Class]
 */
val ArrayMapClass get() = ArrayMap::class.java

/**
 * 获得 [ArraySet] 类型
 * @return [Class]
 *
 * ❗在 Android M (23) 及以上系统加入
 */
val ArraySetClass get() = ArraySet::class.java

/**
 * 获得 [Handler] 类型
 * @return [Class]
 */
val HandlerClass get() = Handler::class.java

/**
 * 获得 [Handler.Callback] 类型
 * @return [Class]
 */
val Handler_CallbackClass get() = Handler.Callback::class.java

/**
 * 获得 [Message] 类型
 * @return [Class]
 */
val MessageClass get() = Message::class.java

/**
 * 获得 [MessageQueue] 类型
 * @return [Class]
 */
val MessageQueueClass get() = MessageQueue::class.java

/**
 * 获得 [Messenger] 类型
 * @return [Class]
 */
val MessengerClass get() = Messenger::class.java

/**
 * 获得 [AsyncTask] 类型
 * @return [Class]
 */
val AsyncTaskClass get() = AsyncTask::class.java

/**
 * 获得 [SimpleDateFormat] 类型
 *
 * ❗在 Android N (24) 及以上系统加入
 * @return [Class]
 */
val SimpleDateFormatClass_Android get() = SimpleDateFormat::class.java

/**
 * 获得 [Base64] 类型
 * @return [Class]
 */
val Base64Class_Android get() = Base64::class.java

/**
 * 获得 [Window] 类型
 * @return [Class]
 */
val WindowClass get() = Window::class.java

/**
 * 获得 [WindowManager] 类型
 * @return [Class]
 */
val WindowManagerClass get() = WindowManager::class.java

/**
 * 获得 [Parcel] 类型
 * @return [Class]
 */
val ParcelClass get() = Parcel::class.java

/**
 * 获得 [Parcelable] 类型
 * @return [Class]
 */
val ParcelableClass get() = Parcelable::class.java

/**
 * 获得 [Parcelable.Creator] 类型
 * @return [Class]
 */
val Parcelable_CreatorClass get() = Parcelable.Creator::class.java

/**
 * 获得 [Dialog] 类型
 * @return [Class]
 */
val DialogClass get() = Dialog::class.java

/**
 * 获得 [AlertDialog] 类型
 * @return [Class]
 */
val AlertDialogClass get() = AlertDialog::class.java

/**
 * 获得 [DisplayMetrics] 类型
 * @return [Class]
 */
val DisplayMetricsClass get() = DisplayMetrics::class.java

/**
 * 获得 [Display] 类型
 * @return [Class]
 */
val DisplayClass get() = Display::class.java

/**
 * 获得 [Toast] 类型
 * @return [Class]
 */
val ToastClass get() = Toast::class.java

/**
 * 获得 [Intent] 类型
 * @return [Class]
 */
val IntentClass get() = Intent::class.java

/**
 * 获得 [PendingIntent] 类型
 * @return [Class]
 */
val PendingIntentClass get() = PendingIntent::class.java

/**
 * 获得 [ColorStateList] 类型
 * @return [Class]
 */
val ColorStateListClass get() = ColorStateList::class.java

/**
 * 获得 [ContentValues] 类型
 * @return [Class]
 */
val ContentValuesClass get() = ContentValues::class.java

/**
 * 获得 [SharedPreferences] 类型
 * @return [Class]
 */
val SharedPreferencesClass get() = SharedPreferences::class.java

/**
 * 获得 [SpannableStringBuilder] 类型
 * @return [Class]
 */
val SpannableStringBuilderClass get() = SpannableStringBuilder::class.java

/**
 * 获得 [MediaPlayer] 类型
 * @return [Class]
 */
val MediaPlayerClass get() = MediaPlayer::class.java

/**
 * 获得 [ProgressDialog] 类型
 * @return [Class]
 */
val ProgressDialogClass get() = ProgressDialog::class.java

/**
 * 获得 [Log] 类型
 * @return [Class]
 */
val LogClass get() = Log::class.java

/**
 * 获得 [Build] 类型
 * @return [Class]
 */
val BuildClass get() = Build::class.java

/**
 * 获得 [Xml] 类型
 * @return [Class]
 */
val XmlClass get() = Xml::class.java

/**
 * 获得 [ContrastColorUtil] 类型
 * @return [Class]
 */
val ContrastColorUtilClass get() = classOf(name = "com.android.internal.util.ContrastColorUtil")

/**
 * 获得 [StatusBarNotification] 类型
 * @return [Class]
 */
val StatusBarNotificationClass get() = StatusBarNotification::class.java

/**
 * 获得 [Notification] 类型
 * @return [Class]
 */
val NotificationClass get() = Notification::class.java

/**
 * 获得 [Notification.Builder] 类型
 * @return [Class]
 */
val Notification_BuilderClass get() = Notification.Builder::class.java

/**
 * 获得 [DialogInterface] 类型
 * @return [Class]
 */
val DialogInterfaceClass get() = DialogInterface::class.java

/**
 * 获得 [DialogInterface.OnClickListener] 类型
 * @return [Class]
 */
val DialogInterface_OnClickListenerClass get() = DialogInterface.OnClickListener::class.java

/**
 * 获得 [DialogInterface.OnCancelListener] 类型
 * @return [Class]
 */
val DialogInterface_OnCancelListenerClass get() = DialogInterface.OnCancelListener::class.java

/**
 * 获得 [DialogInterface.OnDismissListener] 类型
 * @return [Class]
 */
val DialogInterface_OnDismissListenerClass get() = DialogInterface.OnDismissListener::class.java

/**
 * 获得 [Environment] 类型
 * @return [Class]
 */
val EnvironmentClass get() = Environment::class.java

/**
 * 获得 [Process] 类型
 * @return [Class]
 */
val ProcessClass get() = Process::class.java

/**
 * 获得 [Vibrator] 类型
 * @return [Class]
 */
val VibratorClass get() = Vibrator::class.java

/**
 * 获得 [VibrationEffect] 类型
 *
 * ❗在 Android O (26) 及以上系统加入
 * @return [Class]
 */
val VibrationEffectClass get() = VibrationEffect::class.java

/**
 * 获得 [VibrationAttributes] 类型
 *
 * ❗在 Android R (30) 及以上系统加入
 * @return [Class]
 */
val VibrationAttributesClass get() = VibrationAttributes::class.java

/**
 * 获得 [SystemClock] 类型
 * @return [Class]
 */
val SystemClockClass get() = SystemClock::class.java

/**
 * 获得 [PowerManager] 类型
 * @return [Class]
 */
val PowerManagerClass get() = PowerManager::class.java

/**
 * 获得 [PowerManager.WakeLock] 类型
 * @return [Class]
 */
val PowerManager_WakeLockClass get() = PowerManager.WakeLock::class.java