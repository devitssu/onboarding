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
import com.intern.onboarding.infra.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        return new SignInResponse(jwtUtil.generateAccessToken(user.getId(), user.getRole()));
    }

    @Override
    public String generateRefreshToken(SignInRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidSignInException::new);

        return jwtUtil.generateRefreshToken(user.getId(), user.getRole());
    }
}
