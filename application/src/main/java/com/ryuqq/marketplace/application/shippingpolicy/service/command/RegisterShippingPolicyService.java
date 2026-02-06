package com.ryuqq.marketplace.application.shippingpolicy.service.command;

import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.factory.ShippingPolicyCommandFactory;
import com.ryuqq.marketplace.application.shippingpolicy.internal.DefaultShippingPolicyResolver;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.port.in.command.RegisterShippingPolicyUseCase;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
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

    public RegisterShippingPolicyService(
            ShippingPolicyCommandFactory commandFactory,
            ShippingPolicyCommandManager commandManager,
            DefaultShippingPolicyResolver defaultPolicyResolver) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.defaultPolicyResolver = defaultPolicyResolver;
    }

    @Override
    @Transactional
    public Long execute(RegisterShippingPolicyCommand command) {
        ShippingPolicy shippingPolicy = commandFactory.create(command);

        defaultPolicyResolver.resolveForRegistration(
                shippingPolicy.sellerId(), shippingPolicy, shippingPolicy.createdAt());

        return commandManager.persist(shippingPolicy);
    }
}
