package com.cs.equities.marketplace.market;

import com.cs.equities.marketplace.data.Order;
import com.cs.equities.marketplace.data.AcceptedSubmission;
import com.cs.equities.marketplace.data.Submission;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MarketState {

    private Map<String, List<AcceptedSubmission>> bidsByItem = new ConcurrentHashMap<>();
    private Map<String, List<AcceptedSubmission>> offersByItem = new ConcurrentHashMap<>();
    private Map<String, List<AcceptedSubmission>> bidsByUser = new ConcurrentHashMap<>();
    private Map<String, List<AcceptedSubmission>> offersByUser = new ConcurrentHashMap<>();
    private Map<String, List<Order>> purchaseOrdersByUser = new ConcurrentHashMap<>();
    private Map<String, List<Order>> saleOrdersByUser = new ConcurrentHashMap<>();


    public AcceptedSubmission addBid(Submission bid) {
        final AcceptedSubmission item = new AcceptedSubmission(bid);
        updateMap(bid.getItemId(), item, this.bidsByItem);
        updateMap(bid.getUserId(), item, this.bidsByUser);
        return item;
    }


    public void removeBid(AcceptedSubmission bid) {
        removeItem(bid.getSubmission().getItemId(), bid, this.bidsByItem);
        removeItem(bid.getSubmission().getUserId(), bid, this.bidsByUser);
    }


    public AcceptedSubmission addOffer(Submission offer) {
        final AcceptedSubmission item = new AcceptedSubmission(offer);
        updateMap(offer.getItemId(), item, this.offersByItem);
        updateMap(offer.getUserId(), item, this.offersByUser);
        return item;
    }

    public void removeOffer(AcceptedSubmission offer) {
        removeItem(offer.getSubmission().getItemId(), offer, this.offersByItem);
        removeItem(offer.getSubmission().getUserId(), offer, this.offersByUser);
    }

    public Order addOrder(Order order) {
        updateMap(order.getBuyerId(), order, this.purchaseOrdersByUser);
        updateMap(order.getSellerId(), order, this.saleOrdersByUser);
        return order;
    }

    public List<Order> getPurchaseOrdersForUser(String userId) { return this.purchaseOrdersByUser.getOrDefault(userId, Collections.emptyList()); }

    public List<Order> getSaleOrdersForUser(String userId) { return this.saleOrdersByUser.getOrDefault(userId, Collections.emptyList()); }

    public List<AcceptedSubmission> getBidsForUser(String userId) {
        return this.bidsByUser.getOrDefault(userId, Collections.emptyList());
    }

    public List<AcceptedSubmission> getBidsForItem(String itemId) {
        return this.bidsByItem.getOrDefault(itemId, Collections.emptyList());
    }

    public List<AcceptedSubmission> getOffersForUser(String userId) {
        return this.offersByUser.getOrDefault(userId, Collections.emptyList());
    }

    public List<AcceptedSubmission> getOffersForItem(String itemId) {
        return this.offersByItem.getOrDefault(itemId, Collections.emptyList());
    }

    private static <T> List<T> createList(T item) {
        List<T> items = new ArrayList<T>();
        items.add(item);
        return items;
    }

    private static void removeItem(String key, AcceptedSubmission item, Map<String, List<AcceptedSubmission>> map) {
        List<AcceptedSubmission> items =  map.get(key);
        if (items != null) {
            items.remove(item);
        }
    }

    private static <T> void updateMap(String key, T item, Map<String, List<T>> map) {
        map.merge(key, createList(item), (v1, v2) -> {v1.addAll(v2); return v1;} );
    }

}
