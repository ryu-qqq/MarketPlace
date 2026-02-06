package com.ryuqq.marketplace.application.refundpolicy.dto.response;

/**
 * NonReturnableConditionResult - 반품 불가 조건 결과 DTO.
 *
 * <p>APP-DTO-001: Application Result는 record 타입 필수.
 *
 * @param code 조건 코드
 * @param displayName 조건 표시명
 * @author ryu-qqq
 * @since 1.0.0
 */
public record NonReturnableConditionResult(String code, String displayName) {}
