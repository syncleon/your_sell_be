package com.inhouse.yoursell.entity.item

import com.inhouse.yoursell.entity.BaseEntity
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
    var user: User = User(),

    var make: String,
    var model: String,
    var mileage: String,
    var vin: String,
    var year: String,
    var engine: String,
    var fuelType: String,
    var transmission: String,
    var condition: String,
    var drivetrain: String,
    var bodyStyle: String,
    var location: String,
    var price: Double,
    var exteriorColor: String,
    var interiorColor: String,
    var description: String,
    var onAuction: Boolean = false,
    var isSold: Boolean = false,

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
                "user=${user.id}, " +  // Displaying user ID
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
                "imagesFeatured=${imagesFeatured.size}, " +
                "imagesExterior=${imagesExterior.size}, " +
                "imagesInterior=${imagesInterior.size}, " +
                "imagesMechanical=${imagesMechanical.size}, " +
                "imagesOther=${imagesOther.size})"
    }

}
