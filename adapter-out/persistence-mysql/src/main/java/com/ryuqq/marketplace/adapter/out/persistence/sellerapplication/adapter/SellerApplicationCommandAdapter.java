package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.mapper.SellerApplicationJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository.SellerApplicationJpaRepository;
import com.ryuqq.marketplace.application.sellerapplication.port.out.command.SellerApplicationCommandPort;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerApplicationCommandAdapter - 입점 신청 명령 어댑터.
 *
 * <p>SellerApplicationCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerApplicationCommandAdapter implements SellerApplicationCommandPort {

    private final SellerApplicationJpaRepository repository;
    private final SellerApplicationJpaEntityMapper mapper;

    public SellerApplicationCommandAdapter(
            SellerApplicationJpaRepository repository, SellerApplicationJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 입점 신청 영속화 (생성/수정).
     *
     * @param sellerApplication 영속화할 입점 신청
     * @return 영속화된 신청 ID
     */
    @Override
    public Long persist(SellerApplication sellerApplication) {
        SellerApplicationJpaEntity entity = mapper.toEntity(sellerApplication);
        SellerApplicationJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    /**
     * 입점 신청 목록 일괄 영속화.
     *
     * @param sellerApplications 영속화할 입점 신청 목록
     */
    @Override
    public void persistAll(List<SellerApplication> sellerApplications) {
        List<SellerApplicationJpaEntity> entities =
                sellerApplications.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
