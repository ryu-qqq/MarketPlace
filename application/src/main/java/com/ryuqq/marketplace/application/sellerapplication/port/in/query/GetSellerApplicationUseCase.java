package com.ryuqq.marketplace.application.sellerapplication.port.in.query;

import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult;

/**
 * 셀러 입점 신청 상세 조회 UseCase.
 *
 * <p>입점 신청 ID로 상세 정보를 조회합니다.
 *
 * @author ryu-qqq
 */
public interface GetSellerApplicationUseCase {

    /**
     * 입점 신청 상세 정보를 조회합니다.
     *
     * @param sellerApplicationId 신청 ID
     * @return 신청 상세 결과
     */
    SellerApplicationResult execute(Long sellerApplicationId);
}
