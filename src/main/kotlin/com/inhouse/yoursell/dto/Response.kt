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
    val auctions: MutableList<AuctionDto>,
    val bids: MutableList<BidDto>
)

data class VehicleDto(
    val id: UUID,
    val make: String,
    val model: String,
    val mileage: Int,
    val vin: String,
    val year: String,
    val expectedBid: Int,
    val damaged: Boolean,
    val sellerId: Long,
    val sellerUsername: String,
    val images: MutableList<String>,
    val onSale: Boolean,
    val isSold: Boolean,
    val deleted: Boolean,
)

data class BidDto (
    val id: UUID,
    val bidderId: Long,
    val bidValue: BigDecimal,
    val auctionId: UUID
)

data class AuctionDto(
    val id: UUID,
    val auctionOwner: String,
    val vehicle: VehicleDto,
    val bids: MutableList<BidDto>,
    val reservePrice: BigDecimal,
    val auctionStatus: AuctionStatus,
    val startTime: Long,
    val endTime: Long,
    val currentMaxBid: BigDecimal,
    val currentMaxBidderId: Long
)