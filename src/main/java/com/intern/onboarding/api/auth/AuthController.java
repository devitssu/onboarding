package com.intern.onboarding.api.auth;

import com.intern.onboarding.api.auth.dto.SignInRequest;
import com.intern.onboarding.api.auth.dto.SignInResponse;
import com.intern.onboarding.api.auth.dto.SignUpRequest;
import com.intern.onboarding.api.auth.dto.SignUpResponse;
import com.intern.onboarding.exception.AccessDenied;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<SignInResponse> login(@RequestBody SignInRequest request, HttpServletResponse response) {

        SignInResponse signInResponse = authService.signIn(request);

        String refreshToken = authService.generateRefreshToken(request);
        Cookie cookie = new Cookie("RefreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(signInResponse);
    }

    @GetMapping("/token")
    public ResponseEntity<SignInResponse> getToken(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("RefreshToken")).findFirst()
                .orElseThrow(AccessDenied::new)
                .getValue();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.getNewAccessToken(refreshToken));
    }
}