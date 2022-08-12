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
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.type.android

import android.animation.*
import android.appwidget.AppWidgetHostView
import android.util.AttributeSet
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import android.widget.RemoteViews.RemoteView
import com.highcapable.yukihookapi.hook.factory.classOf

/**
 * 获得 [View] 类型
 * @return [Class]
 */
val ViewClass get() = classOf<View>()

/**
 * 获得 [Surface] 类型
 * @return [Class]
 */
val SurfaceClass get() = classOf<Surface>()

/**
 * 获得 [SurfaceView] 类型
 * @return [Class]
 */
val SurfaceViewClass get() = classOf<SurfaceView>()

/**
 * 获得 [TextureView] 类型
 * @return [Class]
 */
val TextureViewClass get() = classOf<TextureView>()

/**
 * 获得 [WebView] 类型
 * @return [Class]
 */
val WebViewClass get() = classOf<WebView>()

/**
 * 获得 [WebViewClient] 类型
 * @return [Class]
 */
val WebViewClientClass get() = classOf<WebViewClient>()

/**
 * 获得 [ViewStructure] 类型
 * @return [Class]
 */
val ViewStructureClass get() = classOf<ViewStructure>()

/**
 * 获得 [ViewGroup] 类型
 * @return [Class]
 */
val ViewGroupClass get() = classOf<ViewGroup>()

/**
 * 获得 [ViewParent] 类型
 * @return [Class]
 */
val ViewParentClass get() = classOf<ViewParent>()

/**
 * 获得 [AppWidgetHostView] 类型
 * @return [Class]
 */
val AppWidgetHostViewClass get() = classOf<AppWidgetHostView>()

/**
 * 获得 [RemoteViews] 类型
 * @return [Class]
 */
val RemoteViewsClass get() = classOf<RemoteViews>()

/**
 * 获得 [RemoteView] 类型
 * @return [Class]
 */
val RemoteViewClass get() = classOf<RemoteView>()

/**
 * 获得 [TextView] 类型
 * @return [Class]
 */
val TextViewClass get() = classOf<TextView>()

/**
 * 获得 [ImageView] 类型
 * @return [Class]
 */
val ImageViewClass get() = classOf<ImageView>()

/**
 * 获得 [ImageButton] 类型
 * @return [Class]
 */
val ImageButtonClass get() = classOf<ImageButton>()

/**
 * 获得 [EditText] 类型
 * @return [Class]
 */
val EditTextClass get() = classOf<EditText>()

/**
 * 获得 [Button] 类型
 * @return [Class]
 */
val ButtonClass get() = classOf<Button>()

/**
 * 获得 [CheckBox] 类型
 * @return [Class]
 */
val CheckBoxClass get() = classOf<CheckBox>()

/**
 * 获得 [CompoundButton] 类型
 * @return [Class]
 */
val CompoundButtonClass get() = classOf<CompoundButton>()

/**
 * 获得 [VideoView] 类型
 * @return [Class]
 */
val VideoViewClass get() = classOf<VideoView>()

/**
 * 获得 [ListView] 类型
 * @return [Class]
 */
val ListViewClass get() = classOf<ListView>()

/**
 * 获得 [LayoutInflater] 类型
 * @return [Class]
 */
val LayoutInflaterClass get() = classOf<LayoutInflater>()

/**
 * 获得 [LayoutInflater.Filter] 类型
 * @return [Class]
 */
val LayoutInflater_FilterClass get() = classOf<LayoutInflater.Filter>()

/**
 * 获得 [LayoutInflater.Factory] 类型
 * @return [Class]
 */
val LayoutInflater_FactoryClass get() = classOf<LayoutInflater.Factory>()

/**
 * 获得 [LayoutInflater.Factory2] 类型
 * @return [Class]
 */
val LayoutInflater_Factory2Class get() = classOf<LayoutInflater.Factory2>()

/**
 * 获得 [ListAdapter] 类型
 * @return [Class]
 */
val ListAdapterClass get() = classOf<ListAdapter>()

/**
 * 获得 [ArrayAdapter] 类型
 * @return [Class]
 */
val ArrayAdapterClass get() = classOf<ArrayAdapter<*>>()

/**
 * 获得 [BaseAdapter] 类型
 * @return [Class]
 */
val BaseAdapterClass get() = classOf<BaseAdapter>()

/**
 * 获得 [RelativeLayout] 类型
 * @return [Class]
 */
val RelativeLayoutClass get() = classOf<RelativeLayout>()

/**
 * 获得 [FrameLayout] 类型
 * @return [Class]
 */
val FrameLayoutClass get() = classOf<FrameLayout>()

/**
 * 获得 [LinearLayout] 类型
 * @return [Class]
 */
val LinearLayoutClass get() = classOf<LinearLayout>()

/**
 * 获得 [ViewGroup.LayoutParams] 类型
 * @return [Class]
 */
val ViewGroup_LayoutParamsClass get() = classOf<ViewGroup.LayoutParams>()

/**
 * 获得 [RelativeLayout.LayoutParams] 类型
 * @return [Class]
 */
val RelativeLayout_LayoutParamsClass get() = classOf<RelativeLayout.LayoutParams>()

/**
 * 获得 [LinearLayout.LayoutParams] 类型
 * @return [Class]
 */
val LinearLayout_LayoutParamsClass get() = classOf<LinearLayout.LayoutParams>()

/**
 * 获得 [FrameLayout.LayoutParams] 类型
 * @return [Class]
 */
val FrameLayout_LayoutParamsClass get() = classOf<FrameLayout.LayoutParams>()

/**
 * 获得 [TextClock] 类型
 * @return [Class]
 */
val TextClockClass get() = classOf<TextClock>()

/**
 * 获得 [MotionEvent] 类型
 * @return [Class]
 */
val MotionEventClass get() = classOf<MotionEvent>()

/**
 * 获得 [View.OnClickListener] 类型
 * @return [Class]
 */
val View_OnClickListenerClass get() = classOf<View.OnClickListener>()

/**
 * 获得 [View.OnLongClickListener] 类型
 * @return [Class]
 */
val View_OnLongClickListenerClass get() = classOf<View.OnLongClickListener>()

/**
 * 获得 [View.OnTouchListener] 类型
 * @return [Class]
 */
val View_OnTouchListenerClass get() = classOf<View.OnTouchListener>()

/**
 * 获得 [AutoCompleteTextView] 类型
 * @return [Class]
 */
val AutoCompleteTextViewClass get() = classOf<AutoCompleteTextView>()

/**
 * 获得 [ViewStub] 类型
 * @return [Class]
 */
val ViewStubClass get() = classOf<ViewStub>()

/**
 * 获得 [ViewStub.OnInflateListener] 类型
 * @return [Class]
 */
val ViewStub_OnInflateListenerClass get() = classOf<ViewStub.OnInflateListener>()

/**
 * 获得 [GestureDetector] 类型
 * @return [Class]
 */
val GestureDetectorClass get() = classOf<GestureDetector>()

/**
 * 获得 [GestureDetector.SimpleOnGestureListener] 类型
 * @return [Class]
 */
val GestureDetector_SimpleOnGestureListenerClass get() = classOf<GestureDetector.SimpleOnGestureListener>()

/**
 * 获得 [ProgressBar] 类型
 * @return [Class]
 */
val ProgressBarClass get() = classOf<ProgressBar>()

/**
 * 获得 [AttributeSet] 类型
 * @return [Class]
 */
val AttributeSetClass get() = classOf<AttributeSet>()

/**
 * 获得 [Animation] 类型
 * @return [Class]
 */
val AnimationClass get() = classOf<Animation>()

/**
 * 获得 [Animation.AnimationListener] 类型
 * @return [Class]
 */
val Animation_AnimationListenerClass get() = classOf<Animation.AnimationListener>()

/**
 * 获得 [TranslateAnimation] 类型
 * @return [Class]
 */
val TranslateAnimationClass get() = classOf<TranslateAnimation>()

/**
 * 获得 [AlphaAnimation] 类型
 * @return [Class]
 */
val AlphaAnimationClass get() = classOf<AlphaAnimation>()

/**
 * 获得 [Animator] 类型
 * @return [Class]
 */
val AnimatorClass get() = classOf<Animator>()

/**
 * 获得 [Animator.AnimatorListener] 类型
 * @return [Class]
 */
val Animator_AnimatorListenerClass get() = classOf<Animator.AnimatorListener>()

/**
 * 获得 [ObjectAnimator] 类型
 * @return [Class]
 */
val ObjectAnimatorClass get() = classOf<ObjectAnimator>()

/**
 * 获得 [ValueAnimator] 类型
 * @return [Class]
 */
val ValueAnimatorClass get() = classOf<ValueAnimator>()

/**
 * 获得 [ValueAnimator.AnimatorUpdateListener] 类型
 * @return [Class]
 */
val ValueAnimator_AnimatorUpdateListenerClass get() = classOf<ValueAnimator.AnimatorUpdateListener>()

/**
 * 获得 [ViewAnimator] 类型
 * @return [Class]
 */
val ViewAnimatorClass get() = classOf<ViewAnimator>()

/**
 * 获得 [AnimatorSet] 类型
 * @return [Class]
 */
val AnimatorSetClass get() = classOf<AnimatorSet>()

/**
 * 获得 [AnimatorSet.Builder] 类型
 * @return [Class]
 */
val AnimatorSet_BuilderClass get() = classOf<AnimatorSet.Builder>()

/**
 * 获得 [PropertyValuesHolder] 类型
 * @return [Class]
 */
val PropertyValuesHolderClass get() = classOf<PropertyValuesHolder>()

/**
 * 获得 [ViewPropertyAnimator] 类型
 * @return [Class]
 */
val ViewPropertyAnimatorClass get() = classOf<ViewPropertyAnimator>()

/**
 * 获得 [View.MeasureSpec] 类型
 * @return [Class]
 */
val View_MeasureSpecClass get() = classOf<View.MeasureSpec>()