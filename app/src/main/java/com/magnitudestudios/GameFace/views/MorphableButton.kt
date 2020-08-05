/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */
package com.magnitudestudios.GameFace.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatButton
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.magnitudestudios.GameFace.R

class MorphableButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : AppCompatButton(context, attrs, defStyle), LifecycleObserver {

    private var state = State.IDLE
    private val drawable = ContextCompat.getDrawable(context, R.drawable.morphable_btn_background) as GradientDrawable
    private var animationInProgress = false
    private var mAnimatedDrawable: CircularAnimatedDrawable? = null


    private lateinit var mAnimationSet: AnimatorSet
    private var initialWidth = width

    private var initialText = ""

    private enum class State {
        PROGRESS, IDLE
    }

    init {
        background = drawable
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun switchAnimation(fromWidth: Int,
                                toWidth: Int,
                                fromRadius: Float,
                                toRadius: Float) {
        animationInProgress = true
        text = null

        val cornerAnimation = ObjectAnimator.ofFloat(
                drawable,
                "cornerRadius",
                fromRadius,
                toRadius)
        val widthAnimation = ValueAnimator.ofInt(fromWidth, toWidth);
        widthAnimation.addUpdateListener {
            updateLayoutParams { width = it.animatedValue as Int }
        }

        mAnimationSet = AnimatorSet().apply {
            duration = 300
            playTogether(cornerAnimation, widthAnimation)
        }
        mAnimationSet.doOnEnd {
            animationInProgress = false
            if (state == State.IDLE) text = initialText

        }
        mAnimationSet.start()
    }

    private fun drawIntermediateProgress(canvas: Canvas) {
        if (mAnimatedDrawable == null || mAnimatedDrawable!!.isRunning) {
            mAnimatedDrawable = CircularAnimatedDrawable(this, 10f, Color.WHITE)
            val offset = (width - height) / 2
            mAnimatedDrawable!!.updateBounds(offset, 0, width - offset, height)
            mAnimatedDrawable!!.callback = this
            mAnimatedDrawable!!.start()
        } else {
            mAnimatedDrawable!!.draw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (state == State.PROGRESS && !animationInProgress) drawIntermediateProgress(canvas!!)
    }

    fun setLoading(boolean: Boolean) {
        if (width == 0) return
        mAnimatedDrawable?.stop()
        mAnimatedDrawable = null
        if (boolean) {
            initialWidth = width
            initialText = text.toString()
            switchAnimation(width, height, resources.getDimension(R.dimen.rounded_rect), 1000f)
            state = State.PROGRESS
            isClickable = false
        } else {
            state = State.IDLE
            switchAnimation(width, initialWidth, 1000f, context.resources.getDimension(R.dimen.rounded_rect))
            isClickable = true
        }
    }

    class CircularAnimatedDrawable(val animView: View, val borderWidth: Float, arcColor: Int) : Drawable(), Animatable {
        private val paint: Paint = Paint()
        private var inProgress = false
        private var valueAnimatorAngle: ValueAnimator? = null
        private var valueAnimatorSweep: ValueAnimator? = null

        private var mCurrentGlobalAngle = 0f
        private var mCurrentSweepAngle = 0f
        private var mCurrentGlobalAngleOffset = 0f

        private val mBounds: RectF by lazy {
            RectF().apply {
                left = bounds.left.toFloat() + borderWidth / 2F + .5F
                right = bounds.right.toFloat() - borderWidth / 2F - .5F
                top = bounds.top.toFloat() + borderWidth / 2F + .5F
                bottom = bounds.bottom.toFloat() - borderWidth / 2F - .5F
            }
        }
        private var modeAppearing = false

        init {
            paint.apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = borderWidth
                color = arcColor
            }
            setUpAnimations()
        }

        override fun draw(canvas: Canvas) {
            var startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset
            var sweepAngle = mCurrentSweepAngle
            if (modeAppearing) {
                startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset
                sweepAngle = mCurrentSweepAngle + 30f
            } else {
                startAngle = (mCurrentGlobalAngle - mCurrentGlobalAngleOffset + mCurrentSweepAngle)
                sweepAngle = 360F - mCurrentSweepAngle - 30f
            }
            canvas.drawArc(mBounds, startAngle, sweepAngle, false, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        override fun isRunning(): Boolean {
            return false
        }

        override fun start() {
            if (inProgress) return
            inProgress = true
            valueAnimatorAngle?.start()
            valueAnimatorSweep?.start()

        }

        override fun stop() {
            if (!inProgress) return
            inProgress = false
            valueAnimatorAngle?.cancel()
            valueAnimatorSweep?.cancel()
        }

        private fun toggleSweep() {
            modeAppearing = !modeAppearing

            if (modeAppearing) {
                mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + 30f * 2) % 360
            }
        }

        private fun setUpAnimations() {
            valueAnimatorAngle = ValueAnimator.ofFloat(0f, 360f).apply {
                interpolator = LinearInterpolator()
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                addUpdateListener {
                    animView.invalidate()
                    mCurrentGlobalAngle = it.animatedValue as Float
                }
            }

            valueAnimatorSweep = ValueAnimator.ofFloat(0f, 360f - 2 * 30f).apply {
                interpolator = DecelerateInterpolator()
                duration = 900
                repeatCount = ValueAnimator.INFINITE
                addUpdateListener {
                    mCurrentSweepAngle = it.animatedValue as Float
                    invalidateSelf()
                }
                addListener {
                    toggleSweep()
                }
            }
        }
    }

}