package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import java.util.List;

/**
 * 세토프 커머스 상품 그룹 고시정보 등록/수정 요청 DTO.
 *
 * <p>POST /api/v2/admin/product-groups/{id}/notice (등록)
 *
 * <p>PUT /api/v2/admin/product-groups/{id}/notice (수정)
 */
public record SetofNoticeRequest(List<NoticeEntryRequest> entries) {

    /** 방어적 복사. */
    public SetofNoticeRequest {
        entries = entries == null ? null : List.copyOf(entries);
    }

    /** 고시정보 항목 요청. */
    public record NoticeEntryRequest(Long noticeFieldId, String fieldName, String fieldValue) {}
}
