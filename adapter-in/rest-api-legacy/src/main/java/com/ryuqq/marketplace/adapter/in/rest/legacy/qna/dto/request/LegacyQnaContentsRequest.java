package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/**
 * 세토프 호환 QnA 컨텐츠 요청 DTO.
 *
 * <p>질문/답변 제목과 내용을 담습니다.
 */
public record LegacyQnaContentsRequest(
        @NotBlank(message = "질문/답변 제목은 비워둘 수 없습니다.")
                @Length(min = 1, max = 100, message = "질문/답변 제목은 최소 1자 최대 100자를 넘길 수 없습니다.")
                String title,
        @NotBlank(message = "질문/답변 내용은 비워둘 수 없습니다.")
                @Length(min = 1, max = 500, message = "질문/답변 내용은 최소 1자 최대 500자를 넘길 수 없습니다.")
                String content) {}
