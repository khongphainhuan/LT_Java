// Admin Dashboard - Load real data from database
document.addEventListener('DOMContentLoaded', function () {
    loadAdminDashboardStats();
    // Auto refresh every 30 seconds
    setInterval(loadAdminDashboardStats, 30000);
});

async function loadAdminDashboardStats() {
    try {
        // Load users data
        const usersResponse = await fetch('/api/users/all', {
            credentials: 'include'
        });

        if (usersResponse.ok) {
            const users = await usersResponse.json();
            console.log('📊 Loaded users:', users.length);

            // Count citizens - User has single 'role' field (CITIZEN, STAFF, ADMIN)
            const citizens = users.filter(u => u.role === 'CITIZEN');
            const totalCitizens = document.querySelector('.stat-card:nth-child(1) .stat-value');
            if (totalCitizens) {
                totalCitizens.textContent = citizens.length.toLocaleString('vi-VN');
                console.log('👥 Citizens:', citizens.length);
            }

            // Count staff - includes both STAFF and ADMIN
            const staff = users.filter(u => u.role === 'STAFF' || u.role === 'ADMIN');
            const onlineStaff = document.querySelector('.stat-card:nth-child(4) .stat-value');
            if (onlineStaff) {
                onlineStaff.textContent = staff.length;
                console.log('👔 Staff:', staff.length);
            }
        }

        // Load queue stats
        const queueResponse = await fetch('/api/queues/stats/public', {
            credentials: 'include'
        });

        if (queueResponse.ok) {
            const queueStats = await queueResponse.json();

            // Completed applications
            const completed = document.querySelector('.stat-card:nth-child(2) .stat-value');
            if (completed) {
                completed.textContent = (queueStats.totalCompletedToday || 0).toLocaleString('vi-VN');
            }

            // Pending queue
            const pending = document.querySelector('.stat-card:nth-child(3) .stat-value');
            if (pending) {
                pending.textContent = (queueStats.totalWaiting || 0);
            }
            console.log('📋 Queue stats loaded');
        }

        console.log('✅ Dashboard stats loaded successfully');
    } catch (error) {
        console.error('❌ Error loading dashboard stats:', error);
    }
}
