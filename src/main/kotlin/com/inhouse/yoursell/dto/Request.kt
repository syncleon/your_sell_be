package com.inhouse.yoursell.dto

data class LoginUserDto(
    val username: String,
    val password: String,
)

data class RegisterUserDto(
    val username: String,
    val password: String,
    val email: String
)

data class RegisterVehicleDto(
    var make: String,
    var model: String,
    var mileage: Double,
    var vin: String,
    var year: String,
    var images: MutableList<String>
)