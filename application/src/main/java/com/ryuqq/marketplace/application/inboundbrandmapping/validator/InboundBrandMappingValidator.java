package com.ryuqq.marketplace.application.inboundbrandmapping.validator;

import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingReadManager;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingDuplicateException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * InboundBrandMapping Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class InboundBrandMappingValidator {

    private final InboundBrandMappingReadManager readManager;

    public InboundBrandMappingValidator(InboundBrandMappingReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 외부 브랜드 매핑 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 외부 브랜드 매핑 ID
     * @return InboundBrandMapping 도메인 객체
     * @throws
     *     com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingNotFoundException
     *     존재하지 않는 경우
     */
    public InboundBrandMapping findExistingOrThrow(long id) {
        return readManager.getById(id);
    }

    /**
     * 동일한 (inboundSourceId, externalBrandCode) 매핑이 이미 존재하는지 검증.
     *
     * @param inboundSourceId 외부 소스 ID
     * @param externalBrandCode 외부 브랜드 코드
     * @throws InboundBrandMappingDuplicateException 이미 존재하는 경우
     */
    public void validateNotDuplicate(long inboundSourceId, String externalBrandCode) {
        List<InboundBrandMapping> existing =
                readManager.findByInboundSourceIdAndCodes(
                        inboundSourceId, List.of(externalBrandCode));
        if (!existing.isEmpty()) {
            throw new InboundBrandMappingDuplicateException(inboundSourceId, externalBrandCode);
        }
    }

    /**
     * 동일한 (inboundSourceId, externalBrandCode) 매핑이 이미 존재하는지 벌크 검증. IN절 단일 쿼리로 처리.
     *
     * @param inboundSourceId 외부 소스 ID
     * @param externalBrandCodes 외부 브랜드 코드 목록
     * @throws InboundBrandMappingDuplicateException 이미 존재하는 코드가 있는 경우
     */
    public void validateNotDuplicateBulk(long inboundSourceId, List<String> externalBrandCodes) {
        List<InboundBrandMapping> existing =
                readManager.findByInboundSourceIdAndCodes(inboundSourceId, externalBrandCodes);
        if (!existing.isEmpty()) {
            List<String> duplicateCodes =
                    existing.stream().map(InboundBrandMapping::externalBrandCode).toList();
            throw new InboundBrandMappingDuplicateException(inboundSourceId, duplicateCodes);
        }
    }
}
