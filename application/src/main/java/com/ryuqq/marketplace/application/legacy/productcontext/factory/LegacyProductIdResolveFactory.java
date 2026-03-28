package com.ryuqq.marketplace.application.legacy.productcontext.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyProductIdResolver;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 PK resolve Factory.
 *
 * <p>APP-FAC-001: Factory는 외부 의존성(TimeProvider, Resolver) 주입 시에만 도입합니다. 레거시 PK → market PK 변환과
 * Command resolve를 담당합니다.
 */
@Component
public class LegacyProductIdResolveFactory {

    private final LegacyProductIdResolver productIdResolver;
    private final TimeProvider timeProvider;

    public LegacyProductIdResolveFactory(
            LegacyProductIdResolver productIdResolver, TimeProvider timeProvider) {
        this.productIdResolver = productIdResolver;
        this.timeProvider = timeProvider;
    }

    /**
     * 레거시 productGroupId 기반으로 PK resolve 결과를 생성합니다.
     *
     * @param legacyProductGroupId 레거시 API에서 들어온 productGroupId
     * @return resolve 결과 도메인 VO
     */
    public ResolvedLegacyProductIds resolve(long legacyProductGroupId) {
        long resolvedGroupId = productIdResolver.resolveProductGroupId(legacyProductGroupId);
        Map<Long, Long> rawMap =
                productIdResolver.resolveProductIdsByLegacyGroupId(legacyProductGroupId);

        Map<Long, ProductId> productIdMap =
                rawMap.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey, e -> ProductId.of(e.getValue())));

        return new ResolvedLegacyProductIds(
                legacyProductGroupId, ProductGroupId.of(resolvedGroupId), productIdMap);
    }

    /**
     * UpdateProductsCommand의 productGroupId/productId를 market PK로 변환합니다.
     *
     * @param command 레거시 PK가 포함된 원본 Command
     * @return market PK로 변환된 Command
     */
    public UpdateProductsCommand resolveUpdateProductsCommand(UpdateProductsCommand command) {
        ResolvedLegacyProductIds resolved = resolve(command.productGroupId());

        List<UpdateProductsCommand.ProductData> resolvedProducts =
                command.products().stream().map(p -> resolveProductData(p, resolved)).toList();

        return new UpdateProductsCommand(
                resolved.resolvedProductGroupId().value(),
                command.optionGroups(),
                resolvedProducts);
    }

    /**
     * UpdateProductGroupFullCommand의 productGroupId/productId를 market PK로 변환합니다.
     *
     * @param command 레거시 PK가 포함된 원본 Command
     * @return market PK로 변환된 Command
     */
    public UpdateProductGroupFullCommand resolveUpdateFullCommand(
            UpdateProductGroupFullCommand command) {
        ResolvedLegacyProductIds resolved = resolve(command.productGroupId());

        List<UpdateProductGroupFullCommand.ProductCommand> resolvedProducts =
                command.products().stream()
                        .map(p -> resolveFullProductCommand(p, resolved))
                        .toList();

        return new UpdateProductGroupFullCommand(
                resolved.resolvedProductGroupId().value(),
                command.productGroupName(),
                command.brandId(),
                command.categoryId(),
                command.shippingPolicyId(),
                command.refundPolicyId(),
                command.optionType(),
                command.images(),
                command.optionGroups(),
                resolvedProducts,
                command.description(),
                command.notice());
    }

    /** 현재 시각을 반환합니다. */
    public Instant now() {
        return timeProvider.now();
    }

    private UpdateProductsCommand.ProductData resolveProductData(
            UpdateProductsCommand.ProductData original, ResolvedLegacyProductIds resolved) {
        Long resolvedProductId = resolveNullableProductId(original.productId(), resolved);
        return new UpdateProductsCommand.ProductData(
                resolvedProductId,
                original.skuCode(),
                original.regularPrice(),
                original.currentPrice(),
                original.stockQuantity(),
                original.sortOrder(),
                original.selectedOptions());
    }

    private UpdateProductGroupFullCommand.ProductCommand resolveFullProductCommand(
            UpdateProductGroupFullCommand.ProductCommand original,
            ResolvedLegacyProductIds resolved) {
        Long resolvedProductId = resolveNullableProductId(original.productId(), resolved);
        return new UpdateProductGroupFullCommand.ProductCommand(
                resolvedProductId,
                original.skuCode(),
                original.regularPrice(),
                original.currentPrice(),
                original.stockQuantity(),
                original.sortOrder(),
                original.selectedOptions());
    }

    private Long resolveNullableProductId(Long productId, ResolvedLegacyProductIds resolved) {
        if (productId == null || productId <= 0) {
            return productId;
        }
        return resolved.resolveProductId(productId).value();
    }
}
