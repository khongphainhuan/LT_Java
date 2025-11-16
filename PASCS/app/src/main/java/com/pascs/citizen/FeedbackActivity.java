package com.pascs.citizen;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.pascs.citizen.activities.BaseActivity;
import com.pascs.citizen.models.FeedbackRequest;
import com.pascs.citizen.R;

public class FeedbackActivity extends BaseActivity {

    private Toolbar toolbar;
    private RatingBar ratingBar;
    private TextView tvRatingText;
    private EditText etComment;
    private Button btnSubmitFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Khởi tạo Views
        toolbar = findViewById(R.id.toolbarFeedback);
        ratingBar = findViewById(R.id.ratingBar);
        tvRatingText = findViewById(R.id.tvRatingText);
        etComment = findViewById(R.id.etComment);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);

        // --- Cài đặt Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // ✅ SỬA: Dùng getString()
            getSupportActionBar().setTitle(getString(R.string.title_feedback));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Xử lý RatingBar ---
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            updateRatingText((int) rating);
        });

        // --- Xử lý Button ---
        btnSubmitFeedback.setOnClickListener(v -> handleSubmitFeedback());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateRatingText(int rating) {
        // ✅ SỬA: Dùng getString()
        switch (rating) {
            case 1:
                tvRatingText.setText(getString(R.string.rating_very_bad));
                break;
            case 2:
                tvRatingText.setText(getString(R.string.rating_bad));
                break;
            case 3:
                tvRatingText.setText(getString(R.string.rating_normal));
                break;
            case 4:
                tvRatingText.setText(getString(R.string.rating_good));
                break;
            case 5:
                tvRatingText.setText(getString(R.string.rating_excellent));
                break;
            default:
                tvRatingText.setText(getString(R.string.not_rated));
                break;
        }
    }

    private void handleSubmitFeedback() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        // Kiểm tra Validation
        if (rating == 0) {
            // ✅ SỬA: Dùng getString()
            Toast.makeText(this, getString(R.string.select_rating), Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            // ✅ SỬA: Dùng getString()
            Toast.makeText(this, getString(R.string.enter_comment), Toast.LENGTH_SHORT).show();
            return;
        }

        // Mock: Tạo feedback request
        FeedbackRequest feedback = new FeedbackRequest(1, (int) rating, comment);

        btnSubmitFeedback.setEnabled(false);
        // ✅ SỬA: Tạo string mới cho "Đang gửi..."
        btnSubmitFeedback.setText(getString(R.string.processing));

        // Giả lập API call
        new android.os.Handler().postDelayed(() -> {
            // ✅ SỬA: Dùng String.format
            Toast.makeText(this,
                    String.format(getString(R.string.test_feedback_success), (int) rating),
                    Toast.LENGTH_LONG).show();

            finish();
        }, 1500);
    }
}