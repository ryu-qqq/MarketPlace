package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundsync.factory.OutboundSyncCommandFactory;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
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
 * 상품그룹 변경 시 UPDATE OutboundSyncOutbox 생성 코디네이터.
 *
 * <p>REGISTERED 상태의 OutboundProduct가 있는 채널에 대해서만 UPDATE outbox를 생성합니다. 이미 PENDING/PROCESSING UPDATE
 * outbox가 있는 채널은 중복 생성하지 않습니다 (Last-Writer-Wins).
 */
@Component
public class ProductGroupUpdateOutboxCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(ProductGroupUpdateOutboxCoordinator.class);

    private final ProductGroupReadManager productGroupReadManager;
    private final OutboundProductReadManager outboundProductReadManager;
    private final OutboundSyncOutboxReadManager outboxReadManager;
    private final OutboundSyncCommandFactory outboundSyncCommandFactory;
    private final OutboundSyncOutboxCommandManager outboxCommandManager;

    public ProductGroupUpdateOutboxCoordinator(
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
     * 상품그룹 변경 시 UPDATE outbox를 생성합니다.
     *
     * <p>변경 영역을 지정하지 않으면 전체 수정으로 간주합니다.
     *
     * @param productGroupId 변경된 상품그룹 ID
     */
    public void createUpdateOutboxesIfNeeded(ProductGroupId productGroupId) {
        createUpdateOutboxesIfNeeded(productGroupId, Set.of());
    }

    /**
     * 변경 영역 정보를 포함하여 UPDATE outbox를 생성합니다.
     *
     * @param productGroupId 변경된 상품그룹 ID
     * @param changedAreas 변경된 영역 집합 (비어있으면 전체 수정으로 간주)
     */
    public void createUpdateOutboxesIfNeeded(
            ProductGroupId productGroupId, Set<ChangedArea> changedAreas) {
        List<OutboundProduct> registeredProducts =
                outboundProductReadManager.findRegisteredByProductGroupId(productGroupId.value());

        if (registeredProducts.isEmpty()) {
            return;
        }

        List<OutboundSyncOutbox> activeOutboxes =
                outboxReadManager.findActiveByProductGroupIdAndSyncType(
                        productGroupId, SyncType.UPDATE);

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

        ProductGroup productGroup = productGroupReadManager.getById(productGroupId);

        List<OutboundSyncOutbox> outboxes =
                outboundSyncCommandFactory.createOutboxesForSync(
                        productGroupId,
                        productGroup.sellerId(),
                        productsNeedingOutbox,
                        SyncType.UPDATE,
                        changedAreas);

        outboxCommandManager.persistAll(outboxes);

        log.info(
                "UPDATE Outbox 생성: productGroupId={}, count={}, skipped={}, changedAreas={}",
                productGroupId.value(),
                productsNeedingOutbox.size(),
                channelsWithActiveOutbox.size(),
                changedAreas);
    }
}
