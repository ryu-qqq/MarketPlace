package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.outboundsync.port.out.strategy.OutboundSyncExecutionStrategy;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 상품 삭제(DELETE) 전략.
 *
 * <p>OutboundProduct에서 externalProductId 조회 → DELETE API 호출. CREATE/UPDATE와 달리 상품 데이터 조회 불필요.
 */
@Component
public class NaverDeleteProductStrategy implements OutboundSyncExecutionStrategy {

    private static final Logger log = LoggerFactory.getLogger(NaverDeleteProductStrategy.class);
    private static final String NAVER_CHANNEL_CODE = "NAVER";

    private final OutboundProductReadManager outboundProductReadManager;
    private final SalesChannelProductClient productClient;

    public NaverDeleteProductStrategy(
            OutboundProductReadManager outboundProductReadManager,
            SalesChannelProductClient productClient) {
        this.outboundProductReadManager = outboundProductReadManager;
        this.productClient = productClient;
    }

    @Override
    public boolean supports(String channelCode, SyncType syncType) {
        return NAVER_CHANNEL_CODE.equals(channelCode) && syncType == SyncType.DELETE;
    }

    @Override
    public OutboundSyncExecutionResult execute(OutboundSyncExecutionContext context) {
        Long salesChannelId = context.outbox().salesChannelIdValue();
        Long productGroupId = context.productGroupId();

        try {
            OutboundProduct outboundProduct =
                    outboundProductReadManager.getByProductGroupIdAndSalesChannelId(
                            productGroupId, salesChannelId);

            if (!outboundProduct.isRegistered() || outboundProduct.externalProductId() == null) {
                return OutboundSyncExecutionResult.failure("외부 상품 미등록 상태", false);
            }

            productClient.deleteProduct(
                    outboundProduct.externalProductId(), context.sellerSalesChannel());

            log.info(
                    "네이버 상품 삭제 성공: productGroupId={}, externalProductId={}",
                    productGroupId,
                    outboundProduct.externalProductId());

            return OutboundSyncExecutionResult.success(outboundProduct.externalProductId());

        } catch (Exception e) {
            log.error(
                    "네이버 상품 삭제 실패: productGroupId={}, error={}", productGroupId, e.getMessage(), e);
            return OutboundSyncExecutionResult.failure(e.getMessage(), true);
        }
    }
}
