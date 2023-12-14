package com.inhouse.yoursell.entity.auction

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*


@Entity
@Table(name="auction")
class Auction (
    @Id
    @UuidGenerator
    var id: UUID = UUID.randomUUID(),

    @OneToOne
    @JoinColumn(name = "user_id")
    var auctionCreator: User = User(),

    @OneToOne(
        mappedBy = "lot",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var vehicle: Vehicle,

    @Column(name="reserved_price")
    var reservedPrice: Double?,

    @OneToMany(
        mappedBy = "auction",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var bids: List<Bid> = mutableListOf(),

    @Column(name = "winner_bid")
    @OneToOne
    var winnerBid: Bid? = null,

): BaseEntity()