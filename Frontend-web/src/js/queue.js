/**
 * Queue Management
 * Quản lý xếp hàng thời gian thực
 */

class QueueManager {
    constructor() {
        this.currentQueue = [];
        this.assignedServices = [];
        this.socket = null;
        this.init();
    }

    init() {
        this.loadQueueData();
        this.setupEventListeners();
        this.connectWebSocket();
    }

    setupEventListeners() {
        // Refresh button
        const refreshBtn = document.getElementById('refreshQueueBtn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => this.loadQueueData());
        }

        // Assign next customer
        const assignNextBtn = document.getElementById('assignNextBtn');
        if (assignNextBtn) {
            assignNextBtn.addEventListener('click', () => this.assignNextCustomer());
        }

        // Service filter
        const serviceFilter = document.getElementById('serviceFilter');
        if (serviceFilter) {
            serviceFilter.addEventListener('change', (e) => this.filterByService(e.target.value));
        }

        // Status filter
        const statusFilter = document.getElementById('statusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => this.filterByStatus(e.target.value));
        }
    }

    connectWebSocket() {
        // Connect to WebSocket for real-time updates
        try {
            this.socket = new WebSocket(`ws://${window.location.host}/ws/queue`);
            
            this.socket.onopen = () => {
                console.log('WebSocket connected for queue updates');
            };

            this.socket.onmessage = (event) => {
                const data = JSON.parse(event.data);
                this.handleRealTimeUpdate(data);
            };

            this.socket.onclose = () => {
                console.log('WebSocket disconnected, attempting reconnect...');
                setTimeout(() => this.connectWebSocket(), 5000);
            };

            this.socket.onerror = (error) => {
                console.error('WebSocket error:', error);
            };
        } catch (error) {
            console.error('WebSocket connection failed:', error);
        }
    }

    handleRealTimeUpdate(data) {
        switch (data.type) {
            case 'QUEUE_UPDATE':
                this.updateQueueDisplay(data.queue);
                break;
            case 'NEW_TICKET':
                this.showNewTicketNotification(data.ticket);
                break;
            case 'TICKET_CALLED':
                this.highlightCalledTicket(data.ticket);
                break;
            case 'TICKET_COMPLETED':
                this.removeCompletedTicket(data.ticketId);
                break;
        }
    }

    async loadQueueData() {
        try {
            this.showLoading(true);
            
            const [queueResponse, servicesResponse] = await Promise.all([
                fetch('/api/queue/current', {
                    headers: getAuthHeaders()
                }),
                fetch('/api/services/active', {
                    headers: getAuthHeaders()
                })
            ]);

            if (queueResponse.ok && servicesResponse.ok) {
                const queueData = await queueResponse.json();
                const servicesData = await servicesResponse.json();
                
                this.currentQueue = queueData;
                this.assignedServices = servicesData;
                
                this.renderQueueTable(queueData);
                this.populateServiceFilter(servicesData);
                this.updateQueueStats(queueData);
            } else {
                throw new Error('Failed to load queue data');
            }
        } catch (error) {
            console.error('Error loading queue data:', error);
            this.showError('Không thể tải dữ liệu hàng đợi');
        } finally {
            this.showLoading(false);
        }
    }

    renderQueueTable(queueData) {
        const tbody = document.getElementById('queueTableBody');
        if (!tbody) return;

        tbody.innerHTML = '';

        if (queueData.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-4">
                        <i class="fas fa-users-slash fa-2x text-muted mb-2"></i>
                        <p class="text-muted">Không có khách hàng trong hàng đợi</p>
                    </td>
                </tr>
            `;
            return;
        }

        queueData.forEach(ticket => {
            const row = document.createElement('tr');
            row.className = this.getTicketRowClass(ticket);
            row.innerHTML = `
                <td>
                    <strong class="ticket-number">${ticket.ticketNumber}</strong>
                    <br>
                    <small class="text-muted">${ticket.serviceCode}</small>
                </td>
                <td>${this.escapeHtml(ticket.serviceName)}</td>
                <td>${this.formatDateTime(ticket.createdAt)}</td>
                <td>${this.getWaitTime(ticket.createdAt)}</td>
                <td>
                    <span class="badge ${this.getStatusBadgeClass(ticket.status)}">
                        ${this.getStatusText(ticket.status)}
                    </span>
                </td>
                <td>
                    <div class="btn-group btn-group-sm">
                        ${this.getActionButtons(ticket)}
                    </div>
                </td>
            `;
            tbody.appendChild(row);
        });

        this.attachTicketEventListeners();
    }

    getTicketRowClass(ticket) {
        const classes = [];
        if (ticket.status === 'called') classes.push('table-warning');
        if (ticket.status === 'processing') classes.push('table-info');
        if (ticket.status === 'completed') classes.push('table-success');
        if (ticket.isPriority) classes.push('priority-ticket');
        return classes.join(' ');
    }

    getActionButtons(ticket) {
        let buttons = '';

        if (ticket.status === 'waiting') {
            buttons += `
                <button class="btn btn-outline-primary call-ticket" 
                        data-ticket-id="${ticket.id}">
                    <i class="fas fa-bell"></i> Gọi
                </button>
            `;
        }

        if (ticket.status === 'called' || ticket.status === 'processing') {
            buttons += `
                <button class="btn btn-outline-success complete-ticket" 
                        data-ticket-id="${ticket.id}">
                    <i class="fas fa-check"></i> Hoàn thành
                </button>
                <button class="btn btn-outline-warning recall-ticket" 
                        data-ticket-id="${ticket.id}">
                    <i class="fas fa-redo"></i> Gọi lại
                </button>
            `;
        }

        if (ticket.status !== 'completed' && ticket.status !== 'cancelled') {
            buttons += `
                <button class="btn btn-outline-danger cancel-ticket" 
                        data-ticket-id="${ticket.id}">
                    <i class="fas fa-times"></i> Hủy
                </button>
            `;
        }

        return buttons || '<span class="text-muted">Không có hành động</span>';
    }

    attachTicketEventListeners() {
        // Call ticket
        document.querySelectorAll('.call-ticket').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const ticketId = e.currentTarget.dataset.ticketId;
                this.callTicket(ticketId);
            });
        });

        // Complete ticket
        document.querySelectorAll('.complete-ticket').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const ticketId = e.currentTarget.dataset.ticketId;
                this.completeTicket(ticketId);
            });
        });

        // Recall ticket
        document.querySelectorAll('.recall-ticket').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const ticketId = e.currentTarget.dataset.ticketId;
                this.recallTicket(ticketId);
            });
        });

        // Cancel ticket
        document.querySelectorAll('.cancel-ticket').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const ticketId = e.currentTarget.dataset.ticketId;
                this.cancelTicket(ticketId);
            });
        });
    }

    async callTicket(ticketId) {
        try {
            const response = await fetch(`/api/queue/${ticketId}/call`, {
                method: 'POST',
                headers: getAuthHeaders()
            });

            if (response.ok) {
                this.showSuccess('Đã gọi số vé thành công');
                this.loadQueueData(); // Reload to reflect changes
            } else {
                throw new Error('Failed to call ticket');
            }
        } catch (error) {
            console.error('Error calling ticket:', error);
            this.showError('Không thể gọi số vé');
        }
    }

    async completeTicket(ticketId) {
        try {
            const response = await fetch(`/api/queue/${ticketId}/complete`, {
                method: 'POST',
                headers: getAuthHeaders()
            });

            if (response.ok) {
                this.showSuccess('Đã hoàn thành xử lý vé');
                this.loadQueueData();
            } else {
                throw new Error('Failed to complete ticket');
            }
        } catch (error) {
            console.error('Error completing ticket:', error);
            this.showError('Không thể hoàn thành vé');
        }
    }

    async recallTicket(ticketId) {
        try {
            const response = await fetch(`/api/queue/${ticketId}/recall`, {
                method: 'POST',
                headers: getAuthHeaders()
            });

            if (response.ok) {
                this.showSuccess('Đã gọi lại số vé');
                this.loadQueueData();
            } else {
                throw new Error('Failed to recall ticket');
            }
        } catch (error) {
            console.error('Error recalling ticket:', error);
            this.showError('Không thể gọi lại vé');
        }
    }

    async cancelTicket(ticketId) {
        if (!confirm('Bạn có chắc muốn hủy vé này?')) return;

        try {
            const response = await fetch(`/api/queue/${ticketId}/cancel`, {
                method: 'POST',
                headers: getAuthHeaders()
            });

            if (response.ok) {
                this.showSuccess('Đã hủy vé thành công');
                this.loadQueueData();
            } else {
                throw new Error('Failed to cancel ticket');
            }
        } catch (error) {
            console.error('Error cancelling ticket:', error);
            this.showError('Không thể hủy vé');
        }
    }

    async assignNextCustomer() {
        try {
            const response = await fetch('/api/queue/assign-next', {
                method: 'POST',
                headers: getAuthHeaders()
            });

            if (response.ok) {
                const assignment = await response.json();
                if (assignment.ticket) {
                    this.showSuccess(`Đã phân công vé ${assignment.ticket.ticketNumber}`);
                    this.loadQueueData();
                } else {
                    this.showInfo('Không có vé nào để phân công');
                }
            } else {
                throw new Error('Failed to assign next customer');
            }
        } catch (error) {
            console.error('Error assigning next customer:', error);
            this.showError('Không thể phân công khách hàng');
        }
    }

    populateServiceFilter(services) {
        const filter = document.getElementById('serviceFilter');
        if (!filter) return;

        filter.innerHTML = '<option value="">Tất cả dịch vụ</option>';
        
        services.forEach(service => {
            filter.innerHTML += `
                <option value="${service.id}">${service.name} (${service.code})</option>
            `;
        });
    }

    filterByService(serviceId) {
        const rows = document.querySelectorAll('#queueTableBody tr');
        
        rows.forEach(row => {
            if (!serviceId) {
                row.style.display = '';
            } else {
                const rowServiceId = row.querySelector('.ticket-number')?.textContent.split('-')[0];
                row.style.display = rowServiceId === serviceId ? '' : 'none';
            }
        });
    }

    filterByStatus(status) {
        const rows = document.querySelectorAll('#queueTableBody tr');
        
        rows.forEach(row => {
            if (!status) {
                row.style.display = '';
            } else {
                const badge = row.querySelector('.badge');
                const rowStatus = badge ? badge.textContent.trim() : '';
                row.style.display = this.getStatusValue(rowStatus) === status ? '' : 'none';
            }
        });
    }

    getStatusValue(statusText) {
        const statusMap = {
            'Đang chờ': 'waiting',
            'Đã gọi': 'called', 
            'Đang xử lý': 'processing',
            'Hoàn thành': 'completed',
            'Đã hủy': 'cancelled'
        };
        return statusMap[statusText] || '';
    }

    updateQueueStats(queueData) {
        const totalWaiting = queueData.filter(t => t.status === 'waiting').length;
        const totalProcessing = queueData.filter(t => t.status === 'processing').length;
        const avgWaitTime = this.calculateAverageWaitTime(queueData);

        document.getElementById('totalWaiting').textContent = totalWaiting;
        document.getElementById('totalProcessing').textContent = totalProcessing;
        document.getElementById('avgWaitTime').textContent = `${avgWaitTime} phút`;
    }

    calculateAverageWaitTime(queueData) {
        const waitingTickets = queueData.filter(t => t.status === 'waiting');
        if (waitingTickets.length === 0) return 0;

        const totalWaitTime = waitingTickets.reduce((sum, ticket) => {
            return sum + this.getWaitTimeMinutes(ticket.createdAt);
        }, 0);

        return Math.round(totalWaitTime / waitingTickets.length);
    }

    getWaitTime(createdAt) {
        const minutes = this.getWaitTimeMinutes(createdAt);
        if (minutes < 60) {
            return `${minutes} phút`;
        } else {
            const hours = Math.floor(minutes / 60);
            const mins = minutes % 60;
            return `${hours}g${mins}p`;
        }
    }

    getWaitTimeMinutes(createdAt) {
        const created = new Date(createdAt);
        const now = new Date();
        return Math.floor((now - created) / (1000 * 60));
    }

    getStatusBadgeClass(status) {
        const statusClasses = {
            'waiting': 'bg-secondary',
            'called': 'bg-warning text-dark',
            'processing': 'bg-info',
            'completed': 'bg-success',
            'cancelled': 'bg-danger'
        };
        return statusClasses[status] || 'bg-secondary';
    }

    getStatusText(status) {
        const statusMap = {
            'waiting': 'Đang chờ',
            'called': 'Đã gọi',
            'processing': 'Đang xử lý',
            'completed': 'Hoàn thành',
            'cancelled': 'Đã hủy'
        };
        return statusMap[status] || status;
    }

    updateQueueDisplay(queue) {
        this.currentQueue = queue;
        this.renderQueueTable(queue);
        this.updateQueueStats(queue);
    }

    showNewTicketNotification(ticket) {
        // Show browser notification if permitted
        if ('Notification' in window && Notification.permission === 'granted') {
            new Notification('Vé mới', {
                body: `Vé ${ticket.ticketNumber} - ${ticket.serviceName}`,
                icon: '/static/icons/notification.png'
            });
        }

        // Show in-app notification
        this.showSuccess(`Vé mới: ${ticket.ticketNumber} - ${ticket.serviceName}`);
    }

    highlightCalledTicket(ticket) {
        const rows = document.querySelectorAll('#queueTableBody tr');
        rows.forEach(row => {
            row.classList.remove('called-highlight');
            const ticketNumber = row.querySelector('.ticket-number')?.textContent;
            if (ticketNumber === ticket.ticketNumber) {
                row.classList.add('called-highlight');
                // Scroll to the called ticket
                row.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        });
    }

    removeCompletedTicket(ticketId) {
        this.currentQueue = this.currentQueue.filter(t => t.id !== ticketId);
        this.renderQueueTable(this.currentQueue);
        this.updateQueueStats(this.currentQueue);
    }

    showLoading(show) {
        const table = document.getElementById('queueTable');
        const loading = document.getElementById('queueLoading');
        
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

    showSuccess(message) {
        console.log('Success:', message); // Replace with toast
    }

    showError(message) {
        console.error('Error:', message); // Replace with toast
    }

    showInfo(message) {
        console.log('Info:', message); // Replace with toast
    }

    formatDateTime(timestamp) {
        return new Date(timestamp).toLocaleTimeString('vi-VN', {
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

// Initialize queue manager
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('queueManagementPage')) {
        window.queueManager = new QueueManager();
    }
});