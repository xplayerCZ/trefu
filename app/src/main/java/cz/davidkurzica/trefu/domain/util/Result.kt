package cz.davidkurzica.trefu.domain.util

import kotlinx.serialization.Serializable

@Serializable
sealed class Result<out R> {
    @Serializable
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}