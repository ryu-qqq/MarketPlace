package com.ryuqq.marketplace.domain.externalcategorymapping.vo;

/**
 * ExternalCategoryMapping 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record ExternalCategoryMappingUpdateData(
        String externalCategoryName,
        long internalCategoryId,
        ExternalCategoryMappingStatus status) {

    public static ExternalCategoryMappingUpdateData of(
            String externalCategoryName,
            long internalCategoryId,
            ExternalCategoryMappingStatus status) {
        return new ExternalCategoryMappingUpdateData(
                externalCategoryName, internalCategoryId, status);
    }
}
