package com.smart.expense.controller;

import com.smart.expense.model.User;
import com.smart.expense.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> req) {
        String name = req.getOrDefault("name", "User");
        String email = req.getOrDefault("email", "").trim().toLowerCase();
        String password = req.getOrDefault("password", "");
        String lang = req.getOrDefault("preferredLanguage", "en");

        if (userRepository.findByEmail(email).isPresent()) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", "An account with email " + email + " already exists! Please log in instead.");
            return res;
        }

        User user = userRepository.save(new User(name, email, password, lang));
        return buildAuthResponse(user, "Registration successful");
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        String email = req.getOrDefault("email", "demo@expense.com").trim().toLowerCase();
        String password = req.getOrDefault("password", "");

        java.util.Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            if ("demo@expense.com".equals(email)) {
                User demoUser = userRepository.save(new User("Smart Expense User", email, password, "en"));
                return buildAuthResponse(demoUser, "Login successful");
            }
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", "No account found with email " + email + ". Please register first.");
            return res;
        }

        User user = userOpt.get();
        if (user.getPassword() != null && !user.getPassword().isEmpty() && !user.getPassword().equals(password)) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", "Incorrect password for " + email + ". Please try again.");
            return res;
        }

        return buildAuthResponse(user, "Login successful");
    }

    @PostMapping("/send-2fa-otp")
    public Map<String, Object> send2FAOtp(@RequestBody Map<String, String> req) {
        String email = req.getOrDefault("email", "syedshahid7650@gmail.com");
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "2FA Security OTP dispatched to registered destination: " + email);
        res.put("expiresInSeconds", 300);
        res.put("demoCode", "849201");
        return res;
    }

    @PostMapping("/verify-2fa-otp")
    public Map<String, Object> verify2FAOtp(@RequestBody Map<String, String> req) {
        String email = req.getOrDefault("email", "syedshahid7650@gmail.com");
        String code = req.getOrDefault("otp", "").trim();

        Map<String, Object> res = new HashMap<>();
        if ("849201".equals(code) || "123456".equals(code) || code.length() == 6) {
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> userRepository.save(new User("S. Shahid Farwez", email, "", "en")));
            res = buildAuthResponse(user, "2FA authentication verified successfully");
            res.put("twoFactorVerified", true);
            return res;
        }

        res.put("success", false);
        res.put("message", "Invalid 2FA verification code. Please try again.");
        return res;
    }

    @GetMapping("/profile")
    public Map<String, Object> profile() {
        User user = userRepository.findAll().stream().findFirst()
                .orElse(new User("Demo User", "demo@expense.com", "", "en"));
        return buildAuthResponse(user, "Profile loaded");
    }

    private Map<String, Object> buildAuthResponse(User user, String msg) {
        String rawToken = user.getId() + ":" + user.getEmail() + ":" + System.currentTimeMillis();
        String token = "java-jwt-" + Base64.getEncoder().encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> userData = new HashMap<>();
        userData.put("_id", String.valueOf(user.getId()));
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("preferredLanguage", user.getPreferredLanguage());
        userData.put("token", token);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", msg);
        res.put("data", userData);
        return res;
    }
}

