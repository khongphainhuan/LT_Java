package com.pascs.service;

import com.pascs.model.Feedback;
import com.pascs.repository.FeedbackRepository;
import com.pascs.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackAnalysisService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    public ServiceRating calculateServiceRating(Long serviceId) {
        List<Feedback> feedbacks = feedbackRepository.findByServiceId(serviceId);
        
        if (feedbacks.isEmpty()) {
            return new ServiceRating(0.0, Map.of(), 0);
        }
        
        // Sửa method reference thành lambda
        double avgRating = feedbacks.stream()
            .mapToInt(feedback -> feedback.getRating())
            .average()
            .orElse(0.0);
            
        Map<Integer, Long> ratingDistribution = feedbacks.stream()
            .collect(Collectors.groupingBy(Feedback::getRating, Collectors.counting()));
            
        return new ServiceRating(avgRating, ratingDistribution, feedbacks.size());
    }

    public OverallFeedbackStats getOverallFeedbackStats() {
        List<Feedback> allFeedbacks = feedbackRepository.findAll();
        
        if (allFeedbacks.isEmpty()) {
            return new OverallFeedbackStats(0.0, 0, Map.of(), 0);
        }
        
        // Sửa method reference thành lambda
        double overallAvgRating = allFeedbacks.stream()
            .mapToInt(feedback -> feedback.getRating())
            .average()
            .orElse(0.0);
            
        Map<Integer, Long> overallDistribution = allFeedbacks.stream()
            .collect(Collectors.groupingBy(Feedback::getRating, Collectors.counting()));
            
        long totalFeedbacks = allFeedbacks.size();
        long recentFeedbacks = feedbackRepository.countRecentFeedbacks();
        
        return new OverallFeedbackStats(overallAvgRating, totalFeedbacks, overallDistribution, recentFeedbacks);
    }

    public List<Feedback> getRecentFeedback(int limit) {
        return feedbackRepository.findRecentFeedbacks(limit);
    }

    public static class ServiceRating {
        private double averageRating;
        private Map<Integer, Long> ratingDistribution;
        private int totalFeedbacks;

        public ServiceRating(double averageRating, Map<Integer, Long> ratingDistribution, int totalFeedbacks) {
            this.averageRating = averageRating;
            this.ratingDistribution = ratingDistribution;
            this.totalFeedbacks = totalFeedbacks;
        }

        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        public Map<Integer, Long> getRatingDistribution() { return ratingDistribution; }
        public void setRatingDistribution(Map<Integer, Long> ratingDistribution) { this.ratingDistribution = ratingDistribution; }
        public int getTotalFeedbacks() { return totalFeedbacks; }
        public void setTotalFeedbacks(int totalFeedbacks) { this.totalFeedbacks = totalFeedbacks; }
    }

    public static class OverallFeedbackStats {
        private double overallAverageRating;
        private long totalFeedbacks;
        private Map<Integer, Long> ratingDistribution;
        private long recentFeedbacks;

        public OverallFeedbackStats(double overallAverageRating, long totalFeedbacks,
                                  Map<Integer, Long> ratingDistribution, long recentFeedbacks) {
            this.overallAverageRating = overallAverageRating;
            this.totalFeedbacks = totalFeedbacks;
            this.ratingDistribution = ratingDistribution;
            this.recentFeedbacks = recentFeedbacks;
        }

        public double getOverallAverageRating() { return overallAverageRating; }
        public void setOverallAverageRating(double overallAverageRating) { this.overallAverageRating = overallAverageRating; }
        public long getTotalFeedbacks() { return totalFeedbacks; }
        public void setTotalFeedbacks(long totalFeedbacks) { this.totalFeedbacks = totalFeedbacks; }
        public Map<Integer, Long> getRatingDistribution() { return ratingDistribution; }
        public void setRatingDistribution(Map<Integer, Long> ratingDistribution) { this.ratingDistribution = ratingDistribution; }
        public long getRecentFeedbacks() { return recentFeedbacks; }
        public void setRecentFeedbacks(long recentFeedbacks) { this.recentFeedbacks = recentFeedbacks; }
    }
}