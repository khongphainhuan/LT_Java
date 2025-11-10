// websocket-client.js - Real-time WebSocket client
class PASCSWebSocket {
    constructor() {
        this.stompClient = null;
        this.connected = false;
        this.userId = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
    }

    connect(userId) {
        this.userId = userId;
        const socket = new SockJS('/ws-pascs');
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);
            this.connected = true;
            this.reconnectAttempts = 0;
            this.subscribeToChannels();
        }, (error) => {
            console.log('WebSocket connection error: ', error);
            this.connected = false;
            this.handleReconnection();
        });
    }

    subscribeToChannels() {
        // Subscribe to queue updates
        this.stompClient.subscribe('/topic/queue-updates', (message) => {
            const queue = JSON.parse(message.body);
            this.handleQueueUpdate(queue);
        });

        // Subscribe to application updates
        this.stompClient.subscribe('/topic/application-updates', (message) => {
            const application = JSON.parse(message.body);
            this.handleApplicationUpdate(application);
        });

        // Subscribe to personal notifications
        if (this.userId) {
            this.stompClient.subscribe(`/user/queue/user-${this.userId}`, (message) => {
                const notification = JSON.parse(message.body);
                this.handlePersonalNotification(notification);
            });

            this.stompClient.subscribe(`/user/queue/notifications-${this.userId}`, (message) => {
                const notification = JSON.parse(message.body);
                this.showNotification(notification);
            });
        }

        // Staff subscriptions
        this.stompClient.subscribe('/topic/staff-applications', (message) => {
            const application = JSON.parse(message.body);
            this.handleStaffApplication(application);
        });

        this.stompClient.subscribe('/topic/staff-queue', (message) => {
            const queue = JSON.parse(message.body);
            this.handleStaffQueue(queue);
        });
    }

    handleQueueUpdate(queue) {
        // Update queue display in real-time
        if (typeof updateQueueDisplay === 'function') {
            updateQueueDisplay(queue);
        }
        
        // Show notification for called numbers
        if (queue.status === 'CALLED') {
            this.showNotification(`Số ${queue.ticketNumber} được gọi đến quầy ${queue.counterId}`);
        }
    }

    handleApplicationUpdate(application) {
        // Update application status in real-time
        if (typeof updateApplicationStatus === 'function') {
            updateApplicationStatus(application);
        }
        
        // Show notification for status changes
        if (application.user && application.user.id === this.userId) {
            const statusMessages = {
                'PROCESSING': 'Hồ sơ của bạn đang được xử lý',
                'NEEDS_SUPPLEMENT': 'Hồ sơ cần bổ sung tài liệu',
                'COMPLETED': 'Hồ sơ đã hoàn thành'
            };
            
            if (statusMessages[application.status]) {
                this.showNotification(statusMessages[application.status]);
            }
        }
    }

    handlePersonalNotification(notification) {
        console.log('Personal notification:', notification);
        this.showNotification(notification.message || 'Bạn có thông báo mới');
    }

    handleStaffApplication(application) {
        if (typeof handleNewStaffApplication === 'function') {
            handleNewStaffApplication(application);
        }
    }

    handleStaffQueue(queue) {
        if (typeof handleNewStaffQueue === 'function') {
            handleNewStaffQueue(queue);
        }
    }

    showNotification(message, type = 'info') {
        // Create toast notification
        const toast = document.createElement('div');
        toast.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        toast.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(toast);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 5000);
    }

    sendMessage(destination, message) {
        if (this.connected && this.stompClient) {
            this.stompClient.send(destination, {}, JSON.stringify(message));
        }
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
        }
        this.connected = false;
    }

    handleReconnection() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
            
            setTimeout(() => {
                this.connect(this.userId);
            }, 3000 * this.reconnectAttempts); // Exponential backoff
        }
    }
}

// Global WebSocket instance
const pascsWebSocket = new PASCSWebSocket();

// Initialize WebSocket when page loads
document.addEventListener('DOMContentLoaded', function() {
    // Get user ID from meta tag or global variable
    const userId = document.querySelector('meta[name="user-id"]')?.getAttribute('content') || window.currentUserId;
    
    if (userId) {
        pascsWebSocket.connect(userId);
    }
    
    // Disconnect when page unloads
    window.addEventListener('beforeunload', function() {
        pascsWebSocket.disconnect();
    });
});