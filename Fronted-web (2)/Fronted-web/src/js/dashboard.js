// dashboard.js
class DashboardManager {
    constructor() {
        this.init();
    }

    init() {
        this.loadStatistics();
        this.loadRecentActivities();
        this.setupEventListeners();
    }

    async loadStatistics() {
        try {
            const response = await fetch('/api/dashboard/statistics', {
                headers: {
                    'Authorization': 'Bearer ' + this.getToken(),
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                this.updateStatistics(data);
            } else {
                console.error('Failed to load statistics');
            }
        } catch (error) {
            console.error('Error loading statistics:', error);
        }
    }

    updateStatistics(data) {
        document.getElementById('todayServices').textContent = data.todayServices || 0;
        document.getElementById('pendingCases').textContent = data.pendingCases || 0;
        document.getElementById('avgRating').textContent = data.avgRating ? data.avgRating.toFixed(1) : '0.0';
        document.getElementById('avgWaitTime').textContent = data.avgWaitTime ? data.avgWaitTime + ' phút' : '0 phút';
        
        // Update rating progress bar
        const ratingProgress = document.getElementById('ratingProgress');
        const ratingPercent = data.avgRating ? (data.avgRating / 5) * 100 : 0;
        ratingProgress.style.width = ratingPercent + '%';
    }

    async loadRecentActivities() {
        try {
            const response = await fetch('/api/dashboard/recent-activities', {
                headers: {
                    'Authorization': 'Bearer ' + this.getToken(),
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                this.populateActivitiesTable(data);
            }
        } catch (error) {
            console.error('Error loading recent activities:', error);
        }
    }

    populateActivitiesTable(activities) {
        const tbody = document.getElementById('activitiesTableBody');
        tbody.innerHTML = '';

        activities.forEach(activity => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${this.formatDateTime(activity.timestamp)}</td>
                <td>${activity.action}</td>
                <td>${activity.performer}</td>
                <td><span class="badge bg-${this.getStatusBadgeClass(activity.status)}">${activity.status}</span></td>
            `;
            tbody.appendChild(row);
        });
    }

    formatDateTime(timestamp) {
        return new Date(timestamp).toLocaleString('vi-VN');
    }

    getStatusBadgeClass(status) {
        const statusClasses = {
            'completed': 'success',
            'pending': 'warning',
            'failed': 'danger',
            'in-progress': 'info'
        };
        return statusClasses[status] || 'secondary';
    }

    setupEventListeners() {
        document.getElementById('refreshBtn').addEventListener('click', () => {
            this.loadStatistics();
            this.loadRecentActivities();
        });

        // Auto refresh every 30 seconds
        setInterval(() => {
            this.loadStatistics();
        }, 30000);
    }

    getToken() {
        // Implementation to get JWT token from storage
        return localStorage.getItem('authToken');
    }
}

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new DashboardManager();
});