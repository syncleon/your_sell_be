package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.auction.Auction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AuctionRepo : JpaRepository<Auction, UUID>