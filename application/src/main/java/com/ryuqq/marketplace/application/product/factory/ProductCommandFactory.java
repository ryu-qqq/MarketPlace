package com.ryuqq.marketplace.application.product.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductCreationData;
import com.ryuqq.marketplace.domain.product.vo.ProductUpdateData;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Product Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 *
 * <p>옵션 이름 → SellerOptionValueId resolve와 도메인 입력 데이터 생성을 담당합니다.
 */
@Component
public class ProductCommandFactory {

    private final TimeProvider timeProvider;

    public ProductCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 가격 수정 컨텍스트 생성. */
    public StatusChangeContext<ProductId> createPriceUpdateContext(
            UpdateProductPriceCommand command) {
        return new StatusChangeContext<>(ProductId.of(command.productId()), timeProvider.now());
    }

    /** 재고 수정 컨텍스트 생성. */
    public StatusChangeContext<ProductId> createStockUpdateContext(
            UpdateProductStockCommand command) {
        return new StatusChangeContext<>(ProductId.of(command.productId()), timeProvider.now());
    }

    /**
     * 등록용 ProductCreationData 일괄 생성 (이름 기반 옵션 resolve 포함).
     *
     * @param products 상품 등록 데이터 목록
     * @param optionGroups 옵션 그룹 등록 데이터 (이름 → ID resolve용)
     * @param allOptionValueIds persist된 SellerOptionValueId 목록 (그룹 순서대로 플랫)
     * @return resolve 완료된 ProductCreationData 목록
     */
    public List<ProductCreationData> toCreationDataList(
            List<RegisterProductsCommand.ProductData> products,
            List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups,
            List<SellerOptionValueId> allOptionValueIds) {
        Map<String, Map<String, SellerOptionValueId>> nameMap =
                buildRegistrationOptionNameMap(optionGroups, allOptionValueIds);

        return products.stream()
                .map(
                        entry -> {
                            List<SellerOptionValueId> resolvedIds =
                                    resolveOptionIds(entry.selectedOptions(), nameMap);
                            return new ProductCreationData(
                                    SkuCode.of(entry.skuCode()),
                                    Money.of(entry.regularPrice()),
                                    Money.of(entry.currentPrice()),
                                    entry.stockQuantity(),
                                    entry.sortOrder(),
                                    resolvedIds);
                        })
                .toList();
    }

    /**
     * 수정용 ProductUpdateData 생성 (이름 기반 옵션 resolve 포함).
     *
     * <p>retained(productId non-null) 엔트리는 옵션 resolve 불필요. added(productId null) 엔트리만 resolve합니다.
     *
     * @param pgId 상품 그룹 ID
     * @param entries 수정 엔트리 목록
     * @param optionGroups 옵션 그룹 수정 데이터 (이름 → ID resolve용)
     * @param resolvedActiveValueIds persist 후 resolved된 활성 SellerOptionValueId 목록
     * @param updatedAt 수정 시각
     * @return resolve 완료된 ProductUpdateData
     */
    public ProductUpdateData toUpdateData(
            ProductGroupId pgId,
            List<ProductDiffUpdateEntry> entries,
            List<UpdateSellerOptionGroupsCommand.OptionGroupCommand> optionGroups,
            List<SellerOptionValueId> resolvedActiveValueIds,
            Instant updatedAt) {
        Map<String, Map<String, SellerOptionValueId>> nameMap =
                buildUpdateOptionNameMap(optionGroups, resolvedActiveValueIds);

        List<ProductUpdateData.Entry> domainEntries =
                entries.stream()
                        .map(
                                entry -> {
                                    List<SellerOptionValueId> resolvedIds =
                                            entry.productId() == null
                                                    ? resolveOptionIds(
                                                            entry.selectedOptions(), nameMap)
                                                    : List.of();
                                    SkuCode skuCode =
                                            entry.skuCode() != null && !entry.skuCode().isBlank()
                                                    ? SkuCode.of(entry.skuCode())
                                                    : null;
                                    return new ProductUpdateData.Entry(
                                            entry.productId(),
                                            skuCode,
                                            Money.of(entry.regularPrice()),
                                            Money.of(entry.currentPrice()),
                                            entry.stockQuantity(),
                                            entry.sortOrder(),
                                            resolvedIds);
                                })
                        .toList();

        return new ProductUpdateData(pgId, domainEntries, updatedAt);
    }

    // ── 옵션 이름 resolve ──

    /**
     * 등록용 옵션 이름 맵 생성.
     *
     * <p>RegisterSellerOptionGroupsCommand의 그룹/값 이름 순서와 persist된 ID 순서가 일치하는 전제하에 매핑합니다.
     */
    private Map<String, Map<String, SellerOptionValueId>> buildRegistrationOptionNameMap(
            List<RegisterSellerOptionGroupsCommand.OptionGroupCommand> optionGroups,
            List<SellerOptionValueId> allOptionValueIds) {
        Map<String, Map<String, SellerOptionValueId>> nameMap = new LinkedHashMap<>();
        int index = 0;
        for (RegisterSellerOptionGroupsCommand.OptionGroupCommand group : optionGroups) {
            Map<String, SellerOptionValueId> valueMap = new LinkedHashMap<>();
            for (RegisterSellerOptionGroupsCommand.OptionValueCommand value :
                    group.optionValues()) {
                valueMap.put(value.optionValueName(), allOptionValueIds.get(index++));
            }
            nameMap.put(group.optionGroupName(), valueMap);
        }
        return nameMap;
    }

    /**
     * 수정용 옵션 이름 맵 생성.
     *
     * <p>UpdateSellerOptionGroupsCommand의 그룹/값 이름과 resolved ID를 매핑합니다.
     */
    private Map<String, Map<String, SellerOptionValueId>> buildUpdateOptionNameMap(
            List<UpdateSellerOptionGroupsCommand.OptionGroupCommand> optionGroups,
            List<SellerOptionValueId> resolvedActiveValueIds) {
        Map<String, Map<String, SellerOptionValueId>> nameMap = new LinkedHashMap<>();
        int index = 0;
        for (UpdateSellerOptionGroupsCommand.OptionGroupCommand group : optionGroups) {
            Map<String, SellerOptionValueId> valueMap = new LinkedHashMap<>();
            for (UpdateSellerOptionGroupsCommand.OptionValueCommand value : group.optionValues()) {
                valueMap.put(value.optionValueName(), resolvedActiveValueIds.get(index++));
            }
            nameMap.put(group.optionGroupName(), valueMap);
        }
        return nameMap;
    }

    /**
     * selectedOptions + 옵션 이름 맵 → SellerOptionValueId 목록 변환.
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

    /** UpdateProductsCommand → UpdateSellerOptionGroupsCommand 변환. */
    public UpdateSellerOptionGroupsCommand toOptionCommand(UpdateProductsCommand command) {
        List<UpdateSellerOptionGroupsCommand.OptionGroupCommand> groups =
                command.optionGroups().stream()
                        .map(
                                g ->
                                        new UpdateSellerOptionGroupsCommand.OptionGroupCommand(
                                                g.sellerOptionGroupId(),
                                                g.optionGroupName(),
                                                g.canonicalOptionGroupId(),
                                                g.inputType(),
                                                g.optionValues().stream()
                                                        .map(
                                                                v ->
                                                                        new UpdateSellerOptionGroupsCommand
                                                                                .OptionValueCommand(
                                                                                v
                                                                                        .sellerOptionValueId(),
                                                                                v.optionValueName(),
                                                                                v
                                                                                        .canonicalOptionValueId(),
                                                                                v.sortOrder()))
                                                        .toList()))
                        .toList();
        return new UpdateSellerOptionGroupsCommand(command.productGroupId(), groups);
    }

    /** UpdateProductsCommand.ProductData 목록 → ProductDiffUpdateEntry 목록 변환. */
    public List<ProductDiffUpdateEntry> toEntries(
            List<UpdateProductsCommand.ProductData> products) {
        return products.stream()
                .map(
                        p ->
                                new ProductDiffUpdateEntry(
                                        p.productId(),
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        p.selectedOptions()))
                .toList();
    }
}
