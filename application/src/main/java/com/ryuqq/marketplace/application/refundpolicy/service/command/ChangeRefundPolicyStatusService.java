package com.ryuqq.marketplace.application.refundpolicy.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.ChangeRefundPolicyStatusCommand;
import com.ryuqq.marketplace.application.refundpolicy.factory.RefundPolicyCommandFactory;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyCommandManager;
import com.ryuqq.marketplace.application.refundpolicy.port.in.command.ChangeRefundPolicyStatusUseCase;
import com.ryuqq.marketplace.application.refundpolicy.validator.RefundPolicyValidator;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncEntityType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOperationType;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ChangeRefundPolicyStatusService - 환불 정책 활성화 상태 변경 Service
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>APP-FAC-001: 상태변경은 StatusChangeContext 사용
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리
 *
 * @author ryu-qqq
 */
@Service
public class ChangeRefundPolicyStatusService implements ChangeRefundPolicyStatusUseCase {

    private final RefundPolicyCommandFactory commandFactory;
    private final RefundPolicyCommandManager commandManager;
    private final RefundPolicyValidator validator;
    private final SetofSyncOutboxCommandManager setofSyncOutboxCommandManager;

    public ChangeRefundPolicyStatusService(
            RefundPolicyCommandFactory commandFactory,
            RefundPolicyCommandManager commandManager,
            RefundPolicyValidator validator,
            SetofSyncOutboxCommandManager setofSyncOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
        this.setofSyncOutboxCommandManager = setofSyncOutboxCommandManager;
    }

    @Override
    @Transactional
    public void execute(ChangeRefundPolicyStatusCommand command) {
        List<StatusChangeContext<RefundPolicyId>> contexts =
                commandFactory.createStatusChangeContexts(command);

        List<RefundPolicyId> ids = contexts.stream().map(StatusChangeContext::id).toList();
        List<RefundPolicy> refundPolicies = validator.findAllExistingOrThrow(ids);

        Instant changedAt = contexts.get(0).changedAt();

        if (command.active()) {
            activateAll(refundPolicies, changedAt);
        } else {
            deactivateAll(command.sellerId(), refundPolicies, changedAt);
        }

        commandManager.persistAll(refundPolicies);
        for (RefundPolicy policy : refundPolicies) {
            createSetofSyncOutbox(
                    SellerId.of(command.sellerId()),
                    policy.idValue(),
                    SetofSyncEntityType.REFUND_POLICY,
                    SetofSyncOperationType.UPDATE,
                    changedAt);
        }
    }

    private void activateAll(List<RefundPolicy> policies, Instant changedAt) {
        for (RefundPolicy policy : policies) {
            policy.activate(changedAt);
        }
    }

    private void deactivateAll(Long sellerId, List<RefundPolicy> policies, Instant changedAt) {
        // POL-DEACT-002: 비활성화 시 마지막 활성 정책 검증
        validator.validateNotLastActivePolicy(SellerId.of(sellerId), policies);

        for (RefundPolicy policy : policies) {
            // POL-DEACT-001: 기본 정책 비활성화 검증은 Domain에서 처리
            policy.deactivate(changedAt);
        }
    }

    private void createSetofSyncOutbox(
            SellerId sellerId,
            Long entityId,
            SetofSyncEntityType entityType,
            SetofSyncOperationType operationType,
            java.time.Instant now) {
        SetofSyncOutbox outbox =
                SetofSyncOutbox.forNew(sellerId, entityId, entityType, operationType, now);
        setofSyncOutboxCommandManager.persist(outbox);
    }
}
