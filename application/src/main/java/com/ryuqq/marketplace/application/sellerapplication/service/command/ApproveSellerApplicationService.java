package com.ryuqq.marketplace.application.sellerapplication.service.command;

import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApproveSellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.internal.SellerApplicationApprovalCoordinator;
import com.ryuqq.marketplace.application.sellerapplication.port.in.command.ApproveSellerApplicationUseCase;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import org.springframework.stereotype.Service;

/**
 * ApproveSellerApplicationService - 셀러 입점 신청 승인 Service.
 *
 * <p>대기 상태의 입점 신청을 승인하고 Seller를 생성합니다.
 *
 * @author ryu-qqq
 */
@Service
public class ApproveSellerApplicationService implements ApproveSellerApplicationUseCase {

    private final SellerApplicationApprovalCoordinator approvalCoordinator;

    public ApproveSellerApplicationService(
            SellerApplicationApprovalCoordinator approvalCoordinator) {
        this.approvalCoordinator = approvalCoordinator;
    }

    @Override
    public Long execute(ApproveSellerApplicationCommand command) {
        SellerApplication application =
                approvalCoordinator.approve(command.sellerApplicationId(), command.processedBy());

        return application.approvedSellerIdValue();
    }
}
