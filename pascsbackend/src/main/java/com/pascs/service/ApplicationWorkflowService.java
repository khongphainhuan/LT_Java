package com.pascs.service;

import com.pascs.model.Application;
import com.pascs.model.User;
import com.pascs.repository.ApplicationRepository;
import com.pascs.repository.UserRepository;
import com.pascs.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ApplicationWorkflowService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Phân công hồ sơ với đầy đủ thông tin
    public Application assignApplication(Long applicationId, Long staffId, String assignmentNote, 
                                       Integer priorityLevel, Integer estimatedProcessingTime) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!staff.getRole().equals(User.UserRole.STAFF)) {
            throw new RuntimeException("User is not a staff member");
        }

        application.setAssignedStaff(staff);
        application.setStatus(Application.ApplicationStatus.PROCESSING);
        application.setProcessedAt(LocalDateTime.now());
        
        // Set additional assignment information
        if (assignmentNote != null) {
            application.setNote(assignmentNote);
        }
        
        if (priorityLevel != null) {
            application.setUrgent(priorityLevel >= 3);
        }

        Application updatedApplication = applicationRepository.save(application);
        
        // Send notifications
        notificationService.notifyApplicationStatus(updatedApplication);
        notificationService.notifyStaffNewApplication(updatedApplication);
        
        // Send personal notification to staff
        notificationService.sendPersonalNotification(
            staffId, 
            "Bạn được phân công hồ sơ: " + updatedApplication.getApplicationCode()
        );

        return updatedApplication;
    }

    // Phân công hồ sơ đơn giản (cho tương thích ngược)
    public Application assignApplication(Long applicationId, Long staffId) {
        return assignApplication(applicationId, staffId, null, 1, null);
    }

    // Cập nhật trạng thái hồ sơ
    public Application updateApplicationStatus(Long applicationId, Application.ApplicationStatus status, String note) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        application.setStatus(status);
        application.setNote(note);

        if (status == Application.ApplicationStatus.PROCESSING) {
            application.setProcessedAt(LocalDateTime.now());
        } else if (status == Application.ApplicationStatus.COMPLETED) {
            application.setCompletedAt(LocalDateTime.now());
        }

        Application updatedApplication = applicationRepository.save(application);
        notificationService.notifyApplicationStatus(updatedApplication);

        return updatedApplication;
    }

    // Yêu cầu bổ sung tài liệu
    public Application requestAdditionalDocuments(Long applicationId, String documentRequest) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        application.setStatus(Application.ApplicationStatus.NEEDS_SUPPLEMENT);
        application.setNote("Yêu cầu bổ sung tài liệu: " + documentRequest);

        Application updatedApplication = applicationRepository.save(application);
        notificationService.notifyApplicationStatus(updatedApplication);

        return updatedApplication;
    }

    // Lấy hồ sơ theo trạng thái
    public List<Application> getApplicationsByStatus(Application.ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }

    // Lấy hồ sơ được phân công cho staff
    public List<Application> getAssignedApplications(Long staffId) {
        return applicationRepository.findByAssignedStaffId(staffId);
    }

    // Thống kê khối lượng công việc của staff
    public WorkloadStats getStaffWorkload(Long staffId) {
        long totalAssigned = applicationRepository.countByAssignedStaffId(staffId);
        long inProgress = applicationRepository.countByAssignedStaffIdAndStatus(staffId, Application.ApplicationStatus.PROCESSING);
        long completed = applicationRepository.countByAssignedStaffIdAndStatus(staffId, Application.ApplicationStatus.COMPLETED);
        
        return new WorkloadStats(totalAssigned, inProgress, completed);
    }

    // Lấy hồ sơ đang hoạt động của staff
    public List<Application> getActiveApplicationsByStaff(Long staffId) {
        return applicationRepository.findActiveApplicationsByStaff(staffId);
    }

    // Thống kê hồ sơ mới hôm nay
    public long getNewApplicationsToday() {
        return applicationRepository.countNewApplicationsToday();
    }

    // DTO cho thống kê workload
    public static class WorkloadStats {
        private long totalAssigned;
        private long inProgress;
        private long completed;

        public WorkloadStats(long totalAssigned, long inProgress, long completed) {
            this.totalAssigned = totalAssigned;
            this.inProgress = inProgress;
            this.completed = completed;
        }

        public long getTotalAssigned() { return totalAssigned; }
        public void setTotalAssigned(long totalAssigned) { this.totalAssigned = totalAssigned; }
        public long getInProgress() { return inProgress; }
        public void setInProgress(long inProgress) { this.inProgress = inProgress; }
        public long getCompleted() { return completed; }
        public void setCompleted(long completed) { this.completed = completed; }
    }
}