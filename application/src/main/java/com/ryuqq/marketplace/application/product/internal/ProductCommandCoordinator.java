package com.ryuqq.marketplace.application.product.internal;

import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.product.factory.ProductCommandFactory;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductOptionMappingCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.selleroption.dto.result.SellerOptionUpdateResult;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.exception.ProductNotFoundException;
import com.ryuqq.marketplace.domain.product.vo.ProductCreationData;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Product Command Coordinator.
 *
 * <p>Product + OptionMapping 등록/수정을 조율합니다.
 *
 * <p>Command 기반 등록: {@link #register(List)} — Product 도메인 객체 → persist
 *
 * <p>OptionGroup diff 기반 수정: {@link #updateWithDiff} — productId 기반 매칭 → retained/added/removed 분류
 * → persist
 */
@Component
public class ProductCommandCoordinator {

    private final ProductCommandFactory productCommandFactory;
    private final ProductCommandManager productCommandManager;
    private final ProductOptionMappingCommandManager optionMappingCommandManager;
    private final ProductReadManager productReadManager;

    public ProductCommandCoordinator(
            ProductCommandFactory productCommandFactory,
            ProductCommandManager productCommandManager,
            ProductOptionMappingCommandManager optionMappingCommandManager,
            ProductReadManager productReadManager) {
        this.productCommandFactory = productCommandFactory;
        this.productCommandManager = productCommandManager;
        this.optionMappingCommandManager = optionMappingCommandManager;
        this.productReadManager = productReadManager;
    }

    /**
     * 도메인 객체 기반 Product + OptionMapping 등록.
     *
     * @param products Product 도메인 객체 목록
     * @return 생성된 Product ID 목록
     */
    @Transactional
    public List<Long> register(List<Product> products) {
        List<Long> productIds = productCommandManager.persistAll(products);

        for (int i = 0; i < products.size(); i++) {
            optionMappingCommandManager.persistAllForProduct(
                    productIds.get(i), products.get(i).optionMappings());
        }

        return productIds;
    }

    /**
     * SellerOption 수정 결과 기반 Product 수정.
     *
     * <p>productId 기반으로 기존 Product와 새 ProductData를 매칭합니다.
     *
     * <ul>
     *   <li>retained: productId가 non-null이고 기존에 존재 → 가격/재고/SKU/정렬 갱신
     *   <li>added: productId가 null → Product 신규 생성
     *   <li>removed: entries에 포함되지 않은 기존 Product → soft delete
     * </ul>
     *
     * @param pgId 상품 그룹 ID
     * @param entries Product diff 엔트리 목록 (productId 포함)
     * @param optionResult SellerOption 수정 결과 (resolved 활성 ValueId 목록 포함)
     * @param optionGroups 옵션 그룹 데이터 (이름→ID resolve용)
     */
    @Transactional
    public void updateWithDiff(
            ProductGroupId pgId,
            List<ProductDiffUpdateEntry> entries,
            SellerOptionUpdateResult optionResult,
            List<UpdateProductsCommand.OptionGroupData> optionGroups) {
        Instant now = optionResult.occurredAt();
        List<SellerOptionValueId> allActiveValueIds = optionResult.resolvedActiveValueIds();

        Map<String, Map<String, SellerOptionValueId>> optionNameMap =
                buildOptionNameMap(optionGroups, allActiveValueIds);

        List<Product> existingProducts = productReadManager.findByProductGroupId(pgId);
        Map<Long, Product> existingById =
                existingProducts.stream().collect(Collectors.toMap(Product::idValue, p -> p));

        List<Product> retained = new ArrayList<>();
        List<Product> added = new ArrayList<>();
        Set<Long> matchedProductIds = new HashSet<>();

        for (ProductDiffUpdateEntry entry : entries) {
            if (entry.productId() != null) {
                Product existing = existingById.get(entry.productId());
                if (existing == null) {
                    throw new ProductNotFoundException(entry.productId());
                }
                SkuCode skuCode =
                        (entry.skuCode() != null && !entry.skuCode().isBlank())
                                ? SkuCode.of(entry.skuCode())
                                : existing.skuCode();
                existing.update(
                        skuCode,
                        Money.of(entry.regularPrice()),
                        Money.of(entry.currentPrice()),
                        entry.stockQuantity(),
                        entry.sortOrder(),
                        now);
                retained.add(existing);
                matchedProductIds.add(entry.productId());
            } else {
                List<SellerOptionValueId> resolvedIds =
                        resolveOptionIds(entry.selectedOptions(), optionNameMap);
                ProductCreationData creationData =
                        productCommandFactory.toCreationData(entry, resolvedIds);
                Product newProduct = creationData.toProduct(pgId, now);
                added.add(newProduct);
            }
        }

        List<Product> removed = new ArrayList<>();
        for (Product product : existingProducts) {
            if (!matchedProductIds.contains(product.idValue())) {
                product.delete(now);
                removed.add(product);
            }
        }

        productCommandManager.persistAll(retained);
        productCommandManager.persistAll(removed);
        register(added);
    }

    /**
     * optionGroups + resolvedActiveValueIds → Map&lt;groupName, Map&lt;valueName,
     * SellerOptionValueId&gt;&gt; 변환.
     *
     * <p>옵션 그룹과 값의 이름을 기반으로 실제 SellerOptionValueId를 매핑합니다. resolvedActiveValueIds는 그룹 순서대로 플랫하게
     * 정렬되어 있어야 합니다.
     */
    private Map<String, Map<String, SellerOptionValueId>> buildOptionNameMap(
            List<UpdateProductsCommand.OptionGroupData> optionGroups,
            List<SellerOptionValueId> resolvedActiveValueIds) {
        Map<String, Map<String, SellerOptionValueId>> nameMap = new LinkedHashMap<>();
        int index = 0;
        for (UpdateProductsCommand.OptionGroupData group : optionGroups) {
            Map<String, SellerOptionValueId> valueMap = new LinkedHashMap<>();
            for (UpdateProductsCommand.OptionValueData value : group.optionValues()) {
                valueMap.put(value.optionValueName(), resolvedActiveValueIds.get(index++));
            }
            nameMap.put(group.optionGroupName(), valueMap);
        }
        return nameMap;
    }

    /**
     * selectedOptions + 옵션 이름 맵 → List&lt;SellerOptionValueId&gt; 변환.
     *
     * @param selectedOptions 이름 기반 옵션 선택 목록
     * @param optionNameMap 그룹명 → (값명 → SellerOptionValueId) 맵
     * @return resolve된 SellerOptionValueId 목록
     */
    private List<SellerOptionValueId> resolveOptionIds(
            List<SelectedOption> selectedOptions,
            Map<String, Map<String, SellerOptionValueId>> optionNameMap) {
        return selectedOptions.stream()
                .map(
                        so -> {
                            Map<String, SellerOptionValueId> valueMap =
                                    optionNameMap.get(so.optionGroupName());
                            if (valueMap == null) {
                                throw new IllegalArgumentException(
                                        "존재하지 않는 옵션 그룹: " + so.optionGroupName());
                            }
                            SellerOptionValueId valueId = valueMap.get(so.optionValueName());
                            if (valueId == null) {
                                throw new IllegalArgumentException(
                                        "존재하지 않는 옵션 값: "
                                                + so.optionGroupName()
                                                + " > "
                                                + so.optionValueName());
                            }
                            return valueId;
                        })
                .toList();
    }
}
