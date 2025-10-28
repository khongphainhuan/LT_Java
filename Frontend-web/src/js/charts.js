// charts.js
class ChartManager {
    constructor() {
        this.serviceChart = null;
        this.serviceTypeChart = null;
        this.init();
    }

    async init() {
        await this.loadServiceChart();
        await this.loadServiceTypeChart();
    }

    async loadServiceChart() {
        try {
            const response = await fetch('/api/dashboard/service-stats', {
                headers: {
                    'Authorization': 'Bearer ' + this.getToken(),
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                this.renderServiceChart(data);
            }
        } catch (error) {
            console.error('Error loading service chart data:', error);
        }
    }

    renderServiceChart(data) {
        const ctx = document.getElementById('serviceChart').getContext('2d');
        
        if (this.serviceChart) {
            this.serviceChart.destroy();
        }

        this.serviceChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.labels || [],
                datasets: [{
                    label: 'Lượt phục vụ',
                    data: data.values || [],
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    tension: 0.1,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: 'Thống kê 7 ngày gần nhất'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Số lượt'
                        }
                    }
                }
            }
        });
    }

    async loadServiceTypeChart() {
        try {
            const response = await fetch('/api/dashboard/service-type-stats', {
                headers: {
                    'Authorization': 'Bearer ' + this.getToken(),
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                this.renderServiceTypeChart(data);
            }
        } catch (error) {
            console.error('Error loading service type chart data:', error);
        }
    }

    renderServiceTypeChart(data) {
        const ctx = document.getElementById('serviceTypeChart').getContext('2d');
        
        if (this.serviceTypeChart) {
            this.serviceTypeChart.destroy();
        }

        this.serviceTypeChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: data.labels || [],
                datasets: [{
                    data: data.values || [],
                    backgroundColor: [
                        'rgb(255, 99, 132)',
                        'rgb(54, 162, 235)',
                        'rgb(255, 205, 86)',
                        'rgb(75, 192, 192)',
                        'rgb(153, 102, 255)'
                    ],
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                    }
                }
            }
        });
    }

    getToken() {
        return localStorage.getItem('authToken');
    }
}

// Initialize charts when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ChartManager();
});