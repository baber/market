package com.cs.equities.marketplace.data;

import java.time.Instant;
import java.util.UUID;

public class AcceptedSubmission implements  Comparable<AcceptedSubmission>{

    private Submission submission;
    private final Instant placementTime;
    private final String id;

    public AcceptedSubmission(Submission submission) {
        this.submission = submission;
        this.placementTime = Instant.now();
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public int compareTo(AcceptedSubmission o) {
        return this.placementTime.compareTo(o.placementTime);
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission request) { this.submission = request; }

    public Instant getPlacementTime() {
        return placementTime;
    }

    public String getId() {
        return id;
    }

}
