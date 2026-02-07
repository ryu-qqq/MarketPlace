package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.mapper.ShippingPolicyJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.repository.ShippingPolicyJpaRepository;
import com.ryuqq.marketplace.application.shippingpolicy.port.out.command.ShippingPolicyCommandPort;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ShippingPolicyCommandAdapter - 배송 정책 Command 어댑터.
 *
 * <p>ShippingPolicyCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ShippingPolicyCommandAdapter implements ShippingPolicyCommandPort {

    private final ShippingPolicyJpaRepository jpaRepository;
    private final ShippingPolicyJpaEntityMapper mapper;

    /**
     * 생성자 주입.
     *
     * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 의존.
     *
     * @param jpaRepository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public ShippingPolicyCommandAdapter(
            ShippingPolicyJpaRepository jpaRepository, ShippingPolicyJpaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * 배송 정책 저장.
     *
     * @param shippingPolicy ShippingPolicy 도메인 객체
     * @return 저장된 배송 정책 ID
     */
    @Override
    public Long persist(ShippingPolicy shippingPolicy) {
        ShippingPolicyJpaEntity entity = mapper.toEntity(shippingPolicy);
        ShippingPolicyJpaEntity saved = jpaRepository.save(entity);
        return saved.getId();
    }

    /**
     * 배송 정책 일괄 저장.
     *
     * @param shippingPolicies ShippingPolicy 도메인 객체 목록
     */
    @Override
    public void persistAll(List<ShippingPolicy> shippingPolicies) {
        List<ShippingPolicyJpaEntity> entities =
                shippingPolicies.stream().map(mapper::toEntity).toList();
        jpaRepository.saveAll(entities);
    }
}
