package com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto;

/** 레거시 표준 성공 응답 래퍼. */
public record LegacyApiResponse<T>(T data, LegacyResponse response) {

    public static <T> LegacyApiResponse<T> success(T data) {
        return new LegacyApiResponse<>(data, LegacyResponse.success());
    }

    public static <T> LegacyApiResponse<T> of(T data) {
        return success(data);
    }

    public static <T> LegacyApiResponse<T> dataNotFoundWithErrorMessage(String errorMessage) {
        return new LegacyApiResponse<>(null, LegacyResponse.of(404, errorMessage));
    }
}
