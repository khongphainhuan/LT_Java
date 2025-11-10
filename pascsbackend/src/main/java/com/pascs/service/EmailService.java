package com.pascs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired(required = false)
    private TemplateEngine templateEngine;

    public void sendSimpleEmail(String to, String subject, String text) {
        if (mailSender == null) {
            System.out.println("Email [DEV]: " + to + " - " + subject + " - " + text);
            return;
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("noreply@pascs.com");
        
        mailSender.send(message);
    }

    public void sendQueueNotification(String to, String queueNumber, String serviceName, int estimatedWaitTime) {
        if (mailSender == null || templateEngine == null) {
            System.out.println("Queue Email [DEV]: " + to + " - Queue: " + queueNumber);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariable("queueNumber", queueNumber);
            context.setVariable("serviceName", serviceName);
            context.setVariable("estimatedWaitTime", estimatedWaitTime);
            
            String htmlContent = templateEngine.process("queue-notification", context);
            
            helper.setTo(to);
            helper.setSubject("Thông báo số thứ tự - PASCS");
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@pascs.com");
            
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendAppointmentConfirmation(String to, String appointmentCode, 
                                          LocalDateTime appointmentTime, String serviceName) {
        if (mailSender == null || templateEngine == null) {
            System.out.println("Appointment Email [DEV]: " + to + " - Appointment: " + appointmentCode);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            context.setVariable("appointmentCode", appointmentCode);
            context.setVariable("appointmentTime", appointmentTime);
            context.setVariable("serviceName", serviceName);
            
            String htmlContent = templateEngine.process("appointment-confirmation", context);
            
            helper.setTo(to);
            helper.setSubject("Xác nhận lịch hẹn - PASCS");
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@pascs.com");
            
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}