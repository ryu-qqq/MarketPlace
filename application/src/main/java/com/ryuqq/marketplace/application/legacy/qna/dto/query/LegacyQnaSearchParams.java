package com.ryuqq.marketplace.application.legacy.qna.dto.query;

import java.time.LocalDateTime;

/**
 * 레거시 QnA 목록 조회 파라미터.
 *
 * @param qnaStatus QnA 상태 (OPEN/CLOSED 등, nullable)
 * @param qnaType QnA 유형 (PRODUCT/ORDER, 필수)
 * @param qnaDetailType QnA 상세 유형 (nullable)
 * @param privateYn 비공개 여부 (nullable)
 * @param lastDomainId 커서 기반 페이징 마지막 ID (nullable)
 * @param sellerId 셀러 ID (nullable)
 * @param searchKeyword 검색 키워드 (nullable)
 * @param startDate 시작일 (nullable)
 * @param endDate 종료일 (nullable)
 * @param size 페이지 크기
 */
public record LegacyQnaSearchParams(
        String qnaStatus,
        String qnaType,
        String qnaDetailType,
        String privateYn,
        Long lastDomainId,
        Long sellerId,
        String searchKeyword,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int size) {}
