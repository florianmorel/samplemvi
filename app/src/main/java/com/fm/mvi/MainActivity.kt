package com.fm.mvi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fm.mvi.base.MviView
import com.fm.mvi.model.MonitoringIntents
import com.fm.mvi.model.MonitoringState
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MviView<MonitoringIntents, MonitoringState> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)
        initViews()
        initClick()
    }

    private fun initViews() {
        indicator.setImageResource(R.drawable.red_light_indicator)
    }

    private fun initClick() {
        start_button.setOnClickListener { renderStartedState() }
        stop_button.setOnClickListener { renderStoppedState() }
    }

    override fun intents(): Observable<MonitoringIntents> {
        return Observable.merge(startIntent(), stopIntent())
    }

    private fun startIntent(): Observable<MonitoringIntents.StartIntent> {
        return RxView.clicks(stop_button).map { MonitoringIntents.StartIntent}
    }

    private fun stopIntent(): Observable<MonitoringIntents.StopIntent> {
        return RxView.clicks(stop_button).map { MonitoringIntents.StopIntent}
    }

    override fun render(state: MonitoringState) {
        when (state){
            MonitoringState.Started->renderStartedState()
            MonitoringState.Stopped->renderStoppedState()
        }
    }

    private fun renderStartedState() {
        indicator.setImageResource(R.drawable.green_light_indicator)
    }

    private fun renderStoppedState() {
        indicator.setImageResource(R.drawable.red_light_indicator)
    }

}
