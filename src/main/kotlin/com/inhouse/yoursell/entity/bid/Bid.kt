package com.inhouse.yoursell.entity.bid

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.user.User
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.util.*

@Entity
data class Bid (
    @Id
    @UuidGenerator
    var id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    var bidder: User,

    @ManyToOne
    @JoinColumn(name = "auction_id")
    var auction: Auction,

    var bidValue: BigDecimal,

    ) : BaseEntity()