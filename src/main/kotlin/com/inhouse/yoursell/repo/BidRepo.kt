package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.auction.Bid
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BidRepo : JpaRepository<Bid, UUID>