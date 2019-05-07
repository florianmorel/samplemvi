package com.fm.mvi.model

import com.fm.mvi.base.MviViewState

sealed class MonitoringViewState : MviViewState {

    object Stopped : MonitoringViewState()

    object Started : MonitoringViewState()

    object Initialization: MonitoringViewState()

    data class Alert(val alertMessage: String): MonitoringViewState()

    companion object {
        fun idle(): MonitoringViewState = Stopped
    }
}