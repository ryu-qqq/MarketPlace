package com.ryuqq.marketplace.adapter.out.persistence.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 민감 정보 컬럼 암호화 AttributeConverter.
 *
 * <p>AES-256-GCM 알고리즘을 사용하여 DB 컬럼의 값을 암/복호화합니다. API 키, Secret, Access Token 등 민감 정보를 평문으로 저장하지 않기
 * 위해 사용합니다.
 *
 * <p>암호화 키는 {@code marketplace.encryption.secret-key} 프로퍼티로 설정합니다. Base64 인코딩된 32바이트 (256비트) 키를
 * 사용해야 합니다.
 *
 * <p><strong>주의</strong>: autoApply = false이므로 필드별 {@code @Convert} 어노테이션이 필요합니다.
 */
@Component
@Converter
public class EncryptingAttributeConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;

    private final byte[] secretKey;

    public EncryptingAttributeConverter(
            @Value("${marketplace.encryption.secret-key:}") String secretKeyBase64) {
        if (secretKeyBase64 == null || secretKeyBase64.isBlank()) {
            this.secretKey = null;
        } else {
            this.secretKey = Base64.getDecoder().decode(secretKeyBase64);
        }
    }

    @Override
    public String convertToDatabaseColumn(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        if (secretKey == null) {
            return plainText;
        }
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            java.security.SecureRandom.getInstanceStrong().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, "AES"), parameterSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[GCM_IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, GCM_IV_LENGTH);
            System.arraycopy(encrypted, 0, combined, GCM_IV_LENGTH, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("컬럼 암호화 실패", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        if (secretKey == null) {
            return cipherText;
        }
        try {
            byte[] combined = Base64.getDecoder().decode(cipherText);

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);

            byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, "AES"), parameterSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return cipherText;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("컬럼 복호화 실패", e);
        }
    }
}
