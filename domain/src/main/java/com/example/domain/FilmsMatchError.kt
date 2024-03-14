package com.example.domain

open class FilmsMatchError : Throwable() {
    object NetworkError : FilmsMatchError() {
        private fun readResolve(): Any = NetworkError
    }

    object BadRequest : FilmsMatchError() {
        private fun readResolve(): Any = BadRequest
    }

    object EmptyResponse : FilmsMatchError() {
        private fun readResolve(): Any = EmptyResponse
    }
}