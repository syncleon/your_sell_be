package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.image.Image

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
    var mileage: Int,
    var vin: String,
    var year: String,
    var expectedBid: Int
)