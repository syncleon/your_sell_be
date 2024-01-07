package com.inhouse.yoursell.entity.auction

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "auction")
class Auction(
    @Id
    @UuidGenerator
    var id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    var auctionOwner: User = User(),

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    val vehicle: Vehicle,

    @Column(name = "auction_status", nullable = false)
    var auctionStatus: AuctionStatus,

    @Column(name = "start_time")
    var startTime: Long = 0L,

    @Column(name = "end_time")
    var endTime: Long = 0L,

    @Column(name = "max_bid_current")
    var currentMaxBid: BigDecimal = BigDecimal.ZERO,

    @Column(name = "max_bidder_id")
    var currentMaxBidderId: Long = 0L,

    @Column(name = "reserve_price")
    var reservePrice: BigDecimal,

    @OneToMany(mappedBy = "auction",
        cascade = [CascadeType.ALL],
        fetch = FetchType.EAGER)
    var bids: MutableList<Bid> = mutableListOf(),

    ) : BaseEntity()