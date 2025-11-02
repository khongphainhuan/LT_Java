package com.pascs.citizen.api;

import com.pascs.citizen.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ========== AUTHENTICATION ==========

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("api/auth/logout")
    Call<ApiResponse<String>> logout(@Header("Authorization") String token);

    @GET("api/user/profile")
    Call<ApiResponse<User>> getUserProfile(@Header("Authorization") String token);

    @PUT("api/user/profile")
    Call<ApiResponse<User>> updateProfile(
            @Header("Authorization") String token,
            @Body User user
    );

    // ========== SERVICES ==========

    @GET("api/services")
    Call<ApiResponse<List<Service>>> getServices();

    @GET("api/services/{id}")
    Call<ApiResponse<Service>> getServiceById(@Path("id") int id);

    // ========== QUEUE ==========

    @POST("api/queue/take")
    Call<ApiResponse<QueueTicket>> takeQueue(
            @Header("Authorization") String token,
            @Body TakeQueueRequest request
    );

    @GET("api/queue/status/{id}")
    Call<ApiResponse<QueueTicket>> getQueueStatus(
            @Header("Authorization") String token,
            @Path("id") int ticketId
    );

    @GET("api/queue/my-tickets")
    Call<ApiResponse<List<QueueTicket>>> getMyTickets(
            @Header("Authorization") String token
    );

    // ========== APPLICATION ==========

    @POST("api/application/submit")
    Call<ApiResponse<Application>> submitApplication(
            @Header("Authorization") String token,
            @Body SubmitApplicationRequest request
    );

    @GET("api/application/list")
    Call<ApiResponse<List<Application>>> getApplicationList(
            @Header("Authorization") String token
    );

    @GET("api/application/{id}")
    Call<ApiResponse<Application>> getApplicationById(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    // ========== FEEDBACK ==========

    @POST("api/feedback/submit")
    Call<ApiResponse<String>> submitFeedback(
            @Header("Authorization") String token,
            @Body FeedbackRequest request
    );

    @GET("api/feedback/my-list")
    Call<ApiResponse<List<FeedbackRequest>>> getMyFeedbacks(
            @Header("Authorization") String token
    );
}