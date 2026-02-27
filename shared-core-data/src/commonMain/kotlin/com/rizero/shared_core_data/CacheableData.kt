package com.rizero.shared_core_data

sealed interface CacheableData<out T>{
    data object Loading : CacheableData<Nothing>
    data class FromCache<T>(val data : T) : CacheableData<T>
    sealed interface FromNetwork<T> : CacheableData<T>{
        class Success<T>(val data : T) : FromNetwork<T>
        class Failure<T>(val error : Throwable, val cached : T) : FromNetwork<T>
    }
    companion object {
        fun loading() : CacheableData<Nothing> = Loading
        fun<T> fromCache(result : T) : CacheableData<T> = FromCache(result)
        fun<T> networkSuccess(result : T) : CacheableData<T> = FromNetwork.Success(result)
        fun<T> networkFailure(error : Throwable, cached : T) : CacheableData<T> = FromNetwork.Failure(error,cached)
    }
}