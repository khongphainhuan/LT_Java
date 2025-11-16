package com.pascs.citizen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.pascs.citizen.LoginActivity;
import com.pascs.citizen.R;
import com.pascs.citizen.TakeQueueActivity;
import com.pascs.citizen.QueueStatusActivity;
import com.pascs.citizen.BookAppointmentActivity;
import com.pascs.citizen.ApplicationListActivity;
import com.pascs.citizen.FeedbackActivity;
import com.pascs.citizen.models.User;
import com.pascs.citizen.utils.SharedPrefManager;
import com.pascs.citizen.utils.LanguageManager;

public class MainActivity extends BaseActivity {

    private TextView tvWelcome, btnLanguage;
    private MaterialCardView cardTakeQueue, cardQueueStatus, cardBookAppointment,
            cardTrackApplication, cardFeedback;
    private FloatingActionButton fabLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo (Init) Views
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLanguage = findViewById(R.id.btnLanguage);
        cardTakeQueue = findViewById(R.id.cardTakeQueue);
        cardQueueStatus = findViewById(R.id.cardQueueStatus);
        cardBookAppointment = findViewById(R.id.cardBookAppointment);
        cardTrackApplication = findViewById(R.id.cardTrackApplication);
        cardFeedback = findViewById(R.id.cardFeedback);
        fabLogout = findViewById(R.id.fabLogout);

        // Set welcome text
        updateWelcomeText();

        // === GÁN CÁC BUTTON CLICKS ===

        // Language button
        btnLanguage.setOnClickListener(v -> showLanguageDialog());

        // 1. Lấy số thứ tự
        cardTakeQueue.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TakeQueueActivity.class);
            startActivity(intent);
        });

        // 2. Xem trạng thái hàng chờ
        cardQueueStatus.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QueueStatusActivity.class);
            startActivity(intent);
        });

        // 3. Đặt lịch hẹn
        cardBookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookAppointmentActivity.class);
            startActivity(intent);
        });

        // 4. Theo dõi hồ sơ
        cardTrackApplication.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ApplicationListActivity.class);
            startActivity(intent);
        });

        // 5. Gửi góp ý
        cardFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        // Logout button
        fabLogout.setOnClickListener(v -> handleLogout());
    }

    private void updateWelcomeText() {
        User user = SharedPrefManager.getInstance(this).getUser();
        String userName = (user != null) ? user.getFullName() : null;

        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText(getString(R.string.welcome_user, userName));
        } else {
            tvWelcome.setText(getString(R.string.welcome));
        }
    }

    private void showLanguageDialog() {
        // Tạo (Create) LanguageManager mới
        LanguageManager langManager = new LanguageManager(this);

        String[] languages = {
                getString(R.string.language_vietnamese),
                getString(R.string.language_english)
        };

        // Check ngôn ngữ (Language) hiện tại (Current)
        int currentSelection = langManager.isVietnamese() ? 0 : 1;

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(languages, currentSelection, (dialog, which) -> {
                    String languageCode;
                    if (which == 0) {
                        languageCode = LanguageManager.getVietnameseCode();
                    } else {
                        languageCode = LanguageManager.getEnglishCode();
                    }

                    // Lưu (Save) và áp dụng (Apply) ngôn ngữ (Language)
                    langManager.setLanguage(languageCode);

                    // Hiển thị (Show) thông báo (Notification)
                    Toast.makeText(this,
                            getString(R.string.language_changed),
                            Toast.LENGTH_SHORT).show();

                    dialog.dismiss();

                    // Restart activity để áp dụng (Apply) ngôn ngữ (Language)
                    recreate();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void handleLogout() {
        SharedPrefManager.getInstance(this).logout();
        Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}