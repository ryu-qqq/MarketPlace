package com.ryuqq.marketplace.application.inboundcategorymapping.validator;

import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingReadManager;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.exception.InboundCategoryMappingDuplicateException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * InboundCategoryMapping Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class InboundCategoryMappingValidator {

    private final InboundCategoryMappingReadManager readManager;

    public InboundCategoryMappingValidator(InboundCategoryMappingReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 외부 카테고리 매핑 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 외부 카테고리 매핑 ID
     * @return InboundCategoryMapping 도메인 객체
     * @throws
     *     com.ryuqq.marketplace.domain.inboundcategorymapping.exception.InboundCategoryMappingNotFoundException
     *     존재하지 않는 경우
     */
    public InboundCategoryMapping findExistingOrThrow(long id) {
        return readManager.getById(id);
    }

    /**
     * 동일한 (inboundSourceId, externalCategoryCode) 매핑이 이미 존재하는지 검증.
     *
     * @param inboundSourceId 외부 소스 ID
     * @param externalCategoryCode 외부 카테고리 코드
     * @throws InboundCategoryMappingDuplicateException 이미 존재하는 경우
     */
    public void validateNotDuplicate(long inboundSourceId, String externalCategoryCode) {
        List<InboundCategoryMapping> existing =
                readManager.findByInboundSourceIdAndCodes(
                        inboundSourceId, List.of(externalCategoryCode));
        if (!existing.isEmpty()) {
            throw new InboundCategoryMappingDuplicateException(
                    inboundSourceId, externalCategoryCode);
        }
    }

    /**
     * 동일한 (inboundSourceId, externalCategoryCode) 매핑이 이미 존재하는지 벌크 검증. IN절 단일 쿼리로 처리.
     *
     * @param inboundSourceId 외부 소스 ID
     * @param externalCategoryCodes 외부 카테고리 코드 목록
     * @throws InboundCategoryMappingDuplicateException 이미 존재하는 코드가 있는 경우
     */
    public void validateNotDuplicateBulk(long inboundSourceId, List<String> externalCategoryCodes) {
        List<InboundCategoryMapping> existing =
                readManager.findByInboundSourceIdAndCodes(inboundSourceId, externalCategoryCodes);
        if (!existing.isEmpty()) {
            List<String> duplicateCodes =
                    existing.stream().map(InboundCategoryMapping::externalCategoryCode).toList();
            throw new InboundCategoryMappingDuplicateException(inboundSourceId, duplicateCodes);
        }
    }
}
