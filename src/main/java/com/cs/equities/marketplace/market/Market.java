package com.cs.equities.marketplace.market;

import com.cs.equities.marketplace.data.Order;
import com.cs.equities.marketplace.data.PlacedRequest;
import com.cs.equities.marketplace.data.PlacementRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class Market {

    private final MarketState marketState = new MarketState();


    public void placeBid(PlacementRequest request) throws IllegalArgumentException {
        this.validatePlacementRequest(request);
        PlacedRequest bid = this.marketState.addBid(request);
        this.processBid(bid);
    }

    public void placeOffer(PlacementRequest request) {
        this.validatePlacementRequest(request);
        PlacedRequest offer = this.marketState.addOffer(request);
        this.processOffer(offer);
    }

    public List<PlacedRequest> getBidsForUser(String userId) {
        return marketState.getBidsForUser(userId);
    }


    public List<PlacedRequest> getOffersForUser(String userId) {
        return marketState.getOffersForUser(userId);
    }

    public List<Order> getPurchaseOrdersForUser(String userId) {
        return this.marketState.getPurchaseOrdersForUser(userId);
    }

    public List<Order> getSaleOrdersForUser(String userId) {
        return this.marketState.getSaleOrdersForUser(userId);
    }

    public int getCurrentBidPrice(String itemId) {
        final List<PlacedRequest> bidsForItem = this.marketState.getBidsForItem(itemId);
        if (bidsForItem == null) {
            return 0;
        }

        OptionalInt maybeItem = bidsForItem.stream().mapToInt(x -> x.getOriginalRequest().getPricePerUnit()).max();
        return maybeItem.isPresent() ? maybeItem.getAsInt() : 0;
    }

    public int getCurrentOfferPrice(String itemId) {
        final List<PlacedRequest> offersForItem = this.marketState.getOffersForItem(itemId);
        if (offersForItem == null) {
            return 0;
        }

        OptionalInt maybeItem = offersForItem.stream().mapToInt(x -> x.getOriginalRequest().getPricePerUnit()).min();
        return maybeItem.isPresent() ? maybeItem.getAsInt() : 0;

    }

    private void validatePlacementRequest(PlacementRequest request) throws IllegalArgumentException {
        if (StringUtils.isEmpty(request.getItemId())) {
            throw new IllegalArgumentException("Placement request must have an item id!");
        }
        if (StringUtils.isEmpty(request.getUserId())) {
            throw new IllegalArgumentException("Placement request must have a user id!");
        }
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Placement request must have a quantity greater than 0!");
        }
        if (request.getPricePerUnit() < 0) {
            throw new IllegalArgumentException("Placement request cannot have a negative price!");
        }
    }

    private void processBid(PlacedRequest bid) {
        List<PlacedRequest> offers = this.marketState.getOffersForItem(bid.getOriginalRequest().getItemId());
        if (offers.size() == 0) {
            return;
        }

        Optional<PlacedRequest> maybeMatchingOffer = offers.stream().filter(
                x -> x.getOriginalRequest().getQuantity() >= bid.getOriginalRequest().getQuantity()
                        && x.getOriginalRequest().getPricePerUnit() <= bid.getOriginalRequest().getPricePerUnit()).findFirst();

        if (maybeMatchingOffer.isPresent()) {
            PlacedRequest matchingOffer = maybeMatchingOffer.get();
            this.processMatchingBidAndOffer(bid, matchingOffer);
        }

    }

    private void processOffer(PlacedRequest offer) {
        List<PlacedRequest> bids = this.marketState.getBidsForItem(offer.getOriginalRequest().getItemId());
        if (bids.size() == 0) {
            return;
        }

        Optional<PlacedRequest> maybeMatchingBid = bids.stream().filter(
                x -> x.getOriginalRequest().getQuantity() <= offer.getOriginalRequest().getQuantity()
                        && x.getOriginalRequest().getPricePerUnit() >= offer.getOriginalRequest().getPricePerUnit()).findFirst();

        if (maybeMatchingBid.isPresent()) {
            PlacedRequest matchingBid = maybeMatchingBid.get();
            this.processMatchingBidAndOffer(matchingBid, offer);
        }

    }

    private void processMatchingBidAndOffer(PlacedRequest bid, PlacedRequest offer) {
        Order order = this.createOrder(bid.getOriginalRequest(), offer.getOriginalRequest());
        this.marketState.addOrder(order);

        this.marketState.removeBid(bid);
        if (offer.getOriginalRequest().getQuantity() == bid.getOriginalRequest().getQuantity()) {
            this.marketState.removeOffer(offer);
        } else {
            PlacementRequest originalOffer = offer.getOriginalRequest();
            int remainingQuantity = originalOffer.getQuantity() - bid.getOriginalRequest().getQuantity();
            offer.setOriginalRequest(new PlacementRequest(originalOffer.getItemId(), originalOffer.getUserId(), remainingQuantity, originalOffer.getPricePerUnit()));
        }
    }

    private Order createOrder(PlacementRequest bid, PlacementRequest offer) {
        return new Order(bid.getUserId(), offer.getUserId(), bid.getItemId(), bid.getQuantity(), offer.getPricePerUnit());
    }

}
