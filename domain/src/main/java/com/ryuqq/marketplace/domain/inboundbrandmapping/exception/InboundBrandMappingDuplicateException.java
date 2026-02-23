package com.ryuqq.marketplace.domain.inboundbrandmapping.exception;

import java.util.List;

/** 외부 브랜드 매핑이 이미 존재하는 경우 예외. */
public class InboundBrandMappingDuplicateException extends InboundBrandMappingException {

    private static final InboundBrandMappingErrorCode ERROR_CODE =
            InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_DUPLICATE;

    public InboundBrandMappingDuplicateException(Long inboundSourceId, String externalBrandCode) {
        super(
                ERROR_CODE,
                String.format(
                        "외부 소스 ID %d, 브랜드 코드 '%s'인 매핑이 이미 존재합니다",
                        inboundSourceId, externalBrandCode));
    }

    public InboundBrandMappingDuplicateException(
            Long inboundSourceId, List<String> duplicateCodes) {
        super(
                ERROR_CODE,
                String.format("외부 소스 ID %d에 이미 존재하는 브랜드 코드: %s", inboundSourceId, duplicateCodes));
    }
}
