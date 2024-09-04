package com.intern.onboarding.infra.security.jwt;

import com.intern.onboarding.domain.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

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

    public String generateAccessToken(Long id, Role role) {
        return generateToken(id, role, Duration.ofHours(accessTokenExpirationHour));
    }

    public String generateRefreshToken(Long id, Role role) {
        return generateToken(id, role, Duration.ofHours(refreshTokenExpirationHour));
    }

    private String generateToken(Long id, Role role, Duration expirationPeriod) {

        Instant now = Instant.now();

        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationPeriod)))
                .subject(id.toString())
                .claim("role", role)
                .signWith(key)
                .compact();
    }

    public Optional<Jws<Claims>> validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return Optional.of(claimsJws);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
