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
                    shared.ofType(MonitoringAction.InitializeMonitoringAction::class.java).compose(initializeMonitoringProcessor),
                    shared.ofType(MonitoringAction.StopMonitoringAction::class.java).compose(stopMonitoringProcessor),
                    shared.ofType(MonitoringAction.AlertMonitoringAction::class.java).compose(alertMonitoringProcessor)
                )
            }
        }
    }

    private val stopMonitoringProcessor: ObservableTransformer<MonitoringAction.StopMonitoringAction, MonitoringResult.MonitoringStopped> =
        ObservableTransformer { action ->
            action.flatMap {
                Observable.just(MonitoringResult.MonitoringStopped)
            }
        }
    private val initializeMonitoringProcessor: ObservableTransformer<MonitoringAction.InitializeMonitoringAction, MonitoringResult.MonitoringInitializing> =
        ObservableTransformer { action ->
            action.flatMap {
                Observable.just(MonitoringResult.MonitoringInitializing)
            }
        }

    private val startMonitoringProcessor: ObservableTransformer<MonitoringAction.StartMonitoringAction, MonitoringResult.MonitoringStarted> =
        ObservableTransformer { action ->
            action.flatMap {
                Observable.just(MonitoringResult.MonitoringStarted)
            }
        }

    private val alertMonitoringProcessor: ObservableTransformer<MonitoringAction.AlertMonitoringAction, MonitoringResult.MonitoringAlert> =
        ObservableTransformer { actions ->
            actions.map { action ->
                val message = action.message
                MonitoringResult.MonitoringAlert(message)
            }
        }

}