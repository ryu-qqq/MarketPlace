package com.ryuqq.marketplace.adapter.out.persistence.cancel.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.mapper.CancelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelQueryDslRepository;
import com.ryuqq.marketplace.application.cancel.port.out.query.CancelQueryPort;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 취소 Query Adapter. */
@Component
public class CancelQueryAdapter implements CancelQueryPort {

    private final CancelQueryDslRepository cancelRepository;
    private final CancelItemJpaRepository cancelItemRepository;
    private final CancelJpaEntityMapper mapper;

    public CancelQueryAdapter(
            CancelQueryDslRepository cancelRepository,
            CancelItemJpaRepository cancelItemRepository,
            CancelJpaEntityMapper mapper) {
        this.cancelRepository = cancelRepository;
        this.cancelItemRepository = cancelItemRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Cancel> findById(CancelId id) {
        return cancelRepository.findById(id.value()).map(entity -> toDomainWithItems(entity));
    }

    @Override
    public Optional<Cancel> findByOrderId(String orderId) {
        return cancelRepository.findByOrderId(orderId).map(entity -> toDomainWithItems(entity));
    }

    @Override
    public List<Cancel> findByOrderIds(List<String> orderIds) {
        List<CancelJpaEntity> cancelEntities = cancelRepository.findByOrderIds(orderIds);
        return toDomainListWithItems(cancelEntities);
    }

    @Override
    public List<Cancel> findByCriteria(CancelSearchCriteria criteria) {
        List<CancelJpaEntity> cancelEntities = cancelRepository.findByCriteria(criteria);
        return toDomainListWithItems(cancelEntities);
    }

    @Override
    public long countByCriteria(CancelSearchCriteria criteria) {
        return cancelRepository.countByCriteria(criteria);
    }

    /**
     * 단일 취소 엔티티를 cancelItems와 함께 도메인으로 변환합니다.
     *
     * @param entity 취소 엔티티
     * @return Cancel 도메인
     */
    private Cancel toDomainWithItems(CancelJpaEntity entity) {
        List<CancelItemJpaEntity> itemEntities =
                cancelItemRepository.findAllByCancelId(entity.getId());
        return mapper.toDomain(entity, itemEntities);
    }

    /**
     * 취소 엔티티 목록을 cancelItems와 함께 도메인 목록으로 변환합니다.
     *
     * <p>N+1 방지를 위해 cancelId 목록으로 cancel_items를 일괄 조회합니다.
     *
     * @param cancelEntities 취소 엔티티 목록
     * @return Cancel 도메인 목록
     */
    private List<Cancel> toDomainListWithItems(List<CancelJpaEntity> cancelEntities) {
        if (cancelEntities.isEmpty()) {
            return List.of();
        }
        List<String> cancelIds = cancelEntities.stream().map(CancelJpaEntity::getId).toList();
        List<CancelItemJpaEntity> allItemEntities =
                cancelItemRepository.findAllByCancelIdIn(cancelIds);

        return cancelEntities.stream()
                .map(
                        cancelEntity -> {
                            List<CancelItemJpaEntity> itemEntities =
                                    allItemEntities.stream()
                                            .filter(
                                                    item ->
                                                            item.getCancelId()
                                                                    .equals(cancelEntity.getId()))
                                            .toList();
                            return mapper.toDomain(cancelEntity, itemEntities);
                        })
                .toList();
    }
}
