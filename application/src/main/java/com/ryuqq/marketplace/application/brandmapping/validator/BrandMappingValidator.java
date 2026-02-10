package com.ryuqq.marketplace.application.brandmapping.validator;

import com.ryuqq.marketplace.application.brandmapping.manager.BrandMappingReadManager;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandmapping.exception.BrandMappingDuplicateException;
import com.ryuqq.marketplace.domain.brandmapping.exception.BrandMappingNotFoundException;
import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import org.springframework.stereotype.Component;

/**
 * BrandMapping Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class BrandMappingValidator {

    private final BrandMappingReadManager readManager;

    public BrandMappingValidator(BrandMappingReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 브랜드 매핑 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 브랜드 매핑 ID
     * @return BrandMapping 도메인 객체
     * @throws BrandMappingNotFoundException 존재하지 않는 경우
     */
    public BrandMapping findExistingOrThrow(BrandMappingId id) {
        return readManager.getById(id);
    }

    /**
     * 해당 외부 브랜드에 대한 매핑 중복 여부 검증.
     *
     * @param salesChannelBrandId 외부 채널 브랜드 ID
     * @throws BrandMappingDuplicateException 이미 매핑이 존재하는 경우
     */
    public void validateNotDuplicate(Long salesChannelBrandId) {
        if (readManager.existsBySalesChannelBrandId(salesChannelBrandId)) {
            throw new BrandMappingDuplicateException(salesChannelBrandId);
        }
    }
}
