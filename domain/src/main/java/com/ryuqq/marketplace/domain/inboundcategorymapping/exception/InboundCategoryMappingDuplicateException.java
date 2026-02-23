package com.ryuqq.marketplace.domain.inboundcategorymapping.exception;

import java.util.List;

/** 외부 카테고리 매핑이 이미 존재하는 경우 예외. */
public class InboundCategoryMappingDuplicateException extends InboundCategoryMappingException {

    private static final InboundCategoryMappingErrorCode ERROR_CODE =
            InboundCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_DUPLICATE;

    public InboundCategoryMappingDuplicateException(
            Long inboundSourceId, String externalCategoryCode) {
        super(
                ERROR_CODE,
                String.format(
                        "외부 소스 ID %d, 카테고리 코드 '%s'인 매핑이 이미 존재합니다",
                        inboundSourceId, externalCategoryCode));
    }

    public InboundCategoryMappingDuplicateException(
            Long inboundSourceId, List<String> duplicateCodes) {
        super(
                ERROR_CODE,
                String.format("외부 소스 ID %d에 이미 존재하는 카테고리 코드: %s", inboundSourceId, duplicateCodes));
    }
}
