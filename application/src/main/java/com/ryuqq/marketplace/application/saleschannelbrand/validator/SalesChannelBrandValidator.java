package com.ryuqq.marketplace.application.saleschannelbrand.validator;

import com.ryuqq.marketplace.application.saleschannelbrand.manager.SalesChannelBrandReadManager;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.exception.SalesChannelBrandCodeDuplicateException;
import com.ryuqq.marketplace.domain.saleschannelbrand.exception.SalesChannelBrandNotFoundException;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import org.springframework.stereotype.Component;

/**
 * SalesChannelBrand Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class SalesChannelBrandValidator {

    private final SalesChannelBrandReadManager readManager;

    public SalesChannelBrandValidator(SalesChannelBrandReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 외부채널 브랜드 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 외부채널 브랜드 ID
     * @return SalesChannelBrand 도메인 객체
     * @throws SalesChannelBrandNotFoundException 존재하지 않는 경우
     */
    public SalesChannelBrand findExistingOrThrow(SalesChannelBrandId id) {
        return readManager.getById(id);
    }

    /**
     * 외부채널 브랜드 코드 중복 여부 검증.
     *
     * @param salesChannelId 판매채널 ID
     * @param externalBrandCode 외부 브랜드 코드
     * @throws SalesChannelBrandCodeDuplicateException 이미 존재하는 경우
     */
    public void validateExternalCodeNotDuplicate(Long salesChannelId, String externalBrandCode) {
        if (readManager.existsBySalesChannelIdAndExternalCode(salesChannelId, externalBrandCode)) {
            throw new SalesChannelBrandCodeDuplicateException(externalBrandCode);
        }
    }
}
