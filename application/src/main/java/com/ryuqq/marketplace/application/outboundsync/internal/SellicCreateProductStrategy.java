package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.SalesChannelMappingResult;
import com.ryuqq.marketplace.application.outboundsync.manager.SalesChannelProductClientManager;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 셀릭 커머스 상품 등록(CREATE) 전략.
 *
 * <p>ProductGroupReadFacade로 상품 데이터 조회 → 매핑 역조회 → SalesChannelProductClient로 API 호출.
 */
@Component
@ConditionalOnProperty(prefix = "sellic-commerce", name = "base-url")
public class SellicCreateProductStrategy implements OutboundSyncExecutionStrategy {

    private static final Logger log = LoggerFactory.getLogger(SellicCreateProductStrategy.class);
    private static final String SELLIC_CHANNEL_CODE = "SELLIC";

    private final ProductGroupReadFacade productGroupReadFacade;
    private final OutboundMappingResolver mappingResolver;
    private final SalesChannelProductClientManager productClientManager;

    public SellicCreateProductStrategy(
            ProductGroupReadFacade productGroupReadFacade,
            OutboundMappingResolver mappingResolver,
            SalesChannelProductClientManager productClientManager) {
        this.productGroupReadFacade = productGroupReadFacade;
        this.mappingResolver = mappingResolver;
        this.productClientManager = productClientManager;
    }

    @Override
    public boolean supports(String channelCode, SyncType syncType) {
        return SELLIC_CHANNEL_CODE.equals(channelCode) && syncType == SyncType.CREATE;
    }

    @Override
    public OutboundSyncExecutionResult execute(OutboundSyncExecutionContext context) {
        Long salesChannelId = context.outbox().salesChannelIdValue();
        Long productGroupId = context.productGroupId();

        try {
            ProductGroupDetailBundle bundle =
                    productGroupReadFacade.getDetailBundle(productGroupId);

            SalesChannelMappingResult mapping =
                    mappingResolver.resolve(
                            salesChannelId,
                            bundle.queryResult().categoryId(),
                            bundle.queryResult().brandId());

            ProductGroupSyncData syncData = ProductGroupSyncData.from(bundle);

            String externalProductId =
                    productClientManager.registerProduct(
                            SELLIC_CHANNEL_CODE,
                            syncData,
                            Long.parseLong(mapping.externalCategoryCode()),
                            Long.parseLong(mapping.externalBrandCode()),
                            context.sellerSalesChannel(),
                            context.shop());

            log.info(
                    "셀릭 상품 등록 성공: productGroupId={}, externalProductId={}",
                    productGroupId,
                    externalProductId);

            return OutboundSyncExecutionResult.success(externalProductId);

        } catch (DomainException e) {
            log.warn(
                    "비즈니스 오류 (재시도 불필요): productGroupId={}, error={}",
                    productGroupId,
                    e.getMessage());
            return OutboundSyncExecutionResult.failure(e.getMessage(), false);
        } catch (Exception e) {
            log.error(
                    "인프라 오류 (재시도 필요): productGroupId={}, error={}",
                    productGroupId,
                    e.getMessage(),
                    e);
            return OutboundSyncExecutionResult.failure(e.getMessage(), true);
        }
    }
}
