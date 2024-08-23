package dac2dac.doctect.user.controller;

import dac2dac.doctect.user.dto.CustomUserDetails;
import dac2dac.doctect.user.dto.UserDTO;
import dac2dac.doctect.user.entity.User;
import dac2dac.doctect.user.jwt.JWTUtil;
import dac2dac.doctect.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "유저", description = "회원가입")
@RestController
@CrossOrigin(origins = "http://192.168.219.136:8081") // Expo 개발 서버 주소
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PostMapping("/api/v1/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(
                    userDTO.getUsername(),
                    userDTO.getEmail(),
                    userDTO.getPassword(),
                    userDTO.getPhoneNumber(),
                    userDTO.getCode(),
                    userDTO.getSocialType()
            );
            return ResponseEntity.ok("Registration successful!!");
        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            String token = userService.authenticateAndGenerateToken(userDTO.getUsername(), userDTO.getPassword());
            response.addHeader("Authorization", "Bearer " + token);
            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Login failed: " + e.getMessage());
        }
    }


    @GetMapping("/api/v1/login/test")
    public String getProtectedResource() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String name = authentication.getName();
            return "Get " + name;
        } else {
            return "No authenticated user";
        }
    }



}
