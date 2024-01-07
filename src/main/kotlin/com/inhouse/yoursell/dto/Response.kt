package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.entity.user.ERole
import java.math.BigDecimal
import java.time.LocalDateTime
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
    val userRoles: MutableSet<RoleDto>,
    val vehicles: MutableList<VehicleDto>,
    val auctions: MutableList<AuctionDto>
)

data class VehicleDto(
    var id: UUID,
    var make: String,
    var model: String,
    var mileage: Int,
    var vin: String,
    var year: String,
    var expectedBid: Int,
    var damaged: Boolean,
    var sellerId: Long,
    var sellerUsername: String,
    var images: MutableList<String>,
    var onSale: Boolean,
    var deleted: Boolean,
)

data class BidDto (
    val id: UUID,
    val bidderId: Long,
    val bidValue: BigDecimal,
    val auctionId: UUID
)

data class AuctionDto(
    val id: UUID,
    val vehicle: VehicleDto,
    val bids: MutableList<BidDto>,
    val reservePrice: BigDecimal,
    val auctionStatus: AuctionStatus,
    val startTime: Long,
    val endTime: Long,
    val currentMaxBid: BigDecimal,
    val currentMaxBidderId: Long
)