package com.inhouse.yoursell.entity.item

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.user.User
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "item")
data class Item(
    @Id @UuidGenerator
    var id: UUID = UUID.randomUUID(),

    @ManyToOne @JoinColumn(name = "user_id")
    var user: User? = null,

    @Column(nullable = false)
    var make: String,

    @Column(nullable = false)
    var model: String,

    var mileage: String? = null,
    var vin: String? = null,
    var year: String,

    var engine: String? = null,
    var fuelType: String? = null,
    var transmission: String? = null,
    var condition: String? = null,
    var drivetrain: String? = null,
    var bodyStyle: String? = null,
    var location: String? = null,
    @Column(nullable = false)
    var price: Double,
    var exteriorColor: String? = null,
    var interiorColor: String? = null,
    var description: String? = null,
    var onAuction: Boolean = false,
    var isSold: Boolean = false,

    @OneToOne(
        mappedBy = "item",
        cascade = [CascadeType.ALL],
        fetch = FetchType.EAGER,
        orphanRemoval = true)
    var auction: Auction? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "item_featured_images",
        joinColumns = [JoinColumn(name = "item_id")])
    @Column(name = "image_url")
    var imagesFeatured: MutableList<String> = mutableListOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "item_exterior_images",
        joinColumns = [JoinColumn(name = "item_id")])
    @Column(name = "image_url")
    var imagesExterior: MutableList<String> = mutableListOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "item_interior_images",
        joinColumns = [JoinColumn(name = "item_id")])
    @Column(name = "image_url")
    var imagesInterior: MutableList<String> = mutableListOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "item_mechanical_images",
        joinColumns = [JoinColumn(name = "item_id")])
    @Column(name = "image_url")
    var imagesMechanical: MutableList<String> = mutableListOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "item_other_images",
        joinColumns = [JoinColumn(name = "item_id")])
    @Column(name = "image_url")
    var imagesOther: MutableList<String> = mutableListOf(),
) : BaseEntity() {
    override fun toString(): String {
        return "Item(id=$id, " +
                "user=${user?.id}, " +
                "make='$make', " +
                "model='$model', " +
                "mileage='$mileage', " +
                "vin='$vin', " +
                "year='$year', " +
                "engine='$engine', " +
                "fuelType='$fuelType', " +
                "transmission='$transmission', " +
                "condition='$condition', " +
                "drivetrain='$drivetrain', " +
                "location='$location', " +
                "price=$price, " +
                "exteriorColor='$exteriorColor', " +
                "interiorColor='$interiorColor', " +
                "description='$description', " +
                "onAuction=$onAuction, " +
                "isSold=$isSold, " +
                "auction=${auction}, " +
                "imagesFeatured=${imagesFeatured.size}, " +
                "imagesExterior=${imagesExterior.size}, " +
                "imagesInterior=${imagesInterior.size}, " +
                "imagesMechanical=${imagesMechanical.size}, " +
                "imagesOther=${imagesOther.size})"
    }
}
