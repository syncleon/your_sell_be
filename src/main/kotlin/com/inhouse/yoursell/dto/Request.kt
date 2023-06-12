package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.Role

data class LoginUserDto(
    val username: String,
    val password: String,
)

data class RegisterUserDto(
    val username: String,
    val password: String,
    val email: String
)