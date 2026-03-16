package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundsync.factory.OutboundSyncCommandFactory;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 상품그룹 비활성화/삭제 시 DELETE OutboundSyncOutbox 생성 코디네이터.
 *
 * <p>REGISTERED 상태의 OutboundProduct가 있는 채널에 대해서만 DELETE outbox를 생성합니다. 이미 PENDING/PROCESSING DELETE
 * outbox가 있는 채널은 중복 생성하지 않습니다.
 */
@Component
public class ProductGroupDeactivationOutboxCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(ProductGroupDeactivationOutboxCoordinator.class);

    private final ProductGroupReadManager productGroupReadManager;
    private final OutboundProductReadManager outboundProductReadManager;
    private final OutboundSyncOutboxReadManager outboxReadManager;
    private final OutboundSyncCommandFactory outboundSyncCommandFactory;
    private final OutboundSyncOutboxCommandManager outboxCommandManager;

    public ProductGroupDeactivationOutboxCoordinator(
            ProductGroupReadManager productGroupReadManager,
            OutboundProductReadManager outboundProductReadManager,
            OutboundSyncOutboxReadManager outboxReadManager,
            OutboundSyncCommandFactory outboundSyncCommandFactory,
            OutboundSyncOutboxCommandManager outboxCommandManager) {
        this.productGroupReadManager = productGroupReadManager;
        this.outboundProductReadManager = outboundProductReadManager;
        this.outboxReadManager = outboxReadManager;
        this.outboundSyncCommandFactory = outboundSyncCommandFactory;
        this.outboxCommandManager = outboxCommandManager;
    }

    /**
     * 상품그룹 비활성화/삭제 시 DELETE outbox를 생성합니다.
     *
     * @param productGroupId 비활성화/삭제된 상품그룹 ID
     */
    public void createDeleteOutboxesIfNeeded(ProductGroupId productGroupId) {
        ProductGroup productGroup = productGroupReadManager.getById(productGroupId);
        createDeleteOutboxesIfNeeded(productGroup);
    }

    /**
     * 이미 조회된 ProductGroup 객체를 사용하여 DELETE outbox를 생성합니다.
     *
     * <p>상태 변경 후 재조회 시 soft delete 필터에 의해 조회 불가능한 경우를 방지합니다.
     *
     * @param productGroup 비활성화/삭제된 상품그룹
     */
    public void createDeleteOutboxesIfNeeded(ProductGroup productGroup) {
        ProductGroupId productGroupId = productGroup.id();

        List<OutboundProduct> registeredProducts =
                outboundProductReadManager.findRegisteredByProductGroupId(productGroupId.value());

        if (registeredProducts.isEmpty()) {
            return;
        }

        List<OutboundSyncOutbox> activeOutboxes =
                outboxReadManager.findActiveByProductGroupIdAndSyncType(
                        productGroupId, SyncType.DELETE);

        Set<Long> channelsWithActiveOutbox =
                activeOutboxes.stream()
                        .map(OutboundSyncOutbox::salesChannelIdValue)
                        .collect(Collectors.toSet());

        List<OutboundProduct> productsNeedingOutbox =
                registeredProducts.stream()
                        .filter(p -> !channelsWithActiveOutbox.contains(p.salesChannelIdValue()))
                        .toList();

        if (productsNeedingOutbox.isEmpty()) {
            return;
        }

        List<OutboundSyncOutbox> outboxes =
                outboundSyncCommandFactory.createOutboxesForSync(
                        productGroupId,
                        productGroup.sellerId(),
                        productsNeedingOutbox,
                        SyncType.DELETE);

        outboxCommandManager.persistAll(outboxes);

        log.info(
                "DELETE Outbox 생성: productGroupId={}, count={}, skipped={}",
                productGroupId.value(),
                productsNeedingOutbox.size(),
                channelsWithActiveOutbox.size());
    }
}
