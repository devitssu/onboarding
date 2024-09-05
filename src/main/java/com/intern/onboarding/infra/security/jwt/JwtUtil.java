package com.intern.onboarding.infra.security.jwt;

import com.intern.onboarding.domain.user.Role;
import com.intern.onboarding.exception.JwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final String issuer;
    private final Key key;
    private final Integer accessTokenExpirationHour;
    private final Integer refreshTokenExpirationHour;

    public JwtUtil(
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.accessTokenExpirationHour}") Integer accessTokenExpirationHour,
            @Value("${jwt.refreshTokenExpirationHour}") Integer refreshTokenExpirationHour
    ) {
        this.issuer = issuer;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpirationHour = accessTokenExpirationHour;
        this.refreshTokenExpirationHour = refreshTokenExpirationHour;
    }

    public String generateAccessToken(Instant now, Long id, Role role) {
        return generateToken(now, TokenType.ACCESS_TOKEN, id, role, Duration.ofHours(accessTokenExpirationHour));
    }

    public String generateRefreshToken(Instant now, Long id, Role role) {
        return generateToken(now, TokenType.REFRESH_TOKEN, id, role, Duration.ofHours(refreshTokenExpirationHour));
    }

    private String generateToken(Instant now, TokenType type, Long id, Role role, Duration expirationPeriod) {

        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationPeriod)))
                .subject(id.toString())
                .claim("role", role)
                .claim("type", type)
                .signWith(key)
                .compact();
    }

    public Jws<Claims> getClaims(String token) throws RuntimeException {
        try {
            return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
        } catch (ExpiredJwtException ee) {
            throw new JwtException("만료된 토큰입니다.");
        } catch (SignatureException se) {
            throw new JwtException("잘못된 시그니처입니다.");
        } catch (MalformedJwtException me) {
            throw new JwtException("손상된 토큰입니다.");
        } catch (UnsupportedJwtException ue) {
            throw new JwtException("지원하지 않는 토큰입니다.");
        } catch (Exception e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
    }
}
