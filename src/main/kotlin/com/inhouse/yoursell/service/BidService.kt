package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.BidDto
import com.inhouse.yoursell.dto.CreateBidDto
import com.inhouse.yoursell.dto.toDto
import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.AuctionRepo
import com.inhouse.yoursell.repo.BidRepo
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class BidService (
    @Autowired private val bidRepo: BidRepo,
    @Autowired private val auctionRepo: AuctionRepo
) {
    fun createBid(
        payload: CreateBidDto,
        authentication: Authentication
    ): BidDto {
        val minIncrement = 100.0
        val maxIncrement = 5000.0
        val currentUser = authentication.toUser()
        val auction = auctionRepo.findById(payload.auctionId)
            .orElseThrow { throw NotFoundException("Auction not found.") }

        val bidForCheck = bidRepo.findByAuctionAndUserAndBidValue(auction, currentUser, payload.bidValue)
        when {
            bidForCheck.isPresent -> throw Exception("Duplicate bid detected.")
            payload.bidValue < auction.currentMaxBid + minIncrement -> throw Exception("Too low bid value.")
            payload.bidValue > auction.currentMaxBid + maxIncrement -> throw Exception("Too big bid value.")
            currentUser.id == auction.user.id -> throw Exception("Cannot make bid on your own.")
            auction.auctionStatus != AuctionStatus.STARTED ->
                throw Exception("Cannot make bid for not STARTED auction.")
            payload.bidValue <= 0.0 -> throw Exception("Bid value must be greater than zero.")
            payload.bidValue <= auction.currentMaxBid -> throw Exception("Can't add bid lower than current.")
            else -> {
                val bid = Bid(auction = auction, bidValue = payload.bidValue, user = currentUser)
                auction.currentMaxBid = payload.bidValue
                auctionRepo.save(auction)
                return bidRepo.save(bid).toDto()
            }
        }
    }

    fun findById(id: UUID): BidDto {
        return bidRepo.findById(id).orElseThrow { throw NotFoundException("Bid not found.") }.toDto()
    }

    fun findAll(): MutableList<BidDto> {
        val bidList = bidRepo.findAll()
        val bidDtoList = mutableListOf<BidDto>()
        bidList.forEach { bid ->
            val bidDto = bid.toDto()
            bidDtoList.add(bidDto)
        }
        return bidDtoList
    }
}