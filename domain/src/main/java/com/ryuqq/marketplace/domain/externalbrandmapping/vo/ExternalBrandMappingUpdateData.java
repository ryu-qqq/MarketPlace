package com.ryuqq.marketplace.domain.externalbrandmapping.vo;

/**
 * ExternalBrandMapping 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record ExternalBrandMappingUpdateData(
        String externalBrandName, long internalBrandId, ExternalBrandMappingStatus status) {

    public static ExternalBrandMappingUpdateData of(
            String externalBrandName, long internalBrandId, ExternalBrandMappingStatus status) {
        return new ExternalBrandMappingUpdateData(externalBrandName, internalBrandId, status);
    }
}
