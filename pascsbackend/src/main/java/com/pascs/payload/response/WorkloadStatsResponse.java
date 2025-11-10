package com.pascs.payload.response;

import lombok.Data;

@Data
public class WorkloadStatsResponse {
    private long totalAssigned;
    private long inProgress;
    private long completed;

    public WorkloadStatsResponse(long totalAssigned, long inProgress, long completed) {
        this.totalAssigned = totalAssigned;
        this.inProgress = inProgress;
        this.completed = completed;
    }
}