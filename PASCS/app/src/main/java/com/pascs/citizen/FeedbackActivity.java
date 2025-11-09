package com.pascs.citizen; // (Gói GỐC)

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pascs.citizen.models.FeedbackRequest;

public class FeedbackActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RatingBar ratingBar;
    private TextView tvRatingText;
    private EditText etComment;
    private Button btnSubmitFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Khởi tạo (Init) Views
        toolbar = findViewById(R.id.toolbarFeedback);
        ratingBar = findViewById(R.id.ratingBar);
        tvRatingText = findViewById(R.id.tvRatingText);
        etComment = findViewById(R.id.etComment);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);

        // --- Cài đặt (Setup) Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gửi góp ý");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Xử lý (Handle) RatingBar ---
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            updateRatingText((int) rating);
        });

        // --- Xử lý (Handle) Button ---
        btnSubmitFeedback.setOnClickListener(v -> handleSubmitFeedback());
    }

    // (Hàm này để xử lý khi nhấn nút Back trên Toolbar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateRatingText(int rating) {
        switch (rating) {
            case 1:
                tvRatingText.setText("😞 Rất không hài lòng");
                break;
            case 2:
                tvRatingText.setText("😐 Không hài lòng");
                break;
            case 3:
                tvRatingText.setText("😊 Bình thường");
                break;
            case 4:
                tvRatingText.setText("😃 Hài lòng");
                break;
            case 5:
                tvRatingText.setText("🤩 Rất hài lòng");
                break;
            default:
                tvRatingText.setText("Chưa đánh giá");
                break;
        }
    }

    private void handleSubmitFeedback() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        // Kiểm tra (Validation)
        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập góp ý của bạn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mock: Tạo (Create) feedback request
        FeedbackRequest feedback = new FeedbackRequest(1, (int) rating, comment);

        // (Hiện tại, chúng ta chỉ hiển thị thông báo TEST)
        btnSubmitFeedback.setEnabled(false);
        btnSubmitFeedback.setText("Đang gửi...");

        Toast.makeText(this,
                "[TEST] Đang gửi góp ý...",
                Toast.LENGTH_SHORT).show();

        // Giả lập (Simulate) API call
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this,
                    "[TEST] Gửi góp ý thành công!\n" +
                            "Đánh giá: " + (int) rating + " sao\n" +
                            "Cảm ơn bạn đã đóng góp ý kiến!",
                    Toast.LENGTH_LONG).show();

            finish(); // Đóng màn hình này
        }, 1500);
    }
}