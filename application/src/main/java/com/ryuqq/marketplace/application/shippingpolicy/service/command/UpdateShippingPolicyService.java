package com.ryuqq.marketplace.application.shippingpolicy.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.UpdateShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.factory.ShippingPolicyCommandFactory;
import com.ryuqq.marketplace.application.shippingpolicy.internal.DefaultShippingPolicyResolver;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.UpdateShippingPolicyUseCase;
import com.ryuqq.marketplace.application.shippingpolicy.validator.ShippingPolicyValidator;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicyUpdateData;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UpdateShippingPolicyService - 배송 정책 수정 Service
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리
 *
 * <p>비즈니스 규칙:
 *
 * <ul>
 *   <li>기본 정책이 아닌 정책을 기본 정책으로 변경 시 기존 기본 정책 해제
 *   <li>유일한 기본 정책은 해제 불가
 * </ul>
 *
 * @author ryu-qqq
 */
@Service
public class UpdateShippingPolicyService implements UpdateShippingPolicyUseCase {

    private final ShippingPolicyCommandFactory commandFactory;
    private final ShippingPolicyCommandManager commandManager;
    private final ShippingPolicyValidator validator;
    private final DefaultShippingPolicyResolver defaultPolicyResolver;

    public UpdateShippingPolicyService(
            ShippingPolicyCommandFactory commandFactory,
            ShippingPolicyCommandManager commandManager,
            ShippingPolicyValidator validator,
            DefaultShippingPolicyResolver defaultPolicyResolver) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
        this.defaultPolicyResolver = defaultPolicyResolver;
    }

    @Override
    @Transactional
    public void execute(UpdateShippingPolicyCommand command) {
        UpdateContext<ShippingPolicyId, ShippingPolicyUpdateData> context =
                commandFactory.createUpdateContext(command);

        SellerId sellerId = SellerId.of(command.sellerId());
        ShippingPolicy shippingPolicy =
                validator.findExistingBySellerOrThrow(sellerId, context.id());

        // 기본 정책 변경 처리
        defaultPolicyResolver.resolveForUpdate(
                sellerId, shippingPolicy, command.defaultPolicy(), context.changedAt());

        // 정책 정보 업데이트
        shippingPolicy.update(context.updateData(), context.changedAt());

        commandManager.persist(shippingPolicy);
    }
}
