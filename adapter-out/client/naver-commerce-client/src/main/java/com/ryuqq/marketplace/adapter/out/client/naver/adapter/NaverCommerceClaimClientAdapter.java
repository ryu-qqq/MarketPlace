package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatus;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatusesResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetail;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceClaimMapper;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.port.out.client.SalesChannelClaimClient;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 클레임 폴링 클라이언트 어댑터.
 *
 * <p>SalesChannelClaimClient를 구현하여 네이버 커머스 클레임 변경 내역을 조회합니다. 2-phase 폴링(last-changed-statuses →
 * product-orders/query)으로 동작하며, claimType이 존재하는 변경건만 필터링합니다.
 *
 * <p>Phase 1: getLastChangedStatusesAll()로 전체 변경 내역 조회 후 클레임 변경건 필터링. Phase 2: 필터링된 상품주문번호로 상세 조회.
 * Phase 3: NaverCommerceClaimMapper로 ExternalClaimPayload 변환.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceClaimClientAdapter implements SalesChannelClaimClient {

    private static final Logger log =
            LoggerFactory.getLogger(NaverCommerceClaimClientAdapter.class);
    private static final int MAX_BATCH_SIZE = 300;

    private static final Set<String> CLAIM_CHANGE_TYPES =
            Set.of(
                    "CLAIM_REQUESTED",
                    "CLAIM_COMPLETED",
                    "CLAIM_REJECTED",
                    "COLLECT_DONE",
                    "CLAIM_REDELIVERING");

    private final NaverCommerceOrderClientAdapter orderClientAdapter;
    private final NaverCommerceClaimMapper claimMapper;

    public NaverCommerceClaimClientAdapter(
            NaverCommerceOrderClientAdapter orderClientAdapter,
            NaverCommerceClaimMapper claimMapper) {
        this.orderClientAdapter = orderClientAdapter;
        this.claimMapper = claimMapper;
    }

    @Override
    public boolean supports(String channelCode) {
        return "NAVER".equals(channelCode);
    }

    @Override
    public List<ExternalClaimPayload> fetchClaimChanges(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant fromTime,
            Instant toTime) {

        log.info(
                "네이버 클레임 폴링 시작: salesChannelId={}, from={}, to={}",
                salesChannelId,
                fromTime,
                toTime);

        List<NaverLastChangedStatus> claimChanges = pollClaimChanges(fromTime, toTime);

        if (claimChanges.isEmpty()) {
            log.info("네이버 클레임 변경 없음: salesChannelId={}", salesChannelId);
            return List.of();
        }

        log.info("네이버 클레임 변경 {}건 감지, 상세 조회 시작", claimChanges.size());

        List<String> productOrderIds =
                claimChanges.stream()
                        .map(NaverLastChangedStatus::productOrderId)
                        .distinct()
                        .toList();

        List<NaverProductOrderDetail> details = queryProductOrderDetails(productOrderIds);

        List<ExternalClaimPayload> result =
                claimMapper.toExternalClaimPayloads(claimChanges, details);

        log.info("네이버 클레임 {}건 변환 완료: salesChannelId={}", result.size(), salesChannelId);

        return result;
    }

    private List<NaverLastChangedStatus> pollClaimChanges(Instant fromTime, Instant toTime) {
        List<NaverLastChangedStatus> claimStatuses = new ArrayList<>();
        String moreSequence = null;

        do {
            NaverLastChangedStatusesResponse response =
                    orderClientAdapter.getLastChangedStatusesAll(fromTime, toTime, moreSequence);

            if (response == null || response.data() == null) {
                break;
            }

            response.data().lastChangeStatuses().stream()
                    .filter(this::isClaimChange)
                    .forEach(claimStatuses::add);

            moreSequence =
                    response.data().more() != null ? response.data().more().moreSequence() : null;

        } while (moreSequence != null);

        return claimStatuses;
    }

    private boolean isClaimChange(NaverLastChangedStatus status) {
        return status.claimType() != null && CLAIM_CHANGE_TYPES.contains(status.lastChangedType());
    }

    private List<NaverProductOrderDetail> queryProductOrderDetails(List<String> productOrderIds) {
        List<NaverProductOrderDetail> allDetails = new ArrayList<>();

        for (int i = 0; i < productOrderIds.size(); i += MAX_BATCH_SIZE) {
            List<String> batch =
                    productOrderIds.subList(
                            i, Math.min(i + MAX_BATCH_SIZE, productOrderIds.size()));
            NaverProductOrderDetailResponse response = orderClientAdapter.queryProductOrders(batch);
            allDetails.addAll(response.data());
        }

        return allDetails;
    }
}
