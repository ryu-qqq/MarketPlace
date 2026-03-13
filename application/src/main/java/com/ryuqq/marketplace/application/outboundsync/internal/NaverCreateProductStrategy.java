package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.outboundproductimage.internal.OutboundImageSyncCoordinator;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.SalesChannelMappingResult;
import com.ryuqq.marketplace.application.outboundsync.manager.SalesChannelProductClientManager;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 상품 등록(CREATE) 전략.
 *
 * <p>ProductGroupReadFacade로 상품 데이터 조회 → 이미지 동기화 → 매핑 역조회 → SalesChannelProductClient로 API 호출.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCreateProductStrategy implements OutboundSyncExecutionStrategy {

    private static final Logger log = LoggerFactory.getLogger(NaverCreateProductStrategy.class);
    private static final String NAVER_CHANNEL_CODE = "NAVER";

    private final ProductGroupReadFacade productGroupReadFacade;
    private final OutboundMappingResolver mappingResolver;
    private final SalesChannelProductClientManager productClientManager;
    private final OutboundProductReadManager outboundProductReadManager;
    private final OutboundImageSyncCoordinator outboundImageSyncCoordinator;

    public NaverCreateProductStrategy(
            ProductGroupReadFacade productGroupReadFacade,
            OutboundMappingResolver mappingResolver,
            SalesChannelProductClientManager productClientManager,
            OutboundProductReadManager outboundProductReadManager,
            OutboundImageSyncCoordinator outboundImageSyncCoordinator) {
        this.productGroupReadFacade = productGroupReadFacade;
        this.mappingResolver = mappingResolver;
        this.productClientManager = productClientManager;
        this.outboundProductReadManager = outboundProductReadManager;
        this.outboundImageSyncCoordinator = outboundImageSyncCoordinator;
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

            OutboundProduct outboundProduct =
                    outboundProductReadManager.getByProductGroupIdAndSalesChannelId(
                            productGroupId, salesChannelId);

            ResolvedExternalImages resolvedImages =
                    outboundImageSyncCoordinator.syncImages(
                            outboundProduct.idValue(), NAVER_CHANNEL_CODE, bundle.group().images());

            SalesChannelMappingResult mapping =
                    mappingResolver.resolve(
                            salesChannelId,
                            bundle.queryResult().categoryId(),
                            bundle.queryResult().brandId());

            String externalProductId =
                    productClientManager.registerProduct(
                            NAVER_CHANNEL_CODE,
                            bundle,
                            Long.parseLong(mapping.externalCategoryCode()),
                            Long.parseLong(mapping.externalBrandCode()),
                            context.sellerSalesChannel(),
                            resolvedImages);

            log.info(
                    "네이버 상품 등록 성공: productGroupId={}, externalProductId={}",
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
