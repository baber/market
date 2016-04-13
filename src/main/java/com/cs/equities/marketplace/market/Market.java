package com.cs.equities.marketplace.market;

import com.cs.equities.marketplace.data.PlacedRequest;
import com.cs.equities.marketplace.data.PlacementRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Market {

    private final MarketState marketState;

    public Market(MarketState marketState) {
        this.marketState = marketState;
    }

    public void placeBid(PlacementRequest request) throws IllegalArgumentException {
        this.validatePlacementRequest(request);
        this.marketState.addBid(request);
    }

    public void placeOffer(PlacementRequest request) {
        this.validatePlacementRequest(request);
        this.marketState.addOffer(request);
    }

    public Set<PlacedRequest> getBidsForUser(String userId) {
        return marketState.getBidsForUser(userId);
    }


    public Set<PlacedRequest> getOffersForUser(String userId) {
        return marketState.getOffersForUser(userId);
    }

    public int getCurrentBidPrice(String itemId) {
        final Set<PlacedRequest> bidsForItem = this.marketState.getBidsForItem(itemId);
        if (bidsForItem == null) { return 0; }

        OptionalInt maybeItem = bidsForItem.stream().mapToInt(x -> x.getPlacementRequest().getPrice()).max();
        return maybeItem.isPresent() ? maybeItem.getAsInt() : 0;
    }

    public int getCurrentOfferPrice(String itemId) {
        final Set<PlacedRequest> offersForItem = this.marketState.getOffersForItem(itemId);
        if (offersForItem == null) { return 0; }

        OptionalInt maybeItem = offersForItem.stream().mapToInt(x -> x.getPlacementRequest().getPrice()).min();
        return maybeItem.isPresent() ? maybeItem.getAsInt() : 0;

    }

    private void validatePlacementRequest(PlacementRequest request) throws IllegalArgumentException {
        if (StringUtils.isEmpty(request.getItemId())) { throw new IllegalArgumentException("Placement request must have an item id!"); }
        if (StringUtils.isEmpty(request.getUserId())) { throw new IllegalArgumentException("Placement request must have a user id!"); }
        if (request.getQuantity() <= 0) { throw new IllegalArgumentException("Placement request must have a quantity greater than 0!"); }
        if (request.getPrice() < 0) { throw new IllegalArgumentException("Placement request cannot have a negative price!"); }
    }

}
