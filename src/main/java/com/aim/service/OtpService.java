package com.aim.service;

import com.aim.entity.OtpToken;
import com.aim.entity.User;
import com.aim.repository.OtpTokenRepository;
import com.aim.service.email.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 3;
    private static final int RESEND_OTP_SECONDS = 20;

    @Autowired
    private OtpTokenRepository otpTokenRepository;
    @Autowired
    private EmailSenderService emailSenderService;

    public OtpToken generateAndSendOtp(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime now = LocalDateTime.now();
        OtpToken otpToken = new OtpToken();
        otpToken.setUser(user);
        otpToken.setOtp(otp);
        otpToken.setCreatedAt(now);
        otpToken.setExpiresAt(now.plusMinutes(OTP_EXPIRY_MINUTES));
        otpToken.setResendAvailableAt(now.plusSeconds(RESEND_OTP_SECONDS));
        otpToken.setUsed(false);
        otpTokenRepository.save(otpToken);
        sendOtpEmail(user, otp);
        return otpToken;
    }

    public boolean canResendOtp(User user) {
        Optional<OtpToken> latest = otpTokenRepository.findTopByUserOrderByCreatedAtDesc(user);
        return latest.map(token -> LocalDateTime.now().isAfter(token.getResendAvailableAt())).orElse(true);
    }

    public OtpToken resendOtp(User user) {
        if (!canResendOtp(user)) {
            throw new IllegalStateException("Resend not allowed yet");
        }
        return generateAndSendOtp(user);
    }

    public boolean validateOtp(User user, String otp) {
        Optional<OtpToken> tokenOpt = otpTokenRepository.findByUserAndOtpAndUsed(user, otp, false);
        if (tokenOpt.isPresent()) {
            OtpToken token = tokenOpt.get();
            if (LocalDateTime.now().isBefore(token.getExpiresAt())) {
                token.setUsed(true);
                otpTokenRepository.save(token);
                return true;
            }
        }
        return false;
    }

    private void sendOtpEmail(User user, String otp) {
        String subject = "Your Login OTP";
        String message = "Your OTP for login is: " + otp + "\nThis OTP is valid for 3 minutes.";
        emailSenderService.sendEmailToUserWithCC(message, user.getEmail(), subject, null, null, null);
    }
} 