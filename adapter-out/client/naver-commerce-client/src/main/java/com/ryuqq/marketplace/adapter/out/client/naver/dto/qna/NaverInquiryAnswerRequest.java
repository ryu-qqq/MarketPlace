package com.ryuqq.marketplace.adapter.out.client.naver.dto.qna;

/** 네이버 고객 문의 답변 등록/수정 요청. */
public record NaverInquiryAnswerRequest(String answerComment, String answerTemplateId) {
    public static NaverInquiryAnswerRequest of(String answerComment) {
        return new NaverInquiryAnswerRequest(answerComment, null);
    }
}
