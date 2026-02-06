package com.ryuqq.marketplace.application.seller.dto.command;

/**
 * 셀러 CS 정보 수정 Command.
 *
 * @param sellerId 셀러 ID
 * @param csContact CS 연락처
 * @param operatingHours 운영시간
 * @param operatingDays 운영요일 (예: "MON,TUE,WED,THU,FRI")
 * @param kakaoChannelUrl 카카오 채널 URL (선택)
 * @author ryu-qqq
 */
public record UpdateSellerCsCommand(
        Long sellerId,
        CsContactCommand csContact,
        OperatingHoursCommand operatingHours,
        String operatingDays,
        String kakaoChannelUrl) {

    /**
     * CS 연락처 Command.
     *
     * @param phone 전화번호
     * @param email 이메일
     * @param mobile 휴대폰번호 (선택)
     */
    public record CsContactCommand(String phone, String email, String mobile) {}

    /**
     * 운영시간 Command.
     *
     * @param startTime 시작 시간 (예: "09:00")
     * @param endTime 종료 시간 (예: "18:00")
     */
    public record OperatingHoursCommand(String startTime, String endTime) {}
}
