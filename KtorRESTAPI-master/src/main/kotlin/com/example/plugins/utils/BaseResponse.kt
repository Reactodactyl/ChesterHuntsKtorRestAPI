package com.example.plugins.utils

data class BaseResponse<T>(val data: T? = null, val message: String? = null)
