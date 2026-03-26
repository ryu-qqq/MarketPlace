package com.ryuqq.marketplace.adapter.out.client.naver.dto.qna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** 네이버 고객 문의 조회 페이지 응답. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverCustomerInquiryPageResponse(
        List<NaverCustomerInquiry> content,
        int totalPages,
        long totalElements,
        boolean first,
        boolean last) {}
