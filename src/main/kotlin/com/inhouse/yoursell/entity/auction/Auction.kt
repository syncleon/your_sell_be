package com.inhouse.yoursell.entity.auction

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.item.Item
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*


@Entity
@Table(name = "auction")
class Auction(
    @Id @UuidGenerator
    var id: UUID = UUID.randomUUID(),

    @ManyToOne @JoinColumn(name = "user_id")
    var user: User = User(),

    @OneToOne @JoinColumn(name = "item_id")
    val item: Item,

    var auctionStatus: AuctionStatus = AuctionStatus.CREATED,
    val expectedPrice: Double  = 0.0,
    var startTime: Long = 0L,
    var endTime: Long  = 0L,
    var currentMaxBid: Double = 0.0,
    @OneToMany(mappedBy = "auction", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var bids: MutableList<Bid> = mutableListOf(),

    ) : BaseEntity()