package com.fm.mvi.view

sealed class MonitoringViewAction {
    object StartClicked: MonitoringViewAction()
    object StopClicked: MonitoringViewAction()
    object ResetClicked: MonitoringViewAction()
}