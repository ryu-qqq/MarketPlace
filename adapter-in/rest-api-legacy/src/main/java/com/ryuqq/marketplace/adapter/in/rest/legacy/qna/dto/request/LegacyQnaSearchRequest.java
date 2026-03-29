package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 세토프 호환 QnA 목록 조회 필터 요청 DTO.
 *
 * <p>GET /api/v1/legacy/qnas
 */
public record LegacyQnaSearchRequest(
        String qnaStatus,
        @NotNull(message = "QnaType is required.") String qnaType,
        String qnaDetailType,
        String privateYn,
        Long lastDomainId,
        Long sellerId,
        String searchText,
        LocalDateTime startDate,
        LocalDateTime endDate) {}
