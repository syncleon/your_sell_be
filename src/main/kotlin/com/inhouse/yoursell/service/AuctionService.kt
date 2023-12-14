package com.inhouse.yoursell.service

import com.inhouse.yoursell.repo.AuctionRepo
import com.inhouse.yoursell.repo.VehicleRepo
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
@Transactional
class AuctionService (
    private val auctionRepo: AuctionRepo,
    private val vehicleRepo: VehicleRepo
) {
}



