package com.fm.mvi

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.fm.mvi.base.MviView
import com.fm.mvi.model.MonitoringIntents
import com.fm.mvi.model.MonitoringViewState
import com.fm.mvi.presentation.MonitoringViewModel
import com.fm.mvi.presentation.MonitoringViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MviView<MonitoringIntents, MonitoringViewState> {

    @Inject
    lateinit var factory: MonitoringViewModelFactory
    private val disposables = CompositeDisposable()
    private val initializeIntent = PublishSubject.create<MonitoringIntents.InitializeMonitoringIntent>()
    private val closeIntent = PublishSubject.create<MonitoringIntents.CloseAlertMonitoringIntent>()

    private val viewModel: MonitoringViewModel by lazy {
        ViewModelProviders.of(this, factory).get(MonitoringViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)
        initViews()
    }

    override fun onStart() {
        super.onStart()
        disposables += viewModel.states().observeOn(AndroidSchedulers.mainThread()).subscribe(this::render)
        disposables += viewModel.registerAlertService()
        viewModel.processIntents(intents())
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun initViews() {
        indicator.setImageResource(R.drawable.error_indicator)
    }

    override fun intents(): Observable<MonitoringIntents> {
        return Observable.merge(stopEvent(), startEvent(), initializeIntent, closeIntent).mergeWith(resetEvent())
    }

    private fun stopEvent(): Observable<MonitoringIntents.StopMonitoringIntent> {
        return RxView.clicks(stop_button).map { MonitoringIntents.StopMonitoringIntent }
    }

    private fun initializationEvent() {
        Observable.timer(3, TimeUnit.SECONDS)
            .map { t -> initializeIntent.onNext(MonitoringIntents.InitializeMonitoringIntent) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun startEvent(): Observable<MonitoringIntents.StartMonitoringIntent> {
        return RxView.clicks(start_button).map { MonitoringIntents.StartMonitoringIntent }
    }

//    private fun closeAlertEvent(): Observable<MonitoringIntents.CloseAlertMonitoringIntent> {
//        return RxView.clicks(close_button)
//            .map { MonitoringIntents.CloseAlertMonitoringIntent(pendingError = getPendingErrorState()) }
//    }

    private fun resetEvent(): Observable<MonitoringIntents.ResetMonitoringIntent> {
        return RxView.clicks(reset_button).map { MonitoringIntents.ResetMonitoringIntent }
    }

    override fun render(state: MonitoringViewState) {
        when (state) {
            is MonitoringViewState.Stopped -> renderStoppedState()
            is MonitoringViewState.Initialization -> renderInitializationState()
            is MonitoringViewState.Started -> renderStartedState()
            is MonitoringViewState.Alert -> renderAlert(state.alertMessage, state.error)
            is MonitoringViewState.Error -> renderError()
        }
    }

    private fun renderStoppedState() {
        indicator.setImageResource(R.drawable.stop_indicator)
        enableView(start_button)
        disableView(stop_button)
        disableView(alert_message_dialog)
        disableView(reset_button)
        current_state.text = getString(R.string.current_state, getString(R.string.state_stopped))
    }

    private fun renderInitializationState() {
        indicator.setImageResource(R.drawable.init_indicator)
        initializationEvent()
        disableView(stop_button)
        disableView(start_button)
        disableView(alert_message_dialog)
        disableView(reset_button)
        current_state.text = getString(R.string.current_state, getString(R.string.state_initializing))
    }

    private fun renderStartedState() {
        indicator.setImageResource(R.drawable.start_indicator)
        enableView(stop_button)
        disableView(start_button)
        disableView(alert_message_dialog)
        disableView(reset_button)
        current_state.text = getString(R.string.current_state, getString(R.string.state_started))
    }

    private fun renderAlert(alertMessage: String, errorPending: Boolean) {
        indicator.setImageResource(R.drawable.alert_indicator)
        enableView(alert_message_dialog)
        disableView(start_button)
        disableView(stop_button)
        disableView(reset_button)
        alert_message.text = alertMessage
        current_state.text = getString(R.string.current_state, getString(R.string.state_alert))
        close_button.setOnClickListener { closeIntent.onNext(MonitoringIntents.CloseAlertMonitoringIntent(pendingError = errorPending))  }
    }

    private fun renderError() {
        indicator.setImageResource(R.drawable.error_indicator)
        disableView(alert_message_dialog)
        disableView(start_button)
        disableView(stop_button)
        enableView(reset_button)
        current_state.text = getString(R.string.current_state, getString(R.string.state_error))
    }

    private fun disableView(v: View) {
        v.visibility = View.GONE
    }

    private fun enableView(v: View) {
        v.visibility = View.VISIBLE
    }

}
