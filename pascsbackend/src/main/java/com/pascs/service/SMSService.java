package com.pascs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SMSService {

    @Value("${sms.api.key:}")
    private String smsApiKey;

    @Value("${sms.api.secret:}")
    private String smsApiSecret;

    @Value("${sms.api.url:}")
    private String smsApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendSMS(String phoneNumber, String message) {
        // Implementation for SMS gateway integration
        // This is a placeholder for actual SMS service integration
        
        try {
            if (isSMSConfigured()) {
                Map<String, String> smsRequest = new HashMap<>();
                smsRequest.put("phone", formatPhoneNumber(phoneNumber));
                smsRequest.put("message", message);
                smsRequest.put("api_key", smsApiKey);
                smsRequest.put("api_secret", smsApiSecret);

                // Uncomment when actual SMS service is configured
                // restTemplate.postForObject(smsApiUrl, smsRequest, String.class);
                
                System.out.println("SMS sent to " + phoneNumber + ": " + message);
            } else {
                // Log for development
                System.out.println("SMS [DEV]: " + phoneNumber + " - " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }

    public void sendQueueNotification(String phoneNumber, String queueNumber, 
                                    String serviceName, int estimatedWaitTime) {
        String message = String.format(
            "PASCS - So thu tu: %s. Dich vu: %s. Thoi gian cho du kien: %d phut. Xin cam on!",
            queueNumber, serviceName, estimatedWaitTime
        );
        sendSMS(phoneNumber, message);
    }

    public void sendAppointmentReminder(String phoneNumber, String appointmentCode, 
                                      String appointmentTime, String serviceName) {
        String message = String.format(
            "PASCS - Lich hen: %s. Dich vu: %s. Thoi gian: %s. Vui long den dung gio!",
            appointmentCode, serviceName, appointmentTime
        );
        sendSMS(phoneNumber, message);
    }

    public void sendApplicationStatusUpdate(String phoneNumber, String applicationCode, 
                                          String status, String note) {
        String message = String.format(
            "PASCS - Ho so %s. Trang thai: %s. %s",
            applicationCode, status, note != null ? "Ghi chu: " + note : ""
        );
        sendSMS(phoneNumber, message);
    }

    private boolean isSMSConfigured() {
        return smsApiKey != null && !smsApiKey.isEmpty() && 
               smsApiSecret != null && !smsApiSecret.isEmpty();
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Format phone number to international format
        if (phoneNumber.startsWith("0")) {
            return "+84" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}