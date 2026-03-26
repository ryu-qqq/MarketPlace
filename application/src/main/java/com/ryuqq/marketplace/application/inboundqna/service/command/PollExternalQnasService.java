package com.ryuqq.marketplace.application.inboundqna.service.command;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaCommandManager;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.PollExternalQnasUseCase;
import com.ryuqq.marketplace.application.inboundqna.port.out.client.SalesChannelQnaClient;
import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 외부몰 QnA 폴링 서비스 — Shop 기반.
 *
 * <p>주문 폴링(PollExternalOrdersService)과 동일한 패턴으로, SalesChannel의 활성 Shop을 순회하며 각 Shop의 credentials로
 * 외부 QnA를 수집합니다.
 */
@Service
public class PollExternalQnasService implements PollExternalQnasUseCase {

    private static final Logger log = LoggerFactory.getLogger(PollExternalQnasService.class);
    private static final long DEFAULT_LOOKBACK_SECONDS = 86400; // 1일

    private final SalesChannelReadManager salesChannelReadManager;
    private final ShopReadManager shopReadManager;
    private final List<SalesChannelQnaClient> qnaClients;
    private final InboundQnaReadManager readManager;
    private final InboundQnaCommandManager commandManager;
    private final InboundQnaConversionProcessor conversionProcessor;

    public PollExternalQnasService(
            SalesChannelReadManager salesChannelReadManager,
            ShopReadManager shopReadManager,
            List<SalesChannelQnaClient> qnaClients,
            InboundQnaReadManager readManager,
            InboundQnaCommandManager commandManager,
            InboundQnaConversionProcessor conversionProcessor) {
        this.salesChannelReadManager = salesChannelReadManager;
        this.shopReadManager = shopReadManager;
        this.qnaClients = qnaClients;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.conversionProcessor = conversionProcessor;
    }

    @Override
    public int execute(long salesChannelId, int batchSize) {
        Instant now = Instant.now();
        Instant from = now.minusSeconds(DEFAULT_LOOKBACK_SECONDS);

        SalesChannel salesChannel =
                salesChannelReadManager.getById(SalesChannelId.of(salesChannelId));
        String channelCode = salesChannel.channelName();

        SalesChannelQnaClient client =
                qnaClients.stream()
                        .filter(c -> c.channelCode().equalsIgnoreCase(channelCode))
                        .findFirst()
                        .orElse(null);

        if (client == null) {
            log.debug(
                    "QnA 폴링 지원하지 않는 채널: salesChannelId={}, channelCode={}",
                    salesChannelId,
                    channelCode);
            return 0;
        }

        List<Shop> shops = shopReadManager.findActiveBySalesChannelId(salesChannelId);
        if (shops.isEmpty()) {
            log.info("활성 Shop 없음: salesChannelId={}", salesChannelId);
            return 0;
        }

        int totalReceived = 0;
        for (Shop shop : shops) {
            try {
                List<ExternalQnaPayload> payloads =
                        client.fetchNewQnas(
                                salesChannelId,
                                shop.idValue(),
                                shop.toCredentials(),
                                from,
                                now,
                                batchSize);

                if (payloads.isEmpty()) {
                    continue;
                }

                int received = processPayloads(payloads, salesChannelId, now);
                totalReceived += received;

                if (received > 0) {
                    log.info(
                            "InboundQna 수신: channelCode={}, shopId={}, 신규 {}건",
                            channelCode,
                            shop.idValue(),
                            received);
                }
            } catch (Exception e) {
                log.error(
                        "InboundQna 폴링 실패: salesChannelId={}, shopId={}",
                        salesChannelId,
                        shop.idValue(),
                        e);
            }
        }

        return totalReceived;
    }

    private int processPayloads(
            List<ExternalQnaPayload> payloads, long salesChannelId, Instant now) {

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
