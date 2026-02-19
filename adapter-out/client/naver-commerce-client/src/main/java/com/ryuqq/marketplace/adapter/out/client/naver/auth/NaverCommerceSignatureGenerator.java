package com.ryuqq.marketplace.adapter.out.client.naver.auth;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

/**
 * Naver Commerce HMAC-SHA256 서명 생성기.
 *
 * <p>OAuth2 토큰 발급 시 필요한 client_secret_sign을 생성합니다. 서명 형식: HMAC-SHA256(clientId + "_" + timestamp,
 * clientSecret) → Base64
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class NaverCommerceSignatureGenerator {

    private static final String ALGORITHM = "HmacSHA256";

    /**
     * HMAC-SHA256 서명을 생성합니다.
     *
     * @param clientId 클라이언트 ID
     * @param clientSecret 클라이언트 시크릿
     * @param timestamp 밀리초 타임스탬프
     * @return Base64 인코딩된 서명 문자열
     */
    public String generateSignature(String clientId, String clientSecret, long timestamp) {
        String message = clientId + "_" + timestamp;
        try {
            SecretKeySpec secretKey =
                    new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(
                    "Failed to generate HMAC-SHA256 signature: " + e.getMessage(), e);
        }
    }
}
