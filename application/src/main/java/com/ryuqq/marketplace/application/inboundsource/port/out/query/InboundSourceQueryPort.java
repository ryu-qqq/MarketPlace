package com.ryuqq.marketplace.application.inboundsource.port.out.query;

import com.ryuqq.marketplace.domain.inboundsource.aggregate.InboundSource;
import com.ryuqq.marketplace.domain.inboundsource.id.InboundSourceId;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSearchCriteria;
import java.util.List;
import java.util.Optional;

public interface InboundSourceQueryPort {
    Optional<InboundSource> findById(InboundSourceId id);

    Optional<InboundSource> findByCode(String code);

    List<InboundSource> findByCriteria(InboundSourceSearchCriteria criteria);

    long countByCriteria(InboundSourceSearchCriteria criteria);
}
