package com.ryuqq.marketplace.application.inboundqna.manager;

import com.ryuqq.marketplace.application.inboundqna.port.out.query.InboundQnaQueryPort;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.exception.InboundQnaErrorCode;
import com.ryuqq.marketplace.domain.inboundqna.exception.InboundQnaException;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InboundQnaReadManager {

    private final InboundQnaQueryPort queryPort;

    public InboundQnaReadManager(InboundQnaQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public InboundQna getById(long id) {
        return queryPort
                .findById(id)
                .orElseThrow(
                        () ->
                                new InboundQnaException(
                                        InboundQnaErrorCode.INBOUND_QNA_NOT_FOUND,
                                        "InboundQna not found: " + id));
    }

    @Transactional(readOnly = true)
    public boolean existsBySalesChannelIdAndExternalQnaId(
            long salesChannelId, String externalQnaId) {
        return queryPort.existsBySalesChannelIdAndExternalQnaId(salesChannelId, externalQnaId);
    }

    @Transactional(readOnly = true)
    public List<InboundQna> findByStatus(InboundQnaStatus status, int limit) {
        return queryPort.findByStatus(status, limit);
    }
}
