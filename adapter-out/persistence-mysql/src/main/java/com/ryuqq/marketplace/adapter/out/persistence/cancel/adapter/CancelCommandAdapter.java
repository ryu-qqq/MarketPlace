package com.ryuqq.marketplace.adapter.out.persistence.cancel.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.mapper.CancelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelJpaRepository;
import com.ryuqq.marketplace.application.cancel.port.out.command.CancelCommandPort;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 취소 Command Adapter.
 *
 * <p>취소 저장 시 cancels, cancel_items 테이블에 순차적으로 저장합니다. cancel_items는 upsert 방식으로 처리합니다 (기존 항목 삭제 후
 * 재삽입).
 */
@Component
public class CancelCommandAdapter implements CancelCommandPort {

    private final CancelJpaRepository cancelRepository;
    private final CancelItemJpaRepository cancelItemRepository;
    private final CancelJpaEntityMapper mapper;

    public CancelCommandAdapter(
            CancelJpaRepository cancelRepository,
            CancelItemJpaRepository cancelItemRepository,
            CancelJpaEntityMapper mapper) {
        this.cancelRepository = cancelRepository;
        this.cancelItemRepository = cancelItemRepository;
        this.mapper = mapper;
    }

    @Override
    public void persist(Cancel cancel) {
        cancelRepository.save(mapper.toEntity(cancel));
        persistItems(cancel);
    }

    @Override
    public void persistAll(List<Cancel> cancels) {
        cancelRepository.saveAll(cancels.stream().map(mapper::toEntity).toList());
        cancels.forEach(this::persistItems);
    }

    /**
     * 취소 상품 목록을 저장합니다.
     *
     * <p>기존 항목을 삭제한 후 새로운 항목을 삽입합니다.
     *
     * @param cancel 취소 Aggregate
     */
    private void persistItems(Cancel cancel) {
        String cancelId = cancel.idValue();
        cancelItemRepository.deleteAllByCancelId(cancelId);
        List<CancelItemJpaEntity> itemEntities =
                mapper.toItemEntities(cancelId, cancel.cancelItems());
        cancelItemRepository.saveAll(itemEntities);
    }
}
