package com.ryuqq.marketplace.domain.externalcategorymapping.exception;

/** 외부 카테고리 매핑을 찾을 수 없는 경우 예외. */
public class ExternalCategoryMappingNotFoundException extends ExternalCategoryMappingException {

    private static final ExternalCategoryMappingErrorCode ERROR_CODE =
            ExternalCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_NOT_FOUND;

    public ExternalCategoryMappingNotFoundException() {
        super(ERROR_CODE);
    }

    public ExternalCategoryMappingNotFoundException(Long id) {
        super(ERROR_CODE, String.format("ID가 %d인 외부 카테고리 매핑을 찾을 수 없습니다", id));
    }

    public ExternalCategoryMappingNotFoundException(
            Long externalSourceId, String externalCategoryCode) {
        super(
                ERROR_CODE,
                String.format(
                        "외부 소스 ID %d, 카테고리 코드 %s인 매핑을 찾을 수 없습니다",
                        externalSourceId, externalCategoryCode));
    }
}
