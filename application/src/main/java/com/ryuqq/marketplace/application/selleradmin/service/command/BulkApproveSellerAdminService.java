package com.ryuqq.marketplace.application.selleradmin.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.internal.SellerAdminApprovalCoordinator;
import com.ryuqq.marketplace.application.selleradmin.port.in.command.BulkApproveSellerAdminUseCase;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * BulkApproveSellerAdminService - 셀러 관리자 가입 신청 일괄 승인 Service.
 *
 * <p>PENDING_APPROVAL / REJECTED 상태의 신청을 일괄 승인하고 인증 서버 연동용 Outbox를 생성합니다.
 *
 * <p>이벤트 발행 없이 스케줄러가 Outbox를 처리하도록 위임합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Service
public class BulkApproveSellerAdminService implements BulkApproveSellerAdminUseCase {

    private final SellerAdminApprovalCoordinator approvalCoordinator;

    public BulkApproveSellerAdminService(SellerAdminApprovalCoordinator approvalCoordinator) {
        this.approvalCoordinator = approvalCoordinator;
    }

    @Override
    public BatchProcessingResult<String> execute(BulkApproveSellerAdminCommand command) {
        List<BatchItemResult<String>> results = new ArrayList<>();

        for (String sellerAdminId : command.sellerAdminIds()) {
            try {
                ApproveSellerAdminCommand approveCommand =
                        new ApproveSellerAdminCommand(sellerAdminId, command.statuses());
                approvalCoordinator.approve(approveCommand);
                results.add(BatchItemResult.success(sellerAdminId));
            } catch (DomainException e) {
                results.add(
                        BatchItemResult.failure(
                                sellerAdminId, e.getErrorCode().getCode(), e.getMessage()));
            }
        }

        return BatchProcessingResult.from(results);
    }
}
