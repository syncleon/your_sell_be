package com.inhouse.yoursell.entity.auction

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*


@Entity
@Table(name = "bid")
data class BidEntity(
    @Id
    @UuidGenerator
    var id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var bidder: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    var auction: AuctionEntity,

    @Column(name = "bid_amount")
    var bidAmount: Double,

    @Column(name = "bid_status")
    var bidStatus: Boolean,

    ) : BaseEntity()