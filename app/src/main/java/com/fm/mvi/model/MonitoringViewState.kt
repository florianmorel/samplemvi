package com.fm.mvi.model

import com.fm.mvi.base.MviViewState

sealed class MonitoringViewState : MviViewState {

    object Stopped : MonitoringViewState() {
        fun startMonitoring() = Started
    }

    object Started : MonitoringViewState() {
        fun stopMonitoring() = Stopped
    }

    object Initialization: MonitoringViewState()

    companion object {
        fun idle(): MonitoringViewState = Stopped
    }
}