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
@file:Suppress("unused", "DEPRECATION", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.type.android

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.appwidget.AppWidgetHostView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.ViewPropertyAnimator
import android.view.ViewStructure
import android.view.ViewStub
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import android.widget.TextClock
import android.widget.TextView
import android.widget.VideoView
import android.widget.ViewAnimator
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.factory.classOf

/**
 * Gets the [View] type.
 * @return [Class]<[View]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewClass get() = classOf<View>()

/**
 * Gets the [Surface] type.
 * @return [Class]<[Surface]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SurfaceClass get() = classOf<Surface>()

/**
 * Gets the [SurfaceView] type.
 * @return [Class]<[SurfaceView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SurfaceViewClass get() = classOf<SurfaceView>()

/**
 * Gets the [TextureView] type.
 * @return [Class]<[TextureView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TextureViewClass get() = classOf<TextureView>()

/**
 * Gets the [WebView] type.
 * @return [Class]<[WebView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WebViewClass get() = classOf<WebView>()

/**
 * Gets the [WebViewClient] type.
 * @return [Class]<[WebViewClient]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WebViewClientClass get() = classOf<WebViewClient>()

/**
 * Gets the [ViewStructure] type.
 * @return [Class]<[ViewStructure]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewStructureClass get() = classOf<ViewStructure>()

/**
 * Gets the [ViewGroup] type.
 * @return [Class]<[ViewGroup]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewGroupClass get() = classOf<ViewGroup>()

/**
 * Gets the [ViewParent] type.
 * @return [Class]<[ViewParent]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewParentClass get() = classOf<ViewParent>()

/**
 * Gets the [AppWidgetHostView] type.
 * @return [Class]<[AppWidgetHostView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AppWidgetHostViewClass get() = classOf<AppWidgetHostView>()

/**
 * Gets the [RemoteViews] type.
 * @return [Class]<[RemoteViews]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RemoteViewsClass get() = classOf<RemoteViews>()

/**
 * Gets the [RemoteView] type.
 * @return [Class]<[RemoteView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RemoteViewClass get() = classOf<RemoteView>()

/**
 * Gets the [TextView] type.
 * @return [Class]<[TextView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TextViewClass get() = classOf<TextView>()

/**
 * Gets the [ImageView] type.
 * @return [Class]<[ImageView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ImageViewClass get() = classOf<ImageView>()

/**
 * Gets the [ImageButton] type.
 * @return [Class]<[ImageButton]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ImageButtonClass get() = classOf<ImageButton>()

/**
 * Gets the [EditText] type.
 * @return [Class]<[EditText]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val EditTextClass get() = classOf<EditText>()

/**
 * Gets the [Button] type.
 * @return [Class]<[Button]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ButtonClass get() = classOf<Button>()

/**
 * Gets the [CheckBox] type.
 * @return [Class]<[CheckBox]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CheckBoxClass get() = classOf<CheckBox>()

/**
 * Gets the [CompoundButton] type.
 * @return [Class]<[CompoundButton]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CompoundButtonClass get() = classOf<CompoundButton>()

/**
 * Gets the [VideoView] type.
 * @return [Class]<[VideoView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val VideoViewClass get() = classOf<VideoView>()

/**
 * Gets the [ListView] type.
 * @return [Class]<[ListView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ListViewClass get() = classOf<ListView>()

/**
 * Gets the [LayoutInflater] type.
 * @return [Class]<[LayoutInflater]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LayoutInflaterClass get() = classOf<LayoutInflater>()

/**
 * Gets the [LayoutInflater.Filter] type.
 * @return [Class]<[LayoutInflater.Filter]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LayoutInflater_FilterClass get() = classOf<LayoutInflater.Filter>()

/**
 * Gets the [LayoutInflater.Factory] type.
 * @return [Class]<[LayoutInflater.Factory]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LayoutInflater_FactoryClass get() = classOf<LayoutInflater.Factory>()

/**
 * Gets the [LayoutInflater.Factory2] type.
 * @return [Class]<[LayoutInflater.Factory2]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LayoutInflater_Factory2Class get() = classOf<LayoutInflater.Factory2>()

/**
 * Gets the [ListAdapter] type.
 * @return [Class]<[ListAdapter]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ListAdapterClass get() = classOf<ListAdapter>()

/**
 * Gets the [ArrayAdapter] type.
 * @return [Class]<[ArrayAdapter]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ArrayAdapterClass get() = classOf<ArrayAdapter<*>>()

/**
 * Gets the [BaseAdapter] type.
 * @return [Class]<[BaseAdapter]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BaseAdapterClass get() = classOf<BaseAdapter>()

/**
 * Gets the [RelativeLayout] type.
 * @return [Class]<[RelativeLayout]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RelativeLayoutClass get() = classOf<RelativeLayout>()

/**
 * Gets the [FrameLayout] type.
 * @return [Class]<[FrameLayout]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FrameLayoutClass get() = classOf<FrameLayout>()

/**
 * Gets the [LinearLayout] type.
 * @return [Class]<[LinearLayout]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LinearLayoutClass get() = classOf<LinearLayout>()

/**
 * Gets the [ViewGroup.LayoutParams] type.
 * @return [Class]<[ViewGroup.LayoutParams]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewGroup_LayoutParamsClass get() = classOf<ViewGroup.LayoutParams>()

/**
 * Gets the [RelativeLayout.LayoutParams] type.
 * @return [Class]<[RelativeLayout.LayoutParams]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RelativeLayout_LayoutParamsClass get() = classOf<RelativeLayout.LayoutParams>()

/**
 * Gets the [LinearLayout.LayoutParams] type.
 * @return [Class]<[LinearLayout.LayoutParams]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LinearLayout_LayoutParamsClass get() = classOf<LinearLayout.LayoutParams>()

/**
 * Gets the [FrameLayout.LayoutParams] type.
 * @return [Class]<[FrameLayout.LayoutParams]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FrameLayout_LayoutParamsClass get() = classOf<FrameLayout.LayoutParams>()

/**
 * Gets the [TextClock] type.
 * @return [Class]<[TextClock]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TextClockClass get() = classOf<TextClock>()

/**
 * Gets the [MotionEvent] type.
 * @return [Class]<[MotionEvent]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MotionEventClass get() = classOf<MotionEvent>()

/**
 * Gets the [View.OnClickListener] type.
 * @return [Class]<[View.OnClickListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val View_OnClickListenerClass get() = classOf<View.OnClickListener>()

/**
 * Gets the [View.OnLongClickListener] type.
 * @return [Class]<[View.OnLongClickListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val View_OnLongClickListenerClass get() = classOf<View.OnLongClickListener>()

/**
 * Gets the [View.OnTouchListener] type.
 * @return [Class]<[View.OnTouchListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val View_OnTouchListenerClass get() = classOf<View.OnTouchListener>()

/**
 * Gets the [AutoCompleteTextView] type.
 * @return [Class]<[AutoCompleteTextView]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AutoCompleteTextViewClass get() = classOf<AutoCompleteTextView>()

/**
 * Gets the [ViewStub] type.
 * @return [Class]<[ViewStub]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewStubClass get() = classOf<ViewStub>()

/**
 * Gets the [ViewStub.OnInflateListener] type.
 * @return [Class]<[ViewStub.OnInflateListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewStub_OnInflateListenerClass get() = classOf<ViewStub.OnInflateListener>()

/**
 * Gets the [GestureDetector] type.
 * @return [Class]<[GestureDetector]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val GestureDetectorClass get() = classOf<GestureDetector>()

/**
 * Gets the [GestureDetector.SimpleOnGestureListener] type.
 * @return [Class]<[GestureDetector.SimpleOnGestureListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val GestureDetector_SimpleOnGestureListenerClass get() = classOf<GestureDetector.SimpleOnGestureListener>()

/**
 * Gets the [ProgressBar] type.
 * @return [Class]<[ProgressBar]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ProgressBarClass get() = classOf<ProgressBar>()

/**
 * Gets the [AttributeSet] type.
 * @return [Class]<[AttributeSet]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AttributeSetClass get() = classOf<AttributeSet>()

/**
 * Gets the [Animation] type.
 * @return [Class]<[Animation]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AnimationClass get() = classOf<Animation>()

/**
 * Gets the [Animation.AnimationListener] type.
 * @return [Class]<[Animation.AnimationListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Animation_AnimationListenerClass get() = classOf<Animation.AnimationListener>()

/**
 * Gets the [TranslateAnimation] type.
 * @return [Class]<[TranslateAnimation]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TranslateAnimationClass get() = classOf<TranslateAnimation>()

/**
 * Gets the [AlphaAnimation] type.
 * @return [Class]<[AlphaAnimation]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AlphaAnimationClass get() = classOf<AlphaAnimation>()

/**
 * Gets the [Animator] type.
 * @return [Class]<[Animator]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AnimatorClass get() = classOf<Animator>()

/**
 * Gets the [Animator.AnimatorListener] type.
 * @return [Class]<[Animator.AnimatorListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Animator_AnimatorListenerClass get() = classOf<Animator.AnimatorListener>()

/**
 * Gets the [ObjectAnimator] type.
 * @return [Class]<[ObjectAnimator]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ObjectAnimatorClass get() = classOf<ObjectAnimator>()

/**
 * Gets the [ValueAnimator] type.
 * @return [Class]<[ValueAnimator]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ValueAnimatorClass get() = classOf<ValueAnimator>()

/**
 * Gets the [ValueAnimator.AnimatorUpdateListener] type.
 * @return [Class]<[ValueAnimator.AnimatorUpdateListener]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ValueAnimator_AnimatorUpdateListenerClass get() = classOf<ValueAnimator.AnimatorUpdateListener>()

/**
 * Gets the [ViewAnimator] type.
 * @return [Class]<[ViewAnimator]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewAnimatorClass get() = classOf<ViewAnimator>()

/**
 * Gets the [AnimatorSet] type.
 * @return [Class]<[AnimatorSet]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AnimatorSetClass get() = classOf<AnimatorSet>()

/**
 * Gets the [AnimatorSet.Builder] type.
 * @return [Class]<[AnimatorSet.Builder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AnimatorSet_BuilderClass get() = classOf<AnimatorSet.Builder>()

/**
 * Gets the [PropertyValuesHolder] type.
 * @return [Class]<[PropertyValuesHolder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PropertyValuesHolderClass get() = classOf<PropertyValuesHolder>()

/**
 * Gets the [ViewPropertyAnimator] type.
 * @return [Class]<[ViewPropertyAnimator]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ViewPropertyAnimatorClass get() = classOf<ViewPropertyAnimator>()

/**
 * Gets the [View.MeasureSpec] type.
 * @return [Class]<[View.MeasureSpec]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val View_MeasureSpecClass get() = classOf<View.MeasureSpec>()