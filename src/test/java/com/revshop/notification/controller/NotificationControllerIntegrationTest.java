package com.revshop.notification.controller;

import com.revshop.notification.model.Notification;
import com.revshop.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@ActiveProfiles("test")
public class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void createNotification_ReturnsOk() throws Exception {
        Notification notification = Notification.builder()
                .notificationId(1L)
                .title("Test")
                .build();

        when(notificationService.createNotification(any(), any(), any(), any(), any())).thenReturn(notification);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1, \"title\": \"Test\", \"message\": \"Msg\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1));
    }

    @Test
    void getNotifications_ReturnsList() throws Exception {
        Notification notification = Notification.builder()
                .notificationId(1L)
                .build();

        when(notificationService.getNotificationsForUser(1L)).thenReturn(Arrays.asList(notification));

        mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(1));
    }

    @Test
    void sendOtpEmail_ReturnsOk() throws Exception {
        mockMvc.perform(post("/api/notifications/otp/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\": \"test@example.com\", \"otp\": \"123456\"}"))
                .andExpect(status().isOk());
    }
}
