package com.inhouse.yoursell.entity.image

import com.inhouse.yoursell.entity.vehicle.Vehicle
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Image (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val name: String = "",

    val contentType: String = "",

    val size: Long = 0L,

    val uploadTime: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    var vehicle: Vehicle = Vehicle()
) {
    override fun toString(): String {
        return "Image(id=$id, " +
                "name='$name', " +
                "contentType='$contentType', " +
                "size=$size, " +
                "uploadTime=$uploadTime, v" +
                "ehicle=${vehicle.id})"
    }
}