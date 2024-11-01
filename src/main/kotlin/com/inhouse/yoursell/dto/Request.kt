package com.inhouse.yoursell.dto

import java.util.*

data class LoginUserDto(
    val username: String,
    val password: String,
)

data class RegisterUserDto(
    val username: String,
    val password: String,
    val email: String
)

data class CreateItemDto(
    var make: String,
    var model: String,
    var mileage: String,
    var year: String,
    var price: Double,
    var color: String,
    var engineSize: String,
    var fuelType: String,
    var transmissionType: String,
    var condition: String,
    var location: String,
    var description: String,
    var vin: String
)


data class CreateAuctionDto(
    val itemId: UUID,
    val reservePrice: Double,
    val duration: String
)

data class RestartAuctionDto(
    val auctionId: UUID,
    val duration: String
)

data class CreateBidDto(
    val bidValue: Double,
    val auctionId: UUID
)
