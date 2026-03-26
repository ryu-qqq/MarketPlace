package com.ryuqq.marketplace.adapter.out.persistence.cancel.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.mapper.CancelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelJpaRepository;
import com.ryuqq.marketplace.application.cancel.port.out.command.CancelCommandPort;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import java.util.List;
import org.springframework.stereotype.Component;

/** 취소 Command Adapter. */
@Component
public class CancelCommandAdapter implements CancelCommandPort {

    private final CancelJpaRepository cancelRepository;
    private final CancelJpaEntityMapper mapper;

    public CancelCommandAdapter(
            CancelJpaRepository cancelRepository, CancelJpaEntityMapper mapper) {
        this.cancelRepository = cancelRepository;
        this.mapper = mapper;
    }

    @Override
    public void persist(Cancel cancel) {
        cancelRepository.save(mapper.toEntity(cancel));
    }

    @Override
    public void persistAll(List<Cancel> cancels) {
        cancelRepository.saveAll(cancels.stream().map(mapper::toEntity).toList());
    }
}
