package com.revshop.notification.service;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class TwilioSmsService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.verify_service_sid:VAXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX}")
    private String verifyServiceSid;

    @PostConstruct
    public void init() {
        if (!"your_sid".equals(accountSid)) {
            Twilio.init(accountSid, authToken);
        } else {
            log.warn("Twilio not initialized: placeholders used.");
        }
    }

    public void sendSmsOtp(String mobileNumber) {
        log.info("Sending SMS OTP to {}", mobileNumber);
        if ("your_sid".equals(accountSid)) return;
        
        Verification.creator(verifyServiceSid, mobileNumber, "sms").create();
    }

    public boolean verifyMobileKyc(String mobileNumber, String otp) {
        log.info("Checking SMS OTP for {}", mobileNumber);
        if ("your_sid".equals(accountSid)) return true; // Simulate success if not configured

        VerificationCheck verificationCheck = VerificationCheck.creator(verifyServiceSid)
                .setCode(otp)
                .setTo(mobileNumber).create();
        return "approved".equals(verificationCheck.getStatus());
    }
}
