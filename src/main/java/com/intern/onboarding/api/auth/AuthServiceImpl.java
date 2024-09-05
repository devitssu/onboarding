package com.intern.onboarding.api.auth;

import com.intern.onboarding.api.auth.dto.SignInRequest;
import com.intern.onboarding.api.auth.dto.SignInResponse;
import com.intern.onboarding.api.auth.dto.SignUpRequest;
import com.intern.onboarding.api.auth.dto.SignUpResponse;
import com.intern.onboarding.domain.user.Role;
import com.intern.onboarding.domain.user.User;
import com.intern.onboarding.domain.user.UserRepository;
import com.intern.onboarding.exception.AlreadyExistUsernameException;
import com.intern.onboarding.exception.InvalidSignInException;
import com.intern.onboarding.exception.UnAuthorized;
import com.intern.onboarding.infra.security.jwt.JwtUtil;
import com.intern.onboarding.infra.security.jwt.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(e -> {
            throw new AlreadyExistUsernameException();
        });

        User user = request.toEntity(passwordEncoder);
        return SignUpResponse.from(userRepository.save(user));
    }

    @Override
    public SignInResponse signIn(SignInRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidSignInException::new);

        if (!user.checkPassword(request.password(), passwordEncoder))
            throw new InvalidSignInException();

        return new SignInResponse(jwtUtil.generateAccessToken(Instant.now(), user.getId(), user.getRole()));
    }

    @Override
    public String generateRefreshToken(SignInRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidSignInException::new);

        return jwtUtil.generateRefreshToken(Instant.now(), user.getId(), user.getRole());
    }

    @Override
    public SignInResponse getNewAccessToken(String refreshToken) {
        Jws<Claims> claimsJws = jwtUtil.getClaims(refreshToken);
        if (!TokenType.REFRESH_TOKEN.name().equals(claimsJws.getPayload().get("type"))) throw new UnAuthorized();

        Long id = Long.valueOf(claimsJws.getPayload().getSubject());
        Role role = Role.valueOf(claimsJws.getPayload().get("role", String.class));

        return new SignInResponse(jwtUtil.generateAccessToken(Instant.now(), id, role));
    }
}
