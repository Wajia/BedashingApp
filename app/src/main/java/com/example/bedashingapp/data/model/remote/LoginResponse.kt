package com.example.bedashingapp.data.model.remote

data class LoginResponse(
    val SessionId: String?,
    val Version: String?,
    val SessionTimeout: Int?,
    val error: Error?
)



