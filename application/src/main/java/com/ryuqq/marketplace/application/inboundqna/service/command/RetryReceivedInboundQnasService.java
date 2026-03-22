package com.ryuqq.marketplace.application.inboundqna.service.command;

import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.RetryReceivedInboundQnasUseCase;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** RECEIVED 상태 InboundQna 일괄 재변환 서비스. */
@Service
public class RetryReceivedInboundQnasService implements RetryReceivedInboundQnasUseCase {

    private static final Logger log = LoggerFactory.getLogger(RetryReceivedInboundQnasService.class);

    private final InboundQnaReadManager readManager;
    private final InboundQnaConversionProcessor conversionProcessor;

    public RetryReceivedInboundQnasService(
            InboundQnaReadManager readManager,
            InboundQnaConversionProcessor conversionProcessor) {
        this.readManager = readManager;
        this.conversionProcessor = conversionProcessor;
    }

    @Override
    public int execute(int batchSize) {
        List<InboundQna> receivedQnas =
                readManager.findByStatus(InboundQnaStatus.RECEIVED, batchSize);

        if (receivedQnas.isEmpty()) {
            return 0;
        }

        int converted = 0;
        for (InboundQna inboundQna : receivedQnas) {
            try {
                conversionProcessor.convert(inboundQna, null, null);
                converted++;
            } catch (Exception e) {
                log.warn(
                        "InboundQna 재변환 실패: inboundQnaId={}, reason={}",
                        inboundQna.idValue(),
                        e.getMessage());
            }
        }

        log.info(
                "InboundQna 재변환 완료: total={}, converted={}",
                receivedQnas.size(),
                converted);

        return converted;
    }
}
