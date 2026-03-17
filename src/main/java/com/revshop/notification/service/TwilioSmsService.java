package com.revshop.notification.service;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TwilioSmsService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.verify.service.sid:VAXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX}")
    private String verifyServiceSid;

    // In-memory OTP store for simulation mode (mobileNumber -> otp)
    private final ConcurrentHashMap<String, String> simulatedOtpStore = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    @PostConstruct
    public void init() {
        // Only initialize if real credentials are provided (real Twilio SIDs start with 'AC')
        if (accountSid != null && accountSid.startsWith("AC")) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully.");
        } else {
            log.warn("Twilio not initialized: placeholder credentials detected. OTP will be simulated in-memory.");
        }
    }

    private boolean isSimulated() {
        return accountSid == null || !accountSid.startsWith("AC");
    }

    public void sendSmsOtp(String mobileNumber) {
        log.info("Sending SMS OTP to {}", mobileNumber);
        if (isSimulated()) {
            // Generate a 6-digit OTP and store it
            String otp = String.format("%06d", random.nextInt(1_000_000));
            simulatedOtpStore.put(mobileNumber, otp);
            // Print clearly so it's visible in service logs for dev/demo purposes
            log.warn("============================================");
            log.warn("  [DEV MODE] OTP for {} is: {}", mobileNumber, otp);
            log.warn("  (Twilio is not configured — check server logs for OTP)");
            log.warn("============================================");
            return;
        }
        Verification.creator(verifyServiceSid, mobileNumber, "sms").create();
    }

    public boolean verifyMobileKyc(String mobileNumber, String otp) {
        log.info("Checking SMS OTP for {}", mobileNumber);
        if (isSimulated()) {
            String storedOtp = simulatedOtpStore.get(mobileNumber);
            if (storedOtp == null) {
                log.warn("No OTP found for {} — did you call sendSmsOtp first?", mobileNumber);
                return false;
            }
            boolean matched = storedOtp.equals(otp);
            if (matched) {
                simulatedOtpStore.remove(mobileNumber); // OTP consumed after successful verification
                log.info("Simulated OTP verified successfully for {}", mobileNumber);
            } else {
                log.warn("Simulated OTP mismatch for {} — entered: {}, expected: {}", mobileNumber, otp, storedOtp);
            }
            return matched;
        }
        VerificationCheck verificationCheck = VerificationCheck.creator(verifyServiceSid)
                .setCode(otp)
                .setTo(mobileNumber).create();
        return "approved".equals(verificationCheck.getStatus());
    }
}
