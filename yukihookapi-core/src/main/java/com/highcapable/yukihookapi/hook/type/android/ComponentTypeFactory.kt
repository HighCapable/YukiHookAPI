/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("DEPRECATION", "KDocUnresolvedReference", "ktlint:standard:no-wildcard-imports", "unused", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.type.android

import android.app.*
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.appwidget.AppWidgetProviderInfo
import android.content.*
import android.content.Intent.ShortcutIconResource
import android.content.pm.*
import android.content.pm.LauncherApps.ShortcutQuery
import android.content.res.*
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.*
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.*
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.util.*
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.factory.toClassOrNull

/**
 * 获得 [android.R] 类型
 * @return [Class]<[android.R]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AndroidRClass get() = classOf<android.R>()

/**
 * 获得 [Context] 类型
 * @return [Class]<[Context]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContextClass get() = classOf<Context>()

/**
 * 获得 [ContextImpl] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContextImplClass get() = "android.app.ContextImpl".toClass()

/**
 * 获得 [ContextWrapper] 类型
 * @return [Class]<[ContextWrapper]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContextWrapperClass get() = classOf<ContextWrapper>()

/**
 * 获得 [Application] 类型
 * @return [Class]<[Application]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ApplicationClass get() = classOf<Application>()

/**
 * 获得 [ApplicationInfo] 类型
 * @return [Class]<[ApplicationInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ApplicationInfoClass get() = classOf<ApplicationInfo>()

/**
 * 获得 [Instrumentation] 类型
 * @return [Class]<[Instrumentation]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val InstrumentationClass get() = classOf<Instrumentation>()

/**
 * 获得 [PackageInfo] 类型
 * @return [Class]<[PackageInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PackageInfoClass get() = classOf<PackageInfo>()

/**
 * 获得 [ApplicationPackageManager] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ApplicationPackageManagerClass get() = "android.app.ApplicationPackageManager".toClass()

/**
 * 获得 [ActivityThread] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityThreadClass get() = "android.app.ActivityThread".toClass()

/**
 * 获得 [ActivityManager] 类型
 * @return [Class]<[ActivityManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityManagerClass get() = classOf<ActivityManager>()

/**
 * 获得 [IActivityManager] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IActivityManagerClass get() = "android.app.IActivityManager".toClass()

/**
 * 获得 [ActivityManagerNative] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityManagerNativeClass get() = "android.app.ActivityManagerNative".toClass()

/**
 * 获得 [IActivityTaskManager] 类型
 *
 * - 在 Android O (26) 及以上系统加入
 * @return [Class] or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IActivityTaskManagerClass get() = "android.app.IActivityTaskManager".toClassOrNull()

/**
 * 获得 [ActivityTaskManager] 类型
 *
 * - 在 Android O (26) 及以上系统加入
 * @return [Class] or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityTaskManagerClass get() = "android.app.ActivityTaskManager".toClassOrNull()

/**
 * 获得 [IPackageManager] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IPackageManagerClass get() = "android.content.pm.IPackageManager".toClass()

/**
 * 获得 [ClientTransaction] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ClientTransactionClass get() = "android.app.servertransaction.ClientTransaction".toClass()

/**
 * 获得 [LoadedApk] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LoadedApkClass get() = "android.app.LoadedApk".toClass()

/**
 * 获得 [Singleton] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SingletonClass get() = "android.util.Singleton".toClass()

/**
 * 获得 [Activity] 类型
 * @return [Class]<[Activity]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityClass get() = classOf<Activity>()

/**
 * 获得 [Looper] 类型
 * @return [Class]<[Looper]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LooperClass get() = classOf<Looper>()

/**
 * 获得 [Fragment] 类型 - Support
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FragmentClass_AndroidSupport get() = "android.support.v4.app.Fragment".toClass()

/**
 * 获得 [Fragment] 类型 - AndroidX
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FragmentClass_AndroidX get() = "androidx.fragment.app.Fragment".toClass()

/**
 * 获得 [FragmentActivity] 类型 - Support
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FragmentActivityClass_AndroidSupport get() = "android.support.v4.app.FragmentActivity".toClass()

/**
 * 获得 [FragmentActivity] 类型 - AndroidX
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FragmentActivityClass_AndroidX get() = "androidx.fragment.app.FragmentActivity".toClass()

/**
 * 获得 [DocumentFile] 类型 - AndroidX
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DocumentFileClass get() = "androidx.documentfile.provider.DocumentFile".toClass()

/**
 * 获得 [Service] 类型
 * @return [Class]<[Service]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ServiceClass get() = classOf<Service>()

/**
 * 获得 [Binder] 类型
 * @return [Class]<[Binder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BinderClass get() = classOf<Binder>()

/**
 * 获得 [IBinder] 类型
 * @return [Class]<[IBinder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IBinderClass get() = classOf<IBinder>()

/**
 * 获得 [BroadcastReceiver] 类型
 * @return [Class]<[BroadcastReceiver]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BroadcastReceiverClass get() = classOf<BroadcastReceiver>()

/**
 * 获得 [Bundle] 类型
 * @return [Class]<[Bundle]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BundleClass get() = classOf<Bundle>()

/**
 * 获得 [BaseBundle] 类型
 * @return [Class]<[BaseBundle]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BaseBundleClass get() = classOf<BaseBundle>()

/**
 * 获得 [Resources] 类型
 * @return [Class]<[Resources]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ResourcesClass get() = classOf<Resources>()

/**
 * 获得 [Configuration] 类型
 * @return [Class]<[Configuration]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ConfigurationClass get() = classOf<Configuration>()

/**
 * 获得 [ConfigurationInfo] 类型
 * @return [Class]<[ConfigurationInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ConfigurationInfoClass get() = classOf<ConfigurationInfo>()

/**
 * 获得 [ContentResolver] 类型
 * @return [Class]<[ContentResolver]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContentResolverClass get() = classOf<ContentResolver>()

/**
 * 获得 [ContentProvider] 类型
 * @return [Class]<[ContentProvider]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContentProviderClass get() = classOf<ContentProvider>()

/**
 * 获得 [Settings] 类型
 * @return [Class]<[Settings]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SettingsClass get() = classOf<Settings>()

/**
 * 获得 [Settings.System] 类型
 * @return [Class]<[Settings.System]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Settings_SystemClass get() = classOf<Settings.System>()

/**
 * 获得 [Settings.Secure] 类型
 * @return [Class]<[Settings.Secure]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Settings_SecureClass get() = classOf<Settings.Secure>()

/**
 * 获得 [TypedArray] 类型
 * @return [Class]<[TypedArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TypedArrayClass get() = classOf<TypedArray>()

/**
 * 获得 [TypedValue] 类型
 * @return [Class]<[TypedValue]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TypedValueClass get() = classOf<TypedValue>()

/**
 * 获得 [SparseArray] 类型
 * @return [Class]<[SparseArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SparseArrayClass get() = classOf<SparseArray<*>>()

/**
 * 获得 [SparseIntArray] 类型
 * @return [Class]<[SparseIntArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SparseIntArrayClass get() = classOf<SparseIntArray>()

/**
 * 获得 [SparseBooleanArray] 类型
 * @return [Class]<[SparseBooleanArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SparseBooleanArrayClass get() = classOf<SparseBooleanArray>()

/**
 * 获得 [SparseLongArray] 类型
 * @return [Class]<[SparseLongArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SparseLongArrayClass get() = classOf<SparseLongArray>()

/**
 * 获得 [LongSparseArray] 类型
 * @return [Class]<[LongSparseArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongSparseArrayClass get() = classOf<LongSparseArray<*>>()

/**
 * 获得 [ArrayMap] 类型
 * @return [Class]<[ArrayMap]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ArrayMapClass get() = classOf<ArrayMap<*, *>>()

/**
 * 获得 [ArraySet] 类型
 *
 * - 在 Android M (23) 及以上系统加入
 * @return [Class]<[ArraySet]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ArraySetClass get() = if (Build.VERSION.SDK_INT >= 23) classOf<ArraySet<*>>() else null

/**
 * 获得 [Handler] 类型
 * @return [Class]<[Handler]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HandlerClass get() = classOf<Handler>()

/**
 * 获得 [Handler.Callback] 类型
 * @return [Class]<[Handler.Callback]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Handler_CallbackClass get() = classOf<Handler.Callback>()

/**
 * 获得 [Message] 类型
 * @return [Class]<[Message]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MessageClass get() = classOf<Message>()

/**
 * 获得 [MessageQueue] 类型
 * @return [Class]<[MessageQueue]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MessageQueueClass get() = classOf<MessageQueue>()

/**
 * 获得 [Messenger] 类型
 * @return [Class]<[Messenger]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MessengerClass get() = classOf<Messenger>()

/**
 * 获得 [AsyncTask] 类型
 * @return [Class]<[AsyncTask]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AsyncTaskClass get() = classOf<AsyncTask<*, *, *>>()

/**
 * 获得 [SimpleDateFormat] 类型
 *
 * - 在 Android N (24) 及以上系统加入
 * @return [Class]<[SimpleDateFormat]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SimpleDateFormatClass_Android get() = if (Build.VERSION.SDK_INT >= 24) classOf<SimpleDateFormat>() else null

/**
 * 获得 [Base64] 类型
 * @return [Class]<[Base64]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Base64Class_Android get() = classOf<Base64>()

/**
 * 获得 [Window] 类型
 * @return [Class]<[Window]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowClass get() = classOf<Window>()

/**
 * 获得 [WindowMetrics] 类型
 *
 * - 在 Android R (30) 及以上系统加入
 * @return [Class]<[WindowMetrics]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowMetricsClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<WindowMetrics>() else null

/**
 * 获得 [WindowInsets] 类型
 * @return [Class]<[WindowInsets]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowInsetsClass get() = classOf<WindowInsets>()

/**
 * 获得 [WindowInsets.Type] 类型
 *
 * - 在 Android R (30) 及以上系统加入
 * @return [Class]<[WindowInsets.Type]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowInsets_TypeClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<WindowInsets.Type>() else null

/**
 * 获得 [WindowManager] 类型
 * @return [Class]<[WindowManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowManagerClass get() = classOf<WindowManager>()

/**
 * 获得 [WindowManager.LayoutParams] 类型
 * @return [Class]<[WindowManager.LayoutParams]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowManager_LayoutParamsClass get() = classOf<WindowManager.LayoutParams>()

/**
 * 获得 [ViewManager] 类型
 * @return [Class]<[ViewManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewManagerClass get() = classOf<ViewManager>()

/**
 * 获得 [Parcel] 类型
 * @return [Class]<[Parcel]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ParcelClass get() = classOf<Parcel>()

/**
 * 获得 [Parcelable] 类型
 * @return [Class]<[Parcelable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ParcelableClass get() = classOf<Parcelable>()

/**
 * 获得 [Parcelable.Creator] 类型
 * @return [Class]<[Parcelable.Creator]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Parcelable_CreatorClass get() = classOf<Parcelable.Creator<*>>()

/**
 * 获得 [Dialog] 类型
 * @return [Class]<[Dialog]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogClass get() = classOf<Dialog>()

/**
 * 获得 [AlertDialog] 类型
 * @return [Class]<[AlertDialog]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AlertDialogClass get() = classOf<AlertDialog>()

/**
 * 获得 [DisplayMetrics] 类型
 * @return [Class]<[DisplayMetrics]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DisplayMetricsClass get() = classOf<DisplayMetrics>()

/**
 * 获得 [Display] 类型
 * @return [Class]<[Display]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DisplayClass get() = classOf<Display>()

/**
 * 获得 [Toast] 类型
 * @return [Class]<[Toast]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ToastClass get() = classOf<Toast>()

/**
 * 获得 [Intent] 类型
 * @return [Class]<[Intent]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntentClass get() = classOf<Intent>()

/**
 * 获得 [ComponentInfo] 类型
 * @return [Class]<[ComponentInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ComponentInfoClass get() = classOf<ComponentInfo>()

/**
 * 获得 [ComponentName] 类型
 * @return [Class]<[ComponentName]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ComponentNameClass get() = classOf<ComponentName>()

/**
 * 获得 [PendingIntent] 类型
 * @return [Class]<[PendingIntent]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PendingIntentClass get() = classOf<PendingIntent>()

/**
 * 获得 [ColorStateList] 类型
 * @return [Class]<[ColorStateList]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ColorStateListClass get() = classOf<ColorStateList>()

/**
 * 获得 [ContentValues] 类型
 * @return [Class]<[ContentValues]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContentValuesClass get() = classOf<ContentValues>()

/**
 * 获得 [SharedPreferences] 类型
 * @return [Class]<[SharedPreferences]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SharedPreferencesClass get() = classOf<SharedPreferences>()

/**
 * 获得 [MediaPlayer] 类型
 * @return [Class]<[MediaPlayer]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MediaPlayerClass get() = classOf<MediaPlayer>()

/**
 * 获得 [ProgressDialog] 类型
 * @return [Class]<[ProgressDialog]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ProgressDialogClass get() = classOf<ProgressDialog>()

/**
 * 获得 [Log] 类型
 * @return [Class]<[Log]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LogClass get() = classOf<Log>()

/**
 * 获得 [Build] 类型
 * @return [Class]<[Build]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BuildClass get() = classOf<Build>()

/**
 * 获得 [Xml] 类型
 * @return [Class]<[Xml]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val XmlClass get() = classOf<Xml>()

/**
 * 获得 [ContrastColorUtil] 类型
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContrastColorUtilClass get() = "com.android.internal.util.ContrastColorUtil".toClass()

/**
 * 获得 [StatusBarNotification] 类型
 * @return [Class]<[StatusBarNotification]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StatusBarNotificationClass get() = classOf<StatusBarNotification>()

/**
 * 获得 [Notification] 类型
 * @return [Class]<[Notification]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NotificationClass get() = classOf<Notification>()

/**
 * 获得 [Notification.Builder] 类型
 * @return [Class]<[Notification.Builder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Notification_BuilderClass get() = classOf<Notification.Builder>()

/**
 * 获得 [Notification.Action] 类型
 * @return [Class]<[Notification.Action]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Notification_ActionClass get() = classOf<Notification.Action>()

/**
 * 获得 [DialogInterface] 类型
 * @return [Class]<[DialogInterface]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogInterfaceClass get() = classOf<DialogInterface>()

/**
 * 获得 [DialogInterface.OnClickListener] 类型
 * @return [Class]<[DialogInterface.OnClickListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogInterface_OnClickListenerClass get() = classOf<DialogInterface.OnClickListener>()

/**
 * 获得 [DialogInterface.OnCancelListener] 类型
 * @return [Class]<[DialogInterface.OnCancelListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogInterface_OnCancelListenerClass get() = classOf<DialogInterface.OnCancelListener>()

/**
 * 获得 [DialogInterface.OnDismissListener] 类型
 * @return [Class]<[DialogInterface.OnDismissListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogInterface_OnDismissListenerClass get() = classOf<DialogInterface.OnDismissListener>()

/**
 * 获得 [Environment] 类型
 * @return [Class]<[Environment]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val EnvironmentClass get() = classOf<Environment>()

/**
 * 获得 [Process] 类型
 * @return [Class]<[Process]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ProcessClass get() = classOf<Process>()

/**
 * 获得 [Vibrator] 类型
 * @return [Class]<[Vibrator]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val VibratorClass get() = classOf<Vibrator>()

/**
 * 获得 [VibrationEffect] 类型
 *
 * - 在 Android O (26) 及以上系统加入
 * @return [Class]<[VibrationEffect]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val VibrationEffectClass get() = if (Build.VERSION.SDK_INT >= 26) classOf<VibrationEffect>() else null

/**
 * 获得 [VibrationAttributes] 类型
 *
 * - 在 Android R (30) 及以上系统加入
 * @return [Class]<[VibrationAttributes]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val VibrationAttributesClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<VibrationAttributes>() else null

/**
 * 获得 [SystemClock] 类型
 * @return [Class]<[SystemClock]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SystemClockClass get() = classOf<SystemClock>()

/**
 * 获得 [PowerManager] 类型
 * @return [Class]<[PowerManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PowerManagerClass get() = classOf<PowerManager>()

/**
 * 获得 [PowerManager.WakeLock] 类型
 * @return [Class]<[PowerManager.WakeLock]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PowerManager_WakeLockClass get() = classOf<PowerManager.WakeLock>()

/**
 * 获得 [UserHandle] 类型
 * @return [Class]<[UserHandle]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val UserHandleClass get() = classOf<UserHandle>()

/**
 * 获得 [ShortcutInfo] 类型
 *
 * - 在 Android N_MR1 (25) 及以上系统加入
 * @return [Class]<[ShortcutInfo]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortcutInfoClass get() = if (Build.VERSION.SDK_INT >= 25) classOf<ShortcutInfo>() else null

/**
 * 获得 [ShortcutManager] 类型
 *
 * - 在 Android R (30) 及以上系统加入
 * @return [Class]<[ShortcutManager]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortcutManagerClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<ShortcutManager>() else null

/**
 * 获得 [ShortcutQuery] 类型
 *
 * - 在 Android N_MR1 (25) 及以上系统加入
 * @return [Class]<[ShortcutQuery]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortcutQueryClass get() = if (Build.VERSION.SDK_INT >= 25) classOf<ShortcutQuery>() else null

/**
 * 获得 [KeyboardShortcutInfo] 类型
 * @return [Class]<[KeyboardShortcutInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val KeyboardShortcutInfoClass get() = classOf<KeyboardShortcutInfo>()

/**
 * 获得 [KeyboardShortcutGroup] 类型
 * @return [Class]<[KeyboardShortcutGroup]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val KeyboardShortcutGroupClass get() = classOf<KeyboardShortcutGroup>()

/**
 * 获得 [ShortcutIconResource] 类型
 * @return [Class]<[ShortcutIconResource]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortcutIconResourceClass get() = classOf<ShortcutIconResource>()

/**
 * 获得 [AssetManager] 类型
 * @return [Class]<[AssetManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AssetManagerClass get() = classOf<AssetManager>()

/**
 * 获得 [AppWidgetManager] 类型
 * @return [Class]<[AppWidgetManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AppWidgetManagerClass get() = classOf<AppWidgetManager>()

/**
 * 获得 [AppWidgetProvider] 类型
 * @return [Class]<[AppWidgetProvider]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AppWidgetProviderClass get() = classOf<AppWidgetProvider>()

/**
 * 获得 [AppWidgetProviderInfo] 类型
 * @return [Class]<[AppWidgetProviderInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AppWidgetProviderInfoClass get() = classOf<AppWidgetProviderInfo>()

/**
 * 获得 [AppWidgetHost] 类型
 * @return [Class]<[AppWidgetHost]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AppWidgetHostClass get() = classOf<AppWidgetHost>()

/**
 * 获得 [ActivityInfo] 类型
 * @return [Class]<[ActivityInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityInfoClass get() = classOf<ActivityInfo>()

/**
 * 获得 [ResolveInfo] 类型
 * @return [Class]<[ResolveInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ResolveInfoClass get() = classOf<ResolveInfo>()

/**
 * 获得 [Property] 类型
 * @return [Class]<[Property]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PropertyClass get() = classOf<Property<*, *>>()

/**
 * 获得 [IntProperty] 类型
 * @return [Class]<[IntProperty]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntPropertyClass get() = classOf<IntProperty<*>>()

/**
 * 获得 [FloatProperty] 类型
 * @return [Class]<[FloatProperty]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatPropertyClass get() = classOf<FloatProperty<*>>()

/**
 * 获得 [SQLiteDatabase] 类型
 * @return [Class]<[SQLiteDatabase]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SQLiteDatabaseClass get() = classOf<SQLiteDatabase>()

/**
 * 获得 [StrictMode] 类型
 * @return [Class]<[StrictMode]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StrictModeClass get() = classOf<StrictMode>()

/**
 * 获得 [AccessibilityManager] 类型
 * @return [Class]<[AccessibilityManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AccessibilityManagerClass get() = classOf<AccessibilityManager>()

/**
 * 获得 [AccessibilityEvent] 类型
 * @return [Class]<[AccessibilityEvent]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AccessibilityEventClass get() = classOf<AccessibilityEvent>()

/**
 * 获得 [AccessibilityNodeInfo] 类型
 * @return [Class]<[AccessibilityNodeInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AccessibilityNodeInfoClass get() = classOf<AccessibilityNodeInfo>()

/**
 * 获得 [IInterface] 类型
 * @return [Class]<[IInterface]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IInterfaceClass get() = classOf<IInterface>()