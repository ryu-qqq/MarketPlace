package com.ryuqq.marketplace.application.refundpolicy.service.command;

import com.ryuqq.marketplace.application.refundpolicy.dto.command.RegisterRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.factory.RefundPolicyCommandFactory;
import com.ryuqq.marketplace.application.refundpolicy.internal.DefaultRefundPolicyResolver;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyCommandManager;
import com.ryuqq.marketplace.application.refundpolicy.port.in.command.RegisterRefundPolicyUseCase;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncEntityType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOperationType;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RegisterRefundPolicyService - 환불 정책 등록 Service
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>비즈니스 로직:
 *
 * <ul>
 *   <li>기본 정책으로 등록 시 기존 기본 정책 해제
 *   <li>첫 번째 정책 등록 시 자동으로 기본 정책 설정
 * </ul>
 *
 * @author ryu-qqq
 */
@Service
public class RegisterRefundPolicyService implements RegisterRefundPolicyUseCase {

    private final RefundPolicyCommandFactory commandFactory;
    private final RefundPolicyCommandManager commandManager;
    private final DefaultRefundPolicyResolver defaultPolicyResolver;
    private final SetofSyncOutboxCommandManager setofSyncOutboxCommandManager;

    public RegisterRefundPolicyService(
            RefundPolicyCommandFactory commandFactory,
            RefundPolicyCommandManager commandManager,
            DefaultRefundPolicyResolver defaultPolicyResolver,
            Optional<SetofSyncOutboxCommandManager> setofSyncOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.defaultPolicyResolver = defaultPolicyResolver;
        this.setofSyncOutboxCommandManager = setofSyncOutboxCommandManager.orElse(null);
    }

    @Override
    @Transactional
    public Long execute(RegisterRefundPolicyCommand command) {
        RefundPolicy refundPolicy = commandFactory.create(command);

        defaultPolicyResolver.resolveForRegistration(
                refundPolicy.sellerId(), refundPolicy, refundPolicy.createdAt());

        Long policyId = commandManager.persist(refundPolicy);
        createSetofSyncOutbox(
                refundPolicy.sellerId(),
                policyId,
                SetofSyncEntityType.REFUND_POLICY,
                SetofSyncOperationType.CREATE,
                refundPolicy.createdAt());
        return policyId;
    }

    private void createSetofSyncOutbox(
            SellerId sellerId,
            Long entityId,
            SetofSyncEntityType entityType,
            SetofSyncOperationType operationType,
            java.time.Instant now) {
        if (setofSyncOutboxCommandManager != null) {
            SetofSyncOutbox outbox =
                    SetofSyncOutbox.forNew(sellerId, entityId, entityType, operationType, now);
            setofSyncOutboxCommandManager.persist(outbox);
        }
    }
}
