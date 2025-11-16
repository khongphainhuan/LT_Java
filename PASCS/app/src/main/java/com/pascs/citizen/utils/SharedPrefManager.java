package com.pascs.citizen.utils; // Gói (Package) này 100% "khớp" (match) với lỗi của bạn

import android.content.Context;
import android.content.SharedPreferences;

import com.pascs.citizen.models.User; // Chúng ta sẽ dùng file (tệp) User (Người dùng) model (mô hình)

/**
 * Lớp (Class) Tiện ích (Utility) (Singleton) (Đơn lẻ) để quản lý (lưu/lấy/xóa)
 * dữ liệu phiên (session) (như Auth Token (Mã thông báo xác thực) và thông tin User (Người dùng))
 * vào SharedPreferences (Tùy chọn được chia sẻ) (bộ nhớ local (cục bộ) của app).
 */
public class SharedPrefManager {

    // Tên của file (tệp) SharedPreferences (Tùy chọn được chia sẻ) (bộ nhớ local (cục bộ))
    private static final String SHARED_PREF_NAME = "pascs_citizen_shared_pref";

    // Khóa (Key) để lưu Auth Token (Mã thông báo xác thực) (JWT (Mã thông báo web JSON))
    private static final String KEY_AUTH_TOKEN = "key_auth_token";

    // Khóa (Key) để lưu thông tin User (Người dùng) (ID (Mã số), Tên (Name), Email)
    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_USER_FULLNAME = "key_user_fullname";
    private static final String KEY_USER_EMAIL = "key_user_email";
    private static final String KEY_USER_USERNAME = "key_user_username";

    // Biến (Variable) Singleton (Đơn lẻ)
    private static SharedPrefManager mInstance;
    private static Context mCtx; // Dùng để gọi SharedPreferences (Tùy chọn được chia sẻ)

    // Constructor (Hàm dựng) (private (riêng tư) để đảm bảo Singleton (Đơn lẻ))
    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    // Phương thức (Method) "getInstance" (lấy thể hiện) (của Singleton (Đơn lẻ))
    // (Đây là cách duy nhất để gọi lớp (class) này từ Activities (Hoạt động))
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    // --- 1. Quản lý (Management) Đăng nhập (Login) / Token (Mã thông báo) ---

    /**
     * Lưu (Save) Auth Token (Mã thông báo xác thực) (JWT (Mã thông báo web JSON)) vào SharedPreferences (Tùy chọn được chia sẻ)
     * (Hàm (Function) này được gọi trong LoginActivity (Hoạt động Đăng nhập) sau khi Login (Đăng nhập) thành công)
     */
    public void saveAuthToken(String token) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    /**
     * Kiểm tra (Check) xem người dùng đã Đăng nhập (Login) (đã có Token (Mã thông báo)) hay chưa?
     */
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        // Nếu Auth Token (Mã thông báo xác thực) (KEY_AUTH_TOKEN) không "null" (rỗng) (nghĩa là đã được lưu (save)),
        // thì người dùng 100% đã đăng nhập (login).
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null) != null;
    }

    /**
     * Lấy (Get) Auth Token (Mã thông báo xác thực) (JWT (Mã thông báo web JSON)) đã lưu (save)
     * (Chúng ta sẽ cần hàm (function) này để "đính kèm" (attach) Token (Mã thông báo) vào Header (Tiêu đề)
     * của mọi API (API) call (cuộc gọi) (của Dũng) sau này)
     */
    public String getAuthToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null); // Trả về "null" (rỗng) nếu không tìm thấy
    }

    /**
     * Xóa (Clear) toàn bộ phiên (session) (Token (Mã thông báo) + User (Người dùng))
     * (Hàm (Function) này được gọi khi người dùng nhấn nút "Đăng xuất" (Logout))
     */
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa SẠCH mọi thứ
        editor.apply();
    }


    // --- 2. Quản lý (Management) Thông tin User (Người dùng) ---

    /**
     * Lưu (Save) thông tin User (Người dùng) (từ đối tượng (object) User (Người dùng)) vào SharedPreferences (Tùy chọn được chia sẻ)
     * (Hàm (Function) này cũng được gọi trong LoginActivity (Hoạt động Đăng nhập) sau khi Login (Đăng nhập) thành công)
     */
    public void saveUser(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_FULLNAME, user.getFullName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_USERNAME, user.getUsername());
        editor.apply();
    }

    /**
     * Lấy (Get) thông tin User (Người dùng) đã lưu (save)
     * (Hàm (Function) này được gọi trong MainActivity (Hoạt động chính) (Homescreen) (Màn hình chính)
     * để hiển thị "Chào mừng, [Tên Công Dân]!")
     */
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_USER_ID, -1), // -1 là ID (Mã số) mặc định (default) (lỗi)
                sharedPreferences.getString(KEY_USER_USERNAME, null), // null (rỗng) là mặc định (default)
                sharedPreferences.getString(KEY_USER_EMAIL, null),
                sharedPreferences.getString(KEY_USER_FULLNAME, null)
        );
    }
}

