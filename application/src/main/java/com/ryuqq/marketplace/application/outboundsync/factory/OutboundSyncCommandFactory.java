package com.ryuqq.marketplace.application.outboundsync.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 외부 연동 Outbox/OutboundProduct 객체 생성 Factory.
 *
 * <p>순수 객체 생성만 담당합니다. 조회/필터링은 호출측(Coordinator)에서 수행합니다.
 */
@Component
public class OutboundSyncCommandFactory {

    private final TimeProvider timeProvider;

    public OutboundSyncCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 셀러의 CONNECTED 채널들에 대한 OutboundSyncOutbox 목록 생성. */
    public List<OutboundSyncOutbox> createOutboxes(
            ProductGroupId productGroupId,
            SellerId sellerId,
            List<SellerSalesChannel> connectedChannels) {
        Instant now = timeProvider.now();
        return connectedChannels.stream()
                .map(
                        channel ->
                                OutboundSyncOutbox.forNew(
                                        productGroupId,
                                        channel.salesChannelId(),
                                        channel.shopId(),
                                        sellerId,
                                        SyncType.CREATE,
                                        "{}",
                                        now))
                .toList();
    }

    /** 지정된 SyncType으로 채널별 OutboundSyncOutbox 생성. (UPDATE/DELETE 등 범용) */
    public List<OutboundSyncOutbox> createOutboxesForSync(
            ProductGroupId productGroupId,
            SellerId sellerId,
            List<OutboundProduct> outboundProducts,
            SyncType syncType) {
        return createOutboxesForSync(
                productGroupId, sellerId, outboundProducts, syncType, Set.of());
    }

    /**
     * 변경 영역 정보를 포함하여 채널별 OutboundSyncOutbox를 생성합니다.
     *
     * @param productGroupId 상품그룹 ID
     * @param sellerId 셀러 ID
     * @param outboundProducts 대상 OutboundProduct 목록
     * @param syncType 연동 타입
     * @param changedAreas 변경된 영역 집합 (비어있으면 전체 수정으로 간주)
     * @return 생성된 Outbox 목록
     */
    public List<OutboundSyncOutbox> createOutboxesForSync(
            ProductGroupId productGroupId,
            SellerId sellerId,
            List<OutboundProduct> outboundProducts,
            SyncType syncType,
            Set<ChangedArea> changedAreas) {
        Instant now = timeProvider.now();
        String payload = toPayload(changedAreas);
        return outboundProducts.stream()
                .map(
                        product ->
                                OutboundSyncOutbox.forNew(
                                        productGroupId,
                                        product.salesChannelId(),
                                        product.shopId(),
                                        sellerId,
                                        syncType,
                                        payload,
                                        now))
                .toList();
    }

    private static String toPayload(Set<ChangedArea> changedAreas) {
        if (changedAreas == null || changedAreas.isEmpty()) {
            return "{}";
        }
        String areas =
                changedAreas.stream()
                        .map(ChangedArea::name)
                        .sorted()
                        .map(name -> "\"" + name + "\"")
                        .collect(Collectors.joining(","));
        return "{\"changedAreas\":[" + areas + "]}";
    }

    /** 채널 목록에 대한 OutboundProduct 목록 생성. (필터링은 호출측에서 수행) */
    public List<OutboundProduct> createOutboundProducts(
            ProductGroupId productGroupId, List<SellerSalesChannel> channels) {
        Instant now = timeProvider.now();
        return channels.stream()
                .map(
                        channel ->
                                OutboundProduct.forNew(
                                        productGroupId,
                                        channel.salesChannelId(),
                                        channel.shopId(),
                                        now))
                .toList();
    }
}
