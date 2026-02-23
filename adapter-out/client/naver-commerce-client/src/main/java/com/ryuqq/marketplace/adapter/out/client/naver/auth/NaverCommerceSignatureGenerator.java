package com.ryuqq.marketplace.adapter.out.client.naver.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * Naver Commerce BCrypt 서명 생성기.
 *
 * <p>OAuth2 토큰 발급 시 필요한 client_secret_sign을 생성합니다. 서명 형식: Base64(BCrypt.hashpw(clientId + "_" +
 * timestamp, Base64Decode(clientSecret)))
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class NaverCommerceSignatureGenerator {

    /**
     * BCrypt 기반 서명을 생성합니다.
     *
     * @param clientId 클라이언트 ID
     * @param clientSecret Base64 인코딩된 BCrypt salt (클라이언트 시크릿)
     * @param timestamp 밀리초 타임스탬프
     * @return Base64 인코딩된 서명 문자열
     */
    public String generateSignature(String clientId, String clientSecret, long timestamp) {
        String message = clientId + "_" + timestamp;
        String bcryptSalt =
                new String(Base64.getDecoder().decode(clientSecret), StandardCharsets.UTF_8);
        String hashed = BCrypt.hashpw(message, bcryptSalt);
        return Base64.getEncoder().encodeToString(hashed.getBytes(StandardCharsets.UTF_8));
    }
}
