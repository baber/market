package com.cs.equities.marketplace.data;

import java.util.UUID;

public class Order {

    private final String id = UUID.randomUUID().toString();

    private final String buyerId;
    private final String sellerId;
    private final String itemId;
    private final int quantity;
    private final int pricePerUnit;

    public Order(String buyerId, String sellerId, String itemId, int quantity, int pricePerUnit) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public String getId() {
        return id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPricePerUnit() {
        return pricePerUnit;
    }
}
