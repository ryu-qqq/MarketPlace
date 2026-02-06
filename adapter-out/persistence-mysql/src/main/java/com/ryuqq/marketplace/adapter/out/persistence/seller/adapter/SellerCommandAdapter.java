package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.application.seller.port.out.command.SellerCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerCommandAdapter - 셀러 명령 어댑터.
 *
 * <p>SellerCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerCommandAdapter implements SellerCommandPort {

    private final SellerJpaRepository repository;
    private final SellerJpaEntityMapper mapper;

    public SellerCommandAdapter(SellerJpaRepository repository, SellerJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Seller 영속화 (생성/수정).
     *
     * @param seller 영속화할 Seller
     * @return 영속화된 Seller ID
     */
    @Override
    public Long persist(Seller seller) {
        SellerJpaEntity entity = mapper.toEntity(seller);
        SellerJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    /**
     * Seller 목록 일괄 영속화.
     *
     * @param sellers 영속화할 Seller 목록
     */
    @Override
    public void persistAll(List<Seller> sellers) {
        List<SellerJpaEntity> entities = sellers.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }

    /**
     * 셀러 인증 정보 업데이트.
     *
     * @param sellerId 셀러 ID
     * @param tenantId 인증 서버 테넌트 ID
     * @param organizationId 인증 서버 조직 ID
     */
    @Override
    public void updateAuthInfo(SellerId sellerId, String tenantId, String organizationId) {
        SellerJpaEntity entity =
                repository
                        .findById(sellerId.value())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "셀러를 찾을 수 없습니다: " + sellerId.value()));
        entity.updateAuthInfo(tenantId, organizationId, Instant.now());
        repository.save(entity);
    }
}
