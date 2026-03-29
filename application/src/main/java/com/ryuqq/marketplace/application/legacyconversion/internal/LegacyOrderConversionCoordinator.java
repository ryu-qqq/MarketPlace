package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderConversionBundle;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderResolvedIds;
import com.ryuqq.marketplace.application.legacyconversion.factory.LegacyOrderConversionFactory;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderCompositeReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyProductIdMappingReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacySellerIdMappingReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.time.Instant;
import java.util.Optional;
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
 *   <li>ID 변환 Phase: legacy_product_id_mappings + legacy_seller_id_mapping 조회
 *   <li>변환 Phase: Factory에서 도메인 객체 조립 (트랜잭션 없음, 순수 로직)
 *   <li>저장 Phase: PersistenceFacade에서 Order+Cancel/Refund+Mapping 원자적 저장
 *   <li>완료 Phase: outbox 완료 처리
 * </ul>
 */
@Component
public class LegacyOrderConversionCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(LegacyOrderConversionCoordinator.class);

    private final LegacyOrderCompositeReadManager compositeReadManager;
    private final LegacyOrderChannelResolver channelResolver;
    private final LegacyOrderStatusMapper statusMapper;
    private final LegacyOrderConversionFactory conversionFactory;
    private final LegacyOrderPersistenceFacade persistenceFacade;
    private final LegacyOrderConversionOutboxCommandManager outboxCommandManager;
    private final LegacyOrderIdMappingReadManager mappingReadManager;
    private final LegacyProductIdMappingReadManager productIdMappingReadManager;
    private final LegacySellerIdMappingReadManager sellerIdMappingReadManager;
    private final LegacyOrderStatusSyncCoordinator statusSyncCoordinator;

    public LegacyOrderConversionCoordinator(
            LegacyOrderCompositeReadManager compositeReadManager,
            LegacyOrderChannelResolver channelResolver,
            LegacyOrderStatusMapper statusMapper,
            LegacyOrderConversionFactory conversionFactory,
            LegacyOrderPersistenceFacade persistenceFacade,
            LegacyOrderConversionOutboxCommandManager outboxCommandManager,
            LegacyOrderIdMappingReadManager mappingReadManager,
            LegacyProductIdMappingReadManager productIdMappingReadManager,
            LegacySellerIdMappingReadManager sellerIdMappingReadManager,
            LegacyOrderStatusSyncCoordinator statusSyncCoordinator) {
        this.compositeReadManager = compositeReadManager;
        this.channelResolver = channelResolver;
        this.statusMapper = statusMapper;
        this.conversionFactory = conversionFactory;
        this.persistenceFacade = persistenceFacade;
        this.outboxCommandManager = outboxCommandManager;
        this.mappingReadManager = mappingReadManager;
        this.productIdMappingReadManager = productIdMappingReadManager;
        this.sellerIdMappingReadManager = sellerIdMappingReadManager;
        this.statusSyncCoordinator = statusSyncCoordinator;
    }

    public void convert(LegacyOrderConversionOutbox outbox) {
        Instant now = Instant.now();
        long legacyOrderId = outbox.legacyOrderId();

        try {
            // 1. PROCESSING 상태로 변경
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            // 2. 이미 이관된 주문이면 상태 동기화로 위임
            Optional<LegacyOrderIdMapping> existingMapping =
                    mappingReadManager.findByLegacyOrderId(legacyOrderId);
            if (existingMapping.isPresent()) {
                log.info("이미 이관된 주문, 상태 동기화 위임: legacyOrderId={}", legacyOrderId);
                statusSyncCoordinator.sync(existingMapping.get(), outbox);
                return;
            }

            // 3. luxurydb 상세 조회
            LegacyOrderCompositeResult composite =
                    compositeReadManager.fetchOrderComposite(legacyOrderId);

            // 4. 이관 대상 확인 (ORDER_FAILED 제외)
            if (!statusMapper.isEligibleForMigration(composite.orderStatus())) {
                log.info(
                        "이관 제외 대상: legacyOrderId={}, status={}",
                        legacyOrderId,
                        composite.orderStatus());
                completeOutbox(outbox, now);
                return;
            }

            // 5. 채널 식별 + 상태 매핑 (순수 로직)
            LegacyOrderChannelResolver.ChannelResolution channel =
                    channelResolver.resolve(
                            composite.externalOrderPkId(),
                            composite.externalSiteId(),
                            composite.interlockingSiteName());

            LegacyOrderStatusMapper.OrderStatusResolution statusResolution =
                    statusMapper.resolve(composite.orderStatus());

            String externalOrderNo =
                    channelResolver.resolveExternalOrderNo(
                            composite.externalOrderPkId(), legacyOrderId);

            // 6. 내부 ID 변환
            LegacyOrderResolvedIds resolvedIds = resolveInternalIds(composite);

            // 7. 도메인 객체 조립 (순수 변환, 트랜잭션 없음)
            LegacyOrderConversionBundle bundle =
                    conversionFactory.create(
                            composite,
                            channel,
                            statusResolution,
                            externalOrderNo,
                            outbox.legacyPaymentId(),
                            resolvedIds,
                            now);

            // 8. 원자적 저장 (Order + Cancel/Refund + Mapping)
            persistenceFacade.persist(bundle);

            // 9. 완료
            completeOutbox(outbox, now);

            log.info(
                    "레거시 주문 이관 완료: legacyOrderId={} → internalOrderId={}, channel={}",
                    legacyOrderId,
                    bundle.mapping().internalOrderId(),
                    channel.channelName());

        } catch (Exception e) {
            log.error("레거시 주문 이관 실패: legacyOrderId={}", legacyOrderId, e);
            outboxCommandManager.failInNewTransaction(
                    outbox, truncateMessage(e.getMessage()), Instant.now());
        }
    }

    /**
     * 레거시 ID → 내부 ID 변환.
     *
     * <p>legacy_product_id_mappings에서 상품 매핑을 조회하고, legacy_seller_id_mapping에서 셀러명을 조회합니다. 매핑이 없으면
     * 레거시 ID를 그대로 사용합니다.
     */
    private LegacyOrderResolvedIds resolveInternalIds(LegacyOrderCompositeResult composite) {
        // 상품 ID 변환
        Optional<LegacyProductIdMapping> productMapping =
                productIdMappingReadManager.findByLegacyProductId(composite.legacyProductId());

        long internalProductGroupId =
                productMapping
                        .map(LegacyProductIdMapping::internalProductGroupId)
                        .orElse(composite.productGroupId());

        long internalProductId =
                productMapping
                        .map(LegacyProductIdMapping::internalProductId)
                        .orElse(composite.legacyProductId());

        // 셀러 ID + 이름 조회
        Long internalSellerId =
                sellerIdMappingReadManager
                        .findInternalSellerIdByLegacySellerId(composite.legacySellerId())
                        .orElse(null);
        String sellerName =
                sellerIdMappingReadManager
                        .findSellerNameByLegacySellerId(composite.legacySellerId())
                        .orElse(null);

        // 브랜드 ID + 이름
        Long internalBrandId = composite.brandId();
        String brandName = composite.brandName();

        return new LegacyOrderResolvedIds(
                internalProductGroupId,
                internalProductId,
                internalSellerId,
                internalBrandId,
                sellerName,
                brandName);
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
