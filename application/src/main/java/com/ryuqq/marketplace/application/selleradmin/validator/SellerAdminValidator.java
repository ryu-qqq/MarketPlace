package com.ryuqq.marketplace.application.selleradmin.validator;

import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

/**
 * SellerAdmin Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 void 또는 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminValidator {

    private final SellerReadManager sellerReadManager;

    public SellerAdminValidator(SellerReadManager sellerReadManager) {
        this.sellerReadManager = sellerReadManager;
    }

    /**
     * 셀러 존재 여부 검증.
     *
     * <p>셀러가 존재하지 않으면 SellerNotFoundException이 발생합니다.
     *
     * @param sellerId 셀러 ID
     * @throws com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException 셀러가 존재하지 않는 경우
     */
    public void validateSellerExists(Long sellerId) {
        sellerReadManager.getById(SellerId.of(sellerId));
    }
}
