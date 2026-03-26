package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.outboundproductimage.internal.OutboundImageSyncCoordinator;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.SalesChannelMappingResult;
import com.ryuqq.marketplace.application.outboundsync.manager.SalesChannelProductClientManager;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 상품 수정(UPDATE) 전략.
 *
 * <p>OutboundProduct에서 externalProductId 조회 → 이미지 동기화 → 최신 상품 데이터 조회 → 매핑 역조회 → PUT API 호출.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverUpdateProductStrategy implements OutboundSyncExecutionStrategy {

    private static final Logger log = LoggerFactory.getLogger(NaverUpdateProductStrategy.class);
    private static final String NAVER_CHANNEL_CODE = "NAVER";

    private final OutboundProductReadManager outboundProductReadManager;
    private final ProductGroupReadFacade productGroupReadFacade;
    private final OutboundMappingResolver mappingResolver;
    private final SalesChannelProductClientManager productClientManager;
    private final OutboundImageSyncCoordinator outboundImageSyncCoordinator;

    public NaverUpdateProductStrategy(
            OutboundProductReadManager outboundProductReadManager,
            ProductGroupReadFacade productGroupReadFacade,
            OutboundMappingResolver mappingResolver,
            SalesChannelProductClientManager productClientManager,
            OutboundImageSyncCoordinator outboundImageSyncCoordinator) {
        this.outboundProductReadManager = outboundProductReadManager;
        this.productGroupReadFacade = productGroupReadFacade;
        this.mappingResolver = mappingResolver;
        this.productClientManager = productClientManager;
        this.outboundImageSyncCoordinator = outboundImageSyncCoordinator;
    }

    @Override
    public boolean supports(String channelCode, SyncType syncType) {
        return NAVER_CHANNEL_CODE.equals(channelCode) && syncType == SyncType.UPDATE;
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

            ProductGroupDetailBundle bundle =
                    productGroupReadFacade.getDetailBundle(productGroupId);

            ProductGroupSyncData syncData = ProductGroupSyncData.from(bundle);

            ResolvedExternalImages resolvedImages =
                    outboundImageSyncCoordinator.syncImages(
                            outboundProduct.idValue(), NAVER_CHANNEL_CODE, bundle.group().images());

            SalesChannelMappingResult mapping =
                    mappingResolver.resolve(
                            salesChannelId,
                            bundle.queryResult().categoryId(),
                            bundle.queryResult().brandId());

            productClientManager.updateProduct(
                    NAVER_CHANNEL_CODE,
                    syncData,
                    Long.parseLong(mapping.externalCategoryCode()),
                    Long.parseLong(mapping.externalBrandCode()),
                    outboundProduct.externalProductId(),
                    context.sellerSalesChannel(),
                    context.changedAreas(),
                    resolvedImages);

            log.info(
                    "네이버 상품 수정 성공: productGroupId={}, externalProductId={}",
                    productGroupId,
                    outboundProduct.externalProductId());

            return OutboundSyncExecutionResult.success(outboundProduct.externalProductId());

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
