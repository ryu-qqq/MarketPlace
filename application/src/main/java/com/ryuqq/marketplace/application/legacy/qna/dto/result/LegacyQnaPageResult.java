package com.ryuqq.marketplace.application.legacy.qna.dto.result;

import java.util.List;

/**
 * 레거시 QnA 목록 페이지 결과.
 *
 * @param items         QnA 목록
 * @param totalElements 전체 건수
 * @param lastDomainId  마지막 QnA ID (커서 기반 페이징용, nullable)
 */
public record LegacyQnaPageResult(
        List<LegacyQnaDetailResult> items,
        long totalElements,
        Long lastDomainId) {}
