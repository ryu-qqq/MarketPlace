package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import java.util.List;

/**
 * 네이버 커머스 변경 상품주문 내역 응답.
 *
 * <p>GET /v1/pay-order/seller/product-orders/last-changed-statuses 응답 래퍼.
 *
 * @param data 응답 데이터
 */
public record NaverLastChangedStatusesResponse(Data data) {

    /**
     * 응답 데이터.
     *
     * @param lastChangeStatuses 변경 상태 목록
     * @param count 조회 결과 건수
     * @param more 페이지네이션 정보 (다음 페이지 없으면 null)
     */
    public record Data(List<NaverLastChangedStatus> lastChangeStatuses, Integer count, More more) {

        public Data {
            lastChangeStatuses =
                    lastChangeStatuses != null ? List.copyOf(lastChangeStatuses) : List.of();
        }
    }

    /**
     * 페이지네이션 커서.
     *
     * @param moreFrom 다음 조회 시작 일시
     * @param moreSequence 다음 페이지 시퀀스
     */
    public record More(String moreFrom, String moreSequence) {}
}
