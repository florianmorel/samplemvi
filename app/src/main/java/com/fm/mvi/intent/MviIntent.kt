package com.fm.mvi.intent

interface MviIntent<S> {

    fun reduce(previousState: S): S
}