package com.ryuqq.marketplace.application.legacyauth.validator;

import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacySellerAuthCompositeReadManager;
import com.ryuqq.marketplace.application.legacyauth.port.out.LegacyPasswordEncoder;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminInvalidPasswordException;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotApprovedException;
import org.springframework.stereotype.Component;

/**
 * 레거시 셀러 인증 검증기.
 *
 * <p>셀러 존재 여부 + 승인 상태 + 비밀번호 일치를 검증합니다.
 */
@Component
public class LegacySellerAuthValidator {

    private final LegacySellerAuthCompositeReadManager sellerAuthReadManager;
    private final LegacyPasswordEncoder passwordEncoder;

    public LegacySellerAuthValidator(
            LegacySellerAuthCompositeReadManager sellerAuthReadManager,
            LegacyPasswordEncoder passwordEncoder) {
        this.sellerAuthReadManager = sellerAuthReadManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 레거시 셀러 인증을 검증하고 인증 정보를 반환합니다.
     *
     * @param email 관리자 이메일
     * @param rawPassword 평문 비밀번호
     * @return 검증된 셀러 인증 정보
     * @throws com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotFoundException 셀러
     *     미발견 시
     * @throws SellerAdminNotApprovedException 미승인 셀러 시
     * @throws SellerAdminInvalidPasswordException 비밀번호 불일치 시
     */
    public LegacySellerAuthResult validateAndGet(String email, String rawPassword) {
        LegacySellerAuthResult authResult = sellerAuthReadManager.getByEmail(email);

        if (!authResult.isApproved()) {
            throw new SellerAdminNotApprovedException(authResult.email());
        }

        if (!passwordEncoder.matches(rawPassword, authResult.passwordHash())) {
            throw new SellerAdminInvalidPasswordException();
        }

        return authResult;
    }
}
