package com.pascs.service;

import com.pascs.model.*;
import com.pascs.repository.*;
import com.pascs.payload.response.DashboardStatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CounterRepository counterRepository;

    public DashboardStatsResponse getDashboardStats() {
        // Application statistics
        long totalApplications = applicationRepository.count();
        long pendingApplications = applicationRepository.countByStatus(Application.ApplicationStatus.PENDING);
        long processingApplications = applicationRepository.countByStatus(Application.ApplicationStatus.PROCESSING);
        long completedApplications = applicationRepository.countByStatus(Application.ApplicationStatus.COMPLETED);
        
        // Queue statistics
        long totalQueues = queueRepository.count();
        long waitingQueues = queueRepository.countByStatus(Queue.QueueStatus.WAITING);
        long processingQueues = queueRepository.countByStatus(Queue.QueueStatus.PROCESSING);
        long completedQueuesToday = queueRepository.countCompletedToday();
        
        // Feedback statistics
        long totalFeedbacks = feedbackRepository.count();
        long recentFeedbacks = feedbackRepository.countRecentFeedbacks();
        
        // User statistics
        long totalCitizens = userRepository.countByRole(User.UserRole.CITIZEN);
        long totalStaff = userRepository.countByRole(User.UserRole.STAFF);
        
        // Performance metrics
        Integer avgWaitTime = queueRepository.calculateAverageWaitTime();
        
        return new DashboardStatsResponse(
            totalApplications, 
            pendingApplications, 
            processingApplications, 
            completedApplications,
            totalQueues, 
            waitingQueues, 
            processingQueues, 
            completedQueuesToday,
            totalFeedbacks, 
            recentFeedbacks,
            totalCitizens, 
            totalStaff,
            avgWaitTime != null ? avgWaitTime : 0
        );
    }

    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic counts
        stats.put("totalApplications", applicationRepository.count());
        stats.put("totalQueues", queueRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalServices", serviceRepository.count());
        stats.put("activeCounters", counterRepository.findByActiveTrue().size());
        
        // Application status distribution
        Map<String, Long> applicationStatus = new HashMap<>();
        for (Application.ApplicationStatus status : Application.ApplicationStatus.values()) {
            long count = applicationRepository.countByStatus(status);
            applicationStatus.put(status.name(), count);
        }
        stats.put("applicationStatus", applicationStatus);
        
        // Queue status distribution
        Map<String, Long> queueStatus = new HashMap<>();
        for (Queue.QueueStatus status : Queue.QueueStatus.values()) {
            long count = queueRepository.countByStatus(status);
            queueStatus.put(status.name(), count);
        }
        stats.put("queueStatus", queueStatus);
        
        // User role distribution
        Map<String, Long> userRoles = new HashMap<>();
        for (User.UserRole role : User.UserRole.values()) {
            long count = userRepository.countByRole(role);
            userRoles.put(role.name(), count);
        }
        stats.put("userRoles", userRoles);
        
        // Recent activity
        stats.put("newApplicationsToday", applicationRepository.countNewApplicationsToday());
        stats.put("completedQueuesToday", queueRepository.countCompletedToday());
        stats.put("recentFeedbacks", feedbackRepository.countRecentFeedbacks());
        
        // Performance metrics
        Integer avgProcessingTime = queueRepository.calculateAverageWaitTime();
        stats.put("averageProcessingTime", avgProcessingTime != null ? avgProcessingTime : 0);
        
        return stats;
    }

    public Map<String, Object> getDailyQueueStats(LocalDate date) {
        Map<String, Object> chartData = new HashMap<>();
        
        // Sample hourly queue statistics
        Map<String, Long> hourlyStats = new HashMap<>();
        for (int hour = 7; hour <= 17; hour++) {
            hourlyStats.put(String.format("%02d:00", hour), (long) (Math.random() * 20));
        }
        
        chartData.put("date", date.toString());
        chartData.put("hourlyStats", hourlyStats);
        chartData.put("totalProcessed", hourlyStats.values().stream().mapToLong(Long::longValue).sum());
        
        return chartData;
    }

    public Map<String, Object> getServiceDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        
        List<com.pascs.model.Service> services = serviceRepository.findAll();
        Map<String, Long> serviceCounts = new HashMap<>();
        Map<String, Long> serviceQueues = new HashMap<>();
        
        for (com.pascs.model.Service service : services) {
            long appCount = applicationRepository.findByServiceId(service.getId()).size();
            long queueCount = queueRepository.findByServiceId(service.getId()).size();
            
            serviceCounts.put(service.getName(), appCount);
            serviceQueues.put(service.getName(), queueCount);
        }
        
        distribution.put("applicationsByService", serviceCounts);
        distribution.put("queuesByService", serviceQueues);
        distribution.put("totalServices", services.size());
        
        return distribution;
    }

    public Map<String, Object> getStaffPerformanceStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> performanceStats = new HashMap<>();
        
        List<User> staffMembers = userRepository.findByRole(User.UserRole.STAFF);
        Map<String, Object> staffPerformance = new HashMap<>();
        
        for (User staff : staffMembers) {
            Map<String, Object> staffStats = new HashMap<>();
            long assignedApps = applicationRepository.countByAssignedStaffId(staff.getId());
            long completedApps = applicationRepository.countByAssignedStaffIdAndStatus(
                staff.getId(), Application.ApplicationStatus.COMPLETED);
            long processingApps = applicationRepository.countByAssignedStaffIdAndStatus(
                staff.getId(), Application.ApplicationStatus.PROCESSING);
            
            staffStats.put("assignedApplications", assignedApps);
            staffStats.put("completedApplications", completedApps);
            staffStats.put("processingApplications", processingApps);
            staffStats.put("completionRate", assignedApps > 0 ? 
                (double) completedApps / assignedApps * 100 : 0);
            
            staffPerformance.put(staff.getFullName(), staffStats);
        }
        
        performanceStats.put("staffPerformance", staffPerformance);
        performanceStats.put("period", startDate + " to " + endDate);
        
        return performanceStats;
    }
}