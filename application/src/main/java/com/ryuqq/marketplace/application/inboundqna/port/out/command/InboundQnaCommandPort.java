package com.ryuqq.marketplace.application.inboundqna.port.out.command;

import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import java.util.List;

/** InboundQna 저장 포트. */
public interface InboundQnaCommandPort {
    void persist(InboundQna inboundQna);

    void persistAll(List<InboundQna> inboundQnas);
}
