package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.outboundsync.port.out.strategy.OutboundSyncExecutionStrategy;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 상품 등록(CREATE) 전략.
 *
 * <p>ProductGroupReadFacade로 상품 데이터 조회 → 매핑 역조회 → SalesChannelProductClient로 API 호출.
 */
@Component
public class NaverCreateProductStrategy implements OutboundSyncExecutionStrategy {

    private static final Logger log = LoggerFactory.getLogger(NaverCreateProductStrategy.class);
    private static final String NAVER_CHANNEL_CODE = "NAVER";

    private final ProductGroupReadFacade productGroupReadFacade;
    private final OutboundMappingResolver mappingResolver;
    private final SalesChannelProductClient productClient;

    public NaverCreateProductStrategy(
            ProductGroupReadFacade productGroupReadFacade,
            OutboundMappingResolver mappingResolver,
            SalesChannelProductClient productClient) {
        this.productGroupReadFacade = productGroupReadFacade;
        this.mappingResolver = mappingResolver;
        this.productClient = productClient;
    }

    @Override
    public boolean supports(String channelCode, SyncType syncType) {
        return NAVER_CHANNEL_CODE.equals(channelCode) && syncType == SyncType.CREATE;
    }

    @Override
    public OutboundSyncExecutionResult execute(OutboundSyncExecutionContext context) {
        Long salesChannelId = context.outbox().salesChannelIdValue();
        Long productGroupId = context.productGroupId();

        try {
            ProductGroupDetailBundle bundle =
                    productGroupReadFacade.getDetailBundle(productGroupId);

            Long naverCategoryId =
                    mappingResolver.resolveSalesChannelCategoryId(
                            salesChannelId, bundle.queryResult().categoryId());

            Long naverBrandId =
                    mappingResolver
                            .findSalesChannelBrandId(salesChannelId, bundle.queryResult().brandId())
                            .orElse(null);

            String externalProductId =
                    productClient.registerProduct(
                            bundle, naverCategoryId, naverBrandId, context.sellerSalesChannel());

            log.info(
                    "네이버 상품 등록 성공: productGroupId={}, externalProductId={}",
                    productGroupId,
                    externalProductId);

            return OutboundSyncExecutionResult.success(externalProductId);

        } catch (Exception e) {
            log.error(
                    "네이버 상품 등록 실패: productGroupId={}, error={}", productGroupId, e.getMessage(), e);
            return OutboundSyncExecutionResult.failure(e.getMessage(), true);
        }
    }
}
