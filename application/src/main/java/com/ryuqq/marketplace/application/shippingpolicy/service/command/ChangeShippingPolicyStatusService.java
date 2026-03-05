package com.ryuqq.marketplace.application.shippingpolicy.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.ChangeShippingPolicyStatusCommand;
import com.ryuqq.marketplace.application.shippingpolicy.factory.ShippingPolicyCommandFactory;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.ChangeShippingPolicyStatusUseCase;
import com.ryuqq.marketplace.application.shippingpolicy.validator.ShippingPolicyValidator;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncEntityType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOperationType;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ChangeShippingPolicyStatusService - 배송 정책 활성화 상태 변경 Service
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
public class ChangeShippingPolicyStatusService implements ChangeShippingPolicyStatusUseCase {

    private final ShippingPolicyCommandFactory commandFactory;
    private final ShippingPolicyCommandManager commandManager;
    private final ShippingPolicyValidator validator;
    private final SetofSyncOutboxCommandManager setofSyncOutboxCommandManager;

    public ChangeShippingPolicyStatusService(
            ShippingPolicyCommandFactory commandFactory,
            ShippingPolicyCommandManager commandManager,
            ShippingPolicyValidator validator,
            Optional<SetofSyncOutboxCommandManager> setofSyncOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
        this.setofSyncOutboxCommandManager = setofSyncOutboxCommandManager.orElse(null);
    }

    @Override
    @Transactional
    public void execute(ChangeShippingPolicyStatusCommand command) {
        List<StatusChangeContext<ShippingPolicyId>> contexts =
                commandFactory.createStatusChangeContexts(command);

        List<ShippingPolicyId> ids = contexts.stream().map(StatusChangeContext::id).toList();
        List<ShippingPolicy> shippingPolicies = validator.findAllExistingOrThrow(ids);

        Instant changedAt = contexts.get(0).changedAt();

        if (command.active()) {
            activateAll(shippingPolicies, changedAt);
        } else {
            deactivateAll(command.sellerId(), shippingPolicies, changedAt);
        }

        commandManager.persistAll(shippingPolicies);

        for (ShippingPolicy policy : shippingPolicies) {
            createSetofSyncOutbox(
                    SellerId.of(command.sellerId()),
                    policy.idValue(),
                    SetofSyncEntityType.SHIPPING_POLICY,
                    SetofSyncOperationType.UPDATE,
                    changedAt);
        }
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

    private void activateAll(List<ShippingPolicy> policies, Instant changedAt) {
        for (ShippingPolicy policy : policies) {
            policy.activate(changedAt);
        }
    }

    private void deactivateAll(Long sellerId, List<ShippingPolicy> policies, Instant changedAt) {
        // POL-DEACT-002: 비활성화 시 마지막 활성 정책 검증
        validator.validateNotLastActivePolicy(SellerId.of(sellerId), policies);

        for (ShippingPolicy policy : policies) {
            // POL-DEACT-001: 기본 정책 비활성화 검증은 Domain에서 처리
            policy.deactivate(changedAt);
        }
    }
}
