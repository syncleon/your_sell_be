package com.inhouse.yoursell.entity.user

import com.inhouse.yoursell.entity.BaseEntity
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.entity.item.Item
import jakarta.persistence.*

@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", allocationSize = 1)
    var id: Long = 0L,

    var username: String = "",
    var password: String = "",
    var email: String = "",

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles: MutableSet<Role> = mutableSetOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var items: MutableList<Item> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var auctions: MutableList<Auction> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var bids: MutableList<Bid> = mutableListOf()

) : BaseEntity() {
    override fun toString(): String {
        return "User(id=$id, username='$username', email='$email', roles=${roles.size}, items=${items.size}, auctions=${auctions.size}, bids=${bids.size})"
    }
}