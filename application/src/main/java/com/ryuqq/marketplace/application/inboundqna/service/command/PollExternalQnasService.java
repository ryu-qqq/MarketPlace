package com.ryuqq.marketplace.application.inboundqna.service.command;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaCommandManager;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.PollExternalQnasUseCase;
import com.ryuqq.marketplace.application.inboundqna.port.out.client.SalesChannelQnaClient;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PollExternalQnasService implements PollExternalQnasUseCase {

    private static final Logger log = LoggerFactory.getLogger(PollExternalQnasService.class);
    private static final long DEFAULT_LOOKBACK_SECONDS = 86400; // 1일

    private final List<SalesChannelQnaClient> qnaClients;
    private final InboundQnaReadManager readManager;
    private final InboundQnaCommandManager commandManager;
    private final InboundQnaConversionProcessor conversionProcessor;

    public PollExternalQnasService(
            List<SalesChannelQnaClient> qnaClients,
            InboundQnaReadManager readManager,
            InboundQnaCommandManager commandManager,
            InboundQnaConversionProcessor conversionProcessor) {
        this.qnaClients = qnaClients;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.conversionProcessor = conversionProcessor;
    }

    @Override
    public int execute(long salesChannelId, int batchSize) {
        Instant now = Instant.now();
        Instant from = now.minusSeconds(DEFAULT_LOOKBACK_SECONDS);

        // 지원하는 클라이언트 찾기 (현재는 채널코드 기반 라우팅 간소화)
        SalesChannelQnaClient client =
                qnaClients.stream()
                        .filter(c -> c.supports(String.valueOf(salesChannelId)))
                        .findFirst()
                        .orElse(null);

        if (client == null) {
            log.debug("QnA 폴링 지원하지 않는 채널: salesChannelId={}", salesChannelId);
            return 0;
        }

        List<ExternalQnaPayload> payloads =
                client.fetchNewQnas(salesChannelId, from, now, batchSize);
        if (payloads.isEmpty()) {
            return 0;
        }

        // 중복 제거
        List<ExternalQnaPayload> newPayloads =
                payloads.stream()
                        .filter(
                                p ->
                                        !readManager.existsBySalesChannelIdAndExternalQnaId(
                                                salesChannelId, p.externalQnaId()))
                        .toList();

        if (newPayloads.isEmpty()) {
            return 0;
        }

        List<InboundQna> newQnas =
                newPayloads.stream()
                        .map(
                                p ->
                                        InboundQna.forNew(
                                                salesChannelId,
                                                p.externalQnaId(),
                                                parseQnaType(p.qnaType()),
                                                p.questionContent(),
                                                p.questionAuthor(),
                                                p.rawPayload(),
                                                now))
                        .toList();

        commandManager.persistAll(newQnas);
        log.info("InboundQna 수신 완료: salesChannelId={}, 신규 {}건", salesChannelId, newQnas.size());

        // 수신 직후 즉시 변환 시도 (개별 실패가 전체를 중단하지 않음)
        for (int i = 0; i < newQnas.size(); i++) {
            ExternalQnaPayload payload = newPayloads.get(i);
            InboundQna inboundQna = newQnas.get(i);
            try {
                conversionProcessor.convert(
                        inboundQna, payload.externalProductId(), payload.externalOrderId());
            } catch (Exception e) {
                log.warn(
                        "InboundQna 즉시 변환 실패: externalQnaId={}, reason={}",
                        inboundQna.externalQnaId(),
                        e.getMessage());
            }
        }

        return newQnas.size();
    }

    private QnaType parseQnaType(String qnaType) {
        try {
            return QnaType.valueOf(qnaType);
        } catch (IllegalArgumentException e) {
            return QnaType.ETC;
        }
    }
}
