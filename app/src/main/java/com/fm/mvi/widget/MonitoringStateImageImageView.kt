package com.fm.mvi.widget

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.fm.mvi.R

class MonitoringStateImageImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ImageView(context, attrs) {

    private lateinit var enterAnimation: Animation
    private lateinit var exitAnimation: Animation
    private lateinit var setupAnimation: Animation

    private var isEnable: Boolean = false

    init {
        applyAnimation(attrs)
    }

    private fun applyAnimation(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            with(
                context.obtainStyledAttributes(
                    attributeSet,
                    R.styleable.MonitoringStateImageImageView
                )
            ) {
                val state = getString(R.styleable.MonitoringStateImageImageView_state)
                enterAnimation = MonitoringImageViewUtil.selectEnterStateTransition(context, state)
                exitAnimation = MonitoringImageViewUtil.selectExitStateTransition(context, state)
                setupAnimation = MonitoringImageViewUtil.selectSetupStateTransition(context, state)
                recycle()
            }
        }
    }
    fun enableState() {
        if (isEnable.not()) {
            animateEnable()
        }
        isEnable = true
    }

    fun disableState() {
        if (isEnable) {
            animateDisable()
        } else {
            ensureDisable()
        }
        isEnable = false
    }

    private fun animateEnable() {
        this.animation = enterAnimation
        this.animation.start()
    }

    private fun animateDisable() {
        this.animation = exitAnimation
        this.animation.start()
    }

    private fun ensureDisable() {
        this.animation = setupAnimation
        this.animation.start()
    }

}