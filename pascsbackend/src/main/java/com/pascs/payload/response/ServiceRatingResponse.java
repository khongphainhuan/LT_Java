package com.pascs.payload.response;

import lombok.Data;
import java.util.Map;

@Data
public class ServiceRatingResponse {
    private double averageRating;
    private Map<Integer, Long> ratingDistribution;
    private int totalFeedbacks;

    public ServiceRatingResponse(double averageRating, Map<Integer, Long> ratingDistribution, int totalFeedbacks) {
        this.averageRating = averageRating;
        this.ratingDistribution = ratingDistribution;
        this.totalFeedbacks = totalFeedbacks;
    }
}