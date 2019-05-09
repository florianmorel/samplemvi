package com.fm.mvi.service

import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertService @Inject constructor() {

    companion object {

        private val ALERT_SENSOR_UNPLUGGED = Alert("Sensor Unplugged Alert")
        private val ALERT_SENSOR_POSITION = Alert("Sensor Position Alert")
        private val ALERT_LOST_CONNECTION = Alert("Lost Connection Alert")
        private val ERROR = Error

        val alertCollection = mapOf<Long, AlertMessage>(
            5L to ALERT_LOST_CONNECTION,
            10L to ALERT_SENSOR_POSITION,
            6L to ALERT_SENSOR_UNPLUGGED,
            8L to ERROR
        )
    }

    val stackOfAlert: Observable<AlertMessage> = Observable
        .fromIterable(alertCollection.keys)
        .concatMap { key ->
            Observable.just(alertCollection.getValue(key)).delay(key, TimeUnit.SECONDS)
        }
        .repeat()

}