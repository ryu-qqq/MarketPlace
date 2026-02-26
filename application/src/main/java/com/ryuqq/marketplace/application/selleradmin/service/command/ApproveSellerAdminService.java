package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.common.component.TransactionEventRegistry;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.internal.SellerAdminApprovalCoordinator;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.ApproveSellerAdminUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import org.springframework.stereotype.Service;

/**
 * ApproveSellerAdminService - 셀러 관리자 가입 신청 승인 Service.
 *
 * <p>PENDING_APPROVAL 상태의 신청을 승인하고 인증 서버 연동용 Outbox를 생성합니다.
 *
 * <p>실제 인증 서버 연동은 이벤트 리스너/스케줄러가 Outbox를 처리하여 수행합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Service
public class ApproveSellerAdminService implements ApproveSellerAdminUseCase {

    private final SellerAdminApprovalCoordinator approvalCoordinator;
    private final TransactionEventRegistry eventRegistry;

    public ApproveSellerAdminService(
            SellerAdminApprovalCoordinator approvalCoordinator,
            TransactionEventRegistry eventRegistry) {
        this.approvalCoordinator = approvalCoordinator;
        this.eventRegistry = eventRegistry;
    }

    @Override
    public String execute(ApproveSellerAdminCommand command) {
        SellerAdmin sellerAdmin = approvalCoordinator.approve(command);

        sellerAdmin.pollEvents().forEach(eventRegistry::registerForPublish);

        return sellerAdmin.idValue();
    }
}
