package com.cs.equities.marketplace.market;

import com.cs.equities.marketplace.data.Order;
import com.cs.equities.marketplace.data.AcceptedSubmission;
import com.cs.equities.marketplace.data.Submission;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class Market {

    private final MarketState marketState = new MarketState();


    public synchronized void placeBid(Submission request) throws IllegalArgumentException {
        this.validatePlacementRequest(request);
        AcceptedSubmission bid = this.marketState.addBid(request);
        this.processBid(bid);
    }

    public synchronized void placeOffer(Submission request) {
        this.validatePlacementRequest(request);
        AcceptedSubmission offer = this.marketState.addOffer(request);
        this.processOffer(offer);
    }

    public List<AcceptedSubmission> getBidsForUser(String userId) {
        return marketState.getBidsForUser(userId);
    }


    public List<AcceptedSubmission> getOffersForUser(String userId) {
        return marketState.getOffersForUser(userId);
    }

    public List<Order> getPurchaseOrdersForUser(String userId) {
        return this.marketState.getPurchaseOrdersForUser(userId);
    }

    public List<Order> getSaleOrdersForUser(String userId) {
        return this.marketState.getSaleOrdersForUser(userId);
    }

    public int getCurrentBidPrice(String itemId) {
        OptionalInt maybeItem = this.marketState.getBidsForItem(itemId).stream().mapToInt(x -> x.getSubmission().getPricePerUnit()).max();
        return maybeItem.isPresent() ? maybeItem.getAsInt() : 0;
    }

    public int getCurrentOfferPrice(String itemId) {
        OptionalInt maybeItem = this.marketState.getOffersForItem(itemId).stream().mapToInt(x -> x.getSubmission().getPricePerUnit()).min();
        return maybeItem.isPresent() ? maybeItem.getAsInt() : 0;
    }

    private void validatePlacementRequest(Submission request) throws IllegalArgumentException {
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

    private void processBid(AcceptedSubmission bid) {
        List<AcceptedSubmission> offers = this.marketState.getOffersForItem(bid.getSubmission().getItemId());
        Optional<AcceptedSubmission> maybeMatchingOffer = offers.stream().filter(
                x -> x.getSubmission().getQuantity() >= bid.getSubmission().getQuantity()
                        && x.getSubmission().getPricePerUnit() <= bid.getSubmission().getPricePerUnit()).findFirst();

        if (maybeMatchingOffer.isPresent()) {
            AcceptedSubmission matchingOffer = maybeMatchingOffer.get();
            this.processMatchingBidAndOffer(bid, matchingOffer);
        }

    }

    private void processOffer(AcceptedSubmission offer) {
        List<AcceptedSubmission> bids = this.marketState.getBidsForItem(offer.getSubmission().getItemId());
        Optional<AcceptedSubmission> maybeMatchingBid = bids.stream().filter(
                x -> x.getSubmission().getQuantity() <= offer.getSubmission().getQuantity()
                        && x.getSubmission().getPricePerUnit() >= offer.getSubmission().getPricePerUnit()).findFirst();

        if (maybeMatchingBid.isPresent()) {
            AcceptedSubmission matchingBid = maybeMatchingBid.get();
            this.processMatchingBidAndOffer(matchingBid, offer);
        }

    }

    private void processMatchingBidAndOffer(AcceptedSubmission bid, AcceptedSubmission offer) {
        Order order = this.createOrder(bid.getSubmission(), offer.getSubmission());
        this.marketState.addOrder(order);

        this.marketState.removeBid(bid);
        if (offer.getSubmission().getQuantity() == bid.getSubmission().getQuantity()) {
            this.marketState.removeOffer(offer);
        } else {
            Submission originalOffer = offer.getSubmission();
            int remainingQuantity = originalOffer.getQuantity() - bid.getSubmission().getQuantity();
            offer.setSubmission(new Submission(originalOffer.getItemId(), originalOffer.getUserId(), remainingQuantity, originalOffer.getPricePerUnit()));
        }
    }

    private Order createOrder(Submission bid, Submission offer) {
        return new Order(bid.getUserId(), offer.getUserId(), bid.getItemId(), bid.getQuantity(), offer.getPricePerUnit());
    }

}
