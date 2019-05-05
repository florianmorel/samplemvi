package com.fm.mvi.model

sealed class MonitoringState {
    object STOP : MonitoringState()
    object INIT : MonitoringState()
    object START : MonitoringState()
    data class ALERT(val throwable: Throwable) : MonitoringState()
    data class ERROR(val message: String) : MonitoringState()
}