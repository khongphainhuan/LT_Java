// services.js
class ServiceManager {
    constructor() {
        this.currentPage = 1;
        this.init();
    }

    init() {
        this.loadServices();
        this.setupEventListeners();
    }

    async loadServices() {
        try {
            const response = await fetch(`/api/services?page=${this.currentPage}`, {
                headers: {
                    'Authorization': 'Bearer ' + this.getToken(),
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                this.renderServicesTable(data.services);
                this.updatePagination(data.totalPages);
            }
        } catch (error) {
            console.error('Error loading services:', error);
        }
    }

    renderServicesTable(services) {
        const tbody = document.getElementById('servicesTableBody');
        tbody.innerHTML = '';

        services.forEach(service => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${service.code}</td>
                <td>${service.name}</td>
                <td>${service.description}</td>
                <td>${service.estimatedTime} phút</td>
                <td>
                    <span class="badge bg-${service.active ? 'success' : 'secondary'}">
                        ${service.active ? 'Hoạt động' : 'Ngừng'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-outline-primary edit-service" 
                            data-id="${service.id}">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger delete-service" 
                            data-id="${service.id}">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(row);
        });
    }

    setupEventListeners() {
        // Add service button
        document.getElementById('addServiceBtn').addEventListener('click', () => {
            this.showServiceModal();
        });

        // Edit service buttons
        document.addEventListener('click', (e) => {
            if (e.target.closest('.edit-service')) {
                const serviceId = e.target.closest('.edit-service').dataset.id;
                this.editService(serviceId);
            }
            
            if (e.target.closest('.delete-service')) {
                const serviceId = e.target.closest('.delete-service').dataset.id;
                this.deleteService(serviceId);
            }
        });

        // Search functionality
        document.getElementById('searchServices').addEventListener('input', (e) => {
            this.searchServices(e.target.value);
        });
    }

    async searchServices(query) {
        if (query.length < 2) {
            this.loadServices();
            return;
        }

        try {
            const response = await fetch(`/api/services/search?q=${encodeURIComponent(query)}`, {
                headers: {
                    'Authorization': 'Bearer ' + this.getToken(),
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                this.renderServicesTable(data);
            }
        } catch (error) {
            console.error('Error searching services:', error);
        }
    }

    getToken() {
        return localStorage.getItem('authToken');
    }
}