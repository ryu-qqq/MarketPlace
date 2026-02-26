package com.ryuqq.marketplace.application.commoncode.dto.command;

/**
 * RegisterCommonCodeCommand - 공통 코드 등록 Command.
 *
 * <p>APP-CMD-001: Command는 record로 정의.
 *
 * <p>APP-CMD-002: 불변 객체로 구현.
 *
 * @param commonCodeTypeId 공통 코드 타입 ID (필수)
 * @param code 코드값 (필수)
 * @param displayName 표시명 (필수)
 * @param displayOrder 표시 순서 (0 이상)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RegisterCommonCodeCommand(
        Long commonCodeTypeId, String code, String displayName, int displayOrder) {}
