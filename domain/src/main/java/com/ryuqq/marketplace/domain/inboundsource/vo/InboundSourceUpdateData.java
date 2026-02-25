package com.ryuqq.marketplace.domain.inboundsource.vo;

/**
 * InboundSource 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record InboundSourceUpdateData(String name, String description, InboundSourceStatus status) {

    public static InboundSourceUpdateData of(
            String name, String description, InboundSourceStatus status) {
        return new InboundSourceUpdateData(name, description, status);
    }
}
