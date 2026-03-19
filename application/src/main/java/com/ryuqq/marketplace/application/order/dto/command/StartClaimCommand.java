package com.ryuqq.marketplace.application.order.dto.command;

import java.util.List;

/**
 * 클레임 시작(반품 요청) Command (배치).
 *
 * @param orderItemIds 대상 주문상품 ID 목록
 * @param reason 클레임 사유
 * @param changedBy 변경자
 */
public record StartClaimCommand(
        List<String> orderItemIds,
        String reason,
        String changedBy) {}
