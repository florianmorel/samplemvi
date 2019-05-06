package com.fm.mvi.service

import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertService @Inject constructor(){

    companion object {

        val ALERT_SENSOR_UNPLUGGED = AlertMessage.Alert("Sensor Unplugged Alert")
        val ALERT_SENSOR_POSITION = AlertMessage.Alert("Sensor Position Alert")
        val ALERT_LOST_CONNECTION = AlertMessage.Alert("Lost Connection Alert")
        val ERROR = AlertMessage.Error

        val collectionAlert = mapOf(
            15L to ALERT_LOST_CONNECTION,
            30L to ALERT_SENSOR_POSITION,
            50L to ERROR,
            70L to ALERT_SENSOR_UNPLUGGED
        )
    }

    val stackOfAlert : Observable<AlertMessage> = Observable.fromIterable(collectionAlert.keys).concatMap { key ->
        Observable.just(collectionAlert.getValue(key)).delay(key, TimeUnit.SECONDS)
    }

}