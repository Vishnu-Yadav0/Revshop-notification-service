package com.revshop.notification.service;

import com.revshop.notification.dto.OrderItemResponseDTO;
import com.revshop.notification.dto.OrdersDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String body, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, isHtml);

            mailSender.send(message);
            log.info("Email sent to {} with subject: {}", toEmail, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendOrderConfirmation(OrdersDTO order, String toEmail, List<OrderItemResponseDTO> items) {
        try {
            StringBuilder itemsHtml = new StringBuilder();
            if (items != null) {
                for (OrderItemResponseDTO item : items) {
                    itemsHtml.append("<tr>")
                            .append("<td style='padding:8px;border-bottom:1px solid #eee;'>").append(item.getProductName()).append("</td>")
                            .append("<td style='padding:8px;border-bottom:1px solid #eee;text-align:center;'>").append(item.getQuantity()).append("</td>")
                            .append("<td style='padding:8px;border-bottom:1px solid #eee;text-align:right;'>₹").append(item.getPrice()).append("</td>")
                            .append("<td style='padding:8px;border-bottom:1px solid #eee;text-align:right;'>₹").append(item.getSubtotal()).append("</td>")
                            .append("</tr>");
                }
            }

            String orderDate = order.getOrderDate() != null
                    ? order.getOrderDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
                    : "N/A";

            String html = "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;'>"
                    + "<div style='background:#0d6efd;color:white;padding:20px;text-align:center;border-radius:8px 8px 0 0;'>"
                    + "<h1 style='margin:0;'>Order Confirmed! ✅</h1></div>"
                    + "<div style='padding:20px;background:#f8f9fa;'>"
                    + "<p>Hi there,</p>"
                    + "<p>Your order has been placed successfully.</p>"
                    + "<table style='width:100%;margin:10px 0;'>"
                    + "<tr><td><strong>Order Number:</strong></td><td>" + order.getOrderNumber() + "</td></tr>"
                    + "<tr><td><strong>Order Date:</strong></td><td>" + orderDate + "</td></tr>"
                    + "<tr><td><strong>Payment Method:</strong></td><td>" + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "COD") + "</td></tr>"
                    + "</table>"
                    + "<h3 style='border-bottom:2px solid #0d6efd;padding-bottom:5px;'>Order Items</h3>"
                    + "<table style='width:100%;border-collapse:collapse;'>"
                    + "<thead><tr style='background:#e9ecef;'>"
                    + "<th style='padding:8px;text-align:left;'>Product</th>"
                    + "<th style='padding:8px;text-align:center;'>Qty</th>"
                    + "<th style='padding:8px;text-align:right;'>Price</th>"
                    + "<th style='padding:8px;text-align:right;'>Subtotal</th>"
                    + "</tr></thead><tbody>" + itemsHtml + "</tbody></table>"
                    + "<div style='text-align:right;margin-top:10px;font-size:18px;'>"
                    + "<strong>Total: ₹" + order.getTotalAmount() + "</strong></div>"
                    + "<hr style='margin:20px 0;'>"
                    + "<p style='text-align:center;color:#6c757d;'>Thank you for shopping with RevShop! 🛍️</p>"
                    + "</div></div>";

            sendEmail(toEmail, "Order Confirmed - " + order.getOrderNumber(), html, true);
        } catch (Exception e) {
            log.warn("Failed to send order confirmation: {}", e.getMessage());
        }
    }

    public void sendShippingNotification(OrdersDTO order, String toEmail) {
        String shipperHtml = "";
        if (order.getShipper() != null) {
            shipperHtml = "<div style='background:white;padding:15px;border-radius:8px;margin:15px 0;'>"
                    + "<h3 style='margin-top:0;'>Delivery Agent Details</h3>"
                    + "<p><strong>Name:</strong> " + order.getShipper().getName() + "</p>"
                    + "<p><strong>Vehicle:</strong> " + order.getShipper().getVehicleNumber() + "</p>"
                    + "<p><strong>Contact:</strong> " + order.getShipper().getPhone() + "</p>"
                    + "</div>";
        }

        String html = "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;'>"
                + "<div style='background:#198754;color:white;padding:20px;text-align:center;border-radius:8px 8px 0 0;'>"
                + "<h1 style='margin:0;'>Order Shipped! 🚚</h1></div>"
                + "<div style='padding:20px;background:#f8f9fa;'>"
                + "<p>Hi there,</p>"
                + "<p>Great news! Your order <strong>" + order.getOrderNumber()
                + "</strong> has been dispatched and is on its way to you.</p>"
                + shipperHtml
                + "<p>You will receive another notification once your order is delivered.</p>"
                + "<hr style='margin:20px 0;'>"
                + "<p style='text-align:center;color:#6c757d;'>Thank you for shopping with RevShop! 🛍️</p>"
                + "</div></div>";

        sendEmail(toEmail, "Your Order " + order.getOrderNumber() + " Has Been Shipped", html, true);
    }

    public void sendUserRegistrationEmail(String toEmail, String name) {
        String html = "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;'>"
                + "<div style='background:#6f42c1;color:white;padding:20px;text-align:center;border-radius:8px 8px 0 0;'>"
                + "<h1 style='margin:0;'>Welcome to RevShop! 🎉</h1></div>"
                + "<div style='padding:20px;background:#f8f9fa;'>"
                + "<p>Hi <strong>" + name + "</strong>,</p>"
                + "<p>Thank you for registering. Your account has been created successfully.</p>"
                + "<a href='http://localhost:4200/login' style='display:inline-block;padding:10px 20px;margin:20px 0;background:#6f42c1;color:white;text-decoration:none;border-radius:5px;'>Login Now</a>"
                + "<hr style='margin:20px 0;'>"
                + "</div></div>";
        sendEmail(toEmail, "Welcome to RevShop, " + name + "!", html, true);
    }

    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "RevShop Email Verification";
        String html = "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;'>"
                + "<div style='background:#fd7e14;color:white;padding:20px;text-align:center;border-radius:8px 8px 0 0;'>"
                + "<h1 style='margin:0;'>RevShop Email Verification</h1></div>"
                + "<div style='padding:20px;background:#f8f9fa;'>"
                + "<p>Hi there,</p>"
                + "<p>Your verification code is:</p>"
                + "<div style='font-size:32px;font-weight:bold;letter-spacing:5px;text-align:center;padding:20px;margin:20px;background:white;border:2px dashed #fd7e14;border-radius:8px;'>"
                + otp
                + "</div>"
                + "<p style='color:red;'>This code will expire in 10 minutes.</p>"
                + "<hr style='margin:20px 0;'>"
                + "<p style='text-align:center;color:#6c757d;'>RevShop Security Team 🔒</p>"
                + "</div></div>";
        sendEmail(toEmail, subject, html, true);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Password Reset Request";
        String content = "<h3>Password Reset</h3>" +
                "<p>To reset your password, click the link below:</p>" +
                "<a href=\"" + resetLink + "\">Reset Password</a>" +
                "<p>If you did not request this, please ignore this email.</p>";
        sendEmail(to, subject, content, true);
    }
}
