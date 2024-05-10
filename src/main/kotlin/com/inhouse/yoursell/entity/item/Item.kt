package com.inhouse.yoursell.entity.item

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.user.User
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
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
    var mileage: Int,
    var year: String,
    var onAuction: Boolean = false,
    var isSold: Boolean = false,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "item_images",
        joinColumns = [JoinColumn(name = "item_id")])
    @Column(name = "image_url")
    var images: MutableList<String> = mutableListOf(),

    ) : BaseEntity()