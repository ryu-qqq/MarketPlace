package com.ryuqq.marketplace.application.imagetransform.dto.response;

/** 이미지 변환 상태. */
public enum ImageTransformStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED;

    /** 완료 또는 실패 등 최종 상태인지 확인. */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }
}
