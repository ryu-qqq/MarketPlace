package com.ryuqq.marketplace.application.selleradmin.dto.query;

import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.List;

/**
 * 셀러 관리자 상세 조회 Query.
 *
 * <p>승인된 관리자 상태 (ACTIVE, INACTIVE, SUSPENDED)만 조회합니다.
 *
 * @param sellerId 셀러 ID
 * @param sellerAdminId 셀러 관리자 ID (UUIDv7)
 * @author ryu-qqq
 * @since 1.1.0
 */
public record GetSellerAdminQuery(Long sellerId, String sellerAdminId) {

    private static final List<SellerAdminStatus> ADMIN_STATUSES =
            List.of(
                    SellerAdminStatus.ACTIVE,
                    SellerAdminStatus.INACTIVE,
                    SellerAdminStatus.SUSPENDED);

    public static GetSellerAdminQuery of(Long sellerId, String sellerAdminId) {
        return new GetSellerAdminQuery(sellerId, sellerAdminId);
    }

    public List<SellerAdminStatus> statuses() {
        return ADMIN_STATUSES;
    }
}
