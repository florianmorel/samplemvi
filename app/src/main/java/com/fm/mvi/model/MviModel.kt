package com.fm.mvi.model

import io.reactivex.Observable

interface MviModel<S> {

    fun processIntents(intents: Observable<S>)

    fun states(): Observable<S>
}