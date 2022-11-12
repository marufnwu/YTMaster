package com.sikderithub.ytmaster.Model

data class GenericResponse<T : Any> (
    val error: Boolean = true,
    val msg: String = "",
    val data: T? = null
)