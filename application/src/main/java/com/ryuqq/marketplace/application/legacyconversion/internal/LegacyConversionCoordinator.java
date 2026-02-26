package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupReadFacade;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.application.legacyconversion.factory.LegacyToInternalBundleFactory;
import com.ryuqq.marketplace.application.legacyconversion.factory.LegacyToInternalUpdateBundleFactory;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyProductIdMappingCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyProductIdMappingReadManager;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupUpdateCoordinator;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.exception.DefaultRefundPolicyNotFoundException;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.DefaultShippingPolicyNotFoundException;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 변환 Coordinator.
 *
 * <p>PENDING 상태의 Outbox를 받아 luxurydb에서 최신 데이터를 조회합니다. SKU 매핑 존재 여부로 신규 등록 / 업데이트를 분기합니다.
 *
 * <ul>
 *   <li>매핑 없음 → 신규 등록 (FullProductGroupRegistrationCoordinator) + SKU 매핑 생성
 *   <li>매핑 있음 → 업데이트 (FullProductGroupUpdateCoordinator) + SKU 매핑 갱신
 * </ul>
 */
@Component
@SuppressWarnings("PMD.ExcessiveImports")
public class LegacyConversionCoordinator {

    private static final Logger log = LoggerFactory.getLogger(LegacyConversionCoordinator.class);

    private final LegacyProductGroupReadFacade readFacade;
    private final LegacyToInternalBundleFactory registrationBundleFactory;
    private final LegacyToInternalUpdateBundleFactory updateBundleFactory;
    private final FullProductGroupRegistrationCoordinator registrationCoordinator;
    private final FullProductGroupUpdateCoordinator updateCoordinator;
    private final LegacyConversionOutboxCommandManager outboxCommandManager;
    private final LegacyProductIdMappingCommandManager mappingCommandManager;
    private final LegacyProductIdMappingReadManager mappingReadManager;
    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final RefundPolicyReadManager refundPolicyReadManager;

    public LegacyConversionCoordinator(
            LegacyProductGroupReadFacade readFacade,
            LegacyToInternalBundleFactory registrationBundleFactory,
            LegacyToInternalUpdateBundleFactory updateBundleFactory,
            FullProductGroupRegistrationCoordinator registrationCoordinator,
            FullProductGroupUpdateCoordinator updateCoordinator,
            LegacyConversionOutboxCommandManager outboxCommandManager,
            LegacyProductIdMappingCommandManager mappingCommandManager,
            LegacyProductIdMappingReadManager mappingReadManager,
            ShippingPolicyReadManager shippingPolicyReadManager,
            RefundPolicyReadManager refundPolicyReadManager) {
        this.readFacade = readFacade;
        this.registrationBundleFactory = registrationBundleFactory;
        this.updateBundleFactory = updateBundleFactory;
        this.registrationCoordinator = registrationCoordinator;
        this.updateCoordinator = updateCoordinator;
        this.outboxCommandManager = outboxCommandManager;
        this.mappingCommandManager = mappingCommandManager;
        this.mappingReadManager = mappingReadManager;
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
    }

    /**
     * 단일 Outbox 엔트리를 변환 처리합니다.
     *
     * @param outbox 처리할 Outbox
     */
    @Transactional
    public void convert(LegacyConversionOutbox outbox) {
        Instant now = Instant.now();
        long legacyProductGroupId = outbox.legacyProductGroupId();

        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            LegacyProductGroupDetailBundle legacyBundle =
                    readFacade.getDetail(legacyProductGroupId);

            List<LegacyProductIdMapping> existingMappings =
                    mappingReadManager.findByLegacyProductGroupId(legacyProductGroupId);

            if (existingMappings.isEmpty()) {
                handleRegistration(legacyBundle, legacyProductGroupId, now);
            } else {
                handleUpdate(legacyBundle, existingMappings, legacyProductGroupId, now);
            }

            outbox.complete(now);
            outboxCommandManager.persist(outbox);

        } catch (Exception e) {
            log.error(
                    "레거시 변환 실패: legacyProductGroupId={}, error={}",
                    legacyProductGroupId,
                    e.getMessage(),
                    e);
            outbox.failAndRetry(truncateMessage(e.getMessage()), Instant.now());
            outboxCommandManager.persist(outbox);
        }
    }

    private void handleRegistration(
            LegacyProductGroupDetailBundle legacyBundle, long legacyProductGroupId, Instant now) {

        SellerId sellerId = SellerId.of(legacyBundle.composite().sellerId());
        ShippingPolicyId shippingPolicyId = resolveShippingPolicy(sellerId);
        RefundPolicyId refundPolicyId = resolveRefundPolicy(sellerId);

        ProductGroupRegistrationBundle bundle =
                registrationBundleFactory.create(
                        legacyBundle, shippingPolicyId, refundPolicyId, now);
        ProductGroupRegistrationResult result = registrationCoordinator.register(bundle);

        persistSkuMappings(
                legacyBundle.products(),
                result.productIds(),
                legacyProductGroupId,
                result.productGroupId(),
                now);

        log.info(
                "레거시 신규 등록 완료: legacyGroupId={} → internalGroupId={}, SKU 매핑 {}건",
                legacyProductGroupId,
                result.productGroupId(),
                result.productIds().size());
    }

    private void handleUpdate(
            LegacyProductGroupDetailBundle legacyBundle,
            List<LegacyProductIdMapping> existingMappings,
            long legacyProductGroupId,
            Instant now) {

        long internalProductGroupId = deriveInternalProductGroupId(existingMappings);
        SellerId sellerId = SellerId.of(legacyBundle.composite().sellerId());
        ShippingPolicyId shippingPolicyId = resolveShippingPolicy(sellerId);
        RefundPolicyId refundPolicyId = resolveRefundPolicy(sellerId);

        ProductGroupUpdateBundle updateBundle =
                updateBundleFactory.create(
                        legacyBundle,
                        internalProductGroupId,
                        existingMappings,
                        shippingPolicyId,
                        refundPolicyId,
                        now);

        updateCoordinator.update(updateBundle);

        log.info(
                "레거시 업데이트 동기화 완료: legacyGroupId={} → internalGroupId={}",
                legacyProductGroupId,
                internalProductGroupId);
    }

    private long deriveInternalProductGroupId(List<LegacyProductIdMapping> mappings) {
        return mappings.get(0).internalProductGroupId();
    }

    private void persistSkuMappings(
            List<LegacyProductCompositeResult> legacyProducts,
            List<Long> internalProductIds,
            long legacyProductGroupId,
            long internalProductGroupId,
            Instant now) {
        List<LegacyProductIdMapping> mappings = new ArrayList<>(legacyProducts.size());
        for (int i = 0; i < legacyProducts.size(); i++) {
            mappings.add(
                    LegacyProductIdMapping.forNew(
                            legacyProducts.get(i).productId(),
                            internalProductIds.get(i),
                            legacyProductGroupId,
                            internalProductGroupId,
                            now));
        }
        mappingCommandManager.persistAll(mappings);
    }

    private ShippingPolicyId resolveShippingPolicy(SellerId sellerId) {
        return shippingPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(ShippingPolicy::id)
                .orElseThrow(() -> new DefaultShippingPolicyNotFoundException(sellerId.value()));
    }

    private RefundPolicyId resolveRefundPolicy(SellerId sellerId) {
        return refundPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(RefundPolicy::id)
                .orElseThrow(() -> new DefaultRefundPolicyNotFoundException(sellerId.value()));
    }

    private String truncateMessage(String message) {
        if (message == null) {
            return "알 수 없는 오류";
        }
        return message.length() > 900 ? message.substring(0, 900) : message;
    }
}
