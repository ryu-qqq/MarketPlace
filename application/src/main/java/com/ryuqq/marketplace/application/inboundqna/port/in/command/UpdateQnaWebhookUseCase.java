package com.ryuqq.marketplace.application.inboundqna.port.in.command;

import com.ryuqq.marketplace.application.inboundqna.dto.external.QnaUpdatePayload;
import java.util.List;

/** QnA 수정 웹훅 수신 UseCase. */
public interface UpdateQnaWebhookUseCase {
    int execute(List<QnaUpdatePayload> payloads, long salesChannelId);
}
