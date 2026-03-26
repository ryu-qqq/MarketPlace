package com.ryuqq.marketplace.application.inboundqna.service.command;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.dto.result.QnaWebhookResult;
import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaCommandManager;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.ReceiveQnaWebhookUseCase;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * QnA 웹훅 수신 서비스.
 *
 * <p>PollExternalQnasService의 저장+변환 로직을 재사용합니다.
 */
@Service
public class ReceiveQnaWebhookService implements ReceiveQnaWebhookUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReceiveQnaWebhookService.class);

    private final InboundQnaReadManager readManager;
    private final InboundQnaCommandManager commandManager;
    private final InboundQnaConversionProcessor conversionProcessor;

    public ReceiveQnaWebhookService(
            InboundQnaReadManager readManager,
            InboundQnaCommandManager commandManager,
            InboundQnaConversionProcessor conversionProcessor) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.conversionProcessor = conversionProcessor;
    }

    @Override
    public QnaWebhookResult execute(
            List<ExternalQnaPayload> payloads, long salesChannelId, long shopId) {
        Instant now = Instant.now();
        int total = payloads.size();
        int duplicated = 0;
        int created = 0;
        int failed = 0;

        for (ExternalQnaPayload payload : payloads) {
            if (readManager.existsBySalesChannelIdAndExternalQnaId(
                    salesChannelId, payload.externalQnaId())) {
                duplicated++;
                continue;
            }

            try {
                QnaType qnaType = parseQnaType(payload.qnaType());
                InboundQna inboundQna =
                        InboundQna.forNew(
                                salesChannelId,
                                payload.externalQnaId(),
                                qnaType,
                                payload.questionContent(),
                                payload.questionAuthor(),
                                payload.rawPayload(),
                                now);

                commandManager.persist(inboundQna);

                conversionProcessor.convert(
                        inboundQna, payload.externalProductId(), payload.externalOrderId());
                created++;

            } catch (Exception e) {
                log.warn(
                        "QnA 웹훅 처리 실패: externalQnaId={}, reason={}",
                        payload.externalQnaId(),
                        e.getMessage());
                failed++;
            }
        }

        log.info(
                "QnA 웹훅 수신 완료: salesChannelId={}, total={}, created={}, duplicated={}, failed={}",
                salesChannelId,
                total,
                created,
                duplicated,
                failed);

        return QnaWebhookResult.of(total, created, duplicated, failed);
    }

    private QnaType parseQnaType(String qnaType) {
        try {
            return QnaType.valueOf(qnaType);
        } catch (IllegalArgumentException e) {
            return QnaType.ETC;
        }
    }
}
