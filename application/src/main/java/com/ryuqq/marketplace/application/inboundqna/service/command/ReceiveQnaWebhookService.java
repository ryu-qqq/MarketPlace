package com.ryuqq.marketplace.application.inboundqna.service.command;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.dto.result.QnaWebhookResult;
import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaCommandManager;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.ReceiveQnaWebhookUseCase;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * QnA 웹훅 수신 서비스.
 *
 * <p>parentExternalQnaId가 있으면 기존 Qna에 추가 질문(addFollowUp)으로 처리하고, 없으면 새 QnA로 생성합니다.
 */
@Service
public class ReceiveQnaWebhookService implements ReceiveQnaWebhookUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReceiveQnaWebhookService.class);

    private final InboundQnaReadManager readManager;
    private final InboundQnaCommandManager commandManager;
    private final InboundQnaConversionProcessor conversionProcessor;
    private final QnaReadManager qnaReadManager;
    private final QnaCommandManager qnaCommandManager;

    public ReceiveQnaWebhookService(
            InboundQnaReadManager readManager,
            InboundQnaCommandManager commandManager,
            InboundQnaConversionProcessor conversionProcessor,
            QnaReadManager qnaReadManager,
            QnaCommandManager qnaCommandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.conversionProcessor = conversionProcessor;
        this.qnaReadManager = qnaReadManager;
        this.qnaCommandManager = qnaCommandManager;
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
                if (payload.parentExternalQnaId() != null) {
                    handleFollowUp(payload, salesChannelId, now);
                } else {
                    handleNewQna(payload, salesChannelId, now);
                }
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

    /** 새 QnA 등록 — InboundQna 파이프라인 태우기. */
    private void handleNewQna(ExternalQnaPayload payload, long salesChannelId, Instant now) {
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
    }

    /** 대댓글(추가 질문) — 부모 QnA를 찾아서 addFollowUp() 호출. */
    private void handleFollowUp(ExternalQnaPayload payload, long salesChannelId, Instant now) {
        Qna parentQna =
                qnaReadManager.getBySalesChannelIdAndExternalQnaId(
                        salesChannelId, payload.parentExternalQnaId());

        parentQna.addFollowUp(
                payload.questionContent(),
                payload.questionAuthor(),
                null,
                now);

        qnaCommandManager.persist(parentQna);

        // InboundQna에도 기록 (중복 방지용)
        InboundQna inboundQna =
                InboundQna.forNew(
                        salesChannelId,
                        payload.externalQnaId(),
                        parentQna.qnaType(),
                        payload.questionContent(),
                        payload.questionAuthor(),
                        payload.rawPayload(),
                        now);
        inboundQna.markConverted(parentQna.idValue(), now);
        commandManager.persist(inboundQna);

        log.debug(
                "대댓글 처리 완료: parentExternalQnaId={}, externalQnaId={}",
                payload.parentExternalQnaId(),
                payload.externalQnaId());
    }

    private QnaType parseQnaType(String qnaType) {
        try {
            return QnaType.valueOf(qnaType);
        } catch (IllegalArgumentException e) {
            return QnaType.ETC;
        }
    }
}
