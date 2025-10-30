// applications.js
document.addEventListener('DOMContentLoaded', function() {
  const updateForms = document.querySelectorAll('.application-form');
  updateForms.forEach(form => {
    form.addEventListener('submit', function(e) {
      e.preventDefault();
      alert("Hồ sơ đã được cập nhật thành công!");
    });
  });
});
