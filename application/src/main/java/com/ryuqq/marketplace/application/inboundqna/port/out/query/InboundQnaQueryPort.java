package com.ryuqq.marketplace.application.inboundqna.port.out.query;

import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import java.util.List;
import java.util.Optional;

/** InboundQna 조회 포트. */
public interface InboundQnaQueryPort {
    Optional<InboundQna> findById(long id);

    boolean existsBySalesChannelIdAndExternalQnaId(long salesChannelId, String externalQnaId);

    List<InboundQna> findByStatus(InboundQnaStatus status, int limit);
}
