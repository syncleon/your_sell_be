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
    val id: UUID,
    val make: String,
    val model: String,
    val mileage: String,
    val year: String,
    val featured: MutableList<String>,
    val exterior: MutableList<String>,
    val interior: MutableList<String>,
    val mechanical: MutableList<String>,
    val other: MutableList<String>,
    val onAuction: Boolean,
    val isSold: Boolean,
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