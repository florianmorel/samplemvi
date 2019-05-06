package com.fm.mvi.presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

data class MonitoringViewModelFactory @Inject constructor(private val monitoringActionProcessor: MonitoringActionProcessor) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MonitoringViewModel(monitoringActionProcessor) as T
}