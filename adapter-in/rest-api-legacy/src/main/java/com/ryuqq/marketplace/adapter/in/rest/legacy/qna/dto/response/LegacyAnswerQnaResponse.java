package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

/** 세토프 호환 QnA 답변 응답 DTO. */
public record LegacyAnswerQnaResponse(
        long qnaAnswerId,
        Long qnaAnswerParentId,
        String qnaWriterType,
        LegacyQnaContentsResponse qnaContents,
        List<LegacyQnaImageResponse> qnaImages,
        String insertOperator,
        String updateOperator,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime insertDate,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updateDate) {}
