package com.ryuqq.marketplace.adapter.in.rest.common.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** 날짜/시간 포맷 변환 유틸리티. */
public final class DateTimeFormatUtils {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    private DateTimeFormatUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String formatIso8601(Instant instant) {
        if (instant == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = instant.atZone(ZONE_ID);
        return zonedDateTime.format(ISO_FORMATTER);
    }

    /** yyyy-MM-dd HH:mm:ss 형식으로 포맷합니다 (KST). */
    public static String formatDisplay(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZONE_ID).format(DISPLAY_FORMATTER);
    }

    public static String formatTime(LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(TIME_FORMATTER);
    }

    public static String nowIso8601() {
        return formatIso8601(Instant.now());
    }
}
