package com.fm.mvi.model

import com.fm.mvi.base.MviResult

sealed class MonitoringResult : MviResult {
    object MonitoringStarted : MonitoringResult()
    object MonitoringStopped : MonitoringResult()
    object MonitoringInitializing : MonitoringResult()
}