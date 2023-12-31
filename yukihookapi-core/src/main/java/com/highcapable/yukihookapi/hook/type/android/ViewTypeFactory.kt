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
@file:Suppress("unused")

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
import com.highcapable.yukihookapi.hook.factory.classOf

/**
 * 获得 [View] 类型
 * @return [Class]<[View]>
 */
val ViewClass get() = classOf<View>()

/**
 * 获得 [Surface] 类型
 * @return [Class]<[Surface]>
 */
val SurfaceClass get() = classOf<Surface>()

/**
 * 获得 [SurfaceView] 类型
 * @return [Class]<[SurfaceView]>
 */
val SurfaceViewClass get() = classOf<SurfaceView>()

/**
 * 获得 [TextureView] 类型
 * @return [Class]<[TextureView]>
 */
val TextureViewClass get() = classOf<TextureView>()

/**
 * 获得 [WebView] 类型
 * @return [Class]<[WebView]>
 */
val WebViewClass get() = classOf<WebView>()

/**
 * 获得 [WebViewClient] 类型
 * @return [Class]<[WebViewClient]>
 */
val WebViewClientClass get() = classOf<WebViewClient>()

/**
 * 获得 [ViewStructure] 类型
 * @return [Class]<[ViewStructure]>
 */
val ViewStructureClass get() = classOf<ViewStructure>()

/**
 * 获得 [ViewGroup] 类型
 * @return [Class]<[ViewGroup]>
 */
val ViewGroupClass get() = classOf<ViewGroup>()

/**
 * 获得 [ViewParent] 类型
 * @return [Class]<[ViewParent]>
 */
val ViewParentClass get() = classOf<ViewParent>()

/**
 * 获得 [AppWidgetHostView] 类型
 * @return [Class]<[AppWidgetHostView]>
 */
val AppWidgetHostViewClass get() = classOf<AppWidgetHostView>()

/**
 * 获得 [RemoteViews] 类型
 * @return [Class]<[RemoteViews]>
 */
val RemoteViewsClass get() = classOf<RemoteViews>()

/**
 * 获得 [RemoteView] 类型
 * @return [Class]<[RemoteView]>
 */
val RemoteViewClass get() = classOf<RemoteView>()

/**
 * 获得 [TextView] 类型
 * @return [Class]<[TextView]>
 */
val TextViewClass get() = classOf<TextView>()

/**
 * 获得 [ImageView] 类型
 * @return [Class]<[ImageView]>
 */
val ImageViewClass get() = classOf<ImageView>()

/**
 * 获得 [ImageButton] 类型
 * @return [Class]<[ImageButton]>
 */
val ImageButtonClass get() = classOf<ImageButton>()

/**
 * 获得 [EditText] 类型
 * @return [Class]<[EditText]>
 */
val EditTextClass get() = classOf<EditText>()

/**
 * 获得 [Button] 类型
 * @return [Class]<[Button]>
 */
val ButtonClass get() = classOf<Button>()

/**
 * 获得 [CheckBox] 类型
 * @return [Class]<[CheckBox]>
 */
val CheckBoxClass get() = classOf<CheckBox>()

/**
 * 获得 [CompoundButton] 类型
 * @return [Class]<[CompoundButton]>
 */
val CompoundButtonClass get() = classOf<CompoundButton>()

/**
 * 获得 [VideoView] 类型
 * @return [Class]<[VideoView]>
 */
val VideoViewClass get() = classOf<VideoView>()

/**
 * 获得 [ListView] 类型
 * @return [Class]<[ListView]>
 */
val ListViewClass get() = classOf<ListView>()

/**
 * 获得 [LayoutInflater] 类型
 * @return [Class]<[LayoutInflater]>
 */
val LayoutInflaterClass get() = classOf<LayoutInflater>()

/**
 * 获得 [LayoutInflater.Filter] 类型
 * @return [Class]<[LayoutInflater.Filter]>
 */
val LayoutInflater_FilterClass get() = classOf<LayoutInflater.Filter>()

/**
 * 获得 [LayoutInflater.Factory] 类型
 * @return [Class]<[LayoutInflater.Factory]>
 */
val LayoutInflater_FactoryClass get() = classOf<LayoutInflater.Factory>()

/**
 * 获得 [LayoutInflater.Factory2] 类型
 * @return [Class]<[LayoutInflater.Factory2]>
 */
val LayoutInflater_Factory2Class get() = classOf<LayoutInflater.Factory2>()

/**
 * 获得 [ListAdapter] 类型
 * @return [Class]<[ListAdapter]>
 */
val ListAdapterClass get() = classOf<ListAdapter>()

/**
 * 获得 [ArrayAdapter] 类型
 * @return [Class]<[ArrayAdapter]>
 */
val ArrayAdapterClass get() = classOf<ArrayAdapter<*>>()

/**
 * 获得 [BaseAdapter] 类型
 * @return [Class]<[BaseAdapter]>
 */
val BaseAdapterClass get() = classOf<BaseAdapter>()

/**
 * 获得 [RelativeLayout] 类型
 * @return [Class]<[RelativeLayout]>
 */
val RelativeLayoutClass get() = classOf<RelativeLayout>()

/**
 * 获得 [FrameLayout] 类型
 * @return [Class]<[FrameLayout]>
 */
val FrameLayoutClass get() = classOf<FrameLayout>()

/**
 * 获得 [LinearLayout] 类型
 * @return [Class]<[LinearLayout]>
 */
val LinearLayoutClass get() = classOf<LinearLayout>()

/**
 * 获得 [ViewGroup.LayoutParams] 类型
 * @return [Class]<[ViewGroup.LayoutParams]>
 */
val ViewGroup_LayoutParamsClass get() = classOf<ViewGroup.LayoutParams>()

/**
 * 获得 [RelativeLayout.LayoutParams] 类型
 * @return [Class]<[RelativeLayout.LayoutParams]>
 */
val RelativeLayout_LayoutParamsClass get() = classOf<RelativeLayout.LayoutParams>()

/**
 * 获得 [LinearLayout.LayoutParams] 类型
 * @return [Class]<[LinearLayout.LayoutParams]>
 */
val LinearLayout_LayoutParamsClass get() = classOf<LinearLayout.LayoutParams>()

/**
 * 获得 [FrameLayout.LayoutParams] 类型
 * @return [Class]<[FrameLayout.LayoutParams]>
 */
val FrameLayout_LayoutParamsClass get() = classOf<FrameLayout.LayoutParams>()

/**
 * 获得 [TextClock] 类型
 * @return [Class]<[TextClock]>
 */
val TextClockClass get() = classOf<TextClock>()

/**
 * 获得 [MotionEvent] 类型
 * @return [Class]<[MotionEvent]>
 */
val MotionEventClass get() = classOf<MotionEvent>()

/**
 * 获得 [View.OnClickListener] 类型
 * @return [Class]<[View.OnClickListener]>
 */
val View_OnClickListenerClass get() = classOf<View.OnClickListener>()

/**
 * 获得 [View.OnLongClickListener] 类型
 * @return [Class]<[View.OnLongClickListener]>
 */
val View_OnLongClickListenerClass get() = classOf<View.OnLongClickListener>()

/**
 * 获得 [View.OnTouchListener] 类型
 * @return [Class]<[View.OnTouchListener]>
 */
val View_OnTouchListenerClass get() = classOf<View.OnTouchListener>()

/**
 * 获得 [AutoCompleteTextView] 类型
 * @return [Class]<[AutoCompleteTextView]>
 */
val AutoCompleteTextViewClass get() = classOf<AutoCompleteTextView>()

/**
 * 获得 [ViewStub] 类型
 * @return [Class]<[ViewStub]>
 */
val ViewStubClass get() = classOf<ViewStub>()

/**
 * 获得 [ViewStub.OnInflateListener] 类型
 * @return [Class]<[ViewStub.OnInflateListener]>
 */
val ViewStub_OnInflateListenerClass get() = classOf<ViewStub.OnInflateListener>()

/**
 * 获得 [GestureDetector] 类型
 * @return [Class]<[GestureDetector]>
 */
val GestureDetectorClass get() = classOf<GestureDetector>()

/**
 * 获得 [GestureDetector.SimpleOnGestureListener] 类型
 * @return [Class]<[GestureDetector.SimpleOnGestureListener]>
 */
val GestureDetector_SimpleOnGestureListenerClass get() = classOf<GestureDetector.SimpleOnGestureListener>()

/**
 * 获得 [ProgressBar] 类型
 * @return [Class]<[ProgressBar]>
 */
val ProgressBarClass get() = classOf<ProgressBar>()

/**
 * 获得 [AttributeSet] 类型
 * @return [Class]<[AttributeSet]>
 */
val AttributeSetClass get() = classOf<AttributeSet>()

/**
 * 获得 [Animation] 类型
 * @return [Class]<[Animation]>
 */
val AnimationClass get() = classOf<Animation>()

/**
 * 获得 [Animation.AnimationListener] 类型
 * @return [Class]<[Animation.AnimationListener]>
 */
val Animation_AnimationListenerClass get() = classOf<Animation.AnimationListener>()

/**
 * 获得 [TranslateAnimation] 类型
 * @return [Class]<[TranslateAnimation]>
 */
val TranslateAnimationClass get() = classOf<TranslateAnimation>()

/**
 * 获得 [AlphaAnimation] 类型
 * @return [Class]<[AlphaAnimation]>
 */
val AlphaAnimationClass get() = classOf<AlphaAnimation>()

/**
 * 获得 [Animator] 类型
 * @return [Class]<[Animator]>
 */
val AnimatorClass get() = classOf<Animator>()

/**
 * 获得 [Animator.AnimatorListener] 类型
 * @return [Class]<[Animator.AnimatorListener]>
 */
val Animator_AnimatorListenerClass get() = classOf<Animator.AnimatorListener>()

/**
 * 获得 [ObjectAnimator] 类型
 * @return [Class]<[ObjectAnimator]>
 */
val ObjectAnimatorClass get() = classOf<ObjectAnimator>()

/**
 * 获得 [ValueAnimator] 类型
 * @return [Class]<[ValueAnimator]>
 */
val ValueAnimatorClass get() = classOf<ValueAnimator>()

/**
 * 获得 [ValueAnimator.AnimatorUpdateListener] 类型
 * @return [Class]<[ValueAnimator.AnimatorUpdateListener]>
 */
val ValueAnimator_AnimatorUpdateListenerClass get() = classOf<ValueAnimator.AnimatorUpdateListener>()

/**
 * 获得 [ViewAnimator] 类型
 * @return [Class]<[ViewAnimator]>
 */
val ViewAnimatorClass get() = classOf<ViewAnimator>()

/**
 * 获得 [AnimatorSet] 类型
 * @return [Class]<[AnimatorSet]>
 */
val AnimatorSetClass get() = classOf<AnimatorSet>()

/**
 * 获得 [AnimatorSet.Builder] 类型
 * @return [Class]<[AnimatorSet.Builder]>
 */
val AnimatorSet_BuilderClass get() = classOf<AnimatorSet.Builder>()

/**
 * 获得 [PropertyValuesHolder] 类型
 * @return [Class]<[PropertyValuesHolder]>
 */
val PropertyValuesHolderClass get() = classOf<PropertyValuesHolder>()

/**
 * 获得 [ViewPropertyAnimator] 类型
 * @return [Class]<[ViewPropertyAnimator]>
 */
val ViewPropertyAnimatorClass get() = classOf<ViewPropertyAnimator>()

/**
 * 获得 [View.MeasureSpec] 类型
 * @return [Class]<[View.MeasureSpec]>
 */
val View_MeasureSpecClass get() = classOf<View.MeasureSpec>()