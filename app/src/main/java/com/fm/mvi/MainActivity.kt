package com.fm.mvi

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fm.mvi.base.MviView
import com.fm.mvi.model.MonitoringIntents
import com.fm.mvi.model.MonitoringViewState
import com.fm.mvi.presentation.MonitoringViewModel
import com.fm.mvi.presentation.MonitoringViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MviView<MonitoringIntents, MonitoringViewState> {

    @Inject
    lateinit var factory: MonitoringViewModelFactory
    private val disposables = CompositeDisposable()

    private val viewModel: MonitoringViewModel by lazy {
        ViewModelProviders.of(this, factory).get(MonitoringViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)
        initViews()
        disposables += viewModel.states().subscribe(this::render)
        viewModel.processIntents(intents())
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun initViews() {
        indicator.setImageResource(R.drawable.red_light_indicator)
    }

    override fun intents(): Observable<MonitoringIntents> {
        return Observable.merge(stopEvent(), startEvent())
    }

    private fun stopEvent(): Observable<MonitoringIntents.StopMonitoringIntent> {
        return RxView.clicks(stop_button).map { MonitoringIntents.StopMonitoringIntent }
    }

    private fun startEvent(): Observable<MonitoringIntents.StartMonitoringIntent> {
        return RxView.clicks(start_button).map { MonitoringIntents.StartMonitoringIntent }
    }

    override fun render(state: MonitoringViewState) = when (state) {
        MonitoringViewState.Started -> renderStartedState()
        MonitoringViewState.Stopped -> renderStoppedState()
    }

    private fun renderStartedState() {
        indicator.setImageResource(R.drawable.green_light_indicator)
    }

    private fun renderStoppedState() {
        indicator.setImageResource(R.drawable.red_light_indicator)
    }

}
