package com.ryuqq.marketplace.adapter.out.client.naver.dto.qna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 네이버 고객 문의 답변 등록/수정 응답. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverInquiryAnswerResponse(
        String code, String message, Data data, String timestamp, String traceId) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(Long inquiryNo, Long inquiryCommentNo) {}
}
