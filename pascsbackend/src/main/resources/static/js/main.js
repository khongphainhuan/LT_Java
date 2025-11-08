// main.js
document.addEventListener('DOMContentLoaded', function() {
  console.log("PASCS main.js loaded");

  // Dropdown user menu fix on mobile
  const dropdowns = document.querySelectorAll('.dropdown-toggle');
  dropdowns.forEach(drop => {
    drop.addEventListener('click', function(e) {
      e.preventDefault();
      const menu = this.nextElementSibling;
      menu.classList.toggle('show');
    });
  });
});
