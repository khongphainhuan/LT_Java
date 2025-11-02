package com.pascs.citizen.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiConfig {

    // TODO: HỎI MẠNH BASE URL NÀY ⚠️
    // Ví dụ: "http://192.168.1.100:8080/" hoặc "http://your-server.com/"
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // ← THAY ĐỔI

    // Note: 10.0.2.2 = localhost khi chạy emulator Android
    // Nếu dùng máy thật, dùng IP máy Mạnh (ví dụ: 192.168.1.100)

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            // Logging interceptor để debug API calls
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp client với timeout
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    // Helper method để lấy ApiService
    public static ApiService getApiService() {
        return getRetrofit().create(ApiService.class);
    }
}