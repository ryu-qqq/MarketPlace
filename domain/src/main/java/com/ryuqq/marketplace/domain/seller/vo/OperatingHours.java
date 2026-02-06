package com.ryuqq.marketplace.domain.seller.vo;

import java.time.LocalTime;

/**
 * 운영 시간 Value Object.
 *
 * @param startTime 운영 시작 시간
 * @param endTime 운영 종료 시간
 */
public record OperatingHours(LocalTime startTime, LocalTime endTime) {

    public OperatingHours {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("운영 시작 시간은 종료 시간보다 이전이어야 합니다");
        }
    }

    public static OperatingHours of(LocalTime startTime, LocalTime endTime) {
        return new OperatingHours(startTime, endTime);
    }

    public static OperatingHours businessHours() {
        return new OperatingHours(LocalTime.of(9, 0), LocalTime.of(18, 0));
    }

    public static OperatingHours allDay() {
        return new OperatingHours(LocalTime.MIN, LocalTime.MAX);
    }

    public boolean isOpen(LocalTime time) {
        if (startTime == null || endTime == null) {
            return true;
        }
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }

    public String formatted() {
        if (startTime == null || endTime == null) {
            return "24시간";
        }
        return String.format(
                "%02d:%02d - %02d:%02d",
                startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute());
    }
}
