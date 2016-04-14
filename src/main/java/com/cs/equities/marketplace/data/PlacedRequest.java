package com.cs.equities.marketplace.data;

import java.time.Instant;
import java.util.UUID;

public class PlacedRequest implements  Comparable<PlacedRequest>{

    private PlacementRequest originalRequest;
    private final Instant placementTime;
    private final String id;

    public PlacedRequest(PlacementRequest originalRequest) {
        this.originalRequest = originalRequest;
        this.placementTime = Instant.now();
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public int compareTo(PlacedRequest o) {
        return this.placementTime.compareTo(o.placementTime);
    }

    public PlacementRequest getOriginalRequest() {
        return originalRequest;
    }

    public void setOriginalRequest(PlacementRequest request) { this.originalRequest = request; }

    public Instant getPlacementTime() {
        return placementTime;
    }

    public String getId() {
        return id;
    }

}
