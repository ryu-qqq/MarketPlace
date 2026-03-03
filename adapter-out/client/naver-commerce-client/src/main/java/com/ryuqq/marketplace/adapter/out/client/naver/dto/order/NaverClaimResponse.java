package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 클레임 처리 공통 응답.
 *
 * <p>취소/반품/교환 API의 공통 응답 구조.
 *
 * @param data 클레임 데이터
 */
public record NaverClaimResponse(Data data) {

    /**
     * 클레임 응답 데이터.
     *
     * @param claimId 클레임 ID
     * @param claimStatus 클레임 상태
     */
    public record Data(String claimId, String claimStatus) {}
}
