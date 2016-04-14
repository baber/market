package com.cs.equities.marketplace.market;

import com.cs.equities.marketplace.data.Order;
import com.cs.equities.marketplace.data.PlacedRequest;
import com.cs.equities.marketplace.data.PlacementRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MarketState {

    private Map<String, List<PlacedRequest>> bidsByItem = new ConcurrentHashMap<>();
    private Map<String, List<PlacedRequest>> offersByItem = new ConcurrentHashMap<>();
    private Map<String, List<PlacedRequest>> bidsByUser = new ConcurrentHashMap<>();
    private Map<String, List<PlacedRequest>> offersByUser = new ConcurrentHashMap<>();
    private Map<String, List<Order>> purchaseOrdersByUser = new ConcurrentHashMap<>();
    private Map<String, List<Order>> saleOrdersByUser = new ConcurrentHashMap<>();


    public PlacedRequest addBid(PlacementRequest bid) {
        final PlacedRequest item = new PlacedRequest(bid);
        this.bidsByItem.merge(bid.getItemId(), createList(item), (v1, v2) -> {v1.addAll(v2); return v1;} );
        this.bidsByUser.merge(bid.getUserId(), createList(item), (v1, v2) -> {v1.addAll(v2); return v1;} );
        return item;
    }

    public void removeBid(PlacedRequest bid) {
        List<PlacedRequest> bidsForItem =  this.bidsByItem.get(bid.getOriginalRequest().getItemId());
        if (bidsForItem != null) {
            bidsForItem.remove(bid);
        }

        List<PlacedRequest> bidsForUser =  this.bidsByUser.get(bid.getOriginalRequest().getUserId());
        if (bidsForUser != null) {
            bidsForUser.remove(bid);
        }
    }


    public PlacedRequest addOffer(PlacementRequest offer) {
        final PlacedRequest item = new PlacedRequest(offer);
        this.offersByItem.merge(offer.getItemId(), createList(item), (v1, v2) -> {v1.addAll(v2); return v1;} );
        this.offersByUser.merge(offer.getUserId(), createList(item), (v1, v2) -> {v1.addAll(v2); return v1;} );
        return item;
    }

    public void removeOffer(PlacedRequest offer) {
        List<PlacedRequest> offersForItem =  this.offersByItem.get(offer.getOriginalRequest().getItemId());
        if (offersForItem != null) {
            offersForItem.remove(offer);
        }

        List<PlacedRequest> offersForUser =  this.offersByUser.get(offer.getOriginalRequest().getUserId());
        if (offersForUser != null) {
            offersForUser.remove(offer);
        }
    }

    public Order addOrder(Order order) {
        this.purchaseOrdersByUser.merge(order.getBuyerId(), createList(order), (v1, v2) -> {v1.addAll(v2); return v1;} );
        this.saleOrdersByUser.merge(order.getSellerId(), createList(order), (v1, v2) -> {v1.addAll(v2); return v1;} );
        return order;
    }

    public List<Order> getPurchaseOrdersForUser(String userId) { return this.purchaseOrdersByUser.getOrDefault(userId, Collections.emptyList()); }

    public List<Order> getSaleOrdersForUser(String userId) { return this.saleOrdersByUser.getOrDefault(userId, Collections.emptyList()); }

    public List<PlacedRequest> getBidsForUser(String userId) {
        return this.bidsByUser.getOrDefault(userId, Collections.emptyList());
    }

    public List<PlacedRequest> getBidsForItem(String itemId) {
        return this.bidsByItem.getOrDefault(itemId, Collections.emptyList());
    }

    public List<PlacedRequest> getOffersForUser(String userId) {
        return this.offersByUser.getOrDefault(userId, Collections.emptyList());
    }

    public List<PlacedRequest> getOffersForItem(String itemId) {
        return this.offersByItem.getOrDefault(itemId, Collections.emptyList());
    }

    private static List<PlacedRequest> createList(PlacedRequest item) {
        List<PlacedRequest> items = new ArrayList<PlacedRequest>();
        items.add(item);
        return items;
    }

    private static List<Order> createList(Order order) {
        List<Order> items = new ArrayList<Order>();
        items.add(order);
        return items;
    }

}
