package com.ryuqq.marketplace.adapter.out.persistence.inboundqna.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.mapper.InboundQnaJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.repository.InboundQnaQueryDslRepository;
import com.ryuqq.marketplace.application.inboundqna.port.out.query.InboundQnaQueryPort;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** InboundQna 조회 어댑터. */
@Component
public class InboundQnaQueryAdapter implements InboundQnaQueryPort {

    private final InboundQnaQueryDslRepository queryDslRepository;
    private final InboundQnaJpaEntityMapper mapper;

    public InboundQnaQueryAdapter(
            InboundQnaQueryDslRepository queryDslRepository, InboundQnaJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<InboundQna> findById(long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsBySalesChannelIdAndExternalQnaId(
            long salesChannelId, String externalQnaId) {
        return queryDslRepository.existsBySalesChannelIdAndExternalQnaId(
                salesChannelId, externalQnaId);
    }

    @Override
    public List<InboundQna> findByStatus(InboundQnaStatus status, int limit) {
        InboundQnaJpaEntity.Status entityStatus = InboundQnaJpaEntity.Status.valueOf(status.name());
        return queryDslRepository.findByStatusOrderByIdAsc(entityStatus, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
