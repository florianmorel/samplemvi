package com.fm.mvi.presentation

import android.arch.lifecycle.ViewModel
import com.fm.mvi.base.MviViewModel
import com.fm.mvi.model.MonitoringAction
import com.fm.mvi.model.MonitoringIntents
import com.fm.mvi.model.MonitoringResult
import com.fm.mvi.model.MonitoringViewState
import com.fm.mvi.service.Alert
import com.fm.mvi.service.AlertMessage
import com.fm.mvi.service.AlertService
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton


@Singleton
class MonitoringViewModel(
    private val monitoringActionProcessor: MonitoringActionProcessor,
    private val alertService: AlertService
) : ViewModel(),
    MviViewModel<MonitoringIntents, MonitoringViewState> {

    private val intentsSubject: PublishSubject<MonitoringIntents> = PublishSubject.create()
    private val mStatesObservable: Observable<MonitoringViewState> = compose()
    private val mAlertsObservable: Observable<AlertMessage> = alertService.stackOfAlert
    private val disposables = CompositeDisposable()

    override fun processIntents(intents: Observable<MonitoringIntents>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<MonitoringViewState> = mStatesObservable

    fun registerAlertService() {
        disposables += mAlertsObservable
            .map { alertMessage: AlertMessage ->
                when (alertMessage) {
                    is Alert -> intentsSubject.onNext(MonitoringIntents.AlertMonitoringIntent(alertMessage.description))
                }
            }
            .subscribe()
    }

    fun unregisterAlertService() {
        disposables.clear()
    }

    private fun actionFromIntent(intent: MonitoringIntents): MonitoringAction = when (intent) {
        is MonitoringIntents.StartMonitoringIntent -> MonitoringAction.InitializeMonitoringAction
        is MonitoringIntents.StopMonitoringIntent -> MonitoringAction.StopMonitoringAction
        is MonitoringIntents.InitializeMonitoringIntent -> MonitoringAction.StartMonitoringAction
        is MonitoringIntents.AlertMonitoringIntent -> MonitoringAction.AlertMonitoringAction(intent.message)
    }

    private fun compose(): Observable<MonitoringViewState> {
        return intentsSubject
            .map(this::actionFromIntent)
            .compose(monitoringActionProcessor.transformFromAction())
            .scan(MonitoringViewState.idle(), reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    companion object {
        private val reducer =
            BiFunction { previousState: MonitoringViewState, result: MonitoringResult ->
                when (result) {
                    is MonitoringResult.MonitoringStopped -> MonitoringViewState.Stopped
                    is MonitoringResult.MonitoringInitializing -> MonitoringViewState.Initialization
                    is MonitoringResult.MonitoringStarted -> MonitoringViewState.Started
                    is MonitoringResult.MonitoringAlert -> MonitoringViewState.Alert(result.message)
                }
            }
    }

}