package com.ryuqq.marketplace.application.selleradmin.dto.command;

import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.List;

/**
 * 셀러 관리자 가입 신청 일괄 승인 Command.
 *
 * @param sellerAdminIds 승인할 셀러 관리자 ID 목록
 * @param statuses 승인 가능한 상태 목록
 * @author ryu-qqq
 * @since 1.1.0
 */
public record BulkApproveSellerAdminCommand(
        List<String> sellerAdminIds, List<SellerAdminStatus> statuses) {

    private static final List<SellerAdminStatus> APPROVABLE_STATUSES =
            List.of(SellerAdminStatus.PENDING_APPROVAL, SellerAdminStatus.REJECTED);

    public BulkApproveSellerAdminCommand(List<String> sellerAdminIds) {
        this(sellerAdminIds, APPROVABLE_STATUSES);
    }
}
