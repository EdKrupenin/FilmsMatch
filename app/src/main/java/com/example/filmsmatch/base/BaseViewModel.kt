package com.example.filmsmatch.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<T : BaseState>(initialState: T) : ViewModel() {
    private val _state = MutableStateFlow<T>(initialState)
    val stateFlow: StateFlow<T> = _state

    protected fun setState(state: T) {
        _state.value = state
    }
}
