package com.ryuqq.marketplace.domain.inboundbrandmapping.exception;

/** 외부 브랜드 매핑을 찾을 수 없는 경우 예외. */
public class InboundBrandMappingNotFoundException extends InboundBrandMappingException {

    private static final InboundBrandMappingErrorCode ERROR_CODE =
            InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_NOT_FOUND;

    public InboundBrandMappingNotFoundException() {
        super(ERROR_CODE);
    }

    public InboundBrandMappingNotFoundException(Long id) {
        super(ERROR_CODE, String.format("ID가 %d인 외부 브랜드 매핑을 찾을 수 없습니다", id));
    }

    public InboundBrandMappingNotFoundException(Long inboundSourceId, String externalBrandCode) {
        super(
                ERROR_CODE,
                String.format(
                        "외부 소스 ID %d, 브랜드 코드 %s인 매핑을 찾을 수 없습니다",
                        inboundSourceId, externalBrandCode));
    }
}
