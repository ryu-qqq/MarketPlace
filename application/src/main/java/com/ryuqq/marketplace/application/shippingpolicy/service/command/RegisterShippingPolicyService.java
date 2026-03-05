package com.ryuqq.marketplace.application.shippingpolicy.service.command;

import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.factory.ShippingPolicyCommandFactory;
import com.ryuqq.marketplace.application.shippingpolicy.internal.DefaultShippingPolicyResolver;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.RegisterShippingPolicyUseCase;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncEntityType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOperationType;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RegisterShippingPolicyService - 배송 정책 등록 Service
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
public class RegisterShippingPolicyService implements RegisterShippingPolicyUseCase {

    private final ShippingPolicyCommandFactory commandFactory;
    private final ShippingPolicyCommandManager commandManager;
    private final DefaultShippingPolicyResolver defaultPolicyResolver;
    private final SetofSyncOutboxCommandManager setofSyncOutboxCommandManager;

    public RegisterShippingPolicyService(
            ShippingPolicyCommandFactory commandFactory,
            ShippingPolicyCommandManager commandManager,
            DefaultShippingPolicyResolver defaultPolicyResolver,
            Optional<SetofSyncOutboxCommandManager> setofSyncOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.defaultPolicyResolver = defaultPolicyResolver;
        this.setofSyncOutboxCommandManager = setofSyncOutboxCommandManager.orElse(null);
    }

    @Override
    @Transactional
    public Long execute(RegisterShippingPolicyCommand command) {
        ShippingPolicy shippingPolicy = commandFactory.create(command);

        defaultPolicyResolver.resolveForRegistration(
                shippingPolicy.sellerId(), shippingPolicy, shippingPolicy.createdAt());

        Long policyId = commandManager.persist(shippingPolicy);
        createSetofSyncOutbox(
                shippingPolicy.sellerId(),
                policyId,
                SetofSyncEntityType.SHIPPING_POLICY,
                SetofSyncOperationType.CREATE,
                shippingPolicy.createdAt());
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
