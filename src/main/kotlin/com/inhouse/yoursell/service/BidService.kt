package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.BidDto
import com.inhouse.yoursell.dto.CreateBidDto
import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.AuctionRepo
import com.inhouse.yoursell.repo.BidRepo
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import toDto
import java.util.*

@Service
@Transactional
class BidService(
    @Autowired private val bidRepo: BidRepo,
    @Autowired private val auctionRepo: AuctionRepo
) {

    /**
     * Places a new bid on an auction.
     */
    fun placeBid(payload: CreateBidDto, authentication: Authentication): BidDto {
        val minIncrement = 100.0
        val maxIncrement = 5000.0
        val currentUser = authentication.toUser()

        val auction = auctionRepo.findById(payload.auctionId)
            .orElseThrow { throw NotFoundException("Auction not found.") }

        val existingBid = bidRepo.findByAuctionAndUserAndBidValue(auction, currentUser, payload.bidValue)
        when {
            existingBid.isPresent -> throw Exception("Duplicate bid detected.")
            payload.bidValue < auction.currentHighestBid + minIncrement -> throw Exception("Bid value is too low.")
            payload.bidValue > auction.currentHighestBid + maxIncrement -> throw Exception("Bid value is too high.")
            currentUser.id == auction.user.id -> throw Exception("You cannot bid on your own auction.")
            auction.auctionStatus != AuctionStatus.STARTED -> throw Exception("Auction is not in STARTED status.")
            payload.bidValue <= 0.0 -> throw Exception("Bid value must be greater than zero.")
            payload.bidValue <= auction.currentHighestBid -> throw Exception("Bid must be higher than the current max bid.")
            else -> {
                val bid = Bid(auction = auction, bidValue = payload.bidValue, user = currentUser)
                auction.currentHighestBid = payload.bidValue
                auctionRepo.save(auction)
                return bidRepo.save(bid).toDto()
            }
        }
    }

    /**
     * Retrieves a bid by its ID.
     */
    fun getBidById(id: UUID): BidDto {
        return bidRepo.findById(id).orElseThrow { throw NotFoundException("Bid not found.") }.toDto()
    }

    /**
     * Retrieves all bids.
     */
    fun getAllBids(): List<BidDto> {
        return bidRepo.findAll().map { it.toDto() }
    }
}
