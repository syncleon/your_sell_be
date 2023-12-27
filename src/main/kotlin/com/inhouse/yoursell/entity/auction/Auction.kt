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

    @ManyToOne
    @JoinColumn(name = "user_id")
    var auctionOwner: User = User(),

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    val vehicle: Vehicle

): BaseEntity()