package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.BidDto
import com.inhouse.yoursell.dto.CreateBidDto
import com.inhouse.yoursell.dto.UpdateBidDto
import com.inhouse.yoursell.dto.toDto
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
        val currentUser = authentication.toUser()
        val auction = auctionRepo.findById(payload.auctionId)
            .orElseThrow { throw NotFoundException("Auction not found.") }
        val existingBid = bidRepo.findByAuctionAndBidder(auction, currentUser)

        if (existingBid.isPresent) {
            throw Exception("User can create only one bid.")
        }

        if (currentUser.id == auction.auctionOwner.id) {
            throw Exception("Cannot make bid on your own.")
        }
        val bid = Bid(
            auction = auction,
            bidValue = payload.bidValue,
            bidder = currentUser
        )
        return bidRepo.save(bid).toDto()
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

    fun updateBid(
        id: UUID,
        payload: UpdateBidDto,
        authentication: Authentication,
    ): BidDto {
        val bid = bidRepo.findById(id).orElseThrow {
            throw NotFoundException("Bid not found.")
        }
        if (bid.bidder.id != authentication.toUser().id) {
            throw Exception("Cannot update bid.")
        }
        bid.bidValue = payload.bidValue
        return bidRepo.save(bid).toDto()
    }
}