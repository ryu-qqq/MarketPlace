package com.ryuqq.marketplace.application.selleradmin.internal;

import com.ryuqq.marketplace.application.selleradmin.dto.bundle.SellerAdminApprovalBundle;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.factory.SellerAdminCommandFactory;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import org.springframework.stereotype.Component;

/**
 * 셀러 관리자 승인 Coordinator.
 *
 * <p>검증 → Bundle 생성 → 승인 및 저장을 조율합니다.
 *
 * <p>@Transactional은 Facade에만 적용합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminApprovalCoordinator {

    private final SellerAdminReadManager sellerAdminReadManager;
    private final SellerAdminCommandFactory commandFactory;
    private final SellerAdminApprovalFacade approvalFacade;

    public SellerAdminApprovalCoordinator(
            SellerAdminReadManager sellerAdminReadManager,
            SellerAdminCommandFactory commandFactory,
            SellerAdminApprovalFacade approvalFacade) {
        this.sellerAdminReadManager = sellerAdminReadManager;
        this.commandFactory = commandFactory;
        this.approvalFacade = approvalFacade;
    }

    /**
     * 셀러 관리자 가입 신청을 승인합니다.
     *
     * <p>1. 셀러 관리자 조회 및 상태 검증
     *
     * <p>2. 승인 Bundle 생성 (SellerAdmin + AuthOutbox)
     *
     * <p>3. 승인 및 저장 (Facade에서 approve() 호출)
     *
     * <p>이벤트는 SellerAdmin.approve() 내부에서 등록됩니다.
     *
     * @param command 승인 Command (sellerAdminId, statuses)
     * @return 승인된 SellerAdmin (이벤트 발행용)
     */
    public SellerAdmin approve(ApproveSellerAdminCommand command) {
        SellerAdmin sellerAdmin =
                sellerAdminReadManager.getByIdAndStatuses(
                        SellerAdminId.of(command.sellerAdminId()), command.statuses());

        SellerAdminApprovalBundle bundle = commandFactory.createApprovalBundle(sellerAdmin);

        approvalFacade.approveAndPersist(bundle);

        return bundle.sellerAdmin();
    }
}
