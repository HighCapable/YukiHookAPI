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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "KDocUnresolvedReference", "DEPRECATION")

package com.highcapable.yukihookapi.hook.type.android

import android.app.* // ktlint-disable no-wildcard-imports
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
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.factory.toClassOrNull

/**
 * 获得 [android.R] 类型
 * @return [Class]<[android.R]>
 */
val AndroidRClass get() = classOf<android.R>()

/**
 * 获得 [Context] 类型
 * @return [Class]<[Context]>
 */
val ContextClass get() = classOf<Context>()

/**
 * 获得 [ContextImpl] 类型
 * @return [Class]
 */
val ContextImplClass get() = "android.app.ContextImpl".toClass()

/**
 * 获得 [ContextWrapper] 类型
 * @return [Class]<[ContextWrapper]>
 */
val ContextWrapperClass get() = classOf<ContextWrapper>()

/**
 * 获得 [Application] 类型
 * @return [Class]<[Application]>
 */
val ApplicationClass get() = classOf<Application>()

/**
 * 获得 [ApplicationInfo] 类型
 * @return [Class]<[ApplicationInfo]>
 */
val ApplicationInfoClass get() = classOf<ApplicationInfo>()

/**
 * 获得 [Instrumentation] 类型
 * @return [Class]<[Instrumentation]>
 */
val InstrumentationClass get() = classOf<Instrumentation>()

/**
 * 获得 [PackageInfo] 类型
 * @return [Class]<[PackageInfo]>
 */
val PackageInfoClass get() = classOf<PackageInfo>()

/**
 * 获得 [ApplicationPackageManager] 类型
 * @return [Class]
 */
val ApplicationPackageManagerClass get() = "android.app.ApplicationPackageManager".toClass()

/**
 * 获得 [ActivityThread] 类型
 * @return [Class]
 */
val ActivityThreadClass get() = "android.app.ActivityThread".toClass()

/**
 * 获得 [ActivityManager] 类型
 * @return [Class]<[ActivityManager]>
 */
val ActivityManagerClass get() = classOf<ActivityManager>()

/**
 * 获得 [IActivityManager] 类型
 * @return [Class]
 */
val IActivityManagerClass get() = "android.app.IActivityManager".toClass()

/**
 * 获得 [ActivityManagerNative] 类型
 * @return [Class]
 */
val ActivityManagerNativeClass get() = "android.app.ActivityManagerNative".toClass()

/**
 * 获得 [IActivityTaskManager] 类型
 *
 * - 在 Android O (26) 及以上系统加入
 * @return [Class] or null
 */
val IActivityTaskManagerClass get() = "android.app.IActivityTaskManager".toClassOrNull()

/**
 * 获得 [ActivityTaskManager] 类型
 *
 * - 在 Android O (26) 及以上系统加入
 * @return [Class] or null
 */
val ActivityTaskManagerClass get() = "android.app.ActivityTaskManager".toClassOrNull()

/**
 * 获得 [IPackageManager] 类型
 * @return [Class]
 */
val IPackageManagerClass get() = "android.content.pm.IPackageManager".toClass()

/**
 * 获得 [ClientTransaction] 类型
 * @return [Class]
 */
val ClientTransactionClass get() = "android.app.servertransaction.ClientTransaction".toClass()

/**
 * 获得 [LoadedApk] 类型
 * @return [Class]
 */
val LoadedApkClass get() = "android.app.LoadedApk".toClass()

/**
 * 获得 [Singleton] 类型
 * @return [Class]
 */
val SingletonClass get() = "android.util.Singleton".toClass()

/**
 * 获得 [Activity] 类型
 * @return [Class]<[Activity]>
 */
val ActivityClass get() = classOf<Activity>()

/**
 * 获得 [Looper] 类型
 * @return [Class]<[Looper]>
 */
val LooperClass get() = classOf<Looper>()

/**
 * 获得 [Fragment] 类型 - Support
 * @return [Class]
 */
val FragmentClass_AndroidSupport get() = "android.support.v4.app.Fragment".toClass()

/**
 * 获得 [Fragment] 类型 - AndroidX
 * @return [Class]
 */
val FragmentClass_AndroidX get() = "androidx.fragment.app.Fragment".toClass()

/**
 * 获得 [FragmentActivity] 类型 - Support
 * @return [Class]
 */
val FragmentActivityClass_AndroidSupport get() = "android.support.v4.app.FragmentActivity".toClass()

/**
 * 获得 [FragmentActivity] 类型 - AndroidX
 * @return [Class]
 */
val FragmentActivityClass_AndroidX get() = "androidx.fragment.app.FragmentActivity".toClass()

/**
 * 获得 [DocumentFile] 类型 - AndroidX
 * @return [Class]
 */
val DocumentFileClass get() = "androidx.documentfile.provider.DocumentFile".toClass()

/**
 * 获得 [Service] 类型
 * @return [Class]<[Service]>
 */
val ServiceClass get() = classOf<Service>()

/**
 * 获得 [Binder] 类型
 * @return [Class]<[Binder]>
 */
val BinderClass get() = classOf<Binder>()

/**
 * 获得 [IBinder] 类型
 * @return [Class]<[IBinder]>
 */
val IBinderClass get() = classOf<IBinder>()

/**
 * 获得 [BroadcastReceiver] 类型
 * @return [Class]<[BroadcastReceiver]>
 */
val BroadcastReceiverClass get() = classOf<BroadcastReceiver>()

/**
 * 获得 [Bundle] 类型
 * @return [Class]<[Bundle]>
 */
val BundleClass get() = classOf<Bundle>()

/**
 * 获得 [BaseBundle] 类型
 * @return [Class]<[BaseBundle]>
 */
val BaseBundleClass get() = classOf<BaseBundle>()

/**
 * 获得 [Resources] 类型
 * @return [Class]<[Resources]>
 */
val ResourcesClass get() = classOf<Resources>()

/**
 * 获得 [Configuration] 类型
 * @return [Class]<[Configuration]>
 */
val ConfigurationClass get() = classOf<Configuration>()

/**
 * 获得 [ConfigurationInfo] 类型
 * @return [Class]<[ConfigurationInfo]>
 */
val ConfigurationInfoClass get() = classOf<ConfigurationInfo>()

/**
 * 获得 [ContentResolver] 类型
 * @return [Class]<[ContentResolver]>
 */
val ContentResolverClass get() = classOf<ContentResolver>()

/**
 * 获得 [ContentProvider] 类型
 * @return [Class]<[ContentProvider]>
 */
val ContentProviderClass get() = classOf<ContentProvider>()

/**
 * 获得 [Settings] 类型
 * @return [Class]<[Settings]>
 */
val SettingsClass get() = classOf<Settings>()

/**
 * 获得 [Settings.System] 类型
 * @return [Class]<[Settings.System]>
 */
val Settings_SystemClass get() = classOf<Settings.System>()

/**
 * 获得 [Settings.Secure] 类型
 * @return [Class]<[Settings.Secure]>
 */
val Settings_SecureClass get() = classOf<Settings.Secure>()

/**
 * 获得 [TypedArray] 类型
 * @return [Class]<[TypedArray]>
 */
val TypedArrayClass get() = classOf<TypedArray>()

/**
 * 获得 [TypedValue] 类型
 * @return [Class]<[TypedValue]>
 */
val TypedValueClass get() = classOf<TypedValue>()

/**
 * 获得 [SparseArray] 类型
 * @return [Class]<[SparseArray]>
 */
val SparseArrayClass get() = classOf<SparseArray<*>>()

/**
 * 获得 [SparseIntArray] 类型
 * @return [Class]<[SparseIntArray]>
 */
val SparseIntArrayClass get() = classOf<SparseIntArray>()

/**
 * 获得 [SparseBooleanArray] 类型
 * @return [Class]<[SparseBooleanArray]>
 */
val SparseBooleanArrayClass get() = classOf<SparseBooleanArray>()

/**
 * 获得 [SparseLongArray] 类型
 * @return [Class]<[SparseLongArray]>
 */
val SparseLongArrayClass get() = classOf<SparseLongArray>()

/**
 * 获得 [LongSparseArray] 类型
 * @return [Class]<[LongSparseArray]>
 */
val LongSparseArrayClass get() = classOf<LongSparseArray<*>>()

/**
 * 获得 [ArrayMap] 类型
 * @return [Class]<[ArrayMap]>
 */
val ArrayMapClass get() = classOf<ArrayMap<*, *>>()

/**
 * 获得 [ArraySet] 类型
 *
 * - 在 Android M (23) 及以上系统加入
 * @return [Class]<[ArraySet]> or null
 */
val ArraySetClass get() = if (Build.VERSION.SDK_INT >= 23) classOf<ArraySet<*>>() else null

/**
 * 获得 [Handler] 类型
 * @return [Class]<[Handler]>
 */
val HandlerClass get() = classOf<Handler>()

/**
 * 获得 [Handler.Callback] 类型
 * @return [Class]<[Handler.Callback]>
 */
val Handler_CallbackClass get() = classOf<Handler.Callback>()

/**
 * 获得 [Message] 类型
 * @return [Class]<[Message]>
 */
val MessageClass get() = classOf<Message>()

/**
 * 获得 [MessageQueue] 类型
 * @return [Class]<[MessageQueue]>
 */
val MessageQueueClass get() = classOf<MessageQueue>()

/**
 * 获得 [Messenger] 类型
 * @return [Class]<[Messenger]>
 */
val MessengerClass get() = classOf<Messenger>()

/**
 * 获得 [AsyncTask] 类型
 * @return [Class]<[AsyncTask]>
 */
val AsyncTaskClass get() = classOf<AsyncTask<*, *, *>>()

/**
 * 获得 [SimpleDateFormat] 类型
 *
 * - 在 Android N (24) 及以上系统加入
 * @return [Class]<[SimpleDateFormat]> or null
 */
val SimpleDateFormatClass_Android get() = if (Build.VERSION.SDK_INT >= 24) classOf<SimpleDateFormat>() else null

/**
 * 获得 [Base64] 类型
 * @return [Class]<[Base64]>
 */
val Base64Class_Android get() = classOf<Base64>()

/**
 * 获得 [Window] 类型
 * @return [Class]<[Window]>
 */
val WindowClass get() = classOf<Window>()

/**
 * 获得 [WindowMetrics] 类型
 *
 * - 在 Android R (30) 及以上系统加入
 * @return [Class]<[WindowMetrics]> or null
 */
val WindowMetricsClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<WindowMetrics>() else null

/**
 * 获得 [WindowInsets] 类型
 * @return [Class]<[WindowInsets]>
 */
val WindowInsetsClass get() = classOf<WindowInsets>()

/**
 * 获得 [WindowInsets.Type] 类型
 *
 * - 在 Android R (30) 及以上系统加入
 * @return [Class]<[WindowInsets.Type]> or null
 */
val WindowInsets_TypeClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<WindowInsets.Type>() else null

/**
 * 获得 [WindowManager] 类型
 * @return [Class]<[WindowManager]>
 */
val WindowManagerClass get() = classOf<WindowManager>()

/**
 * 获得 [WindowManager.LayoutParams] 类型
 * @return [Class]<[WindowManager.LayoutParams]>
 */
val WindowManager_LayoutParamsClass get() = classOf<WindowManager.LayoutParams>()

/**
 * 获得 [ViewManager] 类型
 * @return [Class]<[ViewManager]>
 */
val ViewManagerClass get() = classOf<ViewManager>()

/**
 * 获得 [Parcel] 类型
 * @return [Class]<[Parcel]>
 */
val ParcelClass get() = classOf<Parcel>()

/**
 * 获得 [Parcelable] 类型
 * @return [Class]<[Parcelable]>
 */
val ParcelableClass get() = classOf<Parcelable>()

/**
 * 获得 [Parcelable.Creator] 类型
 * @return [Class]<[Parcelable.Creator]>
 */
val Parcelable_CreatorClass get() = classOf<Parcelable.Creator<*>>()

/**
 * 获得 [Dialog] 类型
 * @return [Class]<[Dialog]>
 */
val DialogClass get() = classOf<Dialog>()

/**
 * 获得 [AlertDialog] 类型
 * @return [Class]<[AlertDialog]>
 */
val AlertDialogClass get() = classOf<AlertDialog>()

/**
 * 获得 [DisplayMetrics] 类型
 * @return [Class]<[DisplayMetrics]>
 */
val DisplayMetricsClass get() = classOf<DisplayMetrics>()

/**
 * 获得 [Display] 类型
 * @return [Class]<[Display]>
 */
val DisplayClass get() = classOf<Display>()

/**
 * 获得 [Toast] 类型
 * @return [Class]<[Toast]>
 */
val ToastClass get() = classOf<Toast>()

/**
 * 获得 [Intent] 类型
 * @return [Class]<[Intent]>
 */
val IntentClass get() = classOf<Intent>()

/**
 * 获得 [ComponentInfo] 类型
 * @return [Class]<[ComponentInfo]>
 */
val ComponentInfoClass get() = classOf<ComponentInfo>()

/**
 * 获得 [ComponentName] 类型
 * @return [Class]<[ComponentName]>
 */
val ComponentNameClass get() = classOf<ComponentName>()

/**
 * 获得 [PendingIntent] 类型
 * @return [Class]<[PendingIntent]>
 */
val PendingIntentClass get() = classOf<PendingIntent>()

/**
 * 获得 [ColorStateList] 类型
 * @return [Class]<[ColorStateList]>
 */
val ColorStateListClass get() = classOf<ColorStateList>()

/**
 * 获得 [ContentValues] 类型
 * @return [Class]<[ContentValues]>
 */
val ContentValuesClass get() = classOf<ContentValues>()

/**
 * 获得 [SharedPreferences] 类型
 * @return [Class]<[SharedPreferences]>
 */
val SharedPreferencesClass get() = classOf<SharedPreferences>()

/**
 * 获得 [MediaPlayer] 类型
 * @return [Class]<[MediaPlayer]>
 */
val MediaPlayerClass get() = classOf<MediaPlayer>()

/**
 * 获得 [ProgressDialog] 类型
 * @return [Class]<[ProgressDialog]>
 */
val ProgressDialogClass get() = classOf<ProgressDialog>()

/**
 * 获得 [Log] 类型
 * @return [Class]<[Log]>
 */
val LogClass get() = classOf<Log>()

/**
 * 获得 [Build] 类型
 * @return [Class]<[Build]>
 */
val BuildClass get() = classOf<Build>()

/**
 * 获得 [Xml] 类型
 * @return [Class]<[Xml]>
 */
val XmlClass get() = classOf<Xml>()

/**
 * 获得 [ContrastColorUtil] 类型
 * @return [Class]
 */
val ContrastColorUtilClass get() = "com.android.internal.util.ContrastColorUtil".toClass()

/**
 * 获得 [StatusBarNotification] 类型
 * @return [Class]<[StatusBarNotification]>
 */
val StatusBarNotificationClass get() = classOf<StatusBarNotification>()

/**
 * 获得 [Notification] 类型
 * @return [Class]<[Notification]>
 */
val NotificationClass get() = classOf<Notification>()

/**
 * 获得 [Notification.Builder] 类型
 * @return [Class]<[Notification.Builder]>
 */
val Notification_BuilderClass get() = classOf<Notification.Builder>()

/**
 * 获得 [Notification.Action] 类型
 * @return [Class]<[Notification.Action]>
 */
val Notification_ActionClass get() = classOf<Notification.Action>()

/**
 * 获得 [DialogInterface] 类型
 * @return [Class]<[DialogInterface]>
 */
val DialogInterfaceClass get() = classOf<DialogInterface>()

/**
 * 获得 [DialogInterface.OnClickListener] 类型
 * @return [Class]<[DialogInterface.OnClickListener]>
 */
val DialogInterface_OnClickListenerClass get() = classOf<DialogInterface.OnClickListener>()

/**
 * 获得 [DialogInterface.OnCancelListener] 类型
 * @return [Class]<[DialogInterface.OnCancelListener]>
 */
val DialogInterface_OnCancelListenerClass get() = classOf<DialogInterface.OnCancelListener>()

/**
 * 获得 [DialogInterface.OnDismissListener] 类型
 * @return [Class]<[DialogInterface.OnDismissListener]>
 */
val DialogInterface_OnDismissListenerClass get() = classOf<DialogInterface.OnDismissListener>()

/**
 * 获得 [Environment] 类型
 * @return [Class]<[Environment]>
 */
val EnvironmentClass get() = classOf<Environment>()

/**
 * 获得 [Process] 类型
 * @return [Class]<[Process]>
 */
val ProcessClass get() = classOf<Process>()

/**
 * 获得 [Vibrator] 类型
 * @return [Class]<[Vibrator]>
 */
val VibratorClass get() = classOf<Vibrator>()

/**
 * 获得 [VibrationEffect] 类型
 *
 * - 在 Android O (26) 及以上系统加入
 * @return [Class]<[VibrationEffect]> or null
 */
val VibrationEffectClass get() = if (Build.VERSION.SDK_INT >= 26) classOf<VibrationEffect>() else null

/**
 * 获得 [VibrationAttributes] 类型
 *
 * - 在 Android R (30) 及以上系统加入
 * @return [Class]<[VibrationAttributes]> or null
 */
val VibrationAttributesClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<VibrationAttributes>() else null

/**
 * 获得 [SystemClock] 类型
 * @return [Class]<[SystemClock]>
 */
val SystemClockClass get() = classOf<SystemClock>()

/**
 * 获得 [PowerManager] 类型
 * @return [Class]<[PowerManager]>
 */
val PowerManagerClass get() = classOf<PowerManager>()

/**
 * 获得 [PowerManager.WakeLock] 类型
 * @return [Class]<[PowerManager.WakeLock]>
 */
val PowerManager_WakeLockClass get() = classOf<PowerManager.WakeLock>()

/**
 * 获得 [UserHandle] 类型
 * @return [Class]<[UserHandle]>
 */
val UserHandleClass get() = classOf<UserHandle>()

/**
 * 获得 [ShortcutInfo] 类型
 *
 * - 在 Android N_MR1 (25) 及以上系统加入
 * @return [Class]<[ShortcutInfo]> or null
 */
val ShortcutInfoClass get() = if (Build.VERSION.SDK_INT >= 25) classOf<ShortcutInfo>() else null

/**
 * 获得 [ShortcutManager] 类型
 *
 * - 在 Android R (30) 及以上系统加入
 * @return [Class]<[ShortcutManager]> or null
 */
val ShortcutManagerClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<ShortcutManager>() else null

/**
 * 获得 [ShortcutQuery] 类型
 *
 * - 在 Android N_MR1 (25) 及以上系统加入
 * @return [Class]<[ShortcutQuery]> or null
 */
val ShortcutQueryClass get() = if (Build.VERSION.SDK_INT >= 25) classOf<ShortcutQuery>() else null

/**
 * 获得 [KeyboardShortcutInfo] 类型
 * @return [Class]<[KeyboardShortcutInfo]>
 */
val KeyboardShortcutInfoClass get() = classOf<KeyboardShortcutInfo>()

/**
 * 获得 [KeyboardShortcutGroup] 类型
 * @return [Class]<[KeyboardShortcutGroup]>
 */
val KeyboardShortcutGroupClass get() = classOf<KeyboardShortcutGroup>()

/**
 * 获得 [ShortcutIconResource] 类型
 * @return [Class]<[ShortcutIconResource]>
 */
val ShortcutIconResourceClass get() = classOf<ShortcutIconResource>()

/**
 * 获得 [AssetManager] 类型
 * @return [Class]<[AssetManager]>
 */
val AssetManagerClass get() = classOf<AssetManager>()

/**
 * 获得 [AppWidgetManager] 类型
 * @return [Class]<[AppWidgetManager]>
 */
val AppWidgetManagerClass get() = classOf<AppWidgetManager>()

/**
 * 获得 [AppWidgetProvider] 类型
 * @return [Class]<[AppWidgetProvider]>
 */
val AppWidgetProviderClass get() = classOf<AppWidgetProvider>()

/**
 * 获得 [AppWidgetProviderInfo] 类型
 * @return [Class]<[AppWidgetProviderInfo]>
 */
val AppWidgetProviderInfoClass get() = classOf<AppWidgetProviderInfo>()

/**
 * 获得 [AppWidgetHost] 类型
 * @return [Class]<[AppWidgetHost]>
 */
val AppWidgetHostClass get() = classOf<AppWidgetHost>()

/**
 * 获得 [ActivityInfo] 类型
 * @return [Class]<[ActivityInfo]>
 */
val ActivityInfoClass get() = classOf<ActivityInfo>()

/**
 * 获得 [ResolveInfo] 类型
 * @return [Class]<[ResolveInfo]>
 */
val ResolveInfoClass get() = classOf<ResolveInfo>()

/**
 * 获得 [Property] 类型
 * @return [Class]<[Property]>
 */
val PropertyClass get() = classOf<Property<*, *>>()

/**
 * 获得 [IntProperty] 类型
 * @return [Class]<[IntProperty]>
 */
val IntPropertyClass get() = classOf<IntProperty<*>>()

/**
 * 获得 [FloatProperty] 类型
 * @return [Class]<[FloatProperty]>
 */
val FloatPropertyClass get() = classOf<FloatProperty<*>>()

/**
 * 获得 [SQLiteDatabase] 类型
 * @return [Class]<[SQLiteDatabase]>
 */
val SQLiteDatabaseClass get() = classOf<SQLiteDatabase>()

/**
 * 获得 [StrictMode] 类型
 * @return [Class]<[StrictMode]>
 */
val StrictModeClass get() = classOf<StrictMode>()

/**
 * 获得 [AccessibilityManager] 类型
 * @return [Class]<[AccessibilityManager]>
 */
val AccessibilityManagerClass get() = classOf<AccessibilityManager>()

/**
 * 获得 [AccessibilityEvent] 类型
 * @return [Class]<[AccessibilityEvent]>
 */
val AccessibilityEventClass get() = classOf<AccessibilityEvent>()

/**
 * 获得 [AccessibilityNodeInfo] 类型
 * @return [Class]<[AccessibilityNodeInfo]>
 */
val AccessibilityNodeInfoClass get() = classOf<AccessibilityNodeInfo>()

/**
 * 获得 [IInterface] 类型
 * @return [Class]<[IInterface]>
 */
val IInterfaceClass get() = classOf<IInterface>()