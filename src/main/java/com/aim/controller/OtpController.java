package com.aim.controller;

import com.aim.entity.User;
import com.aim.service.OtpService;
import com.aim.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/otp")
public class OtpController {
    @Autowired
    private OtpService otpService;
    @Autowired
    private UserService userService;

    @GetMapping("/entry")
    public String otpEntryPage(HttpSession session, ModelMap model) {
        String email = (String) session.getAttribute("otp_email");
        if (email == null) {
            return "redirect:/login";
        }
        session.setAttribute("otp_verified", false);
        model.addAttribute("email", email);
        return "otp/otp-entry";
    }

    @PostMapping("/validate")
    public String validateOtp(@RequestParam("otp") String otp,
                              HttpSession session, ModelMap model) {
        String email = (String) session.getAttribute("otp_email");
        if (email == null) {
            return "redirect:/login";
        }
        User user = userService.findUserByEmail(email);
        if (otpService.validateOtp(user, otp)) {
            // OTP valid, complete login
            session.setAttribute("user", user);
            session.removeAttribute("otp_email");
            session.setAttribute("otp_verified", true);
            return "redirect:/default";
        } else {
            model.addAttribute("error", "Invalid or expired OTP");
            model.addAttribute("email", email);
            return "otp/otp-entry";
        }
    }

    @PostMapping("/resend")
    @ResponseBody
    public String resendOtp(HttpSession session) {
        String email = (String) session.getAttribute("otp_email");
        if (email == null) {
            return "NO_SESSION";
        }
        User user = userService.findUserByEmail(email);
        if (otpService.canResendOtp(user)) {
            otpService.resendOtp(user);
            return "OK";
        } else {
            return "WAIT";
        }
    }
} 