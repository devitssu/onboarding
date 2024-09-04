package com.intern.onboarding.api.auth;

import com.intern.onboarding.api.auth.dto.SignInRequest;
import com.intern.onboarding.api.auth.dto.SignInResponse;
import com.intern.onboarding.api.auth.dto.SignUpRequest;
import com.intern.onboarding.api.auth.dto.SignUpResponse;

public interface AuthService {
    SignUpResponse signUp(SignUpRequest request);

    SignInResponse signIn(SignInRequest request);

    String generateRefreshToken(SignInRequest request);
}
