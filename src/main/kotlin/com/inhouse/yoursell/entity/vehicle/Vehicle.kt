package com.inhouse.yoursell.entity.vehicle

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.user.User
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "vehicle")
data class Vehicle(
    @Id
    @UuidGenerator
    var id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    var seller: User = User(),

    @Column(name = "make", nullable = false)
    var make: String = "",

    @Column(name = "model", nullable = false)
    var model: String = "",

    @Column(name = "mileage", nullable = false)
    var mileage: Int = 0,

    @Column(name = "vin", nullable = false)
    var vin: String = "",

    @Column(name = "year", nullable = false, length = 4)
    var year: String = "",

    @Column(name="expected_bid", nullable = false)
    var expectedBid: Int = 0,

    @Column(name="damaged", nullable = false)
    var damaged: Boolean = false,

    @Column(name = "deleted", nullable = false)
    var deleted: Boolean = false,

    @Column(name="on_sale", nullable = false)
    var onSale: Boolean = false,

    @Column(name="is_sold", nullable = false)
    var isSold: Boolean = false,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "vehicle_images",
        joinColumns = [JoinColumn(name = "vehicle_id")])
    @Column(name = "image_url")
    var images: MutableList<String> = mutableListOf(),


    ) : BaseEntity()

{
    override fun toString(): String {
        return "Vehicle(" +
                "id=$id, " +
                "seller=${seller.id}, " +
                "make='$make', " +
                "model='$model', " +
                "mileage=$mileage, " +
                "vin='$vin', " +
                "year='$year', " +
                "expectedBid=$expectedBid, " +
                "damaged=$damaged, " +
                "deleted=$deleted, " +
                "onSale=$onSale, " +
                "isSold=$isSold, " +
                "images=$images" +
                ")"
    }
}