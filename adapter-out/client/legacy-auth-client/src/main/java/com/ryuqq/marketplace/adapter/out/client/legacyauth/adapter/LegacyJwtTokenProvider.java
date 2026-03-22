package com.ryuqq.marketplace.adapter.out.client.legacyauth.adapter;

import com.ryuqq.marketplace.adapter.out.client.legacyauth.config.LegacyJwtProperties;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacyTokenResult;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 레거시 JWT 토큰 Provider.
 *
 * <p>setof-commerce와 동일한 HS256 서명 + 동일한 secret으로 토큰을 발급/검증합니다.
 */
@Component
@EnableConfigurationProperties(LegacyJwtProperties.class)
public class LegacyJwtTokenProvider implements LegacyTokenClient {

    private static final String ROLE_CLAIM = "role";
    private static final String SELLER_ID_CLAIM = "sellerId";

    private final LegacyJwtProperties properties;
    private Key key;

    public LegacyJwtTokenProvider(LegacyJwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    @Override
    public LegacyTokenResult generateToken(String email, long sellerId, String roleType) {
        Date now = new Date();

        String accessToken = buildToken(email, sellerId, roleType, now, properties.getAccessTokenExpireTime());
        String refreshToken = buildToken(email, sellerId, roleType, now, properties.getRefreshTokenExpireTime());

        long refreshExpiresInSeconds = properties.getRefreshTokenExpireTime() / 1000;

        return new LegacyTokenResult(accessToken, refreshToken, email, refreshExpiresInSeconds);
    }

    @Override
    public String extractSubject(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    @Override
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isExpired(String token) {
        try {
            parseClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long extractSellerId(String token) {
        Claims claims = parseClaimsAllowExpired(token);
        Number sellerId = claims.get(SELLER_ID_CLAIM, Number.class);
        return sellerId != null ? sellerId.longValue() : 0L;
    }

    @Override
    public String extractRole(String token) {
        Claims claims = parseClaimsAllowExpired(token);
        return claims.get(ROLE_CLAIM, String.class);
    }

    private String buildToken(String email, long sellerId, String roleType, Date now, long expireTimeMs) {
        Date expiry = new Date(now.getTime() + expireTimeMs);
        return Jwts.builder()
                .setSubject(email)
                .claim(ROLE_CLAIM, roleType)
                .claim(SELLER_ID_CLAIM, sellerId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims parseClaimsAllowExpired(String token) {
        try {
            return parseClaims(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
