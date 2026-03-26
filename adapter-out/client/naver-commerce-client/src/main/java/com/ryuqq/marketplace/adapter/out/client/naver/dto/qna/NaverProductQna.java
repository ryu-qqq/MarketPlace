package com.ryuqq.marketplace.adapter.out.client.naver.dto.qna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 네이버 상품 문의 내용 구조체. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverProductQna(
        Long questionId,
        Long productId,
        String productName,
        String question,
        String answer,
        boolean answered,
        String maskedWriterId,
        String createDate) {}
