package com.ryuqq.marketplace.domain.externalsource.vo;

/**
 * ExternalSource 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record ExternalSourceUpdateData(
        String name, String description, ExternalSourceStatus status) {

    public static ExternalSourceUpdateData of(
            String name, String description, ExternalSourceStatus status) {
        return new ExternalSourceUpdateData(name, description, status);
    }
}
