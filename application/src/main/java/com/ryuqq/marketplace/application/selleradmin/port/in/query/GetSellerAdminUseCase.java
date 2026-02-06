package com.ryuqq.marketplace.application.selleradmin.port.in.query;

import com.ryuqq.marketplace.application.selleradmin.dto.query.GetSellerAdminQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminResult;

/**
 * 셀러 관리자 상세 조회 UseCase.
 *
 * <p>승인된 관리자 상태 (ACTIVE, INACTIVE, SUSPENDED)만 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface GetSellerAdminUseCase {

    /**
     * 관리자 상세를 조회합니다.
     *
     * @param query 조회 Query (sellerId, sellerAdminId, 허용 상태 포함)
     * @return 관리자 상세 정보
     */
    SellerAdminResult execute(GetSellerAdminQuery query);
}
