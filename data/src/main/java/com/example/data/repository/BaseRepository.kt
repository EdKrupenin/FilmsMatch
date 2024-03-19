package com.example.data.repository

import com.example.domain.FilmsMatchError
import retrofit2.Response

abstract class BaseRepository {
    /**
     * Performs a network request and processes the response.
     * This function abstracts the common pattern of performing a network request,
     * checking for success, and processing the response body. It uses Kotlin's Result
     * type for handling both successful outcomes and failures, including HTTP errors
     * and empty response bodies.
     *
     * @param T The type of the expected response body.
     * @param R The type of the result after processing the response body.
     * @param call A suspend lambda function that performs the network request and returns a [Response].
     * @param processSuccess A lambda function that processes the successful response body and returns a result of type [R].
     * @param isEmptyResponse A lambda function that checks if the response body should be considered as an empty response, which will lead to an [FilmsMatchError.EmptyResponse] error.
     * @return A [Result] containing either the processed data of type [R] if the request and processing are successful, or an error in case of failure.
     */
    private suspend fun <T, R> performRequest(
        call: suspend () -> Response<T>,
        processSuccess: (T) -> R,
        isEmptyResponse: (T) -> Boolean,
    ): Result<R> {
        return runCatching {
            val response = call()
            when {
                response.isSuccessful -> {
                    val body = response.body() ?: throw FilmsMatchError.EmptyResponse
                    if (!isEmptyResponse(body)) processSuccess(body)
                    else throw FilmsMatchError.EmptyResponse
                }

                else -> throw when (response.code()) {
                    400 -> FilmsMatchError.BadRequest
                    else -> FilmsMatchError.NetworkError
                }
            }
        }
    }

    /**
     * Asynchronously fetches model from a network source, processes it, and updates the local cache.
     * This function abstracts the common pattern of performing a network request, processing the response,
     * and caching the result for future use.
     *
     * @param T The type of the raw network response.
     * @param R The type of the processed model.
     * @param performNetworkRequest A suspend lambda function that performs the network request and returns a [Response] of type [T].
     * @param processResponse A lambda function that processes the raw response [T] and converts it to the processed type [R].
     * @param updateCache A lambda function that updates the local cache with the processed model of type [R].
     * @param isEmptyResponse A lambda function that checks if the response is considered "empty," which may trigger an error handling flow.
     * @return A [Result] containing either the processed model of type [R] if the operation is successful, or an error.
     */
    protected suspend fun <T, R> fetchAndUpdateCache(
        performNetworkRequest: suspend () -> Response<T>,
        processResponse: (T) -> R,
        updateCache: (R) -> Unit,
        isEmptyResponse: (T) -> Boolean,
    ): Result<R> = performRequest(
        call = performNetworkRequest,
        processSuccess = { responseBody ->
            processResponse(responseBody).also(updateCache)
        },
        isEmptyResponse = isEmptyResponse
    )
}