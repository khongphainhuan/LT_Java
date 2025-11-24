// Admin Reports - Load data and render charts
document.addEventListener('DOMContentLoaded', function () {
    console.log('📈 Reports page loaded');
    loadReportData();
});

async function loadReportData() {
    try {
        // 1. Load User Stats
        const usersResponse = await fetch('/api/users/all', { credentials: 'include' });
        let userStats = { citizen: 0, staff: 0, admin: 0 };

        if (usersResponse.ok) {
            const users = await usersResponse.json();
            userStats.citizen = users.filter(u => u.role === 'CITIZEN').length;
            userStats.staff = users.filter(u => u.role === 'STAFF').length;
            userStats.admin = users.filter(u => u.role === 'ADMIN').length;
            console.log('👥 User Stats:', userStats);
        }

        // 2. Load Queue Stats
        const queueResponse = await fetch('/api/queues/stats/public', { credentials: 'include' });
        let queueStats = { waiting: 0, completed: 0 };

        if (queueResponse.ok) {
            const stats = await queueResponse.json();
            queueStats.waiting = stats.totalWaiting || 0;
            queueStats.completed = stats.totalCompletedToday || 0; // Tạm dùng số liệu hôm nay
            console.log('📋 Queue Stats:', queueStats);
        }

        // 3. Render Charts
        renderUserChart(userStats);
        renderQueueChart(queueStats);

    } catch (error) {
        console.error('❌ Error loading report data:', error);
    }
}

function renderUserChart(stats) {
    const ctx = document.getElementById('userChart');
    if (!ctx) return;

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Công dân', 'Cán bộ', 'Quản trị viên'],
            datasets: [{
                data: [stats.citizen, stats.staff, stats.admin],
                backgroundColor: ['#36A2EB', '#FFCE56', '#FF6384'],
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { position: 'bottom' },
                title: { display: true, text: 'Cơ cấu người dùng hệ thống' }
            }
        }
    });
}

function renderQueueChart(stats) {
    const ctx = document.getElementById('queueChart');
    if (!ctx) return;

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Đang chờ xử lý', 'Đã hoàn thành (Hôm nay)'],
            datasets: [{
                label: 'Số lượng hồ sơ',
                data: [stats.waiting, stats.completed],
                backgroundColor: ['#FF9F40', '#4BC0C0'],
                borderColor: ['#FF9F40', '#4BC0C0'],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1 } }
            },
            plugins: {
                legend: { display: false },
                title: { display: true, text: 'Tình hình xử lý hồ sơ' }
            }
        }
    });
}
