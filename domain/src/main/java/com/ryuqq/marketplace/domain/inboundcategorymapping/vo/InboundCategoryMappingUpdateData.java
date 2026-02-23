package com.ryuqq.marketplace.domain.inboundcategorymapping.vo;

/**
 * InboundCategoryMapping 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record InboundCategoryMappingUpdateData(
        String externalCategoryName, long internalCategoryId, InboundCategoryMappingStatus status) {

    public static InboundCategoryMappingUpdateData of(
            String externalCategoryName,
            long internalCategoryId,
            InboundCategoryMappingStatus status) {
        return new InboundCategoryMappingUpdateData(
                externalCategoryName, internalCategoryId, status);
    }
}
