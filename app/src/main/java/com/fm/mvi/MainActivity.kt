package com.fm.mvi

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
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

    private val viewModel: MonitoringViewModel by lazy {
        ViewModelProviders.of(this, factory).get(MonitoringViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)
        initViews()
        disposables += viewModel.states().observeOn(AndroidSchedulers.mainThread()).subscribe(this::render)
        viewModel.processIntents(intents())
        viewModel.registerAlertService()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.unregisterAlertService()
        disposables.clear()
    }

    private fun initViews() {
        indicator.setImageResource(R.drawable.red_light_indicator)
    }

    override fun intents(): Observable<MonitoringIntents> {
        return Observable.merge(stopEvent(), startEvent(), initializeIntent)
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

    override fun render(state: MonitoringViewState) = when (state) {
        is MonitoringViewState.Stopped -> renderStoppedState()
        is MonitoringViewState.Initialization -> renderInitializationState()
        is MonitoringViewState.Started -> renderStartedState()
        is MonitoringViewState.Alert -> renderAlert(state.alertMessage)
    }

    private fun renderStoppedState() {
        indicator.setImageResource(R.drawable.red_light_indicator)
        disableView(stop_button)
        enableView(start_button)
    }

    private fun renderInitializationState() {
        indicator.setImageResource(R.drawable.orange_light_indicator)
        initializationEvent()
        disableView(stop_button)
        disableView(start_button)
    }

    private fun renderStartedState() {
        indicator.setImageResource(R.drawable.green_light_indicator)
        disableView(start_button)
        enableView(stop_button)
    }

    private fun renderAlert(alertMessage: String) {
        Toast.makeText(this, alertMessage, Toast.LENGTH_SHORT).show()
    }

    private fun disableView(v: View) {
        v.visibility = View.INVISIBLE
    }

    private fun enableView(v: View) {
        v.visibility = View.VISIBLE
    }

}
