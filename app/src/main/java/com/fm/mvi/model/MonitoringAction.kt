package com.fm.mvi.model

import com.fm.mvi.base.MviAction

sealed class MonitoringAction : MviAction {
    object StartMonitoringAction : MonitoringAction()
    object StopMonitoringAction : MonitoringAction()
    object InitializeMonitoringAction : MonitoringAction()
    data class CloseAlertMonitoringAction(val pendingError: Boolean) : MonitoringAction()
    data class DisplayAlertMonitoringAction(val message: String) : MonitoringAction()
    object ErrorMonitoringAction : MonitoringAction()
    object ResetErrorMonitoringAction : MonitoringAction()
}