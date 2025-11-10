package com.pascs.payload.response;

import lombok.Data;

@Data
public class DashboardStatsResponse {
    private long totalApplications;
    private long pendingApplications;
    private long processingApplications;
    private long completedApplications;
    
    private long totalQueues;
    private long waitingQueues;
    private long processingQueues;
    private long completedQueuesToday;
    
    private long totalFeedbacks;
    private long recentFeedbacks;
    
    private long totalCitizens;
    private long totalStaff;
    
    private int averageWaitTime;

    public DashboardStatsResponse(long totalApplications, long pendingApplications, long processingApplications, 
                                long completedApplications, long totalQueues, long waitingQueues, 
                                long processingQueues, long completedQueuesToday, long totalFeedbacks, 
                                long recentFeedbacks, long totalCitizens, long totalStaff, int averageWaitTime) {
        this.totalApplications = totalApplications;
        this.pendingApplications = pendingApplications;
        this.processingApplications = processingApplications;
        this.completedApplications = completedApplications;
        this.totalQueues = totalQueues;
        this.waitingQueues = waitingQueues;
        this.processingQueues = processingQueues;
        this.completedQueuesToday = completedQueuesToday;
        this.totalFeedbacks = totalFeedbacks;
        this.recentFeedbacks = recentFeedbacks;
        this.totalCitizens = totalCitizens;
        this.totalStaff = totalStaff;
        this.averageWaitTime = averageWaitTime;
    }
}