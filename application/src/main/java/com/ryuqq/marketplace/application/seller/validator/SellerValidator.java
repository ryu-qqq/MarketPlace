package com.ryuqq.marketplace.application.seller.validator;

import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.exception.SellerNameDuplicateException;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

/**
 * Seller Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class SellerValidator {

    private final SellerReadManager readManager;

    public SellerValidator(SellerReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 셀러 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 셀러 ID
     * @return Seller 도메인 객체
     * @throws SellerNotFoundException 존재하지 않는 경우
     */
    public Seller findExistingOrThrow(SellerId id) {
        return readManager.getById(id);
    }

    /**
     * 셀러명 중복 여부 검증. (등록 시 사용)
     *
     * @param sellerName 셀러명
     * @throws SellerNameDuplicateException 이미 존재하는 경우
     */
    public void validateSellerNameNotDuplicate(String sellerName) {
        if (readManager.existsBySellerName(sellerName)) {
            throw new SellerNameDuplicateException(sellerName);
        }
    }

    /**
     * 셀러명 중복 여부 검증 (자기 자신 제외). (수정 시 사용)
     *
     * @param sellerName 셀러명
     * @param excludeId 제외할 셀러 ID
     * @throws SellerNameDuplicateException 이미 존재하는 경우
     */
    public void validateSellerNameNotDuplicateExcluding(String sellerName, SellerId excludeId) {
        if (readManager.existsBySellerNameExcluding(sellerName, excludeId)) {
            throw new SellerNameDuplicateException(sellerName);
        }
    }
}
