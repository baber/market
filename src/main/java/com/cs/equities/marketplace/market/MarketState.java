package com.cs.equities.marketplace.market;

import com.cs.equities.marketplace.data.PlacedRequest;
import com.cs.equities.marketplace.data.PlacementRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MarketState {

    private Map<String, Set<PlacedRequest>> bidsByItem = new HashMap<>();
    private Map<String, Set<PlacedRequest>> offersByItem = new ConcurrentHashMap<>();
    private Map<String, Set<PlacedRequest>> bidsByUser = new ConcurrentHashMap<>();
    private Map<String, Set<PlacedRequest>> offersByUser = new ConcurrentHashMap<>();


    public synchronized void addBid(PlacementRequest bid) {
        this.bidsByItem.merge(bid.getItemId(), createSet(new PlacedRequest(bid)), (v1, v2) -> {v1.addAll(v2); return v1;} );
        this.bidsByUser.merge(bid.getUserId(), createSet(new PlacedRequest(bid)), (v1, v2) -> {v1.addAll(v2); return v1;} );
    }

    public void addOffer(PlacementRequest offer) {
        this.offersByItem.merge(offer.getItemId(), createSet(new PlacedRequest(offer)), (v1, v2) -> {v1.addAll(v2); return v1;} );
        this.offersByUser.merge(offer.getUserId(), createSet(new PlacedRequest(offer)), (v1, v2) -> {v1.addAll(v2); return v1;} );
    }

    public Set<PlacedRequest> getBidsForUser(String userId) {
        return this.bidsByUser.get(userId);
    }

    public Set<PlacedRequest> getBidsForItem(String itemId) {
        return this.bidsByItem.get(itemId);
    }

    public Set<PlacedRequest> getOffersForUser(String userId) {
        return this.offersByUser.get(userId);
    }

    public Set<PlacedRequest> getOffersForItem(String itemId) {
        return this.offersByItem.get(itemId);
    }



    private static Set<PlacedRequest> createSet(PlacedRequest item) {
        Set<PlacedRequest> items = new HashSet<PlacedRequest>();
        items.add(item);
        return items;
    }


}
