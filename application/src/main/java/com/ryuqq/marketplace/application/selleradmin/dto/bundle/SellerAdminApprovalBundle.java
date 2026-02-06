package com.ryuqq.marketplace.application.selleradmin.dto.bundle;

import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import java.time.Instant;

/**
 * 셀러 관리자 승인 Bundle.
 *
 * <p>셀러 관리자 승인 시 필요한 도메인 객체를 묶습니다.
 *
 * @param sellerAdmin 셀러 관리자 도메인 객체
 * @param authOutbox 인증 서버 연동 Outbox
 * @param createdAt 생성 시각
 * @author ryu-qqq
 * @since 1.1.0
 */
public record SellerAdminApprovalBundle(
        SellerAdmin sellerAdmin, SellerAdminAuthOutbox authOutbox, Instant createdAt) {

    /**
     * 셀러 관리자 ID 값을 반환합니다.
     *
     * @return 셀러 관리자 ID
     */
    public String sellerAdminIdValue() {
        return sellerAdmin.idValue();
    }

    /**
     * 셀러 ID 값을 반환합니다.
     *
     * @return 셀러 ID
     */
    public Long sellerIdValue() {
        return sellerAdmin.sellerIdValue();
    }

    /**
     * 로그인 ID 값을 반환합니다.
     *
     * @return 로그인 ID
     */
    public String loginIdValue() {
        return sellerAdmin.loginIdValue();
    }
}
