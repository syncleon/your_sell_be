package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AuctionRepo : JpaRepository<Auction, UUID> {
    fun findByEndTimeLessThanAndAuctionStatus(currentTime: Long, auctionStatus: AuctionStatus): MutableList<Auction>
    fun findByIdAndUser(id: UUID, user: User): Optional<Auction>

    // New methods
    fun findByAuctionStatus(auctionStatus: AuctionStatus): List<Auction>
    fun findByUserId(userId: Long): List<Auction>
}