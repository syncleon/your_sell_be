import com.inhouse.yoursell.dto.*
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.entity.item.Item
import com.inhouse.yoursell.entity.user.Role
import com.inhouse.yoursell.entity.user.User
import java.util.stream.Collectors

fun Role.toDto(): RoleDto {
    return RoleDto(name = name)
}

fun User.toDto(): UserDto {
    return UserDto(
        id=id,
        username=username,
        email=email,
        items = items.stream().map(Item::toDto).collect(Collectors.toList()),
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
        price = price,
        engine = engine,
        fuelType = fuelType,
        transmission = transmission,
        bodyStyle = bodyStyle,
        condition = condition,
        drivetrain = drivetrain,
        location = location,
        description = description,
        vin = vin,
        exteriorColor = exteriorColor,
        interiorColor = interiorColor,
        imagesFeatured = imagesFeatured,
        imagesExterior = imagesExterior,
        imagesInterior = imagesInterior,
        imagesMechanical = imagesMechanical,
        imagesOther = imagesOther,
        isSold = isSold,
        onAuction = onAuction,
        userId = user?.id,
        username = user?.username ?: "Unknown",
        auction = auction?.toDto()
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
        itemId = item.id,
        auctionStatus = auctionStatus,
        bids = bids.stream().map(Bid::toDto).collect(Collectors.toList()),
        currentHighestBid = currentHighestBid,
        expectedPrice = expectedPrice,
        reservePrice = reservePrice,
        winningBidId = winningBidId,
        duration = duration,
        startTime = startTime,
        endTime = endTime,
        bidCount = bidCount,
        isExtended = isExtended,
        isAutoExtendEnabled = isAutoExtendEnabled,
        autoExtendDuration = autoExtendDuration
    )
}

