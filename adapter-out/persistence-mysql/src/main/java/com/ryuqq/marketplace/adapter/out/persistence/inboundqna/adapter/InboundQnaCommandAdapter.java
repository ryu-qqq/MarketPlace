package com.ryuqq.marketplace.adapter.out.persistence.inboundqna.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.mapper.InboundQnaJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.repository.InboundQnaJpaRepository;
import com.ryuqq.marketplace.application.inboundqna.port.out.command.InboundQnaCommandPort;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundQna 저장 어댑터. */
@Component
public class InboundQnaCommandAdapter implements InboundQnaCommandPort {

    private final InboundQnaJpaRepository repository;
    private final InboundQnaJpaEntityMapper mapper;

    public InboundQnaCommandAdapter(
            InboundQnaJpaRepository repository, InboundQnaJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(InboundQna inboundQna) {
        InboundQnaJpaEntity entity = mapper.toEntity(inboundQna);
        repository.save(entity);
    }

    @Override
    public void persistAll(List<InboundQna> inboundQnas) {
        List<InboundQnaJpaEntity> entities = inboundQnas.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
