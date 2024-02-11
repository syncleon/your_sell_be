package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.entity.user.Role
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import java.util.stream.Collectors

fun Role.toDto(): RoleDto{
    return RoleDto(
        name = name
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id=id,
        username=username,
        email=email,
        userRoles = userRoles.stream().map(Role::toDto).collect(Collectors.toSet()),
        vehicles = vehicles.stream().map(Vehicle::toDto).collect(Collectors.toList()),
        auctions = auctions.stream().map(Auction::toDto).collect(Collectors.toList()),
        bids = bids.stream().map(Bid::toDto).collect(Collectors.toList())
    )
}

fun Vehicle.toDto(): VehicleDto {
    return VehicleDto(
        id = id,
        make = make,
        model = model,
        mileage = mileage,
        vin = vin,
        year = year,
        expectedBid = expectedBid,
        damaged = damaged,
        sellerId = seller.id,
        sellerUsername = seller.username,
        images = images,
        onSale = onSale,
        isSold = isSold,
        deleted = deleted
    )
}

fun Bid.toDto(): BidDto {
    return BidDto(
        id = id,
        auctionId = auction.id,
        bidderId = bidder.id,
        bidValue = bidValue
    )
}

fun Auction.toDto(): AuctionDto {
    return AuctionDto(
        id = id,
        vehicle = vehicle.toDto(),
        reservePrice = reservePrice,
        auctionStatus = auctionStatus,
        startTime = startTime,
        endTime = endTime,
        bids = bids.stream().map(Bid::toDto).collect(Collectors.toList()),
        currentMaxBid = currentMaxBid,
        currentMaxBidderId = currentMaxBidderId,
        auctionOwner = auctionOwner.username
    )
}