package com.ryuqq.marketplace.application.inboundqna.port.in.command;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.dto.result.QnaWebhookResult;
import java.util.List;

/**
 * QnA 웹훅 수신 UseCase.
 *
 * <p>자사몰에서 QnA 웹훅을 받아 InboundQna 파이프라인으로 처리합니다.
 */
public interface ReceiveQnaWebhookUseCase {

    /**
     * QnA 웹훅을 수신하고 처리합니다.
     *
     * @param payloads 외부 QnA 페이로드 목록
     * @param salesChannelId 판매채널 ID
     * @param shopId 역조회된 Shop ID
     * @return 처리 결과
     */
    QnaWebhookResult execute(List<ExternalQnaPayload> payloads, long salesChannelId, long shopId);
}
