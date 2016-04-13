package com.cs.equities.marketplace.data;

import java.time.Instant;
import java.util.UUID;

public class PlacedRequest implements  Comparable<PlacedRequest>{

    private final PlacementRequest placementRequest;
    private final Instant placementTime;
    private final String id;

    public PlacedRequest(PlacementRequest placementRequest) {
        this.placementRequest = placementRequest;
        this.placementTime = Instant.now();
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public int compareTo(PlacedRequest o) {
        return this.placementTime.compareTo(o.placementTime);
    }

    public PlacementRequest getPlacementRequest() {
        return placementRequest;
    }

    public Instant getPlacementTime() {
        return placementTime;
    }

    public String getId() {
        return id;
    }
}
