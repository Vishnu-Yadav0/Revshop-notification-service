package com.revshop.notification.controller;

import com.revshop.notification.dto.ApiResponse;
import com.revshop.notification.dto.OrderItemResponseDTO;
import com.revshop.notification.dto.OrdersDTO;
import com.revshop.notification.model.Notification;
import com.revshop.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Notification>> createNotification(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String title = (String) request.get("title");
        String message = (String) request.get("message");
        String type = (String) request.get("type");
        String targetId = (String) request.get("targetId");

        Notification notification = notificationService.createNotification(userId, title, message, type, targetId);
        return ResponseEntity.ok(new ApiResponse<>("Notification created successfully", notification));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse<>("Notifications fetched successfully", notificationService.getNotificationsForUser(userId)));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(new ApiResponse<>("Unread notifications fetched successfully", notificationService.getUnreadNotificationsForUser(userId)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(new ApiResponse<>("Notification marked as read", null));
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String subject = request.get("subject");
        String body = request.get("body");
        boolean isHtml = Boolean.parseBoolean(request.getOrDefault("isHtml", "false"));

        notificationService.sendEmail(to, subject, body, isHtml);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp/email")
    public ResponseEntity<Void> sendOtpEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String otp = request.get("otp");
        notificationService.sendOtpEmail(to, otp);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp/sms/send")
    public ResponseEntity<Void> sendSmsOtp(@RequestBody Map<String, String> request) {
        String mobileNumber = request.get("mobileNumber");
        notificationService.sendSmsOtp(mobileNumber);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp/sms/verify")
    public ResponseEntity<Boolean> verifySmsOtp(@RequestBody Map<String, String> request) {
        String mobileNumber = request.get("mobileNumber");
        String otp = request.get("otp");
        boolean result = notificationService.verifySmsOtp(mobileNumber, otp);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/email/order-confirmation")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Void> sendOrderConfirmation(@RequestBody Map<String, Object> request) {
        OrdersDTO order = (OrdersDTO) request.get("order");
        String toEmail = (String) request.get("toEmail");
        List<OrderItemResponseDTO> items = (List<OrderItemResponseDTO>) request.get("items");
        notificationService.sendOrderConfirmation(order, toEmail, items);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/shipping")
    public ResponseEntity<Void> sendShippingNotification(@RequestBody Map<String, Object> request) {
        OrdersDTO order = (OrdersDTO) request.get("order");
        String toEmail = (String) request.get("toEmail");
        notificationService.sendShippingNotification(order, toEmail);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/registration")
    public ResponseEntity<Void> sendRegistrationEmail(@RequestBody Map<String, String> request) {
        String toEmail = request.get("toEmail");
        String name = request.get("name");
        notificationService.sendRegistrationEmail(toEmail, name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/password-reset")
    public ResponseEntity<Void> sendPasswordReset(@RequestBody Map<String, String> request) {
        notificationService.sendPasswordResetEmail(request.get("to"), request.get("resetLink"));
        return ResponseEntity.ok().build();
    }
}
