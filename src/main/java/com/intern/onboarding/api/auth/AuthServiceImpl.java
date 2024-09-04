package com.intern.onboarding.api.auth;

import com.intern.onboarding.api.auth.dto.SignUpRequest;
import com.intern.onboarding.api.auth.dto.SignUpResponse;
import com.intern.onboarding.domain.user.User;
import com.intern.onboarding.domain.user.UserRepository;
import com.intern.onboarding.exception.AlreadyExistUsernameException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

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
}
