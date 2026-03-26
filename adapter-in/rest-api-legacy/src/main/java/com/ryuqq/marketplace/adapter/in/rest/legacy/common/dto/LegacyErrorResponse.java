package com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** 레거시 표준 에러 응답. */
public record LegacyErrorResponse(int status, String message, String error, String timestamp) {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LegacyErrorResponse of(int status, String message, String error) {
        return new LegacyErrorResponse(
                status, message, error, LocalDateTime.now().format(FORMATTER));
    }
}
