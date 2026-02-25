package com.ryuqq.marketplace.application.inboundsource.manager;

import com.ryuqq.marketplace.application.inboundsource.port.out.query.InboundSourceQueryPort;
import com.ryuqq.marketplace.domain.inboundsource.aggregate.InboundSource;
import com.ryuqq.marketplace.domain.inboundsource.exception.InboundSourceNotFoundException;
import com.ryuqq.marketplace.domain.inboundsource.id.InboundSourceId;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InboundSourceReadManager {

    private final InboundSourceQueryPort queryPort;

    public InboundSourceReadManager(InboundSourceQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public InboundSource getById(long id) {
        InboundSourceId inboundSourceId = InboundSourceId.of(id);
        return queryPort
                .findById(inboundSourceId)
                .orElseThrow(() -> new InboundSourceNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public InboundSource findByCode(String code) {
        return queryPort
                .findByCode(code)
                .orElseThrow(() -> new InboundSourceNotFoundException(code));
    }

    @Transactional(readOnly = true)
    public List<InboundSource> findByCriteria(InboundSourceSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(InboundSourceSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
