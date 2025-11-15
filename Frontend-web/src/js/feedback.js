/**
 * Feedback Management
 * Quản lý phản hồi và đánh giá từ công dân
 */

class FeedbackManager {
    constructor() {
        this.currentPage = 1;
        this.itemsPerPage = 10;
        this.currentFilter = 'all';
        this.feedbackStats = {};
        this.init();
    }

    init() {
        this.loadFeedbackData();
        this.setupEventListeners();
        this.loadFeedbackStats();
    }

    setupEventListeners() {
        // Filter buttons
        document.querySelectorAll('.feedback-filter').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const filter = e.currentTarget.dataset.filter;
                this.applyFilter(filter);
            });
        });

        // Search functionality
        const searchInput = document.getElementById('searchFeedback');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.searchFeedback(e.target.value);
            });
        }

        // Export buttons
        const exportBtn = document.getElementById('exportFeedbackBtn');
        if (exportBtn) {
            exportBtn.addEventListener('click', () => this.exportFeedback());
        }

        // Response modal
        const responseModal = document.getElementById('responseModal');
        if (responseModal) {
            responseModal.addEventListener('show.bs.modal', (e) => {
                const button = e.relatedTarget;
                const feedbackId = button.dataset.feedbackId;
                this.prepareResponseModal(feedbackId);
            });
        }

        // Send response
        const responseForm = document.getElementById('responseForm');
        if (responseForm) {
            responseForm.addEventListener('submit', (e) => this.handleResponseSubmit(e));
        }
    }

    async loadFeedbackData(page = 1, filter = 'all') {
        try {
            this.showLoading(true);
            
            const url = `/api/feedback?page=${page}&limit=${this.itemsPerPage}&filter=${filter}`;
            const response = await fetch(url, {
                headers: getAuthHeaders()
            });

            if (response.ok) {
                const data = await response.json();
                this.renderFeedbackTable(data.feedback);
                this.updatePagination(data.totalPages, data.currentPage);
            } else {
                throw new Error('Failed to load feedback');
            }
        } catch (error) {
            console.error('Error loading feedback:', error);
            this.showError('Không thể tải danh sách phản hồi');
        } finally {
            this.showLoading(false);
        }
    }

    async loadFeedbackStats() {
        try {
            const response = await fetch('/api/feedback/stats', {
                headers: getAuthHeaders()
            });

            if (response.ok) {
                this.feedbackStats = await response.json();
                this.updateStatsDisplay();
            }
        } catch (error) {
            console.error('Error loading feedback stats:', error);
        }
    }

    renderFeedbackTable(feedbackList) {
        const tbody = document.getElementById('feedbackTableBody');
        if (!tbody) return;

        tbody.innerHTML = '';

        if (feedbackList.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center py-4">
                        <i class="fas fa-comment-slash fa-2x text-muted mb-2"></i>
                        <p class="text-muted">Không có phản hồi nào</p>
                    </td>
                </tr>
            `;
            return;
        }

        feedbackList.forEach(feedback => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>
                    <div class="d-flex align-items-center">
                        <div class="avatar-sm bg-light rounded-circle me-2">
                            <i class="fas fa-user text-muted p-2"></i>
                        </div>
                        <div>
                            <strong>${this.escapeHtml(feedback.customerName) || 'Ẩn danh'}</strong>
                            <br>
                            <small class="text-muted">${feedback.contactInfo || 'Không có liên hệ'}</small>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="service-badge">${feedback.serviceCode}</div>
                    <small class="text-muted d-block">${this.escapeHtml(feedback.serviceName)}</small>
                </td>
                <td>
                    <div class="rating-stars">
                        ${this.renderStars(feedback.rating)}
                    </div>
                    <small class="text-muted">${this.formatDateTime(feedback.createdAt)}</small>
                </td>
                <td>
                    <div class="feedback-content">
                        <p class="mb-1">${this.escapeHtml(feedback.comment)}</p>
                        ${feedback.suggestions ? `
                            <small class="text-muted">
                                <strong>Đề xuất:</strong> ${this.escapeHtml(feedback.suggestions)}
                            </small>
                        ` : ''}
                    </div>
                </td>
                <td>
                    <span class="badge ${this.getStatusBadgeClass(feedback.status)}">
                        ${this.getStatusText(feedback.status)}
                    </span>
                </td>
                <td>
                    ${feedback.response ? `
                        <div class="response-content">
                            <small class="text-success">
                                <strong>Đã phản hồi:</strong> ${this.escapeHtml(feedback.response)}
                            </small>
                            <br>
                            <small class="text-muted">
                                ${this.formatDateTime(feedback.respondedAt)}
                            </small>
                        </div>
                    ` : `
                        <span class="text-muted">Chưa phản hồi</span>
                    `}
                </td>
                <td>
                    <div class="btn-group btn-group-sm">
                        ${!feedback.response ? `
                            <button class="btn btn-outline-primary respond-feedback" 
                                    data-feedback-id="${feedback.id}"
                                    data-bs-toggle="modal" 
                                    data-bs-target="#responseModal">
                                <i class="fas fa-reply"></i>
                            </button>
                        ` : ''}
                        <button class="btn btn-outline-info view-feedback" 
                                data-feedback-id="${feedback.id}">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-outline-danger delete-feedback" 
                                data-feedback-id="${feedback.id}">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(row);
        });

        this.attachFeedbackEventListeners();
    }

    renderStars(rating) {
        let stars = '';
        for (let i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars += '<i class="fas fa-star text-warning"></i>';
            } else {
                stars += '<i class="far fa-star text-muted"></i>';
            }
        }
        return stars;
    }

    attachFeedbackEventListeners() {
        // Respond buttons
        document.querySelectorAll('.respond-feedback').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const feedbackId = e.currentTarget.dataset.feedbackId;
                // Handled by modal
            });
        });

        // View buttons
        document.querySelectorAll('.view-feedback').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const feedbackId = e.currentTarget.dataset.feedbackId;
                this.viewFeedbackDetails(feedbackId);
            });
        });

        // Delete buttons
        document.querySelectorAll('.delete-feedback').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const feedbackId = e.currentTarget.dataset.feedbackId;
                this.deleteFeedback(feedbackId);
            });
        });
    }

    applyFilter(filter) {
        this.currentFilter = filter;
        this.currentPage = 1;
        
        // Update active filter button
        document.querySelectorAll('.feedback-filter').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.filter === filter);
        });

        this.loadFeedbackData(this.currentPage, filter);
    }

    async searchFeedback(query) {
        if (query.length < 2) {
            this.loadFeedbackData(this.currentPage, this.currentFilter);
            return;
        }

        try {
            this.showLoading(true);
            
            const response = await fetch(`/api/feedback/search?q=${encodeURIComponent(query)}`, {
                headers: getAuthHeaders()
            });

            if (response.ok) {
                const feedback = await response.json();
                this.renderFeedbackTable(feedback);
                this.hidePagination();
            }
        } catch (error) {
            console.error('Error searching feedback:', error);
            this.showError('Lỗi tìm kiếm phản hồi');
        } finally {
            this.showLoading(false);
        }
    }

    async prepareResponseModal(feedbackId) {
        try {
            const response = await fetch(`/api/feedback/${feedbackId}`, {
                headers: getAuthHeaders()
            });

            if (response.ok) {
                const feedback = await response.json();
                this.populateResponseModal(feedback);
            }
        } catch (error) {
            console.error('Error loading feedback details:', error);
            this.showError('Không thể tải chi tiết phản hồi');
        }
    }

    populateResponseModal(feedback) {
        const modal = document.getElementById('responseModal');
        const form = document.getElementById('responseForm');
        
        form.dataset.feedbackId = feedback.id;
        
        // Populate feedback details
        document.getElementById('feedbackCustomer').textContent = 
            feedback.customerName || 'Ẩn danh';
        document.getElementById('feedbackService').textContent = 
            `${feedback.serviceCode} - ${feedback.serviceName}`;
        document.getElementById('feedbackRating').innerHTML = 
            this.renderStars(feedback.rating);
        document.getElementById('feedbackComment').textContent = 
            feedback.comment;
        
        if (feedback.suggestions) {
            document.getElementById('feedbackSuggestions').textContent = 
                feedback.suggestions;
            document.getElementById('suggestionsSection').classList.remove('d-none');
        } else {
            document.getElementById('suggestionsSection').classList.add('d-none');
        }

        // Pre-fill response if exists
        document.getElementById('responseText').value = feedback.response || '';
    }

    async handleResponseSubmit(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const responseData = {
            response: formData.get('response')
        };

        const feedbackId = event.target.dataset.feedbackId;

        try {
            this.showFormLoading(true);
            
            const apiResponse = await fetch(`/api/feedback/${feedbackId}/respond`, {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(responseData)
            });

            if (apiResponse.ok) {
                this.showSuccess('Đã gửi phản hồi thành công');
                this.hideResponseModal();
                this.loadFeedbackData(this.currentPage, this.currentFilter);
                this.loadFeedbackStats();
            } else {
                throw new Error('Failed to send response');
            }
        } catch (error) {
            console.error('Error sending response:', error);
            this.showError('Không thể gửi phản hồi');
        } finally {
            this.showFormLoading(false);
        }
    }

    async viewFeedbackDetails(feedbackId) {
        try {
            const response = await fetch(`/api/feedback/${feedbackId}`, {
                headers: getAuthHeaders()
            });

            if (response.ok) {
                const feedback = await response.json();
                this.showFeedbackDetailsModal(feedback);
            }
        } catch (error) {
            console.error('Error viewing feedback details:', error);
            this.showError('Không thể xem chi tiết phản hồi');
        }
    }

    showFeedbackDetailsModal(feedback) {
        // Create and show details modal
        const modalHTML = `
            <div class="modal fade" id="feedbackDetailsModal" tabindex="-1">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Chi tiết phản hồi</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <h6>Thông tin khách hàng</h6>
                                    <p><strong>Tên:</strong> ${feedback.customerName || 'Ẩn danh'}</p>
                                    <p><strong>Liên hệ:</strong> ${feedback.contactInfo || 'Không có'}</p>
                                </div>
                                <div class="col-md-6">
                                    <h6>Thông tin dịch vụ</h6>
                                    <p><strong>Mã dịch vụ:</strong> ${feedback.serviceCode}</p>
                                    <p><strong>Tên dịch vụ:</strong> ${feedback.serviceName}</p>
                                </div>
                            </div>
                            <hr>
                            <div class="row">
                                <div class="col-12">
                                    <h6>Đánh giá</h6>
                                    <div class="rating-stars mb-2">
                                        ${this.renderStars(feedback.rating)}
                                    </div>
                                    <p><strong>Nhận xét:</strong></p>
                                    <p class="border p-3 rounded">${this.escapeHtml(feedback.comment)}</p>
                                    ${feedback.suggestions ? `
                                        <p><strong>Đề xuất:</strong></p>
                                        <p class="border p-3 rounded bg-light">${this.escapeHtml(feedback.suggestions)}</p>
                                    ` : ''}
                                </div>
                            </div>
                            ${feedback.response ? `
                                <hr>
                                <div class="row">
                                    <div class="col-12">
                                        <h6>Phản hồi từ hệ thống</h6>
                                        <p class="border p-3 rounded bg-success text-white">
                                            ${this.escapeHtml(feedback.response)}
                                        </p>
                                        <small class="text-muted">
                                            Phản hồi lúc: ${this.formatDateTime(feedback.respondedAt)}
                                        </small>
                                    </div>
                                </div>
                            ` : ''}
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                            ${!feedback.response ? `
                                <button type="button" class="btn btn-primary respond-from-details" 
                                        data-feedback-id="${feedback.id}">
                                    Phản hồi
                                </button>
                            ` : ''}
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Remove existing modal if any
        const existingModal = document.getElementById('feedbackDetailsModal');
        if (existingModal) {
            existingModal.remove();
        }

        // Add modal to DOM
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        // Show modal
        const modal = new bootstrap.Modal(document.getElementById('feedbackDetailsModal'));
        modal.show();

        // Add event listener for respond button
        const respondBtn = document.querySelector('.respond-from-details');
        if (respondBtn) {
            respondBtn.addEventListener('click', () => {
                modal.hide();
                this.prepareResponseModal(feedback.id);
                const responseModal = new bootstrap.Modal(document.getElementById('responseModal'));
                responseModal.show();
            });
        }
    }

    async deleteFeedback(feedbackId) {
        if (!confirm('Bạn có chắc muốn xóa phản hồi này?')) return;

        try {
            const response = await fetch(`/api/feedback/${feedbackId}`, {
                method: 'DELETE',
                headers: getAuthHeaders()
            });

            if (response.ok) {
                this.showSuccess('Đã xóa phản hồi thành công');
                this.loadFeedbackData(this.currentPage, this.currentFilter);
                this.loadFeedbackStats();
            } else {
                throw new Error('Failed to delete feedback');
            }
        } catch (error) {
            console.error('Error deleting feedback:', error);
            this.showError('Không thể xóa phản hồi');
        }
    }

    async exportFeedback() {
        try {
            this.showLoading(true);
            
            const response = await fetch('/api/feedback/export', {
                headers: getAuthHeaders()
            });

            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `feedback-export-${new Date().toISOString().split('T')[0]}.csv`;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
                
                this.showSuccess('Đã xuất dữ liệu thành công');
            } else {
                throw new Error('Failed to export feedback');
            }
        } catch (error) {
            console.error('Error exporting feedback:', error);
            this.showError('Không thể xuất dữ liệu');
        } finally {
            this.showLoading(false);
        }
    }

    updateStatsDisplay() {
        const stats = this.feedbackStats;
        
        document.getElementById('totalFeedback').textContent = stats.total || 0;
        document.getElementById('averageRating').textContent = stats.averageRating ? stats.averageRating.toFixed(1) : '0.0';
        document.getElementById('responseRate').textContent = stats.responseRate ? `${stats.responseRate}%` : '0%';
        
        // Update rating distribution
        this.updateRatingDistribution(stats.ratingDistribution);
    }

    updateRatingDistribution(distribution) {
        const container = document.getElementById('ratingDistribution');
        if (!container || !distribution) return;

        let html = '';
        for (let rating = 5; rating >= 1; rating--) {
            const count = distribution[rating] || 0;
            const percentage = distribution.total ? (count / distribution.total * 100) : 0;
            
            html += `
                <div class="rating-distribution-item mb-2">
                    <div class="d-flex justify-content-between">
                        <span class="rating-stars-small">
                            ${this.renderStars(rating)}
                        </span>
                        <span class="count">${count}</span>
                    </div>
                    <div class="progress" style="height: 8px;">
                        <div class="progress-bar bg-warning" 
                             style="width: ${percentage}%"></div>
                    </div>
                </div>
            `;
        }
        
        container.innerHTML = html;
    }

    updatePagination(totalPages, currentPage) {
        const pagination = document.getElementById('feedbackPagination');
        if (!pagination) return;

        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }

        let paginationHTML = '';
        
        // Previous button
        paginationHTML += `
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">Trước</a>
            </li>
        `;

        // Page numbers
        for (let i = 1; i <= totalPages; i++) {
            if (i === 1 || i === totalPages || (i >= currentPage - 1 && i <= currentPage + 1)) {
                paginationHTML += `
                    <li class="page-item ${i === currentPage ? 'active' : ''}">
                        <a class="page-link" href="#" data-page="${i}">${i}</a>
                    </li>
                `;
            } else if (i === currentPage - 2 || i === currentPage + 2) {
                paginationHTML += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }

        // Next button
        paginationHTML += `
            <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">Sau</a>
            </li>
        `;

        pagination.innerHTML = paginationHTML;

        // Add event listeners to pagination links
        pagination.querySelectorAll('.page-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = parseInt(e.target.dataset.page);
                if (page) this.changePage(page);
            });
        });
    }

    changePage(page) {
        this.currentPage = page;
        this.loadFeedbackData(page, this.currentFilter);
    }

    hidePagination() {
        const pagination = document.getElementById('feedbackPagination');
        if (pagination) {
            pagination.innerHTML = '';
        }
    }

    hideResponseModal() {
        const modal = bootstrap.Modal.getInstance(document.getElementById('responseModal'));
        if (modal) {
            modal.hide();
        }
        document.getElementById('responseForm').reset();
    }

    getStatusBadgeClass(status) {
        const statusClasses = {
            'new': 'bg-primary',
            'read': 'bg-secondary', 
            'responded': 'bg-success',
            'archived': 'bg-dark'
        };
        return statusClasses[status] || 'bg-secondary';
    }

    getStatusText(status) {
        const statusMap = {
            'new': 'Mới',
            'read': 'Đã đọc',
            'responded': 'Đã phản hồi',
            'archived': 'Đã lưu trữ'
        };
        return statusMap[status] || status;
    }

    showLoading(show) {
        const table = document.getElementById('feedbackTable');
        const loading = document.getElementById('feedbackLoading');
        
        if (table && loading) {
            if (show) {
                table.style.opacity = '0.6';
                loading.classList.remove('d-none');
            } else {
                table.style.opacity = '1';
                loading.classList.add('d-none');
            }
        }
    }

    showFormLoading(show) {
        const submitBtn = document.querySelector('#responseForm button[type="submit"]');
        if (submitBtn) {
            if (show) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<span class="loading-spinner"></span> Đang gửi...';
            } else {
                submitBtn.disabled = false;
                submitBtn.innerHTML = 'Gửi phản hồi';
            }
        }
    }

    showSuccess(message) {
        console.log('Success:', message); // Replace with toast
    }

    showError(message) {
        console.error('Error:', message); // Replace with toast
    }

    formatDateTime(timestamp) {
        return new Date(timestamp).toLocaleString('vi-VN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    escapeHtml(unsafe) {
        if (!unsafe) return '';
        return unsafe
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
}

// Initialize feedback manager
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('feedbackManagementPage')) {
        window.feedbackManager = new FeedbackManager();
    }
});