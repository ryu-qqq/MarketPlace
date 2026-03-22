package com.ryuqq.marketplace.adapter.out.client.naver.dto.qna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 네이버 고객 문의 내용 구조체. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverCustomerInquiry(
        Long inquiryNo,
        String category,
        String title,
        String inquiryContent,
        String inquiryRegistrationDateTime,
        Long answerContentId,
        String answerContent,
        String answerRegistrationDateTime,
        boolean answered,
        String orderId,
        String productNo,
        String productName,
        String customerId,
        String customerName) {}
