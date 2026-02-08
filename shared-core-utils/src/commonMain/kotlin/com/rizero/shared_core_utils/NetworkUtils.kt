package com.rizero.shared_core_utils

import com.rizero.shared_core_utils.exceptions.ConnectionException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException

sealed class NetworkResult<out S, out F>(){
    data class Success<out S>(val data: S) : NetworkResult<S, Nothing>()

    sealed class Failure<out F> : NetworkResult<Nothing,F>() {
        data class NetworkError(val networkError : ConnectionException) : Failure<Nothing>()
        data class RequestFailure<out F>(val failure : F) : Failure<F>()
    }
}

inline fun <R,S,F> NetworkResult<S,F>.fold(
    onSuccess: (value : S) -> R,
    onNetworkError: (error : ConnectionException) -> R,
    onFailure: (error : F) -> R
) : R {
    return when(this){
        is NetworkResult.Success<S> -> onSuccess(this.data)
        is NetworkResult.Failure.NetworkError -> onNetworkError(this.networkError)
        is NetworkResult.Failure.RequestFailure<F> -> onFailure(this.failure)
    }
}

inline fun<R,S : R,F> NetworkResult<S,F>.resultOrElse(
    elseAction : (error : NetworkResult.Failure<F>) -> R
) : R = when(this){
    is NetworkResult.Failure<F> -> elseAction(this)
    is NetworkResult.Success<R> -> this.data
}

/**
 * Perform network call and wrap result to NetworkResult
 * @return
 * [NetworkResult.Failure.NetworkError] - if connection interrupted
 *
 *  [NetworkResult.Failure.RequestFailure] - if status code not stay in 200..299
 *
 *  [NetworkResult.Success] - if status code stay in 200..299
 *
 *  @throws io.ktor.client.call.NoTransformationFoundException - if success response type specified incorrect
 * */
suspend inline fun <reified RS,S,F> defaultNetworkCall(
    call: suspend ()-> HttpResponse,
    onRequestSuccess: (successResponseBody : RS, response: HttpResponse) -> S,
    onRequestFailure : (status : HttpStatusCode, response : HttpResponse) ->F
) : NetworkResult<S, F>{
    return try {
        val response = call()
        if (response.status.value in 200..299) {
            val responseResult = response.body<RS>()
            NetworkResult.Success(onRequestSuccess(responseResult,response))
        } else {
            NetworkResult.Failure.RequestFailure(onRequestFailure(response.status, response))
        }
    }catch(e : CancellationException) {
        throw e
    }catch (e : HttpRequestTimeoutException){
        NetworkResult.Failure.NetworkError(ConnectionException(e))
    }catch (e : ConnectTimeoutException){
        NetworkResult.Failure.NetworkError(ConnectionException(e))
    }catch (e : SocketTimeoutException){
        NetworkResult.Failure.NetworkError(ConnectionException(e))
    }catch (e : IOException){
        NetworkResult.Failure.NetworkError(ConnectionException(e))
    }
}

suspend inline fun <reified T> HttpResponse.bodySafely(): T? = try {
    body<T>()
} catch (e: Exception) {
    null
}

