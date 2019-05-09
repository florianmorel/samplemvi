package com.fm.mvi.widget

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.fm.mvi.R

object MonitoringImageViewUtil {

    internal const val STOPPED = "0"
    internal const val INITIALIZING = "1"
    internal const val STARTED = "2"
    internal const val ALERT = "3"
    internal const val ERROR = "4"
    internal const val UNKNOWN = "5"

    fun selectEnterStateTransition(context: Context, state: String?): Animation =
        when (state) {
            STOPPED -> AnimationUtils.loadAnimation(context, R.anim.enter_stop_animation)
            INITIALIZING -> AnimationUtils.loadAnimation(context, R.anim.enter_init_animation)
            STARTED -> AnimationUtils.loadAnimation(context, R.anim.enter_start_animation)
            ALERT -> AnimationUtils.loadAnimation(context, R.anim.enter_alert_animation)
            ERROR -> AnimationUtils.loadAnimation(context, R.anim.enter_error_animation)
            UNKNOWN -> AnimationUtils.loadAnimation(context, R.anim.no_animation)
            else -> AnimationUtils.loadAnimation(context, R.anim.no_animation)
        }

    fun selectExitStateTransition(context: Context, state: String?): Animation =
        when (state) {
            STOPPED -> AnimationUtils.loadAnimation(context, R.anim.exit_stop_animation)
            INITIALIZING -> AnimationUtils.loadAnimation(context, R.anim.exit_init_animation)
            STARTED -> AnimationUtils.loadAnimation(context, R.anim.exit_start_animation)
            ALERT -> AnimationUtils.loadAnimation(context, R.anim.exit_alert_animation)
            ERROR -> AnimationUtils.loadAnimation(context, R.anim.exit_error_animation)
            UNKNOWN -> AnimationUtils.loadAnimation(context, R.anim.no_animation)
            else -> AnimationUtils.loadAnimation(context, R.anim.no_animation)
        }

    fun selectSetupStateTransition(context: Context, state: String?): Animation =
        AnimationUtils.loadAnimation(context, R.anim.setup_animation)

}