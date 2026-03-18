package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderConversionBundle;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.factory.LegacyOrderConversionFactory;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderCompositeReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 변환 Coordinator.
 *
 * <p>오케스트레이션만 담당합니다. 트랜잭션 없음.
 *
 * <ul>
 *   <li>조회 Phase: outbox 상태 변경, 중복 체크, luxurydb 조회 (각각 별도 트랜잭션)
 *   <li>변환 Phase: Factory에서 도메인 객체 조립 (트랜잭션 없음, 순수 로직)
 *   <li>저장 Phase: PersistenceFacade에서 Order+Cancel/Refund+Mapping 원자적 저장
 *   <li>완료 Phase: outbox 완료 처리
 * </ul>
 */
@Component
public class LegacyOrderConversionCoordinator {

    private static final Logger log = LoggerFactory.getLogger(LegacyOrderConversionCoordinator.class);

    private final LegacyOrderCompositeReadManager compositeReadManager;
    private final LegacyOrderChannelResolver channelResolver;
    private final LegacyOrderStatusMapper statusMapper;
    private final LegacyOrderConversionFactory conversionFactory;
    private final LegacyOrderPersistenceFacade persistenceFacade;
    private final LegacyOrderConversionOutboxCommandManager outboxCommandManager;
    private final LegacyOrderIdMappingReadManager mappingReadManager;

    public LegacyOrderConversionCoordinator(
            LegacyOrderCompositeReadManager compositeReadManager,
            LegacyOrderChannelResolver channelResolver,
            LegacyOrderStatusMapper statusMapper,
            LegacyOrderConversionFactory conversionFactory,
            LegacyOrderPersistenceFacade persistenceFacade,
            LegacyOrderConversionOutboxCommandManager outboxCommandManager,
            LegacyOrderIdMappingReadManager mappingReadManager) {
        this.compositeReadManager = compositeReadManager;
        this.channelResolver = channelResolver;
        this.statusMapper = statusMapper;
        this.conversionFactory = conversionFactory;
        this.persistenceFacade = persistenceFacade;
        this.outboxCommandManager = outboxCommandManager;
        this.mappingReadManager = mappingReadManager;
    }

    /**
     * 단일 Outbox 엔트리를 변환 처리합니다.
     *
     * @param outbox 처리할 Outbox
     */
    public void convert(LegacyOrderConversionOutbox outbox) {
        Instant now = Instant.now();
        long legacyOrderId = outbox.legacyOrderId();

        try {
            // 1. PROCESSING 상태로 변경
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            // 2. 중복 체크
            if (mappingReadManager.existsByLegacyOrderId(legacyOrderId)) {
                log.info("이미 이관된 주문, 건너뜀: legacyOrderId={}", legacyOrderId);
                completeOutbox(outbox, now);
                return;
            }

            // 3. luxurydb 상세 조회
            LegacyOrderCompositeResult composite = compositeReadManager.fetchOrderComposite(legacyOrderId);

            // 4. 이관 대상 확인 (ORDER_FAILED 제외)
            if (!statusMapper.isEligibleForMigration(composite.orderStatus())) {
                log.info("이관 제외 대상: legacyOrderId={}, status={}", legacyOrderId, composite.orderStatus());
                completeOutbox(outbox, now);
                return;
            }

            // 5. 채널 식별 + 상태 매핑 (순수 로직)
            LegacyOrderChannelResolver.ChannelResolution channel = channelResolver.resolve(
                    composite.externalOrderPkId(), composite.externalSiteId(), composite.interlockingSiteName());

            LegacyOrderStatusMapper.OrderStatusResolution statusResolution =
                    statusMapper.resolve(composite.orderStatus());

            String externalOrderNo = channelResolver.resolveExternalOrderNo(
                    composite.externalOrderPkId(), legacyOrderId);

            // 6. 도메인 객체 조립 (순수 변환, 트랜잭션 없음)
            LegacyOrderConversionBundle bundle = conversionFactory.create(
                    composite, channel, statusResolution, externalOrderNo,
                    outbox.legacyPaymentId(), now);

            // 7. 원자적 저장 (Order + Cancel/Refund + Mapping)
            persistenceFacade.persist(bundle);

            // 8. 완료
            completeOutbox(outbox, now);

            log.info("레거시 주문 이관 완료: legacyOrderId={} → internalOrderId={}, channel={}",
                    legacyOrderId, bundle.mapping().internalOrderId(), channel.channelName());

        } catch (Exception e) {
            log.error("레거시 주문 이관 실패: legacyOrderId={}", legacyOrderId, e);
            outboxCommandManager.failInNewTransaction(outbox, truncateMessage(e.getMessage()), Instant.now());
        }
    }

    private void completeOutbox(LegacyOrderConversionOutbox outbox, Instant now) {
        outbox.complete(now);
        outboxCommandManager.persist(outbox);
    }

    private String truncateMessage(String message) {
        if (message == null) {
            return "알 수 없는 오류";
        }
        return message.length() > 900 ? message.substring(0, 900) : message;
    }
}
