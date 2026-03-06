package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyOptionCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateDisplayYnRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyCreateProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.mapper.LegacyDescriptionCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.mapper.LegacyImageCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.application.legacy.notice.internal.LegacyNoticeCategoryResolver;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 세토프 상품그룹 수정 요청 DTO → 내부 Command 변환 매퍼.
 *
 * <p>세토프 PK(productGroupId) 기반으로 내부 Command를 생성합니다.
 */
@Component
public class LegacyProductGroupCommandApiMapper {

    private final LegacyNoticeCategoryResolver legacyNoticeCategoryResolver;
    private final LegacyImageCommandApiMapper legacyImageCommandApiMapper;
    private final LegacyDescriptionCommandApiMapper legacyDescriptionCommandApiMapper;
    private final LegacyOptionCommandApiMapper legacyOptionCommandApiMapper;

    public LegacyProductGroupCommandApiMapper(
            LegacyNoticeCategoryResolver legacyNoticeCategoryResolver,
            LegacyImageCommandApiMapper legacyImageCommandApiMapper,
            LegacyDescriptionCommandApiMapper legacyDescriptionCommandApiMapper,
            LegacyOptionCommandApiMapper legacyOptionCommandApiMapper) {
        this.legacyNoticeCategoryResolver = legacyNoticeCategoryResolver;
        this.legacyImageCommandApiMapper = legacyImageCommandApiMapper;
        this.legacyDescriptionCommandApiMapper = legacyDescriptionCommandApiMapper;
        this.legacyOptionCommandApiMapper = legacyOptionCommandApiMapper;
    }

    /**
     * LegacyUpdateProductGroupRequest → ProductGroupUpdateBundle.
     *
     * <p>updateStatus 플래그에 따라 해당 Command를 포함하거나 null로 설정합니다. productGroupId는 0L placeholder이며,
     * Service에서 resolved ID로 대체됩니다.
     */
    public com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle
            toUpdateBundle(LegacyUpdateProductGroupRequest request) {
        var updateStatus = request.updateStatus();
        long placeholder = 0L;

        ProductGroupUpdateData basicInfoUpdateData =
                ProductGroupUpdateData.of(
                        ProductGroupId.of(placeholder),
                        ProductGroupName.of("placeholder"),
                        BrandId.of(0L),
                        CategoryId.of(0L),
                        ShippingPolicyId.of(0L),
                        RefundPolicyId.of(0L),
                        OptionType.NONE,
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
                        ? legacyDescriptionCommandApiMapper.toDescriptionCommand(
                                placeholder, request.detailDescription())
                        : null;

        UpdateProductNoticeCommand noticeCommand =
                updateStatus != null && updateStatus.noticeStatus()
                        ? toNoticeCommand(placeholder, request.productNotice())
                        : null;

        return new com.ryuqq.marketplace.application.productgroup.dto.bundle
                .ProductGroupUpdateBundle(
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
     * <p>productGroupId가 0이면 noticeCategoryId도 0으로 설정합니다.
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

    /** LegacyUpdateDisplayYnRequest → LegacyUpdateDisplayStatusCommand. */
    public LegacyUpdateDisplayStatusCommand toDisplayStatusCommand(
            long productGroupId, LegacyUpdateDisplayYnRequest request) {
        return new LegacyUpdateDisplayStatusCommand(productGroupId, request.displayYn());
    }

    /** productGroupId → LegacyMarkOutOfStockCommand. */
    public LegacyMarkOutOfStockCommand toLegacyMarkOutOfStockCommand(long productGroupId) {
        return new LegacyMarkOutOfStockCommand(productGroupId);
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
}
