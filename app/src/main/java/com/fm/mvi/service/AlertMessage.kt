package com.fm.mvi.service

sealed class AlertMessage {

    object Error : AlertMessage()

    data class Alert(val description: String) : AlertMessage()

}