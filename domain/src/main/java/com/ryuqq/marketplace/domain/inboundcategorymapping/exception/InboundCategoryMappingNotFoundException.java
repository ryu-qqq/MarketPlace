package com.ryuqq.marketplace.domain.inboundcategorymapping.exception;

/** 외부 카테고리 매핑을 찾을 수 없는 경우 예외. */
public class InboundCategoryMappingNotFoundException extends InboundCategoryMappingException {

    private static final InboundCategoryMappingErrorCode ERROR_CODE =
            InboundCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_NOT_FOUND;

    public InboundCategoryMappingNotFoundException() {
        super(ERROR_CODE);
    }

    public InboundCategoryMappingNotFoundException(Long id) {
        super(ERROR_CODE, String.format("ID가 %d인 외부 카테고리 매핑을 찾을 수 없습니다", id));
    }

    public InboundCategoryMappingNotFoundException(
            Long inboundSourceId, String externalCategoryCode) {
        super(
                ERROR_CODE,
                String.format(
                        "외부 소스 ID %d, 카테고리 코드 %s인 매핑을 찾을 수 없습니다",
                        inboundSourceId, externalCategoryCode));
    }
}
