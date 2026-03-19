package com.ryuqq.marketplace.application.legacyauth.port.out;

import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacySellerAuthResult;
import java.util.Optional;

/**
 * 레거시 셀러 인증 정보 복합 조회 포트.
 *
 * <p>luxurydb의 administrators + admin_auth_group + auth_group + seller 4개 테이블을 조인하여 인증에 필요한 셀러 정보를
 * 한 번에 조회합니다.
 */
public interface LegacySellerAuthCompositeQueryPort {

    /**
     * 이메일로 인증용 셀러 정보 조회.
     *
     * @param email 관리자 이메일
     * @return 셀러 인증 정보 Optional
     */
    Optional<LegacySellerAuthResult> findByEmail(String email);
}
