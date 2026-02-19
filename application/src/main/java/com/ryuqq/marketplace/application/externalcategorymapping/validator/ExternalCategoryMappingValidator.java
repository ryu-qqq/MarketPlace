package com.ryuqq.marketplace.application.externalcategorymapping.validator;

import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingReadManager;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.exception.ExternalCategoryMappingDuplicateException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ExternalCategoryMapping Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class ExternalCategoryMappingValidator {

    private final ExternalCategoryMappingReadManager readManager;

    public ExternalCategoryMappingValidator(ExternalCategoryMappingReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 외부 카테고리 매핑 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 외부 카테고리 매핑 ID
     * @return ExternalCategoryMapping 도메인 객체
     * @throws
     *     com.ryuqq.marketplace.domain.externalcategorymapping.exception.ExternalCategoryMappingNotFoundException
     *     존재하지 않는 경우
     */
    public ExternalCategoryMapping findExistingOrThrow(long id) {
        return readManager.getById(id);
    }

    /**
     * 동일한 (externalSourceId, externalCategoryCode) 매핑이 이미 존재하는지 검증.
     *
     * @param externalSourceId 외부 소스 ID
     * @param externalCategoryCode 외부 카테고리 코드
     * @throws ExternalCategoryMappingDuplicateException 이미 존재하는 경우
     */
    public void validateNotDuplicate(long externalSourceId, String externalCategoryCode) {
        List<ExternalCategoryMapping> existing =
                readManager.findByExternalSourceIdAndCodes(
                        externalSourceId, List.of(externalCategoryCode));
        if (!existing.isEmpty()) {
            throw new ExternalCategoryMappingDuplicateException(
                    externalSourceId, externalCategoryCode);
        }
    }

    /**
     * 동일한 (externalSourceId, externalCategoryCode) 매핑이 이미 존재하는지 벌크 검증. IN절 단일 쿼리로 처리.
     *
     * @param externalSourceId 외부 소스 ID
     * @param externalCategoryCodes 외부 카테고리 코드 목록
     * @throws ExternalCategoryMappingDuplicateException 이미 존재하는 코드가 있는 경우
     */
    public void validateNotDuplicateBulk(
            long externalSourceId, List<String> externalCategoryCodes) {
        List<ExternalCategoryMapping> existing =
                readManager.findByExternalSourceIdAndCodes(externalSourceId, externalCategoryCodes);
        if (!existing.isEmpty()) {
            List<String> duplicateCodes =
                    existing.stream().map(ExternalCategoryMapping::externalCategoryCode).toList();
            throw new ExternalCategoryMappingDuplicateException(externalSourceId, duplicateCodes);
        }
    }
}
