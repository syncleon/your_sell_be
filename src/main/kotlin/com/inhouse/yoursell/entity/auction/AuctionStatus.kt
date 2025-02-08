package com.inhouse.yoursell.entity.auction

enum class AuctionStatus {
    NOT_LISTED,  // Auction is not yet listed
    CREATED,     // Auction has been created but not yet started
    STARTED,     // Auction is currently active
    PAUSED,      // Auction is temporarily paused
    CANCELLED,   // Auction has been canceled before closing
    CLOSED,      // Auction has ended normally
    EXPIRED,     // Auction ended without a winner (if applicable)
    SOLD,        // Auction closed with a successful sale
    FAILED       // Auction closed but had an issue (e.g., no valid bids)
}