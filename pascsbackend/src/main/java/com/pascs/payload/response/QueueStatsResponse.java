package com.pascs.payload.response;

import lombok.Data;

@Data
public class QueueStatsResponse {
    private long totalWaiting;
    private long totalProcessing;
    private long totalCompletedToday;
    private int averageWaitTime;

    public QueueStatsResponse(long totalWaiting, long totalProcessing, long totalCompletedToday, int averageWaitTime) {
        this.totalWaiting = totalWaiting;
        this.totalProcessing = totalProcessing;
        this.totalCompletedToday = totalCompletedToday;
        this.averageWaitTime = averageWaitTime;
    }
}