package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.entity.user.Role
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.item.Item
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
        items = items.stream().map(Item::toDto).collect(Collectors.toList()),
        auctions = auctions.stream().map(Auction::toDto).collect(Collectors.toList()),
        bids = bids.stream().map(Bid::toDto).collect(Collectors.toList())
    )
}

fun Item.toDto(): ItemDto {
    return ItemDto(
        id = id,
        make = make,
        model = model,
        mileage = mileage,
        year = year,
        featured = imagesFeatured,
        exterior = imagesExterior,
        interior = imagesInterior,
        other = imagesOther,
        mechanical = imagesMechanical,
        isSold = isSold,
        onAuction = onAuction
    )
}

fun Bid.toDto(): BidDto {
    return BidDto(
        id = id,
        userId = user.id,
        auctionId = auction.id,
        value = bidValue,
        isWinning = isWinningBid
    )
}

fun Auction.toDto(): AuctionDto {
    return AuctionDto(
        id = id,
        userId = user.id,
        item = item.toDto(),
        bids = bids.stream().map(Bid::toDto).collect(Collectors.toList()),
        currentMaxBid = currentMaxBid,
        expectedPrice = expectedPrice,
        auctionStatus = auctionStatus,
        startTime = startTime,
        endTime = endTime
    )
}