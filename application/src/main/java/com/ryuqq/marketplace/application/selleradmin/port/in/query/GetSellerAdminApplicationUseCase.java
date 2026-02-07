package com.ryuqq.marketplace.application.selleradmin.port.in.query;

import com.ryuqq.marketplace.application.selleradmin.dto.query.GetSellerAdminApplicationQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationResult;

/**
 * 셀러 관리자 가입 신청 상세 조회 UseCase.
 *
 * <p>가입 신청 상태 (PENDING_APPROVAL, REJECTED)만 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface GetSellerAdminApplicationUseCase {

    /**
     * 가입 신청 상세를 조회합니다.
     *
     * @param query 조회 Query (sellerId, sellerAdminId, 허용 상태 포함)
     * @return 신청 상세 정보
     */
    SellerAdminApplicationResult execute(GetSellerAdminApplicationQuery query);
}
