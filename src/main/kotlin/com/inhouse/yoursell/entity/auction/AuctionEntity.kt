package com.inhouse.yoursell.entity.auction

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.vehicle.Vehicle
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*


@Entity
@Table(name="auction")
class AuctionEntity (
    @Id
    @UuidGenerator
    var id: UUID = UUID.randomUUID(),

    @OneToOne(mappedBy = "lot", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var vehicle: Vehicle?,

    @Column(name = "auction_date")
    var auctionDate: Long,

    @Column(name = "reserve_price")
    var reservePrice: Double?,

    @Column(name = "auction_location")
    var auctionLocation: String?,

    @Column(name = "auction_status")
    var auctionStatus: String?,

    @Column(name = "winner_bid")
    var winnerBid: Double?,

    @OneToMany(mappedBy = "auction", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var bids: List<BidEntity> = mutableListOf(),

    ): BaseEntity()