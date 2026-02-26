package com.ryuqq.marketplace.application.selleradmin.dto.command;

import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.List;

/**
 * 셀러 관리자 가입 신청 승인 Command.
 *
 * @param sellerAdminId 승인할 셀러 관리자 ID
 * @param statuses 승인 가능한 상태 목록
 * @author ryu-qqq
 * @since 1.1.0
 */
public record ApproveSellerAdminCommand(String sellerAdminId, List<SellerAdminStatus> statuses) {

    private static final List<SellerAdminStatus> APPROVABLE_STATUSES =
            List.of(SellerAdminStatus.PENDING_APPROVAL, SellerAdminStatus.REJECTED);

    public ApproveSellerAdminCommand(String sellerAdminId) {
        this(sellerAdminId, APPROVABLE_STATUSES);
    }
}
