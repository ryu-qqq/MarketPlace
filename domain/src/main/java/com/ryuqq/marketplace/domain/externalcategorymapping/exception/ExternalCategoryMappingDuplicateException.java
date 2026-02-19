package com.ryuqq.marketplace.domain.externalcategorymapping.exception;

import java.util.List;

/** 외부 카테고리 매핑이 이미 존재하는 경우 예외. */
public class ExternalCategoryMappingDuplicateException extends ExternalCategoryMappingException {

    private static final ExternalCategoryMappingErrorCode ERROR_CODE =
            ExternalCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_DUPLICATE;

    public ExternalCategoryMappingDuplicateException(
            Long externalSourceId, String externalCategoryCode) {
        super(
                ERROR_CODE,
                String.format(
                        "외부 소스 ID %d, 카테고리 코드 '%s'인 매핑이 이미 존재합니다",
                        externalSourceId, externalCategoryCode));
    }

    public ExternalCategoryMappingDuplicateException(
            Long externalSourceId, List<String> duplicateCodes) {
        super(
                ERROR_CODE,
                String.format(
                        "외부 소스 ID %d에 이미 존재하는 카테고리 코드: %s", externalSourceId, duplicateCodes));
    }
}
