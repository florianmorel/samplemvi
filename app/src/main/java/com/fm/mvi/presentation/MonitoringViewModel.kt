package com.fm.mvi.presentation

import android.arch.lifecycle.ViewModel
import com.fm.mvi.base.MviAction
import com.fm.mvi.base.MviIntent
import com.fm.mvi.base.MviViewModel
import com.fm.mvi.model.MonitoringAction
import com.fm.mvi.model.MonitoringIntents
import com.fm.mvi.model.MonitoringResult
import com.fm.mvi.model.MonitoringViewState
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton


@Singleton
class MonitoringViewModel(private val monitoringActionProcessor: MonitoringActionProcessor) : ViewModel(),
    MviViewModel<MonitoringIntents, MonitoringViewState> {

    private val intentsSubject: PublishSubject<MonitoringIntents> = PublishSubject.create()
    private val mStatesObservable: Observable<MonitoringViewState> = compose()

    override fun processIntents(intents: Observable<MonitoringIntents>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<MonitoringViewState> = mStatesObservable

    /**
     * take only the first ever InitialIntent and all intents of other types
     * to avoid reloading data on config changes
     */
    private val intentFilter: ObservableTransformer<MonitoringIntents, MonitoringIntents>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                    shared.ofType(MonitoringIntents.StopMonitoringIntent::class.java).take(1),
                    shared.filter { intent -> intent !is MonitoringIntents.StopMonitoringIntent }
                )
            }
        }

    /**
     * Translate an [MviIntent] to an [MviAction].
     */
    private fun actionFromIntent(intent: MonitoringIntents): MonitoringAction = when (intent) {
        MonitoringIntents.StartMonitoringIntent -> MonitoringAction.StartMonitoringAction
        MonitoringIntents.StopMonitoringIntent -> MonitoringAction.StopMonitoringAction
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
                    MonitoringResult.MonitoringStarted -> MonitoringViewState.Started
                    MonitoringResult.MonitoringStopped -> MonitoringViewState.Stopped
                }
            }
    }

}