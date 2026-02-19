package com.ryuqq.marketplace.application.externalbrandmapping.validator;

import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingReadManager;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.exception.ExternalBrandMappingDuplicateException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ExternalBrandMapping Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class ExternalBrandMappingValidator {

    private final ExternalBrandMappingReadManager readManager;

    public ExternalBrandMappingValidator(ExternalBrandMappingReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 외부 브랜드 매핑 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 외부 브랜드 매핑 ID
     * @return ExternalBrandMapping 도메인 객체
     * @throws
     *     com.ryuqq.marketplace.domain.externalbrandmapping.exception.ExternalBrandMappingNotFoundException
     *     존재하지 않는 경우
     */
    public ExternalBrandMapping findExistingOrThrow(long id) {
        return readManager.getById(id);
    }

    /**
     * 동일한 (externalSourceId, externalBrandCode) 매핑이 이미 존재하는지 검증.
     *
     * @param externalSourceId 외부 소스 ID
     * @param externalBrandCode 외부 브랜드 코드
     * @throws ExternalBrandMappingDuplicateException 이미 존재하는 경우
     */
    public void validateNotDuplicate(long externalSourceId, String externalBrandCode) {
        List<ExternalBrandMapping> existing =
                readManager.findByExternalSourceIdAndCodes(
                        externalSourceId, List.of(externalBrandCode));
        if (!existing.isEmpty()) {
            throw new ExternalBrandMappingDuplicateException(externalSourceId, externalBrandCode);
        }
    }

    /**
     * 동일한 (externalSourceId, externalBrandCode) 매핑이 이미 존재하는지 벌크 검증. IN절 단일 쿼리로 처리.
     *
     * @param externalSourceId 외부 소스 ID
     * @param externalBrandCodes 외부 브랜드 코드 목록
     * @throws ExternalBrandMappingDuplicateException 이미 존재하는 코드가 있는 경우
     */
    public void validateNotDuplicateBulk(long externalSourceId, List<String> externalBrandCodes) {
        List<ExternalBrandMapping> existing =
                readManager.findByExternalSourceIdAndCodes(externalSourceId, externalBrandCodes);
        if (!existing.isEmpty()) {
            List<String> duplicateCodes =
                    existing.stream().map(ExternalBrandMapping::externalBrandCode).toList();
            throw new ExternalBrandMappingDuplicateException(externalSourceId, duplicateCodes);
        }
    }
}
