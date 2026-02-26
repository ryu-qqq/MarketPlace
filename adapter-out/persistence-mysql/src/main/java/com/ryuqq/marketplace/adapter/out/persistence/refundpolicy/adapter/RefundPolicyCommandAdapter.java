package com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.mapper.RefundPolicyJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.repository.RefundPolicyJpaRepository;
import com.ryuqq.marketplace.application.refundpolicy.port.out.command.RefundPolicyCommandPort;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RefundPolicyCommandAdapter - 환불 정책 Command 어댑터.
 *
 * <p>RefundPolicyCommandPort를 구현하여 영속성 계층과 연결합니다.
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
public class RefundPolicyCommandAdapter implements RefundPolicyCommandPort {

    private final RefundPolicyJpaRepository jpaRepository;
    private final RefundPolicyJpaEntityMapper mapper;

    /**
     * 생성자 주입.
     *
     * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 의존.
     *
     * @param jpaRepository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public RefundPolicyCommandAdapter(
            RefundPolicyJpaRepository jpaRepository, RefundPolicyJpaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * 환불 정책 저장.
     *
     * @param refundPolicy RefundPolicy 도메인 객체
     * @return 저장된 환불 정책 ID
     */
    @Override
    public Long persist(RefundPolicy refundPolicy) {
        RefundPolicyJpaEntity entity = mapper.toEntity(refundPolicy);
        RefundPolicyJpaEntity saved = jpaRepository.save(entity);
        return saved.getId();
    }

    /**
     * 환불 정책 일괄 저장.
     *
     * @param refundPolicies RefundPolicy 도메인 객체 목록
     */
    @Override
    public void persistAll(List<RefundPolicy> refundPolicies) {
        List<RefundPolicyJpaEntity> entities =
                refundPolicies.stream().map(mapper::toEntity).toList();
        jpaRepository.saveAll(entities);
    }
}
