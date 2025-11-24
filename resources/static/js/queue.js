// queue.js
document.addEventListener('DOMContentLoaded', function() {
  const callButtons = document.querySelectorAll('.btn-call');
  callButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const row = btn.closest('tr');
      const citizen = row.querySelector('td:nth-child(2)').innerText;
      alert(`Đang gọi công dân: ${citizen}`);
      row.classList.add('table-success');
    });
  });
});
