package com.fm.mvi.presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.fm.mvi.service.AlertService
import javax.inject.Inject

data class MonitoringViewModelFactory @Inject constructor(private val monitoringActionProcessor: MonitoringActionProcessor,
                                                          private val alertService: AlertService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MonitoringViewModel(monitoringActionProcessor, alertService) as T
}