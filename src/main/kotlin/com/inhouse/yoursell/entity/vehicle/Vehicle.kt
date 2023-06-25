package com.inhouse.yoursell.entity.vehicle

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.enums.EDrivetrain
import com.inhouse.yoursell.entity.vehicle.enums.EModel
import com.inhouse.yoursell.entity.vehicle.enums.ETransmission
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

    @Column(name = "producer", nullable = false)
    var producer: String = "",

    @Column(name = "model", nullable = false)
    var model: EModel = EModel.DEFAULT,

    @Column(name = "mileage", nullable = false)
    var mileage: Double = 0.0,

    @Column(name = "vin", nullable = false)
    var vin: String = "",

    @Column(name = "year", nullable = false, length = 4)
    var year: String = "",

    @Column(name = "engine")
    var engine: String = "",

    @Column(name = "drivetrain")
    var drivetrain: EDrivetrain = EDrivetrain.DEFAULT,

    @Column(name = "transmission")
    var transmission: ETransmission = ETransmission.DEFAULT,

    @Column(name = "body_style")
    var bodyStyle: String = "",

    @Column(name = "exterior_color")
    var exteriorColor: String = "",

    @Column(name = "interior_color")
    var interiorColor: String = "",

    @Column(name = "seller_type")
    var sellerType: String = "",

    @Column(name = "highlights", length = 1500)
    var highlights: String = "",

    @Column(name="expected_bid", nullable = false)
    var expectedBid: Double = 0.0,

    @Column(name="damaged", nullable = false)
    var damaged: Boolean = false

) : BaseEntity() {

    override fun toString(): String {
        return "Vehicle(id=$id, seller=$seller, " +
                "producer='$producer', model=$model, " +
                "mileage=$mileage, vin='$vin', year='$year'," +
                " engine='$engine', drivetrain=$drivetrain, transmission=$transmission, " +
                "bodyStyle='$bodyStyle', exteriorColor='$exteriorColor', interiorColor='$interiorColor', " +
                "sellerType='$sellerType', highlights='$highlights', expectedBid=$expectedBid, damaged=$damaged)"
    }
}