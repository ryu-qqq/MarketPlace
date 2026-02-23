package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionDetailRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductStockRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyCreateProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyOptionDto;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductStatusResponse;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import com.ryuqq.marketplace.application.product.dto.response.ResolvedProductOptionResult;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

/**
 * 레거시 세토프 수정 요청 DTO → 내부 Command 변환 매퍼.
 *
 * <p>세토프 PK(productGroupId)는 placeholder로 설정하며, 실제 내부 ID는 LegacyProductCommandService에서 resolve 후
 * 대체합니다.
 */
@Component
public class LegacyProductCommandApiMapper {

    private static final long CLOTHING_NOTICE_CATEGORY_ID = 1L;

    private final NoticeCategoryReadManager noticeCategoryReadManager;

    public LegacyProductCommandApiMapper(NoticeCategoryReadManager noticeCategoryReadManager) {
        this.noticeCategoryReadManager = noticeCategoryReadManager;
    }

    /**
     * LegacyUpdateProductGroupRequest → ProductGroupUpdateBundle.
     *
     * <p>updateStatus 플래그에 따라 해당 Command를 포함하거나 null로 설정합니다. productGroupId는 0L placeholder이며,
     * Service에서 resolved ID로 대체됩니다.
     */
    public ProductGroupUpdateBundle toUpdateBundle(LegacyUpdateProductGroupRequest request) {
        var updateStatus = request.updateStatus();
        long placeholder = 0L;

        ProductGroupUpdateData basicInfoUpdateData =
                ProductGroupUpdateData.of(
                        ProductGroupId.of(placeholder),
                        ProductGroupName.of(""),
                        BrandId.of(0L),
                        CategoryId.of(0L),
                        ShippingPolicyId.of(0L),
                        RefundPolicyId.of(0L),
                        Instant.now());

        UpdateProductGroupImagesCommand imageCommand =
                updateStatus != null && updateStatus.imageStatus()
                        ? toImagesCommand(placeholder, request.productImageList())
                        : null;

        UpdateSellerOptionGroupsCommand optionGroupCommand =
                updateStatus != null && updateStatus.stockOptionStatus()
                        ? toOptionGroupsCommand(placeholder, request.productOptions())
                        : null;

        UpdateProductGroupDescriptionCommand descriptionCommand =
                updateStatus != null && updateStatus.descriptionStatus()
                        ? toDescriptionCommand(placeholder, request.detailDescription())
                        : null;

        UpdateProductNoticeCommand noticeCommand =
                updateStatus != null && updateStatus.noticeStatus()
                        ? toNoticeCommand(placeholder, request.productNotice())
                        : null;

        List<ProductDiffUpdateEntry> productEntries =
                updateStatus != null && updateStatus.stockOptionStatus()
                        ? toProductEntries(request.productOptions())
                        : List.of();

        return new ProductGroupUpdateBundle(
                basicInfoUpdateData,
                imageCommand,
                optionGroupCommand,
                descriptionCommand,
                noticeCommand,
                productEntries);
    }

    /** LegacyCreateProductNoticeRequest → UpdateProductNoticeCommand. */
    public UpdateProductNoticeCommand toNoticeCommand(
            long productGroupId, LegacyCreateProductNoticeRequest request) {
        if (request == null) {
            return new UpdateProductNoticeCommand(
                    productGroupId, CLOTHING_NOTICE_CATEGORY_ID, List.of());
        }

        NoticeCategory clothing =
                noticeCategoryReadManager.getById(NoticeCategoryId.of(CLOTHING_NOTICE_CATEGORY_ID));
        Map<String, Long> fieldCodeToId =
                clothing.fields().stream()
                        .collect(
                                Collectors.toMap(
                                        NoticeField::fieldCodeValue, NoticeField::idValue));

        List<UpdateProductNoticeCommand.NoticeEntryCommand> entries = new ArrayList<>();
        addNoticeEntry(entries, fieldCodeToId, "material", request.material());
        addNoticeEntry(entries, fieldCodeToId, "color", request.color());
        addNoticeEntry(entries, fieldCodeToId, "size", request.size());
        addNoticeEntry(entries, fieldCodeToId, "manufacturer", request.maker());
        addNoticeEntry(entries, fieldCodeToId, "made_in", request.origin());
        addNoticeEntry(entries, fieldCodeToId, "wash_care", request.washingMethod());
        addNoticeEntry(entries, fieldCodeToId, "release_date", request.yearMonth());
        addNoticeEntry(entries, fieldCodeToId, "quality_assurance", request.assuranceStandard());

        return new UpdateProductNoticeCommand(productGroupId, CLOTHING_NOTICE_CATEGORY_ID, entries);
    }

    /** List<LegacyCreateProductImageRequest> → UpdateProductGroupImagesCommand. */
    public UpdateProductGroupImagesCommand toImagesCommand(
            long productGroupId, List<LegacyCreateProductImageRequest> request) {
        if (request == null || request.isEmpty()) {
            return new UpdateProductGroupImagesCommand(productGroupId, List.of());
        }

        List<UpdateProductGroupImagesCommand.ImageCommand> images =
                IntStream.range(0, request.size())
                        .mapToObj(
                                i -> {
                                    var img = request.get(i);
                                    return new UpdateProductGroupImagesCommand.ImageCommand(
                                            img.type(), img.originUrl(), i + 1);
                                })
                        .toList();

        return new UpdateProductGroupImagesCommand(productGroupId, images);
    }

    /** LegacyUpdateProductDescriptionRequest → UpdateProductGroupDescriptionCommand. */
    public UpdateProductGroupDescriptionCommand toDescriptionCommand(
            long productGroupId, LegacyUpdateProductDescriptionRequest request) {
        String content = request != null ? request.detailDescription() : "";
        return new UpdateProductGroupDescriptionCommand(productGroupId, content);
    }

    /**
     * List<LegacyCreateOptionRequest> → LegacyOptionConversionResult.
     *
     * <p>옵션 그룹/값 구조와 상품(SKU) 정보를 분리하여 변환합니다.
     */
    public LegacyOptionConversionResult toOptionCommands(
            long productGroupId, List<LegacyCreateOptionRequest> request) {
        if (request == null || request.isEmpty()) {
            return new LegacyOptionConversionResult(
                    new UpdateSellerOptionGroupsCommand(productGroupId, List.of()),
                    List.of(),
                    List.of());
        }

        UpdateSellerOptionGroupsCommand optionGroupCommand =
                toOptionGroupsCommand(productGroupId, request);
        List<ProductDiffUpdateEntry> productEntries = toProductEntries(request);
        List<UpdateProductsCommand.OptionGroupData> optionGroupData =
                toOptionGroupData(optionGroupCommand);

        return new LegacyOptionConversionResult(
                optionGroupCommand, productEntries, optionGroupData);
    }

    /** List<LegacyUpdateProductStockRequest> → List<UpdateProductStockCommand>. */
    public List<UpdateProductStockCommand> toStockCommands(
            List<LegacyUpdateProductStockRequest> request) {
        if (request == null || request.isEmpty()) {
            return List.of();
        }
        return request.stream()
                .map(r -> new UpdateProductStockCommand(r.productId(), r.productStockQuantity()))
                .toList();
    }

    // ===== Private helpers =====

    private UpdateSellerOptionGroupsCommand toOptionGroupsCommand(
            long productGroupId, List<LegacyCreateOptionRequest> options) {
        // 옵션 그룹 구조 추출: optionName → optionValue 매핑
        Map<String, Map<String, LegacyCreateOptionDetailRequest>> groupMap = new LinkedHashMap<>();
        for (LegacyCreateOptionRequest option : options) {
            if (option.options() == null) {
                continue;
            }
            for (LegacyCreateOptionDetailRequest detail : option.options()) {
                groupMap.computeIfAbsent(detail.optionName(), k -> new LinkedHashMap<>())
                        .putIfAbsent(detail.optionValue(), detail);
            }
        }

        List<UpdateSellerOptionGroupsCommand.OptionGroupCommand> optionGroups = new ArrayList<>();
        for (var entry : groupMap.entrySet()) {
            String groupName = entry.getKey();
            Map<String, LegacyCreateOptionDetailRequest> values = entry.getValue();

            List<UpdateSellerOptionGroupsCommand.OptionValueCommand> valueCommands =
                    new ArrayList<>();
            int valueSortOrder = 0;
            for (var valueEntry : values.entrySet()) {
                LegacyCreateOptionDetailRequest detail = valueEntry.getValue();
                valueCommands.add(
                        new UpdateSellerOptionGroupsCommand.OptionValueCommand(
                                detail.optionDetailId(),
                                detail.optionValue(),
                                null,
                                ++valueSortOrder));
            }

            optionGroups.add(
                    new UpdateSellerOptionGroupsCommand.OptionGroupCommand(
                            null, groupName, null, "PREDEFINED", valueCommands));
        }

        return new UpdateSellerOptionGroupsCommand(productGroupId, optionGroups);
    }

    private List<ProductDiffUpdateEntry> toProductEntries(List<LegacyCreateOptionRequest> options) {
        List<ProductDiffUpdateEntry> entries = new ArrayList<>();
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

            entries.add(
                    new ProductDiffUpdateEntry(
                            option.productId(),
                            null,
                            additionalPrice,
                            additionalPrice,
                            option.quantity() != null ? option.quantity() : 0,
                            ++sortOrder,
                            selectedOptions));
        }
        return entries;
    }

    private List<UpdateProductsCommand.OptionGroupData> toOptionGroupData(
            UpdateSellerOptionGroupsCommand optionGroupCommand) {
        return optionGroupCommand.optionGroups().stream()
                .map(
                        g ->
                                new UpdateProductsCommand.OptionGroupData(
                                        g.sellerOptionGroupId(),
                                        g.optionGroupName(),
                                        g.canonicalOptionGroupId(),
                                        g.inputType(),
                                        g.optionValues().stream()
                                                .map(
                                                        v ->
                                                                new UpdateProductsCommand
                                                                        .OptionValueData(
                                                                        v.sellerOptionValueId(),
                                                                        v.optionValueName(),
                                                                        v.canonicalOptionValueId(),
                                                                        v.sortOrder()))
                                                .toList()))
                .toList();
    }

    private void addNoticeEntry(
            List<UpdateProductNoticeCommand.NoticeEntryCommand> entries,
            Map<String, Long> fieldCodeToId,
            String fieldCode,
            String fieldValue) {
        Long fieldId = fieldCodeToId.get(fieldCode);
        if (fieldId != null && fieldValue != null && !fieldValue.isBlank()) {
            entries.add(new UpdateProductNoticeCommand.NoticeEntryCommand(fieldId, fieldValue));
        }
    }

    /**
     * ProductGroupDetailCompositeResult → LegacyCreateProductGroupResponse.
     *
     * <p>상품 등록 후 조회 결과를 세토프 호환 응답으로 변환합니다.
     */
    public LegacyCreateProductGroupResponse toCreateResponse(
            ProductGroupDetailCompositeResult result, long sellerId) {
        Set<LegacyProductFetchResponse> products = toProductFetchResponses(result);
        return new LegacyCreateProductGroupResponse(result.id(), sellerId, products);
    }

    /**
     * ProductGroupDetailCompositeResult → Set<LegacyProductFetchResponse>.
     *
     * <p>옵션/재고 수정 후 반환되는 상품 목록을 세토프 호환 응답으로 변환합니다.
     */
    public Set<LegacyProductFetchResponse> toProductFetchResponses(
            ProductGroupDetailCompositeResult result) {
        if (result.optionProductMatrix() == null
                || result.optionProductMatrix().products() == null) {
            return Set.of();
        }
        return result.optionProductMatrix().products().stream()
                .map(this::toProductFetchResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LegacyProductFetchResponse toProductFetchResponse(ProductDetailResult product) {
        boolean soldOut = "SOLD_OUT".equals(product.status()) || product.stockQuantity() <= 0;
        boolean display = "ON_SALE".equals(product.status()) || "ACTIVE".equals(product.status());
        LegacyProductStatusResponse productStatus =
                LegacyProductStatusResponse.of(soldOut, display);

        String optionString = buildOptionString(product.options());
        Set<LegacyOptionDto> options = buildOptionDtos(product.options());

        int additionalPrice = product.currentPrice() - product.regularPrice();
        BigDecimal additionalPriceBd =
                additionalPrice != 0 ? BigDecimal.valueOf(additionalPrice) : BigDecimal.ZERO;

        return new LegacyProductFetchResponse(
                product.id(),
                product.stockQuantity(),
                productStatus,
                optionString,
                options,
                additionalPriceBd);
    }

    private String buildOptionString(List<ResolvedProductOptionResult> options) {
        if (options == null || options.isEmpty()) {
            return "";
        }
        return options.stream()
                .map(o -> o.optionGroupName() + o.optionValueName())
                .collect(Collectors.joining(" "));
    }

    private Set<LegacyOptionDto> buildOptionDtos(List<ResolvedProductOptionResult> options) {
        if (options == null || options.isEmpty()) {
            return Set.of();
        }
        return options.stream()
                .map(
                        o ->
                                new LegacyOptionDto(
                                        o.sellerOptionGroupId(),
                                        o.sellerOptionValueId(),
                                        o.optionGroupName(),
                                        o.optionValueName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** 옵션 변환 결과 (옵션 그룹 Command + 상품 엔트리 + 옵션 그룹 데이터). */
    public record LegacyOptionConversionResult(
            UpdateSellerOptionGroupsCommand optionGroupCommand,
            List<ProductDiffUpdateEntry> productEntries,
            List<UpdateProductsCommand.OptionGroupData> optionGroupData) {}
}
