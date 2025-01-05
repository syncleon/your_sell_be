package com.inhouse.yoursell.entity.auction

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.entity.item.Item
import com.inhouse.yoursell.entity.user.User
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "auction")
class Auction(
    @Id @UuidGenerator
    var id: UUID = UUID.randomUUID(),

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    var user: User = User(),

    @OneToOne @JoinColumn(name = "item_id", nullable = false)
    var item: Item,

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_status")
    var auctionStatus: AuctionStatus = AuctionStatus.CREATED,

    @Column(name = "expected_price", nullable = false)
    var expectedPrice: Double = 0.0,

    @Column(name = "current_max_bid")
    var currentMaxBid: Double = 0.0,

    @Enumerated(EnumType.STRING)
    @Column(name = "duration", nullable = false)
    var duration: AuctionDuration = AuctionDuration.MINUTE,

    @Column(name = "start_time", nullable = false)
    var startTime: Long = 0L,

    @Column(name = "end_time", nullable = false)
    var endTime: Long = 0L,

    @Column(name = "bid_count")
    var bidCount: Int = 0,

    @Column(name = "is_extended")
    var isExtended: Boolean = false,

    @Column(name = "is_auto_extend_enabled")
    var isAutoExtendEnabled: Boolean = false,

    @Column(name = "auto_extend_duration")
    var autoExtendDuration: Long = 1 * 60 * 1000L, // Default: 5 minutes

    @OneToMany(mappedBy = "auction", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var bids: MutableList<Bid> = mutableListOf()

) : BaseEntity() {
    override fun toString(): String {
        return "Auction(id=$id, " +
                "userId=${user.id}, " +
                "itemId=${item.id}, " +
                "auctionStatus=$auctionStatus, " +
                "expectedPrice=$expectedPrice, " +
                "currentMaxBid=$currentMaxBid, " +
                "duration=$duration, " +
                "startTime=$startTime, " +
                "endTime=$endTime, " +
                "bidCount=$bidCount, " +
                "isExtended=$isExtended, " +
                "isAutoExtendEnabled=$isAutoExtendEnabled, " +
                "autoExtendDuration=$autoExtendDuration, " +
                "bids=${bids.size})"
    }
}
