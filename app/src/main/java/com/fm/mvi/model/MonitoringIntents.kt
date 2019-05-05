package com.fm.mvi.model

import com.fm.mvi.base.MviIntent

sealed class MonitoringIntents : MviIntent {
    object StartIntent : MonitoringIntents()
    object StopIntent : MonitoringIntents()
}