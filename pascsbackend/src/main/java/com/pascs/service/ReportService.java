package com.pascs.service;

import com.pascs.model.*;
import com.pascs.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

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

    public Resource generateDailyQueueReport(LocalDate date) throws IOException {
        // Generate PDF report for daily queue statistics
        // This is a simplified version - in production, you would use a library like Apache PDFBox, iText, or JasperReports
        
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("PASCS - DAILY QUEUE REPORT\n");
        reportContent.append("Date: ").append(date).append("\n\n");
        
        // Queue statistics
        long totalQueues = queueRepository.count();
        long waitingQueues = queueRepository.countByStatus(Queue.QueueStatus.WAITING);
        long processingQueues = queueRepository.countByStatus(Queue.QueueStatus.PROCESSING);
        long completedQueues = queueRepository.countByStatus(Queue.QueueStatus.COMPLETED);
        long completedToday = queueRepository.countCompletedToday();
        
        reportContent.append("QUEUE STATISTICS:\n");
        reportContent.append("Total Queues: ").append(totalQueues).append("\n");
        reportContent.append("Waiting: ").append(waitingQueues).append("\n");
        reportContent.append("Processing: ").append(processingQueues).append("\n");
        reportContent.append("Completed: ").append(completedQueues).append("\n");
        reportContent.append("Completed Today: ").append(completedToday).append("\n\n");
        
        // Service-wise distribution
        reportContent.append("SERVICE DISTRIBUTION:\n");
        List<com.pascs.model.Service> services = serviceRepository.findAll();
        for (com.pascs.model.Service service : services) {
            long serviceQueues = queueRepository.findByServiceId(service.getId()).size();
            reportContent.append(service.getName()).append(": ").append(serviceQueues).append("\n");
        }
        
        // Convert to byte array (simulating PDF generation)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(reportContent.toString().getBytes());
        
        return new ByteArrayResource(outputStream.toByteArray());
    }

    public Resource generateServicePerformanceReport(LocalDate startDate, LocalDate endDate) throws IOException {
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("PASCS - SERVICE PERFORMANCE REPORT\n");
        reportContent.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n\n");
        
        // Service performance metrics
        List<com.pascs.model.Service> services = serviceRepository.findAll();
        
        reportContent.append("SERVICE PERFORMANCE METRICS:\n");
        for (com.pascs.model.Service service : services) {
            List<Application> serviceApplications = applicationRepository.findByServiceId(service.getId());
            List<Queue> serviceQueues = queueRepository.findByServiceId(service.getId());
            
            long completedApplications = serviceApplications.stream()
                .filter(app -> app.getStatus() == Application.ApplicationStatus.COMPLETED)
                .count();
            
            long completedQueues = serviceQueues.stream()
                .filter(queue -> queue.getStatus() == Queue.QueueStatus.COMPLETED)
                .count();
            
            double completionRate = serviceApplications.isEmpty() ? 0 : 
                (double) completedApplications / serviceApplications.size() * 100;
            
            reportContent.append("\nService: ").append(service.getName()).append("\n");
            reportContent.append("  Total Applications: ").append(serviceApplications.size()).append("\n");
            reportContent.append("  Completed Applications: ").append(completedApplications).append("\n");
            reportContent.append("  Completion Rate: ").append(String.format("%.2f", completionRate)).append("%\n");
            reportContent.append("  Total Queues: ").append(serviceQueues.size()).append("\n");
            reportContent.append("  Completed Queues: ").append(completedQueues).append("\n");
        }
        
        // Feedback statistics
        reportContent.append("\nFEEDBACK STATISTICS:\n");
        List<Feedback> allFeedbacks = feedbackRepository.findAll();
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            long count = feedbackRepository.findByRating(i).size();
            ratingDistribution.put(i, count);
        }
        
        double averageRating = allFeedbacks.stream()
            .mapToInt(Feedback::getRating)
            .average()
            .orElse(0.0);
        
        reportContent.append("Total Feedbacks: ").append(allFeedbacks.size()).append("\n");
        reportContent.append("Average Rating: ").append(String.format("%.2f", averageRating)).append("/5\n");
        reportContent.append("Rating Distribution:\n");
        ratingDistribution.forEach((rating, count) -> 
            reportContent.append("  ").append(rating).append(" stars: ").append(count).append("\n"));
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(reportContent.toString().getBytes());
        
        return new ByteArrayResource(outputStream.toByteArray());
    }

    public Resource generateStaffWorkloadReport(String month) throws IOException {
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("PASCS - STAFF WORKLOAD REPORT\n");
        reportContent.append("Month: ").append(month).append("\n\n");
        
        // Staff workload statistics
        List<User> staffMembers = userRepository.findByRole(User.UserRole.STAFF);
        
        reportContent.append("STAFF WORKLOAD METRICS:\n");
        for (User staff : staffMembers) {
            long totalAssigned = applicationRepository.countByAssignedStaffId(staff.getId());
            long inProgress = applicationRepository.countByAssignedStaffIdAndStatus(
                staff.getId(), Application.ApplicationStatus.PROCESSING);
            long completed = applicationRepository.countByAssignedStaffIdAndStatus(
                staff.getId(), Application.ApplicationStatus.COMPLETED);
            
            double completionRate = totalAssigned > 0 ? 
                (double) completed / totalAssigned * 100 : 0;
            
            reportContent.append("\nStaff: ").append(staff.getFullName()).append("\n");
            reportContent.append("  Total Assigned: ").append(totalAssigned).append("\n");
            reportContent.append("  In Progress: ").append(inProgress).append("\n");
            reportContent.append("  Completed: ").append(completed).append("\n");
            reportContent.append("  Completion Rate: ").append(String.format("%.2f", completionRate)).append("%\n");
            
            // Active applications
            List<Application> activeApplications = applicationRepository.findActiveApplicationsByStaff(staff.getId());
            reportContent.append("  Active Applications: ").append(activeApplications.size()).append("\n");
        }
        
        // Counter statistics
        reportContent.append("\nCOUNTER STATISTICS:\n");
        List<Counter> counters = counterRepository.findAll();
        for (Counter counter : counters) {
            List<Queue> counterQueues = queueRepository.findByCounterId(counter.getId());
            reportContent.append("Counter: ").append(counter.getName())
                       .append(" (").append(counter.getLocation()).append(")\n");
            reportContent.append("  Active: ").append(counter.isActive() ? "Yes" : "No").append("\n");
            reportContent.append("  Current Staff: ").append(
                counter.getCurrentStaff() != null ? counter.getCurrentStaff().getFullName() : "None").append("\n");
            reportContent.append("  Total Queues Handled: ").append(counterQueues.size()).append("\n");
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(reportContent.toString().getBytes());
        
        return new ByteArrayResource(outputStream.toByteArray());
    }

    public Map<String, Object> getReportStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        // Application statistics for the period
        // Note: This is a simplified version - in production, you would filter by date range
        long totalApplications = applicationRepository.count();
        long completedApplications = applicationRepository.countByStatus(Application.ApplicationStatus.COMPLETED);
        double applicationCompletionRate = totalApplications > 0 ? 
            (double) completedApplications / totalApplications * 100 : 0;
        
        // Queue statistics
        long totalQueues = queueRepository.count();
        long completedQueues = queueRepository.countByStatus(Queue.QueueStatus.COMPLETED);
        Integer avgWaitTime = queueRepository.calculateAverageWaitTime();
        
        // Feedback statistics
        long totalFeedbacks = feedbackRepository.count();
        double averageRating = feedbackRepository.findAll().stream()
            .mapToInt(Feedback::getRating)
            .average()
            .orElse(0.0);
        
        // Staff performance
        long totalStaff = userRepository.countByRole(User.UserRole.STAFF);
        long activeStaff = counterRepository.findAll().stream()
            .filter(counter -> counter.getCurrentStaff() != null)
            .map(Counter::getCurrentStaff)
            .distinct()
            .count();
        
        stats.put("period", startDate + " to " + endDate);
        stats.put("totalApplications", totalApplications);
        stats.put("completedApplications", completedApplications);
        stats.put("applicationCompletionRate", String.format("%.2f", applicationCompletionRate));
        stats.put("totalQueues", totalQueues);
        stats.put("completedQueues", completedQueues);
        stats.put("averageWaitTime", avgWaitTime != null ? avgWaitTime : 0);
        stats.put("totalFeedbacks", totalFeedbacks);
        stats.put("averageRating", String.format("%.2f", averageRating));
        stats.put("totalStaff", totalStaff);
        stats.put("activeStaff", activeStaff);
        
        return stats;
    }
}