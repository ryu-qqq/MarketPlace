package com.ryuqq.marketplace.application.inboundqna.service.command;

import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.ConvertInboundQnaUseCase;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import org.springframework.stereotype.Service;

/** InboundQna 단건 → Qna 변환 서비스. */
@Service
public class ConvertInboundQnaService implements ConvertInboundQnaUseCase {

    private final InboundQnaReadManager readManager;
    private final InboundQnaConversionProcessor conversionProcessor;

    public ConvertInboundQnaService(
            InboundQnaReadManager readManager, InboundQnaConversionProcessor conversionProcessor) {
        this.readManager = readManager;
        this.conversionProcessor = conversionProcessor;
    }

    @Override
    public void execute(long inboundQnaId) {
        InboundQna inboundQna = readManager.getById(inboundQnaId);
        conversionProcessor.convert(inboundQna, null, null);
    }
}
