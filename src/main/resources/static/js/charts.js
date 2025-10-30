// charts.js
document.addEventListener('DOMContentLoaded', function() {
  if (typeof Chart !== "undefined") {
    const ctx = document.getElementById('chartDashboard');
    if (ctx) {
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Hồ sơ', 'Đang xử lý', 'Hoàn tất', 'Phản hồi'],
          datasets: [{
            label: 'Số lượng',
            data: [12, 5, 28, 3],
            backgroundColor: ['#0d6efd','#ffc107','#198754','#0dcaf0']
          }]
        },
        options: { responsive: true, plugins: { legend: { display: false } } }
      });
    }
  }
});
