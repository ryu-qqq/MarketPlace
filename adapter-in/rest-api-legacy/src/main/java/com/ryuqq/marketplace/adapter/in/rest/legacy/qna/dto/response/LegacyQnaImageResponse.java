package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/** 세토프 호환 QnA 이미지 응답 DTO. */
public record LegacyQnaImageResponse(
        String qnaIssueType,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long qnaImageId,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long qnaId,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long qnaAnswerId,
        String imageUrl,
        int displayOrder) {}
