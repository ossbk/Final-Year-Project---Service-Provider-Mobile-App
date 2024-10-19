package com.services.provider.domain.model

sealed class MyResponse<out T> {
    class Success<T>(val data: T) : MyResponse<T>()
    data object Idle : MyResponse<Nothing>()
    data object Loading : MyResponse<Nothing>()
    data class Failure(val msg: String) : MyResponse<Nothing>()

}