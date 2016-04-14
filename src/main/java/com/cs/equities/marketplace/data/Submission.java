package com.cs.equities.marketplace.data;

public class Submission {


    private final String itemId;
    private final String userId;
    private final int quantity;
    private final int pricePerUnit;

    public Submission(String itemId, String userId, int quantity, int pricePerUnit) {
        this.itemId = itemId;
        this.userId = userId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public String getItemId() {
        return itemId;
    }

    public String getUserId() {
        return userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPricePerUnit() {
        return pricePerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Submission that = (Submission) o;

        if (quantity != that.quantity) return false;
        if (pricePerUnit != that.pricePerUnit) return false;
        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null) return false;
        return userId != null ? userId.equals(that.userId) : that.userId == null;

    }

    @Override
    public int hashCode() {
        int result = itemId != null ? itemId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + quantity;
        result = 31 * result + pricePerUnit;
        return result;
    }
}
