package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreatePriceRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateDisplayYnRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductStockRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyCreateProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyOptionDto;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductStatusResponse;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdatePriceCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateStockCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult.LegacyOptionMappingResult;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult.LegacyProductResult;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyNoticeCategoryResolver;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 세토프 수정 요청 DTO → 내부 Command 변환 매퍼.
 *
 * <p>세토프 PK(productGroupId)는 placeholder로 설정하며, 실제 내부 ID는 LegacyProductCommandFacade에서 resolve 후
 * 대체합니다.
 */
@Component
public class LegacyProductCommandApiMapper {

    private final LegacyNoticeCategoryResolver legacyNoticeCategoryResolver;
    private final LegacyImageCommandApiMapper legacyImageCommandApiMapper;
    private final LegacyOptionCommandApiMapper legacyOptionCommandApiMapper;

    public LegacyProductCommandApiMapper(
            LegacyNoticeCategoryResolver legacyNoticeCategoryResolver,
            LegacyImageCommandApiMapper legacyImageCommandApiMapper,
            LegacyOptionCommandApiMapper legacyOptionCommandApiMapper) {
        this.legacyNoticeCategoryResolver = legacyNoticeCategoryResolver;
        this.legacyImageCommandApiMapper = legacyImageCommandApiMapper;
        this.legacyOptionCommandApiMapper = legacyOptionCommandApiMapper;
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
                        ? legacyImageCommandApiMapper.toImagesCommand(
                                placeholder, request.productImageList())
                        : null;

        UpdateSellerOptionGroupsCommand optionGroupCommand;
        List<ProductDiffUpdateEntry> productEntries;
        if (updateStatus != null && updateStatus.stockOptionStatus()) {
            UpdateProductsCommand updateProductsCommand =
                    legacyOptionCommandApiMapper.toUpdateProductsCommand(
                            placeholder, request.productOptions());
            optionGroupCommand =
                    legacyOptionCommandApiMapper.toUpdateSellerOptionGroupsCommand(
                            placeholder, updateProductsCommand);
            productEntries = legacyOptionCommandApiMapper.toProductEntries(updateProductsCommand);
        } else {
            optionGroupCommand = null;
            productEntries = List.of();
        }

        UpdateProductGroupDescriptionCommand descriptionCommand =
                updateStatus != null && updateStatus.descriptionStatus()
                        ? legacyImageCommandApiMapper.toDescriptionCommand(
                                placeholder, request.detailDescription())
                        : null;

        UpdateProductNoticeCommand noticeCommand =
                updateStatus != null && updateStatus.noticeStatus()
                        ? toNoticeCommand(placeholder, request.productNotice())
                        : null;

        return new ProductGroupUpdateBundle(
                basicInfoUpdateData,
                imageCommand,
                optionGroupCommand,
                descriptionCommand,
                noticeCommand,
                productEntries);
    }

    /**
     * LegacyCreateProductNoticeRequest → UpdateProductNoticeCommand.
     *
     * <p>productGroupId가 0(placeholder)이면 noticeCategoryId도 0으로 두며, LegacyProductCommandFacade의
     * replaceProductGroupId에서 실제 productGroupId 기반으로 resolver가 해석하여 대체합니다.
     */
    public UpdateProductNoticeCommand toNoticeCommand(
            long productGroupId, LegacyCreateProductNoticeRequest request) {
        long noticeCategoryId;
        NoticeCategory noticeCategory;
        if (productGroupId == 0L) {
            noticeCategoryId = 0L;
            noticeCategory = null;
        } else {
            noticeCategory = legacyNoticeCategoryResolver.resolveByProductGroupId(productGroupId);
            noticeCategoryId = noticeCategory.id().value();
        }

        if (request == null) {
            return new UpdateProductNoticeCommand(productGroupId, noticeCategoryId, List.of());
        }

        if (noticeCategory == null) {
            return new UpdateProductNoticeCommand(productGroupId, noticeCategoryId, List.of());
        }

        Map<String, Long> fieldCodeToId =
                noticeCategory.fields().stream()
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

        return new UpdateProductNoticeCommand(productGroupId, noticeCategoryId, entries);
    }

    /** LegacyCreatePriceRequest → LegacyUpdatePriceCommand. */
    public LegacyUpdatePriceCommand toPriceCommand(
            long productGroupId, LegacyCreatePriceRequest request) {
        return new LegacyUpdatePriceCommand(
                productGroupId, request.regularPrice(), request.currentPrice());
    }

    /** LegacyUpdateDisplayYnRequest → LegacyUpdateDisplayStatusCommand. */
    public LegacyUpdateDisplayStatusCommand toDisplayStatusCommand(
            long productGroupId, LegacyUpdateDisplayYnRequest request) {
        return new LegacyUpdateDisplayStatusCommand(productGroupId, request.displayYn());
    }

    /** setofProductGroupId + Request → LegacyUpdateStockCommand. */
    public LegacyUpdateStockCommand toLegacyUpdateStockCommand(
            long setofProductGroupId, List<LegacyUpdateProductStockRequest> request) {
        List<UpdateProductStockCommand> commands = toStockCommands(request);
        return new LegacyUpdateStockCommand(setofProductGroupId, commands);
    }

    /** productGroupId → LegacyMarkOutOfStockCommand. */
    public LegacyMarkOutOfStockCommand toLegacyMarkOutOfStockCommand(long productGroupId) {
        return new LegacyMarkOutOfStockCommand(productGroupId);
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
     * LegacyProductRegistrationResult → LegacyCreateProductGroupResponse.
     *
     * <p>luxurydb에 저장된 결과를 세토프 호환 응답으로 변환합니다.
     */
    public LegacyCreateProductGroupResponse toCreateResponse(
            LegacyProductRegistrationResult result) {
        return new LegacyCreateProductGroupResponse(
                result.productGroupId(), result.sellerId(), result.productIds());
    }

    /**
     * LegacyProductGroupDetailResult → Set<LegacyProductFetchResponse> (productId 역매핑 포함).
     *
     * <p>레거시 상세 조회 결과를 세토프 호환 응답으로 변환합니다. 내부 productId를 외부 productId로 역변환합니다.
     *
     * @param result 레거시 상품그룹 상세 결과
     * @param internalToExternalMap internal → external productId 매핑
     */
    public Set<LegacyProductFetchResponse> toProductFetchResponses(
            LegacyProductGroupDetailResult result, Map<Long, Long> internalToExternalMap) {
        if (result.products() == null || result.products().isEmpty()) {
            return Set.of();
        }
        return result.products().stream()
                .map(p -> toLegacyProductFetchResponse(p, internalToExternalMap))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LegacyProductFetchResponse toLegacyProductFetchResponse(
            LegacyProductResult product, Map<Long, Long> internalToExternalMap) {
        LegacyProductStatusResponse productStatus =
                LegacyProductStatusResponse.of(product.soldOut(), !product.soldOut());

        String optionString = buildLegacyOptionString(product.options());
        Set<LegacyOptionDto> options = buildLegacyOptionDtos(product.options());

        long responseProductId =
                internalToExternalMap.getOrDefault(product.productId(), product.productId());

        return new LegacyProductFetchResponse(
                responseProductId,
                product.stockQuantity(),
                productStatus,
                optionString,
                options,
                BigDecimal.ZERO);
    }

    private String buildLegacyOptionString(List<LegacyOptionMappingResult> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return "";
        }
        return mappings.stream()
                .map(m -> m.optionGroupName() + m.optionValue())
                .collect(Collectors.joining(" "));
    }

    private Set<LegacyOptionDto> buildLegacyOptionDtos(List<LegacyOptionMappingResult> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return Set.of();
        }
        return mappings.stream()
                .map(
                        m ->
                                new LegacyOptionDto(
                                        m.optionGroupId(),
                                        m.optionDetailId(),
                                        m.optionGroupName(),
                                        m.optionValue()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
