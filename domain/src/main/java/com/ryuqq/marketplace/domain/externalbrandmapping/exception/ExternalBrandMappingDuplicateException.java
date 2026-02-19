package com.ryuqq.marketplace.domain.externalbrandmapping.exception;

import java.util.List;

/** 외부 브랜드 매핑이 이미 존재하는 경우 예외. */
public class ExternalBrandMappingDuplicateException extends ExternalBrandMappingException {

    private static final ExternalBrandMappingErrorCode ERROR_CODE =
            ExternalBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_DUPLICATE;

    public ExternalBrandMappingDuplicateException(Long externalSourceId, String externalBrandCode) {
        super(
                ERROR_CODE,
                String.format(
                        "외부 소스 ID %d, 브랜드 코드 '%s'인 매핑이 이미 존재합니다",
                        externalSourceId, externalBrandCode));
    }

    public ExternalBrandMappingDuplicateException(
            Long externalSourceId, List<String> duplicateCodes) {
        super(
                ERROR_CODE,
                String.format("외부 소스 ID %d에 이미 존재하는 브랜드 코드: %s", externalSourceId, duplicateCodes));
    }
}
