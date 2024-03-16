package com.example.filmsmatch.base

open class BaseState

enum class ErrorType {
    EMPTY_RESPONSE,
    BAD_REQUEST,
    NETWORK_ERROR,
    UNKNOWN
}