package com.inhouse.yoursell.entity.vehicle

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.user.User
import jakarta.persistence.*

@Entity
@Table(name = "vehicle")
data class Vehicle(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_id_seq")
    @SequenceGenerator(name = "vehicle_id_seq", allocationSize = 1)
    var id: Long = 0L,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var seller: User = User(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vehicle_images", joinColumns = [JoinColumn(name = "vehicle_id")])
    @Column(name = "image_url")
    val images: MutableList<String> = mutableListOf(),

    @Column(name = "make", nullable = false)
    var make: String = "",

    @Column(name = "model", nullable = false)
    var model: String = "",

    @Column(name = "mileage", nullable = false)
    var mileage: Double = 0.0,

    @Column(name = "vin", nullable = false)
    var vin: String = "",

    @Column(name = "year", nullable = false, length = 4)
    var year: String = "",

    @Column(name="expected_bid", nullable = false)
    var expectedBid: Double = 0.0,

    @Column(name="damaged", nullable = false)
    var damaged: Boolean = false,

    ) : BaseEntity() {

    override fun toString(): String {
        return "Vehicle" +
                "(" +
                "id=$id, " +
                "seller=$seller, " +
                "producer='$make'," +
                "model=$model, " +
                "mileage=$mileage, " +
                "vin='$vin', " +
                "year='$year'," +
                "expectedBid=$expectedBid, " +
                "damaged=$damaged" +
                ")"
    }
}