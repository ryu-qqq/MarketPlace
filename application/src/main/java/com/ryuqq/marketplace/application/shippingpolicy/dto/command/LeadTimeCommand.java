package com.ryuqq.marketplace.application.shippingpolicy.dto.command;

/**
 * 발송 소요일 정보 Command.
 *
 * @param minDays 최소 발송일
 * @param maxDays 최대 발송일
 * @param cutoffTime 당일발송 마감시간 (HH:mm)
 */
public record LeadTimeCommand(Integer minDays, Integer maxDays, String cutoffTime) {}
