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
                    shared.ofType(MonitoringAction.InitializeMonitoringAction::class.java).compose(
                        initializeMonitoringProcessor
                    ),
                    shared.ofType(MonitoringAction.StopMonitoringAction::class.java).compose(stopMonitoringProcessor),
                    shared.ofType(MonitoringAction.DisplayAlertMonitoringAction::class.java).compose(
                        displayAlertMonitoringDisplayProcessor
                    )
                ).mergeWith(
                    shared.ofType(MonitoringAction.CloseAlertMonitoringAction::class.java).compose(
                        closeAlertMonitoringDisplayProcessor
                    )
                ).mergeWith(
                    shared.ofType(MonitoringAction.ErrorMonitoringAction::class.java).compose(
                        errorMonitoringProcessor
                    )
                ).mergeWith(
                    shared.ofType(MonitoringAction.ResetErrorMonitoringAction::class.java).compose(
                        resetMonitoringProcessor
                    )
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

    private val displayAlertMonitoringDisplayProcessor: ObservableTransformer<MonitoringAction.DisplayAlertMonitoringAction, MonitoringResult.MonitoringDisplayAlert> =
        ObservableTransformer { actions ->
            actions.map { action ->
                MonitoringResult.MonitoringDisplayAlert(action.message)
            }
        }

    private val closeAlertMonitoringDisplayProcessor: ObservableTransformer<MonitoringAction.CloseAlertMonitoringAction, MonitoringResult.MonitoringClosedAlert> =
        ObservableTransformer { actions ->
            actions.map {action->
                MonitoringResult.MonitoringClosedAlert(action.pendingError)
            }
        }

    private val errorMonitoringProcessor: ObservableTransformer<MonitoringAction.ErrorMonitoringAction, MonitoringResult.MonitoringError> =
        ObservableTransformer { action ->
            action.flatMap {
                Observable.just(MonitoringResult.MonitoringError)
            }
        }

    private val resetMonitoringProcessor: ObservableTransformer<MonitoringAction.ResetErrorMonitoringAction, MonitoringResult.MonitoringReset> =
        ObservableTransformer { action ->
            action.flatMap {
                Observable.just(MonitoringResult.MonitoringReset)
            }
        }

}