package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
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
 * 세토프 커머스 상품 수정(UPDATE) 전략.
 *
 * <p>OutboundProduct에서 externalProductId 조회 → 최신 상품 데이터 조회 → PUT API 호출. 기존 세토프 상품의 옵션명 기반
 * productId 매칭은 어댑터 레이어에서 처리합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
public class SetofUpdateProductStrategy implements OutboundSyncExecutionStrategy {

    private static final Logger log = LoggerFactory.getLogger(SetofUpdateProductStrategy.class);
    private static final String SETOF_CHANNEL_CODE = "SETOF";

    private final OutboundProductReadManager outboundProductReadManager;
    private final ProductGroupReadFacade productGroupReadFacade;
    private final OutboundMappingResolver mappingResolver;
    private final SalesChannelProductClientManager productClientManager;

    public SetofUpdateProductStrategy(
            OutboundProductReadManager outboundProductReadManager,
            ProductGroupReadFacade productGroupReadFacade,
            OutboundMappingResolver mappingResolver,
            SalesChannelProductClientManager productClientManager) {
        this.outboundProductReadManager = outboundProductReadManager;
        this.productGroupReadFacade = productGroupReadFacade;
        this.mappingResolver = mappingResolver;
        this.productClientManager = productClientManager;
    }

    @Override
    public boolean supports(String channelCode, SyncType syncType) {
        return SETOF_CHANNEL_CODE.equals(channelCode) && syncType == SyncType.UPDATE;
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

            SalesChannelMappingResult mapping =
                    mappingResolver.resolve(
                            salesChannelId,
                            bundle.queryResult().categoryId(),
                            bundle.queryResult().brandId());

            productClientManager.updateProduct(
                    SETOF_CHANNEL_CODE,
                    bundle,
                    Long.parseLong(mapping.externalCategoryCode()),
                    Long.parseLong(mapping.externalBrandCode()),
                    outboundProduct.externalProductId(),
                    context.sellerSalesChannel(),
                    context.changedAreas());

            log.info(
                    "세토프 상품 수정 성공: productGroupId={}, externalProductId={}",
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
