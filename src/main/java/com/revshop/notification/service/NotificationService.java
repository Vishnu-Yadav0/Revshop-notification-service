package com.revshop.notification.service;

import com.revshop.notification.dto.OrderItemResponseDTO;
import com.revshop.notification.dto.OrdersDTO;
import com.revshop.notification.model.Notification;
import com.revshop.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final TwilioSmsService twilioSmsService;

    public NotificationService(NotificationRepository notificationRepository,
                               EmailService emailService,
                               TwilioSmsService twilioSmsService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.twilioSmsService = twilioSmsService;
    }

    @Transactional
    public Notification createNotification(Long userId, String title, String message, String type, String targetId) {
        log.info("Creating notification for user={}: {}", userId, title);
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .targetId(targetId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    public void sendEmail(String toEmail, String subject, String body, boolean isHtml) {
        emailService.sendEmail(toEmail, subject, body, isHtml);
    }

    public void sendOtpEmail(String toEmail, String otp) {
        emailService.sendOtpEmail(toEmail, otp);
    }

    public void sendSmsOtp(String mobileNumber) {
        twilioSmsService.sendSmsOtp(mobileNumber);
    }

    public boolean verifySmsOtp(String mobileNumber, String otp) {
        return twilioSmsService.verifyMobileKyc(mobileNumber, otp);
    }

    public void sendOrderConfirmation(OrdersDTO order, String toEmail, List<OrderItemResponseDTO> items) {
        emailService.sendOrderConfirmation(order, toEmail, items);
    }

    public void sendShippingNotification(OrdersDTO order, String toEmail) {
        emailService.sendShippingNotification(order, toEmail);
    }

    public void sendRegistrationEmail(String to, String name) {
        emailService.sendUserRegistrationEmail(to, name);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        emailService.sendPasswordResetEmail(to, resetLink);
    }
}
