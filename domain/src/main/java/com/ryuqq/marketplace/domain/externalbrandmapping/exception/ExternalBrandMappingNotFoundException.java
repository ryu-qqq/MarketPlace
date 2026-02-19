package com.ryuqq.marketplace.domain.externalbrandmapping.exception;

/** 외부 브랜드 매핑을 찾을 수 없는 경우 예외. */
public class ExternalBrandMappingNotFoundException extends ExternalBrandMappingException {

    private static final ExternalBrandMappingErrorCode ERROR_CODE =
            ExternalBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_NOT_FOUND;

    public ExternalBrandMappingNotFoundException() {
        super(ERROR_CODE);
    }

    public ExternalBrandMappingNotFoundException(Long id) {
        super(ERROR_CODE, String.format("ID가 %d인 외부 브랜드 매핑을 찾을 수 없습니다", id));
    }

    public ExternalBrandMappingNotFoundException(Long externalSourceId, String externalBrandCode) {
        super(
                ERROR_CODE,
                String.format(
                        "외부 소스 ID %d, 브랜드 코드 %s인 매핑을 찾을 수 없습니다",
                        externalSourceId, externalBrandCode));
    }
}
