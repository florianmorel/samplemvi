package com.fm.mvi.model

import com.fm.mvi.base.MviIntent

sealed class MonitoringIntents : MviIntent {
    object StartMonitoringIntent : MonitoringIntents()
    object StopMonitoringIntent : MonitoringIntents()
    object InitializeMonitoringIntent : MonitoringIntents()
    data class AlertMonitoringIntent(val message: String) : MonitoringIntents()
}