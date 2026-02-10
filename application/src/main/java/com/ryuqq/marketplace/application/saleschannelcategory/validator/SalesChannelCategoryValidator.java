package com.ryuqq.marketplace.application.saleschannelcategory.validator;

import com.ryuqq.marketplace.application.saleschannelcategory.manager.SalesChannelCategoryReadManager;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryCodeDuplicateException;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryNotFoundException;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import org.springframework.stereotype.Component;

/**
 * SalesChannelCategory Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class SalesChannelCategoryValidator {

    private final SalesChannelCategoryReadManager readManager;

    public SalesChannelCategoryValidator(SalesChannelCategoryReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 외부 채널 카테고리 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 외부 채널 카테고리 ID
     * @return SalesChannelCategory 도메인 객체
     * @throws SalesChannelCategoryNotFoundException 존재하지 않는 경우
     */
    public SalesChannelCategory findExistingOrThrow(SalesChannelCategoryId id) {
        return readManager.getById(id);
    }

    /**
     * 외부 카테고리 코드 중복 여부 검증.
     *
     * @param salesChannelId 판매채널 ID
     * @param externalCategoryCode 외부 카테고리 코드
     * @throws SalesChannelCategoryCodeDuplicateException 이미 존재하는 경우
     */
    public void validateExternalCodeNotDuplicate(Long salesChannelId, String externalCategoryCode) {
        if (readManager.existsBySalesChannelIdAndExternalCode(
                salesChannelId, externalCategoryCode)) {
            throw new SalesChannelCategoryCodeDuplicateException(externalCategoryCode);
        }
    }
}
