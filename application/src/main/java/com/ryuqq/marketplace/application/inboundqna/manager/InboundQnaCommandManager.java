package com.ryuqq.marketplace.application.inboundqna.manager;

import com.ryuqq.marketplace.application.inboundqna.port.out.command.InboundQnaCommandPort;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InboundQnaCommandManager {

    private final InboundQnaCommandPort commandPort;

    public InboundQnaCommandManager(InboundQnaCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(InboundQna inboundQna) {
        commandPort.persist(inboundQna);
    }

    @Transactional
    public void persistAll(List<InboundQna> inboundQnas) {
        commandPort.persistAll(inboundQnas);
    }
}
