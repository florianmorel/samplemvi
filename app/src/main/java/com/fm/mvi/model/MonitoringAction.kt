package com.fm.mvi.model

import com.fm.mvi.base.MviAction

sealed class MonitoringAction : MviAction {
    object StartMonitoringAction : MonitoringAction()
    object StopMonitoringAction : MonitoringAction()
    object InitializeMonitoringAction : MonitoringAction()
}