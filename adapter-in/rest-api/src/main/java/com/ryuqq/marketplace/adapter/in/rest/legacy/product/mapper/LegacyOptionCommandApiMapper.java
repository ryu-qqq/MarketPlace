package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 옵션 Request → Command 변환 매퍼.
 *
 * <p>LegacyCreateOptionRequest를 레거시 직접 write용 LegacyUpdateProductsCommand와 내부 시스템용
 * UpdateProductsCommand 양쪽으로 변환합니다.
 */
@Component
public class LegacyOptionCommandApiMapper {

    /**
     * List&lt;LegacyCreateOptionRequest&gt; → UpdateProductsCommand (내부 시스템용).
     *
     * <p>null 또는 empty 리스트인 경우 {@link IllegalArgumentException}을 던집니다.
     */
    public UpdateProductsCommand toUpdateProductsCommand(
            long productGroupId, List<LegacyCreateOptionRequest> request) {
        if (request == null || request.isEmpty()) {
            throw new IllegalArgumentException("옵션 목록은 비어있을 수 없습니다");
        }

        List<UpdateProductsCommand.OptionGroupData> optionGroups = toOptionGroups(request);
        List<UpdateProductsCommand.ProductData> products = toProducts(request);

        return new UpdateProductsCommand(productGroupId, optionGroups, products);
    }

    private List<UpdateProductsCommand.OptionGroupData> toOptionGroups(
            List<LegacyCreateOptionRequest> options) {
        Map<String, Map<String, LegacyCreateOptionRequest.OptionDetail>> groupMap =
                new LinkedHashMap<>();
        Map<String, Long> groupIdByOptionName = new LinkedHashMap<>();

        for (LegacyCreateOptionRequest option : options) {
            if (option.options() == null) {
                continue;
            }
            for (LegacyCreateOptionRequest.OptionDetail detail : option.options()) {
                groupMap.computeIfAbsent(detail.optionName(), k -> new LinkedHashMap<>())
                        .putIfAbsent(detail.optionValue(), detail);
                if (detail.optionGroupId() != null
                        && !groupIdByOptionName.containsKey(detail.optionName())) {
                    groupIdByOptionName.put(detail.optionName(), detail.optionGroupId());
                }
            }
        }

        List<UpdateProductsCommand.OptionGroupData> result = new ArrayList<>();
        int valueSortOrder = 0;
        for (var entry : groupMap.entrySet()) {
            String groupName = entry.getKey();
            Map<String, LegacyCreateOptionRequest.OptionDetail> values = entry.getValue();
            Long sellerOptionGroupId = groupIdByOptionName.get(groupName);

            List<UpdateProductsCommand.OptionValueData> valueDataList = new ArrayList<>();
            for (var valueEntry : values.entrySet()) {
                LegacyCreateOptionRequest.OptionDetail detail = valueEntry.getValue();
                valueDataList.add(
                        new UpdateProductsCommand.OptionValueData(
                                detail.optionDetailId(),
                                detail.optionValue(),
                                null,
                                valueSortOrder++));
            }

            result.add(
                    new UpdateProductsCommand.OptionGroupData(
                            sellerOptionGroupId, groupName, null, "PREDEFINED", valueDataList));
        }
        return result;
    }

    private List<UpdateProductsCommand.ProductData> toProducts(
            List<LegacyCreateOptionRequest> options) {
        List<UpdateProductsCommand.ProductData> result = new ArrayList<>();
        int sortOrder = 0;
        for (LegacyCreateOptionRequest option : options) {
            List<SelectedOption> selectedOptions =
                    option.options() != null
                            ? option.options().stream()
                                    .map(d -> new SelectedOption(d.optionName(), d.optionValue()))
                                    .toList()
                            : List.of();

            int additionalPrice =
                    option.additionalPrice() != null ? option.additionalPrice().intValue() : 0;
            int regularPrice = additionalPrice;
            int currentPrice = additionalPrice;

            result.add(
                    new UpdateProductsCommand.ProductData(
                            option.productId(),
                            null,
                            regularPrice,
                            currentPrice,
                            option.quantity() != null ? option.quantity() : 0,
                            ++sortOrder,
                            selectedOptions));
        }
        return result;
    }

    /**
     * UpdateProductsCommand → ProductGroupUpdateBundle용 옵션/상품 파트 변환.
     *
     * <p>FullProductGroupUpdateCoordinator가 요구하는 UpdateSellerOptionGroupsCommand와
     * List&lt;ProductDiffUpdateEntry&gt;를 반환합니다.
     */
    public UpdateSellerOptionGroupsCommand toUpdateSellerOptionGroupsCommand(
            long productGroupId, UpdateProductsCommand command) {
        if (command.optionGroups().isEmpty()) {
            return null;
        }
        var groups =
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
        return new UpdateSellerOptionGroupsCommand(productGroupId, groups);
    }

    /** UpdateProductsCommand.products → List&lt;ProductDiffUpdateEntry&gt; */
    public List<ProductDiffUpdateEntry> toProductEntries(UpdateProductsCommand command) {
        return command.products().stream()
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
