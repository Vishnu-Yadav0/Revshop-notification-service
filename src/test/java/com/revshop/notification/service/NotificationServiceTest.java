package com.revshop.notification.service;

import com.revshop.notification.model.Notification;
import com.revshop.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private TwilioSmsService twilioSmsService;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = Notification.builder()
                .notificationId(1L)
                .userId(1L)
                .title("Test Title")
                .message("Test Message")
                .isRead(false)
                .build();
    }

    @Test
    void createNotification_Success() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification savedNotification = notificationService.createNotification(1L, "Test Title", "Test Message", "INFO", "target1");

        assertNotNull(savedNotification);
        assertEquals(1L, savedNotification.getUserId());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getNotificationsForUser_Success() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(notification));

        List<Notification> result = notificationService.getNotificationsForUser(1L);

        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
    }

    @Test
    void sendOtpEmail_Success() {
        notificationService.sendOtpEmail("test@example.com", "123456");
        verify(emailService, times(1)).sendOtpEmail("test@example.com", "123456");
    }
}
