package com.ryuqq.marketplace.domain.inboundbrandmapping.vo;

/**
 * InboundBrandMapping 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record InboundBrandMappingUpdateData(
        String externalBrandName, long internalBrandId, InboundBrandMappingStatus status) {

    public static InboundBrandMappingUpdateData of(
            String externalBrandName, long internalBrandId, InboundBrandMappingStatus status) {
        return new InboundBrandMappingUpdateData(externalBrandName, internalBrandId, status);
    }
}
