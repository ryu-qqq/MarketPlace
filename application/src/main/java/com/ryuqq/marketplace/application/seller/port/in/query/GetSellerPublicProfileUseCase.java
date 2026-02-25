package com.ryuqq.marketplace.application.seller.port.in.query;

import com.ryuqq.marketplace.application.seller.dto.response.SellerPublicProfileResult;

/**
 * 셀러 공개 프로필 조회 UseCase.
 *
 * <p>인증 없이 접근 가능한 셀러 간소화 프로필을 조회합니다.
 */
public interface GetSellerPublicProfileUseCase {

    SellerPublicProfileResult execute(Long sellerId);
}
