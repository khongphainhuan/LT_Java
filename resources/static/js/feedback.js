// feedback.js
document.addEventListener('DOMContentLoaded', function() {
  const feedbackForms = document.querySelectorAll('.feedback-form');
  feedbackForms.forEach(form => {
    form.addEventListener('submit', function(e) {
      e.preventDefault();
      alert("Cảm ơn bạn đã gửi phản hồi!");
    });
  });
});
