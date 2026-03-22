package com.ryuqq.marketplace.adapter.out.client.naver.dto.qna;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** 네이버 상품 문의 목록 조회 페이지 응답. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverProductQnaPageResponse(
        List<NaverProductQna> contents,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {}
