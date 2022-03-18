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

/**
 * 获得 [View] 类型
 * @return [Class]
 */
val ViewClass get() = View::class.java

/**
 * 获得 [Surface] 类型
 * @return [Class]
 */
val SurfaceClass get() = Surface::class.java

/**
 * 获得 [SurfaceView] 类型
 * @return [Class]
 */
val SurfaceViewClass get() = SurfaceView::class.java

/**
 * 获得 [TextureView] 类型
 * @return [Class]
 */
val TextureViewClass get() = TextureView::class.java

/**
 * 获得 [WebView] 类型
 * @return [Class]
 */
val WebViewClass get() = WebView::class.java

/**
 * 获得 [WebViewClient] 类型
 * @return [Class]
 */
val WebViewClientClass get() = WebViewClient::class.java

/**
 * 获得 [ViewStructure] 类型
 * @return [Class]
 */
val ViewStructureClass get() = ViewStructure::class.java

/**
 * 获得 [ViewGroup] 类型
 * @return [Class]
 */
val ViewGroupClass get() = ViewGroup::class.java

/**
 * 获得 [ViewParent] 类型
 * @return [Class]
 */
val ViewParentClass get() = ViewParent::class.java

/**
 * 获得 [AppWidgetHostView] 类型
 * @return [Class]
 */
val AppWidgetHostViewClass get() = AppWidgetHostView::class.java

/**
 * 获得 [RemoteViews] 类型
 * @return [Class]
 */
val RemoteViewsClass get() = RemoteViews::class.java

/**
 * 获得 [RemoteView] 类型
 * @return [Class]
 */
val RemoteViewClass get() = RemoteView::class.java

/**
 * 获得 [TextView] 类型
 * @return [Class]
 */
val TextViewClass get() = TextView::class.java

/**
 * 获得 [ImageView] 类型
 * @return [Class]
 */
val ImageViewClass get() = ImageView::class.java

/**
 * 获得 [ImageButton] 类型
 * @return [Class]
 */
val ImageButtonClass get() = ImageButton::class.java

/**
 * 获得 [EditText] 类型
 * @return [Class]
 */
val EditTextClass get() = EditText::class.java

/**
 * 获得 [Button] 类型
 * @return [Class]
 */
val ButtonClass get() = Button::class.java

/**
 * 获得 [CheckBox] 类型
 * @return [Class]
 */
val CheckBoxClass get() = CheckBox::class.java

/**
 * 获得 [CompoundButton] 类型
 * @return [Class]
 */
val CompoundButtonClass get() = CompoundButton::class.java

/**
 * 获得 [VideoView] 类型
 * @return [Class]
 */
val VideoViewClass get() = VideoView::class.java

/**
 * 获得 [ListView] 类型
 * @return [Class]
 */
val ListViewClass get() = ListView::class.java

/**
 * 获得 [LayoutInflater] 类型
 * @return [Class]
 */
val LayoutInflaterClass get() = LayoutInflater::class.java

/**
 * 获得 [LayoutInflater.Filter] 类型
 * @return [Class]
 */
val LayoutInflater_FilterClass get() = LayoutInflater.Filter::class.java

/**
 * 获得 [LayoutInflater.Factory] 类型
 * @return [Class]
 */
val LayoutInflater_FactoryClass get() = LayoutInflater.Factory::class.java

/**
 * 获得 [LayoutInflater.Factory2] 类型
 * @return [Class]
 */
val LayoutInflater_Factory2Class get() = LayoutInflater.Factory2::class.java

/**
 * 获得 [ListAdapter] 类型
 * @return [Class]
 */
val ListAdapterClass get() = ListAdapter::class.java

/**
 * 获得 [ArrayAdapter] 类型
 * @return [Class]
 */
val ArrayAdapterClass get() = ArrayAdapter::class.java

/**
 * 获得 [BaseAdapter] 类型
 * @return [Class]
 */
val BaseAdapterClass get() = BaseAdapter::class.java

/**
 * 获得 [RelativeLayout] 类型
 * @return [Class]
 */
val RelativeLayoutClass get() = RelativeLayout::class.java

/**
 * 获得 [FrameLayout] 类型
 * @return [Class]
 */
val FrameLayoutClass get() = FrameLayout::class.java

/**
 * 获得 [LinearLayout] 类型
 * @return [Class]
 */
val LinearLayoutClass get() = LinearLayout::class.java

/**
 * 获得 [ViewGroup.LayoutParams] 类型
 * @return [Class]
 */
val ViewGroup_LayoutParamsClass get() = ViewGroup.LayoutParams::class.java

/**
 * 获得 [RelativeLayout.LayoutParams] 类型
 * @return [Class]
 */
val RelativeLayout_LayoutParamsClass get() = RelativeLayout.LayoutParams::class.java

/**
 * 获得 [LinearLayout.LayoutParams] 类型
 * @return [Class]
 */
val LinearLayout_LayoutParamsClass get() = LinearLayout.LayoutParams::class.java

/**
 * 获得 [FrameLayout.LayoutParams] 类型
 * @return [Class]
 */
val FrameLayout_LayoutParamsClass get() = FrameLayout.LayoutParams::class.java

/**
 * 获得 [TextClock] 类型
 * @return [Class]
 */
val TextClockClass get() = TextClock::class.java

/**
 * 获得 [MotionEvent] 类型
 * @return [Class]
 */
val MotionEventClass get() = MotionEvent::class.java

/**
 * 获得 [View.OnClickListener] 类型
 * @return [Class]
 */
val View_OnClickListenerClass get() = View.OnClickListener::class.java

/**
 * 获得 [View.OnLongClickListener] 类型
 * @return [Class]
 */
val View_OnLongClickListenerClass get() = View.OnLongClickListener::class.java

/**
 * 获得 [View.OnTouchListener] 类型
 * @return [Class]
 */
val View_OnTouchListenerClass get() = View.OnTouchListener::class.java

/**
 * 获得 [AutoCompleteTextView] 类型
 * @return [Class]
 */
val AutoCompleteTextViewClass get() = AutoCompleteTextView::class.java

/**
 * 获得 [ViewStub] 类型
 * @return [Class]
 */
val ViewStubClass get() = ViewStub::class.java

/**
 * 获得 [ViewStub.OnInflateListener] 类型
 * @return [Class]
 */
val ViewStub_OnInflateListenerClass get() = ViewStub.OnInflateListener::class.java

/**
 * 获得 [GestureDetector] 类型
 * @return [Class]
 */
val GestureDetectorClass get() = GestureDetector::class.java

/**
 * 获得 [GestureDetector.SimpleOnGestureListener] 类型
 * @return [Class]
 */
val GestureDetector_SimpleOnGestureListenerClass get() = GestureDetector.SimpleOnGestureListener::class.java

/**
 * 获得 [ProgressBar] 类型
 * @return [Class]
 */
val ProgressBarClass get() = ProgressBar::class.java

/**
 * 获得 [AttributeSet] 类型
 * @return [Class]
 */
val AttributeSetClass get() = AttributeSet::class.java

/**
 * 获得 [Animation] 类型
 * @return [Class]
 */
val AnimationClass get() = Animation::class.java

/**
 * 获得 [Animation.AnimationListener] 类型
 * @return [Class]
 */
val Animation_AnimationListenerClass get() = Animation.AnimationListener::class.java

/**
 * 获得 [TranslateAnimation] 类型
 * @return [Class]
 */
val TranslateAnimationClass get() = TranslateAnimation::class.java

/**
 * 获得 [AlphaAnimation] 类型
 * @return [Class]
 */
val AlphaAnimationClass get() = AlphaAnimation::class.java

/**
 * 获得 [Animator] 类型
 * @return [Class]
 */
val AnimatorClass get() = Animator::class.java

/**
 * 获得 [Animator.AnimatorListener] 类型
 * @return [Class]
 */
val Animator_AnimatorListenerClass get() = Animator.AnimatorListener::class.java

/**
 * 获得 [ObjectAnimator] 类型
 * @return [Class]
 */
val ObjectAnimatorClass get() = ObjectAnimator::class.java

/**
 * 获得 [ValueAnimator] 类型
 * @return [Class]
 */
val ValueAnimatorClass get() = ValueAnimator::class.java

/**
 * 获得 [ValueAnimator.AnimatorUpdateListener] 类型
 * @return [Class]
 */
val ValueAnimator_AnimatorUpdateListenerClass get() = ValueAnimator.AnimatorUpdateListener::class.java

/**
 * 获得 [ViewAnimator] 类型
 * @return [Class]
 */
val ViewAnimatorClass get() = ViewAnimator::class.java

/**
 * 获得 [AnimatorSet] 类型
 * @return [Class]
 */
val AnimatorSetClass get() = AnimatorSet::class.java

/**
 * 获得 [AnimatorSet.Builder] 类型
 * @return [Class]
 */
val AnimatorSet_BuilderClass get() = AnimatorSet.Builder::class.java

/**
 * 获得 [PropertyValuesHolder] 类型
 * @return [Class]
 */
val PropertyValuesHolderClass get() = PropertyValuesHolder::class.java

/**
 * 获得 [ViewPropertyAnimator] 类型
 * @return [Class]
 */
val ViewPropertyAnimatorClass get() = ViewPropertyAnimator::class.java

/**
 * 获得 [View.MeasureSpec] 类型
 * @return [Class]
 */
val View_MeasureSpecClass get() = View.MeasureSpec::class.java