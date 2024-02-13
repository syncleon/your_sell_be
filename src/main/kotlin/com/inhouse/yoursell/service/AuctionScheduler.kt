package com.inhouse.yoursell.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class AuctionScheduler(private val auctionService: AuctionService) {

    @Scheduled(fixedRate = 30000)
    fun closeExpiredAuctions() {
        // Query and close expired auctions
        auctionService.closeAuctions()
    }
}