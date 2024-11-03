package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.entity.user.ERole
import java.util.UUID

data class LoginResponseDto(
    val token: String
)

data class RoleDto(
    val name: ERole
)

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val items: MutableList<ItemDto>,
    val auctions: MutableList<AuctionDto>,
    val bids: MutableList<BidDto>
)

data class ItemDto(
    var id: UUID,
    var make: String,
    var model: String,
    var mileage: String,
    var year: String,
    var price: Double,
    var engineSize: String,
    var fuelType: String,
    var transmission: String,
    var condition: String,
    var bodyStyle: String,
    var drivetrain: String,
    var location: String,
    var description: String,
    var vin: String,
    var exteriorColor: String,
    var interiorColor: String,
    var featured: List<String>,
    var exterior: List<String>,
    var interior: List<String>,
    var mechanical: List<String>,
    var other: List<String>,
    var isSold: Boolean,
    var onAuction: Boolean,
    var userId: Long,
    var username: String
)



data class BidDto (
    val id: UUID,
    val userId: Long,
    val value: Double,
    val isWinning: Boolean,
    val auctionId: UUID
)

data class AuctionDto(
    val id: UUID,
    val userId: Long,
    val item: ItemDto,
    val bids: MutableList<BidDto>,
    val currentMaxBid: Double,
    val expectedPrice: Double,
    val auctionStatus: AuctionStatus,
    val startTime: Long,
    val endTime: Long
)