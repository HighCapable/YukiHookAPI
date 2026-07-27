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
 * Gets the [android.R] type.
 * @return [Class]<[android.R]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AndroidRClass get() = classOf<android.R>()

/**
 * Gets the [Context] type.
 * @return [Class]<[Context]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContextClass get() = classOf<Context>()

/**
 * Gets the [ContextImpl] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContextImplClass get() = "android.app.ContextImpl".toClass()

/**
 * Gets the [ContextWrapper] type.
 * @return [Class]<[ContextWrapper]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContextWrapperClass get() = classOf<ContextWrapper>()

/**
 * Gets the [Application] type.
 * @return [Class]<[Application]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ApplicationClass get() = classOf<Application>()

/**
 * Gets the [ApplicationInfo] type.
 * @return [Class]<[ApplicationInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ApplicationInfoClass get() = classOf<ApplicationInfo>()

/**
 * Gets the [Instrumentation] type.
 * @return [Class]<[Instrumentation]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val InstrumentationClass get() = classOf<Instrumentation>()

/**
 * Gets the [PackageInfo] type.
 * @return [Class]<[PackageInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PackageInfoClass get() = classOf<PackageInfo>()

/**
 * Gets the [ApplicationPackageManager] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ApplicationPackageManagerClass get() = "android.app.ApplicationPackageManager".toClass()

/**
 * Gets the [ActivityThread] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityThreadClass get() = "android.app.ActivityThread".toClass()

/**
 * Gets the [ActivityManager] type.
 * @return [Class]<[ActivityManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityManagerClass get() = classOf<ActivityManager>()

/**
 * Gets the [IActivityManager] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IActivityManagerClass get() = "android.app.IActivityManager".toClass()

/**
 * Gets the [ActivityManagerNative] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityManagerNativeClass get() = "android.app.ActivityManagerNative".toClass()

/**
 * Gets the [IActivityTaskManager] type.
 *
 * - Available on Android O (26) and later.
 * @return [Class] or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IActivityTaskManagerClass get() = "android.app.IActivityTaskManager".toClassOrNull()

/**
 * Gets the [ActivityTaskManager] type.
 *
 * - Available on Android O (26) and later.
 * @return [Class] or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityTaskManagerClass get() = "android.app.ActivityTaskManager".toClassOrNull()

/**
 * Gets the [IPackageManager] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IPackageManagerClass get() = "android.content.pm.IPackageManager".toClass()

/**
 * Gets the [ClientTransaction] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ClientTransactionClass get() = "android.app.servertransaction.ClientTransaction".toClass()

/**
 * Gets the [LoadedApk] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LoadedApkClass get() = "android.app.LoadedApk".toClass()

/**
 * Gets the [Singleton] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SingletonClass get() = "android.util.Singleton".toClass()

/**
 * Gets the [Activity] type.
 * @return [Class]<[Activity]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityClass get() = classOf<Activity>()

/**
 * Gets the [Looper] type.
 * @return [Class]<[Looper]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LooperClass get() = classOf<Looper>()

/**
 * Gets the Support Library [Fragment] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FragmentClass_AndroidSupport get() = "android.support.v4.app.Fragment".toClass()

/**
 * Gets the AndroidX [Fragment] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FragmentClass_AndroidX get() = "androidx.fragment.app.Fragment".toClass()

/**
 * Gets the Support Library [FragmentActivity] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FragmentActivityClass_AndroidSupport get() = "android.support.v4.app.FragmentActivity".toClass()

/**
 * Gets the AndroidX [FragmentActivity] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FragmentActivityClass_AndroidX get() = "androidx.fragment.app.FragmentActivity".toClass()

/**
 * Gets the AndroidX [DocumentFile] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DocumentFileClass get() = "androidx.documentfile.provider.DocumentFile".toClass()

/**
 * Gets the [Service] type.
 * @return [Class]<[Service]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ServiceClass get() = classOf<Service>()

/**
 * Gets the [Binder] type.
 * @return [Class]<[Binder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BinderClass get() = classOf<Binder>()

/**
 * Gets the [IBinder] type.
 * @return [Class]<[IBinder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IBinderClass get() = classOf<IBinder>()

/**
 * Gets the [BroadcastReceiver] type.
 * @return [Class]<[BroadcastReceiver]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BroadcastReceiverClass get() = classOf<BroadcastReceiver>()

/**
 * Gets the [Bundle] type.
 * @return [Class]<[Bundle]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BundleClass get() = classOf<Bundle>()

/**
 * Gets the [BaseBundle] type.
 * @return [Class]<[BaseBundle]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BaseBundleClass get() = classOf<BaseBundle>()

/**
 * Gets the [Resources] type.
 * @return [Class]<[Resources]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ResourcesClass get() = classOf<Resources>()

/**
 * Gets the [Configuration] type.
 * @return [Class]<[Configuration]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ConfigurationClass get() = classOf<Configuration>()

/**
 * Gets the [ConfigurationInfo] type.
 * @return [Class]<[ConfigurationInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ConfigurationInfoClass get() = classOf<ConfigurationInfo>()

/**
 * Gets the [ContentResolver] type.
 * @return [Class]<[ContentResolver]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContentResolverClass get() = classOf<ContentResolver>()

/**
 * Gets the [ContentProvider] type.
 * @return [Class]<[ContentProvider]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContentProviderClass get() = classOf<ContentProvider>()

/**
 * Gets the [Settings] type.
 * @return [Class]<[Settings]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SettingsClass get() = classOf<Settings>()

/**
 * Gets the [Settings.System] type.
 * @return [Class]<[Settings.System]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Settings_SystemClass get() = classOf<Settings.System>()

/**
 * Gets the [Settings.Secure] type.
 * @return [Class]<[Settings.Secure]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Settings_SecureClass get() = classOf<Settings.Secure>()

/**
 * Gets the [TypedArray] type.
 * @return [Class]<[TypedArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TypedArrayClass get() = classOf<TypedArray>()

/**
 * Gets the [TypedValue] type.
 * @return [Class]<[TypedValue]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TypedValueClass get() = classOf<TypedValue>()

/**
 * Gets the [SparseArray] type.
 * @return [Class]<[SparseArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SparseArrayClass get() = classOf<SparseArray<*>>()

/**
 * Gets the [SparseIntArray] type.
 * @return [Class]<[SparseIntArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SparseIntArrayClass get() = classOf<SparseIntArray>()

/**
 * Gets the [SparseBooleanArray] type.
 * @return [Class]<[SparseBooleanArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SparseBooleanArrayClass get() = classOf<SparseBooleanArray>()

/**
 * Gets the [SparseLongArray] type.
 * @return [Class]<[SparseLongArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SparseLongArrayClass get() = classOf<SparseLongArray>()

/**
 * Gets the [LongSparseArray] type.
 * @return [Class]<[LongSparseArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongSparseArrayClass get() = classOf<LongSparseArray<*>>()

/**
 * Gets the [ArrayMap] type.
 * @return [Class]<[ArrayMap]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ArrayMapClass get() = classOf<ArrayMap<*, *>>()

/**
 * Gets the [ArraySet] type.
 *
 * - Available on Android M (23) and later.
 * @return [Class]<[ArraySet]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ArraySetClass get() = if (Build.VERSION.SDK_INT >= 23) classOf<ArraySet<*>>() else null

/**
 * Gets the [Handler] type.
 * @return [Class]<[Handler]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HandlerClass get() = classOf<Handler>()

/**
 * Gets the [Handler.Callback] type.
 * @return [Class]<[Handler.Callback]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Handler_CallbackClass get() = classOf<Handler.Callback>()

/**
 * Gets the [Message] type.
 * @return [Class]<[Message]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MessageClass get() = classOf<Message>()

/**
 * Gets the [MessageQueue] type.
 * @return [Class]<[MessageQueue]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MessageQueueClass get() = classOf<MessageQueue>()

/**
 * Gets the [Messenger] type.
 * @return [Class]<[Messenger]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MessengerClass get() = classOf<Messenger>()

/**
 * Gets the [AsyncTask] type.
 * @return [Class]<[AsyncTask]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AsyncTaskClass get() = classOf<AsyncTask<*, *, *>>()

/**
 * Gets the [SimpleDateFormat] type.
 *
 * - Available on Android N (24) and later.
 * @return [Class]<[SimpleDateFormat]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SimpleDateFormatClass_Android get() = if (Build.VERSION.SDK_INT >= 24) classOf<SimpleDateFormat>() else null

/**
 * Gets the [Base64] type.
 * @return [Class]<[Base64]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Base64Class_Android get() = classOf<Base64>()

/**
 * Gets the [Window] type.
 * @return [Class]<[Window]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowClass get() = classOf<Window>()

/**
 * Gets the [WindowMetrics] type.
 *
 * - Available on Android R (30) and later.
 * @return [Class]<[WindowMetrics]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowMetricsClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<WindowMetrics>() else null

/**
 * Gets the [WindowInsets] type.
 * @return [Class]<[WindowInsets]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowInsetsClass get() = classOf<WindowInsets>()

/**
 * Gets the [WindowInsets.Type] type.
 *
 * - Available on Android R (30) and later.
 * @return [Class]<[WindowInsets.Type]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowInsets_TypeClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<WindowInsets.Type>() else null

/**
 * Gets the [WindowManager] type.
 * @return [Class]<[WindowManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowManagerClass get() = classOf<WindowManager>()

/**
 * Gets the [WindowManager.LayoutParams] type.
 * @return [Class]<[WindowManager.LayoutParams]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WindowManager_LayoutParamsClass get() = classOf<WindowManager.LayoutParams>()

/**
 * Gets the [ViewManager] type.
 * @return [Class]<[ViewManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewManagerClass get() = classOf<ViewManager>()

/**
 * Gets the [Parcel] type.
 * @return [Class]<[Parcel]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ParcelClass get() = classOf<Parcel>()

/**
 * Gets the [Parcelable] type.
 * @return [Class]<[Parcelable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ParcelableClass get() = classOf<Parcelable>()

/**
 * Gets the [Parcelable.Creator] type.
 * @return [Class]<[Parcelable.Creator]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Parcelable_CreatorClass get() = classOf<Parcelable.Creator<*>>()

/**
 * Gets the [Dialog] type.
 * @return [Class]<[Dialog]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogClass get() = classOf<Dialog>()

/**
 * Gets the [AlertDialog] type.
 * @return [Class]<[AlertDialog]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AlertDialogClass get() = classOf<AlertDialog>()

/**
 * Gets the [DisplayMetrics] type.
 * @return [Class]<[DisplayMetrics]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DisplayMetricsClass get() = classOf<DisplayMetrics>()

/**
 * Gets the [Display] type.
 * @return [Class]<[Display]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DisplayClass get() = classOf<Display>()

/**
 * Gets the [Toast] type.
 * @return [Class]<[Toast]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ToastClass get() = classOf<Toast>()

/**
 * Gets the [Intent] type.
 * @return [Class]<[Intent]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntentClass get() = classOf<Intent>()

/**
 * Gets the [ComponentInfo] type.
 * @return [Class]<[ComponentInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ComponentInfoClass get() = classOf<ComponentInfo>()

/**
 * Gets the [ComponentName] type.
 * @return [Class]<[ComponentName]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ComponentNameClass get() = classOf<ComponentName>()

/**
 * Gets the [PendingIntent] type.
 * @return [Class]<[PendingIntent]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PendingIntentClass get() = classOf<PendingIntent>()

/**
 * Gets the [ColorStateList] type.
 * @return [Class]<[ColorStateList]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ColorStateListClass get() = classOf<ColorStateList>()

/**
 * Gets the [ContentValues] type.
 * @return [Class]<[ContentValues]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContentValuesClass get() = classOf<ContentValues>()

/**
 * Gets the [SharedPreferences] type.
 * @return [Class]<[SharedPreferences]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SharedPreferencesClass get() = classOf<SharedPreferences>()

/**
 * Gets the [MediaPlayer] type.
 * @return [Class]<[MediaPlayer]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MediaPlayerClass get() = classOf<MediaPlayer>()

/**
 * Gets the [ProgressDialog] type.
 * @return [Class]<[ProgressDialog]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ProgressDialogClass get() = classOf<ProgressDialog>()

/**
 * Gets the [Log] type.
 * @return [Class]<[Log]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LogClass get() = classOf<Log>()

/**
 * Gets the [Build] type.
 * @return [Class]<[Build]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BuildClass get() = classOf<Build>()

/**
 * Gets the [Xml] type.
 * @return [Class]<[Xml]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val XmlClass get() = classOf<Xml>()

/**
 * Gets the [ContrastColorUtil] type.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ContrastColorUtilClass get() = "com.android.internal.util.ContrastColorUtil".toClass()

/**
 * Gets the [StatusBarNotification] type.
 * @return [Class]<[StatusBarNotification]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StatusBarNotificationClass get() = classOf<StatusBarNotification>()

/**
 * Gets the [Notification] type.
 * @return [Class]<[Notification]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NotificationClass get() = classOf<Notification>()

/**
 * Gets the [Notification.Builder] type.
 * @return [Class]<[Notification.Builder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Notification_BuilderClass get() = classOf<Notification.Builder>()

/**
 * Gets the [Notification.Action] type.
 * @return [Class]<[Notification.Action]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Notification_ActionClass get() = classOf<Notification.Action>()

/**
 * Gets the [DialogInterface] type.
 * @return [Class]<[DialogInterface]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogInterfaceClass get() = classOf<DialogInterface>()

/**
 * Gets the [DialogInterface.OnClickListener] type.
 * @return [Class]<[DialogInterface.OnClickListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogInterface_OnClickListenerClass get() = classOf<DialogInterface.OnClickListener>()

/**
 * Gets the [DialogInterface.OnCancelListener] type.
 * @return [Class]<[DialogInterface.OnCancelListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogInterface_OnCancelListenerClass get() = classOf<DialogInterface.OnCancelListener>()

/**
 * Gets the [DialogInterface.OnDismissListener] type.
 * @return [Class]<[DialogInterface.OnDismissListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DialogInterface_OnDismissListenerClass get() = classOf<DialogInterface.OnDismissListener>()

/**
 * Gets the [Environment] type.
 * @return [Class]<[Environment]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val EnvironmentClass get() = classOf<Environment>()

/**
 * Gets the [Process] type.
 * @return [Class]<[Process]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ProcessClass get() = classOf<Process>()

/**
 * Gets the [Vibrator] type.
 * @return [Class]<[Vibrator]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val VibratorClass get() = classOf<Vibrator>()

/**
 * Gets the [VibrationEffect] type.
 *
 * - Available on Android O (26) and later.
 * @return [Class]<[VibrationEffect]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val VibrationEffectClass get() = if (Build.VERSION.SDK_INT >= 26) classOf<VibrationEffect>() else null

/**
 * Gets the [VibrationAttributes] type.
 *
 * - Available on Android R (30) and later.
 * @return [Class]<[VibrationAttributes]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val VibrationAttributesClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<VibrationAttributes>() else null

/**
 * Gets the [SystemClock] type.
 * @return [Class]<[SystemClock]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SystemClockClass get() = classOf<SystemClock>()

/**
 * Gets the [PowerManager] type.
 * @return [Class]<[PowerManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PowerManagerClass get() = classOf<PowerManager>()

/**
 * Gets the [PowerManager.WakeLock] type.
 * @return [Class]<[PowerManager.WakeLock]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PowerManager_WakeLockClass get() = classOf<PowerManager.WakeLock>()

/**
 * Gets the [UserHandle] type.
 * @return [Class]<[UserHandle]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val UserHandleClass get() = classOf<UserHandle>()

/**
 * Gets the [ShortcutInfo] type.
 *
 * - Available on Android N_MR1 (25) and later.
 * @return [Class]<[ShortcutInfo]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortcutInfoClass get() = if (Build.VERSION.SDK_INT >= 25) classOf<ShortcutInfo>() else null

/**
 * Gets the [ShortcutManager] type.
 *
 * - Available on Android R (30) and later.
 * @return [Class]<[ShortcutManager]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortcutManagerClass get() = if (Build.VERSION.SDK_INT >= 30) classOf<ShortcutManager>() else null

/**
 * Gets the [ShortcutQuery] type.
 *
 * - Available on Android N_MR1 (25) and later.
 * @return [Class]<[ShortcutQuery]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortcutQueryClass get() = if (Build.VERSION.SDK_INT >= 25) classOf<ShortcutQuery>() else null

/**
 * Gets the [KeyboardShortcutInfo] type.
 * @return [Class]<[KeyboardShortcutInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val KeyboardShortcutInfoClass get() = classOf<KeyboardShortcutInfo>()

/**
 * Gets the [KeyboardShortcutGroup] type.
 * @return [Class]<[KeyboardShortcutGroup]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val KeyboardShortcutGroupClass get() = classOf<KeyboardShortcutGroup>()

/**
 * Gets the [ShortcutIconResource] type.
 * @return [Class]<[ShortcutIconResource]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortcutIconResourceClass get() = classOf<ShortcutIconResource>()

/**
 * Gets the [AssetManager] type.
 * @return [Class]<[AssetManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AssetManagerClass get() = classOf<AssetManager>()

/**
 * Gets the [AppWidgetManager] type.
 * @return [Class]<[AppWidgetManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AppWidgetManagerClass get() = classOf<AppWidgetManager>()

/**
 * Gets the [AppWidgetProvider] type.
 * @return [Class]<[AppWidgetProvider]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AppWidgetProviderClass get() = classOf<AppWidgetProvider>()

/**
 * Gets the [AppWidgetProviderInfo] type.
 * @return [Class]<[AppWidgetProviderInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AppWidgetProviderInfoClass get() = classOf<AppWidgetProviderInfo>()

/**
 * Gets the [AppWidgetHost] type.
 * @return [Class]<[AppWidgetHost]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AppWidgetHostClass get() = classOf<AppWidgetHost>()

/**
 * Gets the [ActivityInfo] type.
 * @return [Class]<[ActivityInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ActivityInfoClass get() = classOf<ActivityInfo>()

/**
 * Gets the [ResolveInfo] type.
 * @return [Class]<[ResolveInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ResolveInfoClass get() = classOf<ResolveInfo>()

/**
 * Gets the [Property] type.
 * @return [Class]<[Property]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PropertyClass get() = classOf<Property<*, *>>()

/**
 * Gets the [IntProperty] type.
 * @return [Class]<[IntProperty]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntPropertyClass get() = classOf<IntProperty<*>>()

/**
 * Gets the [FloatProperty] type.
 * @return [Class]<[FloatProperty]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatPropertyClass get() = classOf<FloatProperty<*>>()

/**
 * Gets the [SQLiteDatabase] type.
 * @return [Class]<[SQLiteDatabase]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SQLiteDatabaseClass get() = classOf<SQLiteDatabase>()

/**
 * Gets the [StrictMode] type.
 * @return [Class]<[StrictMode]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StrictModeClass get() = classOf<StrictMode>()

/**
 * Gets the [AccessibilityManager] type.
 * @return [Class]<[AccessibilityManager]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AccessibilityManagerClass get() = classOf<AccessibilityManager>()

/**
 * Gets the [AccessibilityEvent] type.
 * @return [Class]<[AccessibilityEvent]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AccessibilityEventClass get() = classOf<AccessibilityEvent>()

/**
 * Gets the [AccessibilityNodeInfo] type.
 * @return [Class]<[AccessibilityNodeInfo]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AccessibilityNodeInfoClass get() = classOf<AccessibilityNodeInfo>()

/**
 * Gets the [IInterface] type.
 * @return [Class]<[IInterface]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IInterfaceClass get() = classOf<IInterface>()