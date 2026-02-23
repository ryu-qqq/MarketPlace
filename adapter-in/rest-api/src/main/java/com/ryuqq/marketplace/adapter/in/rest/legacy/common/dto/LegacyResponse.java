package com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto;

/** 레거시 응답 메타 정보. */
public record LegacyResponse(int status, String message) {

    public static LegacyResponse success() {
        return new LegacyResponse(200, "success");
    }

    public static LegacyResponse of(int status, String message) {
        return new LegacyResponse(status, message);
    }
}
