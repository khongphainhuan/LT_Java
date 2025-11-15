/**
 * Authentication Management
 * Xử lý đăng nhập, đăng xuất, quản lý session
 */

class AuthManager {
    constructor() {
        this.tokenKey = 'authToken';
        this.userKey = 'userData';
        this.init();
    }

    init() {
        this.checkAuthentication();
        this.setupEventListeners();
    }

    setupEventListeners() {
        // Login form submission
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Logout button
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => this.handleLogout());
        }
    }

    async handleLogin(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const credentials = {
            username: formData.get('username'),
            password: formData.get('password')
        };

        try {
            this.showLoading(true);
            
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(credentials)
            });

            if (response.ok) {
                const data = await response.json();
                this.setAuthData(data);
                this.redirectToDashboard(data.user.role);
            } else {
                const error = await response.json();
                this.showError(error.message || 'Đăng nhập thất bại');
            }
        } catch (error) {
            console.error('Login error:', error);
            this.showError('Lỗi kết nối đến server');
        } finally {
            this.showLoading(false);
        }
    }

    setAuthData(authData) {
        localStorage.setItem(this.tokenKey, authData.token);
        localStorage.setItem(this.userKey, JSON.stringify(authData.user));
        
        // Set authorization header for future requests
        axios.defaults.headers.common['Authorization'] = `Bearer ${authData.token}`;
    }

    redirectToDashboard(role) {
        switch(role) {
            case 'ADMIN':
                window.location.href = '/admin/dashboard';
                break;
            case 'STAFF':
                window.location.href = '/staff/dashboard';
                break;
            default:
                window.location.href = '/dashboard';
        }
    }

    handleLogout() {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
        delete axios.defaults.headers.common['Authorization'];
        window.location.href = '/login';
    }

    checkAuthentication() {
        const token = this.getToken();
        if (token && this.isTokenValid(token)) {
            // Token hợp lệ, có thể refresh token ở đây
            return true;
        } else {
            // Token không hợp lệ, chuyển hướng đến login
            this.redirectToLogin();
            return false;
        }
    }

    getToken() {
        return localStorage.getItem(this.tokenKey);
    }

    getUserData() {
        const userData = localStorage.getItem(this.userKey);
        return userData ? JSON.parse(userData) : null;
    }

    isTokenValid(token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.exp * 1000 > Date.now();
        } catch (error) {
            return false;
        }
    }

    redirectToLogin() {
        if (!window.location.pathname.includes('/login')) {
            window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname);
        }
    }

    showLoading(show) {
        const submitBtn = document.querySelector('#loginForm button[type="submit"]');
        if (submitBtn) {
            if (show) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<span class="loading-spinner"></span> Đang đăng nhập...';
            } else {
                submitBtn.disabled = false;
                submitBtn.innerHTML = 'Đăng nhập';
            }
        }
    }

    showError(message) {
        // Remove existing error alerts
        const existingAlert = document.querySelector('.alert-danger');
        if (existingAlert) {
            existingAlert.remove();
        }

        // Create new error alert
        const alert = document.createElement('div');
        alert.className = 'alert alert-danger alert-dismissible fade show';
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        // Insert after login form header or at the top of form
        const formHeader = document.querySelector('.login-card .card-header');
        if (formHeader) {
            formHeader.parentNode.insertBefore(alert, formHeader.nextSibling);
        }
    }
}

// Initialize auth manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new AuthManager();
});

// Utility function to check if user is authenticated
function isAuthenticated() {
    const token = localStorage.getItem('authToken');
    return !!token;
}

// Utility function to get auth headers
function getAuthHeaders() {
    const token = localStorage.getItem('authToken');
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
}