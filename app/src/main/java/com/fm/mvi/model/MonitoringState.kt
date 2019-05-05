package com.fm.mvi.model

import com.fm.mvi.base.MviViewState

sealed class MonitoringState : MviViewState {

    object Stopped : MonitoringState() {
        fun startMonitoring() = Started
    }

    object Started : MonitoringState() {
        fun stpMonitoring() = Stopped
    }
}