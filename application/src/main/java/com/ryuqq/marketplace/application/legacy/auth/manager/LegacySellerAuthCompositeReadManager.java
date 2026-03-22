package com.ryuqq.marketplace.application.legacy.auth.manager;

import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacySellerAuthCompositeQueryPort;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 셀러 인증 정보 복합 조회 Manager.
 *
 * <p>luxurydb 4개 테이블 조인 조회를 캡슐화합니다.
 */
@Component
public class LegacySellerAuthCompositeReadManager {

    private final LegacySellerAuthCompositeQueryPort queryPort;

    public LegacySellerAuthCompositeReadManager(LegacySellerAuthCompositeQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 이메일로 인증용 셀러 정보 조회.
     *
     * @param email 관리자 이메일
     * @return 셀러 인증 정보
     * @throws SellerAdminNotFoundException 셀러 관리자 미발견 시
     */
    @Transactional(readOnly = true)
    public LegacySellerAuthResult getByEmail(String email) {
        return queryPort
                .findByEmail(email)
                .orElseThrow(
                        () ->
                                SellerAdminNotFoundException.withMessage(
                                        "레거시 셀러 인증 정보 미발견: email=" + email));
    }
}
