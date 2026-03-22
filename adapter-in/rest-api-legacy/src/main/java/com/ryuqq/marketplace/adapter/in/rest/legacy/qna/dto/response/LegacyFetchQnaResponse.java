package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 세토프 호환 QnA 목록 항목 응답 DTO.
 *
 * <p>GET /api/v1/legacy/qnas 목록 항목 및 상세 조회 시 질문 본문.
 */
public record LegacyFetchQnaResponse(
        long qnaId,
        LegacyQnaContentsResponse qnaContents,
        String privateYn,
        String qnaStatus,
        String qnaType,
        String qnaDetailType,
        String sellerName,
        LegacyUserInfoQnaResponse userInfo,
        LegacyQnaTargetResponse qnaTarget,
        List<LegacyQnaImageResponse> qnaImages,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime insertDate,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updateDate) {}
