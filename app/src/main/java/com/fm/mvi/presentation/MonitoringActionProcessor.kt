package com.fm.mvi.presentation

import com.fm.mvi.model.MonitoringAction
import com.fm.mvi.model.MonitoringResult
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject


class MonitoringActionProcessor @Inject constructor() {

    fun transformFromAction(): ObservableTransformer<MonitoringAction, MonitoringResult> {
        return ObservableTransformer { action ->
            action.publish { shared ->
                Observable.merge(
                    shared.ofType(MonitoringAction.StartMonitoringAction::class.java).compose(startMonitoringProcessor),
                    shared.ofType(MonitoringAction.StopMonitoringAction::class.java).compose(stopMonitoringProcessor)
                )
            }
        }
    }

    private val startMonitoringProcessor: ObservableTransformer<MonitoringAction.StartMonitoringAction, MonitoringResult.MonitoringStarted> =
        ObservableTransformer { action ->
            action.flatMap {
                Observable.just(MonitoringResult.MonitoringStarted)
            }
        }

    private val stopMonitoringProcessor: ObservableTransformer<MonitoringAction.StopMonitoringAction, MonitoringResult.MonitoringStopped> =
        ObservableTransformer { action ->
            action.flatMap {
                Observable.just(MonitoringResult.MonitoringStopped)
            }
        }

}