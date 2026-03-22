package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

/** 세토프 호환 QnA 이미지 요청 DTO. */
public record LegacyQnaImageRequest(
        @JsonInclude(JsonInclude.Include.NON_NULL) Long qnaImageId,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long qnaId,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long qnaAnswerId,
        String imageUrl,
        @NotNull(message = "displayOrder는 필수입니다") int displayOrder) {}
