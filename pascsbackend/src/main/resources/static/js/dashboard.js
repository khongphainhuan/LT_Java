// dashboard.js
document.addEventListener('DOMContentLoaded', function() {
  const cards = document.querySelectorAll('.dashboard-card');
  cards.forEach(card => {
    card.addEventListener('mouseenter', () => card.classList.add('shadow-lg'));
    card.addEventListener('mouseleave', () => card.classList.remove('shadow-lg'));
  });
});
