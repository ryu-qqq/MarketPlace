package com.ryuqq.marketplace.application.inboundqna.service.command;

import com.ryuqq.marketplace.application.inboundqna.dto.external.QnaUpdatePayload;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.UpdateQnaWebhookUseCase;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** QnA 수정 웹훅 수신 서비스. */
@Service
public class UpdateQnaWebhookService implements UpdateQnaWebhookUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateQnaWebhookService.class);

    private final QnaReadManager qnaReadManager;
    private final QnaCommandManager qnaCommandManager;

    public UpdateQnaWebhookService(
            QnaReadManager qnaReadManager, QnaCommandManager qnaCommandManager) {
        this.qnaReadManager = qnaReadManager;
        this.qnaCommandManager = qnaCommandManager;
    }

    @Override
    public int execute(List<QnaUpdatePayload> payloads, long salesChannelId) {
        Instant now = Instant.now();
        int updated = 0;

        for (QnaUpdatePayload payload : payloads) {
            try {
                Qna qna =
                        qnaReadManager.getBySalesChannelIdAndExternalQnaId(
                                salesChannelId, payload.externalQnaId());

                qna.updateQuestion(payload.questionTitle(), payload.questionContent(), now);
                qnaCommandManager.persist(qna);
                updated++;

                log.debug("QnA 수정 완료: externalQnaId={}", payload.externalQnaId());

            } catch (Exception e) {
                log.warn(
                        "QnA 수정 실패: externalQnaId={}, reason={}",
                        payload.externalQnaId(),
                        e.getMessage());
            }
        }

        log.info(
                "QnA 수정 웹훅 완료: salesChannelId={}, total={}, updated={}",
                salesChannelId,
                payloads.size(),
                updated);

        return updated;
    }
}
