package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

import java.util.List;

/**
 * 네이버 커머스 주문-클레임 처리 공통 응답.
 *
 * <p>취소/반품/교환/발송 API의 공통 응답 구조.
 *
 * @param data 처리 결과 데이터
 */
public record NaverClaimResponse(Data data) {

    /**
     * 처리 결과 데이터.
     *
     * @param successProductOrderIds 성공한 상품주문번호 목록
     * @param failProductOrderInfos 실패한 상품주문 정보 목록
     */
    public record Data(
            List<String> successProductOrderIds, List<FailProductOrderInfo> failProductOrderInfos) {

        public Data {
            successProductOrderIds =
                    successProductOrderIds != null
                            ? List.copyOf(successProductOrderIds)
                            : List.of();
            failProductOrderInfos =
                    failProductOrderInfos != null ? List.copyOf(failProductOrderInfos) : List.of();
        }
    }

    /**
     * 실패한 상품주문 정보.
     *
     * @param productOrderId 상품주문번호
     * @param code 오류 코드
     * @param message 오류 메시지
     */
    public record FailProductOrderInfo(String productOrderId, String code, String message) {}
}
