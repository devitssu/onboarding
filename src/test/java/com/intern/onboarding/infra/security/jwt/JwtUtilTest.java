package com.intern.onboarding.infra.security.jwt;

import com.intern.onboarding.domain.user.Role;
import com.intern.onboarding.exception.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {
    private JwtUtil jwtUtil;
    private final String ISSUER = "test";
    private final int ACCESS_TOKEN_EXPIRATION_HOUR = 1;
    private final int REFRESH_TOKEN_EXPIRATION_HOUR = 12;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(
                ISSUER,
                "fdbe0892caebc79f7bdf7d274afdb7645d1v35j01c9a6750d9c51af123456789",
                ACCESS_TOKEN_EXPIRATION_HOUR,
                REFRESH_TOKEN_EXPIRATION_HOUR
        );
    }

    @DisplayName("생성한 토큰에 담긴 type 값이 맞는 타입이어야 한다.")
    @Test
    void createToken() {

        String accessToken = jwtUtil.generateAccessToken(Instant.now(), 1L, Role.USER);
        String refreshToken = jwtUtil.generateRefreshToken(Instant.now(), 1L, Role.USER);

        String accessTokenType = jwtUtil.getClaims(accessToken).getPayload().get("type", String.class);
        String refreshTokenType = jwtUtil.getClaims(refreshToken).getPayload().get("type", String.class);

        assertEquals(TokenType.ACCESS_TOKEN.name(), accessTokenType);
        assertEquals(TokenType.REFRESH_TOKEN.name(), refreshTokenType);
    }

    @DisplayName("토큰의 정보가 생성할때 입력한 값과 일치해야 한다.")
    @Test
    void validateToken() {
        String accessToken = jwtUtil.generateAccessToken(Instant.now(), 1L, Role.USER);
        Jws<Claims> claimsJws = jwtUtil.getClaims(accessToken);

        String issuer = claimsJws.getPayload().getIssuer();
        String id = claimsJws.getPayload().getSubject();
        String role = claimsJws.getPayload().get("role", String.class);


        assertEquals(ISSUER, issuer);
        assertEquals(1L, Long.valueOf(id));
        assertEquals(Role.USER.name(), role);
    }

    @DisplayName("토큰 기간이 만료되면 JwtException이 발생한다.")
    @Test
    void expireToken() {
        Instant now = Instant.now().minus(Duration.ofHours(ACCESS_TOKEN_EXPIRATION_HOUR + 1));

        String accessToken = jwtUtil.generateAccessToken(now, 1L, Role.USER);

        JwtException e = assertThrows(JwtException.class, () -> jwtUtil.getClaims(accessToken));
        assertEquals(e.getMessage(), "만료된 토큰입니다.");
    }

    @DisplayName("다른 시그니처를 가진 토큰이면 JwtException이 발생한다.")
    @Test
    void otherSignatureToken() {
        String otherSecret = "fdbe0892caa6750d9c51af12ebc79f7bdf7d274afdb7645d1v35j01c93456789";
        JwtUtil otherJwtUtil = new JwtUtil(ISSUER, otherSecret, ACCESS_TOKEN_EXPIRATION_HOUR, REFRESH_TOKEN_EXPIRATION_HOUR);

        String invalidAccessToken = otherJwtUtil.generateAccessToken(Instant.now(), 1L, Role.USER);

        JwtException e = assertThrows(JwtException.class, () -> jwtUtil.getClaims(invalidAccessToken));
        assertEquals(e.getMessage(), "잘못된 시그니처입니다.");
    }

    @DisplayName("손상된 토큰이면 JwtException이 발생한다.")
    @Test
    void invalidToken() {
        String invalidAccessToken = "this is not a JWT token";

        JwtException e = assertThrows(JwtException.class, () -> jwtUtil.getClaims(invalidAccessToken));
        assertEquals(e.getMessage(), "손상된 토큰입니다.");
    }
}