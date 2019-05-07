package com.fm.mvi.model

import com.fm.mvi.base.MviIntent

sealed class MonitoringIntents : MviIntent {
    object StartMonitoringIntent : MonitoringIntents()
    object StopMonitoringIntent : MonitoringIntents()
    object InitializeMonitoringIntent : MonitoringIntents()
    data class CloseAlertMonitoringIntent(val pendingError: Boolean) : MonitoringIntents()
    data class ReceivedAlertMonitoringIntent(val message: String) : MonitoringIntents()
    object ReceivedErrorMonitoringIntent : MonitoringIntents()
    object ResetMonitoringIntent : MonitoringIntents()
}