package com.test.weatherapp.domain.model

// Sealed class to represent the result of a data operation
sealed class Result<out T> {
    class Success<T>(val data: T) : Result<T>() // Success case
    class Error(val message: String) : Result<Nothing>() // Error case
    data object Loading : Result<Nothing>() // Loading state

}