package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyOptionCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateDisplayYnRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyCreateProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.mapper.LegacyDescriptionCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.mapper.LegacyImageCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.application.legacy.productcontext.dto.command.LegacyDeliveryData;
import com.ryuqq.marketplace.application.legacy.productcontext.dto.command.LegacyRefundData;
import com.ryuqq.marketplace.application.legacy.productcontext.dto.command.ResolveLegacyProductContextCommand;
import com.ryuqq.marketplace.application.legacy.productcontext.dto.result.LegacyProductContext;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyNoticeCategoryResolver;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
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
import java.util.stream.IntStream;
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
    private final com.ryuqq.marketplace.adapter.in.rest.legacy.product.validator.LegacyOptionValidator optionValidator;

    public LegacyProductGroupCommandApiMapper(
            LegacyNoticeCategoryResolver legacyNoticeCategoryResolver,
            LegacyImageCommandApiMapper legacyImageCommandApiMapper,
            LegacyDescriptionCommandApiMapper legacyDescriptionCommandApiMapper,
            LegacyOptionCommandApiMapper legacyOptionCommandApiMapper,
            com.ryuqq.marketplace.adapter.in.rest.legacy.product.validator.LegacyOptionValidator optionValidator) {
        this.legacyNoticeCategoryResolver = legacyNoticeCategoryResolver;
        this.legacyImageCommandApiMapper = legacyImageCommandApiMapper;
        this.legacyDescriptionCommandApiMapper = legacyDescriptionCommandApiMapper;
        this.legacyOptionCommandApiMapper = legacyOptionCommandApiMapper;
        this.optionValidator = optionValidator;
    }

    /** LegacyCreateProductGroupRequest → ResolveLegacyProductContextCommand. */
    public ResolveLegacyProductContextCommand toResolveContextCommand(
            LegacyCreateProductGroupRequest request) {
        return new ResolveLegacyProductContextCommand(
                request.sellerId(),
                request.brandId(),
                request.categoryId(),
                new LegacyDeliveryData(
                        request.deliveryNotice().deliveryArea(),
                        request.deliveryNotice().deliveryFee(),
                        request.deliveryNotice().deliveryPeriodAverage()),
                new LegacyRefundData(
                        request.refundNotice().returnMethodDomestic(),
                        request.refundNotice().returnCourierDomestic(),
                        request.refundNotice().returnChargeDomestic(),
                        request.refundNotice().returnExchangeAreaDomestic()));
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
        Map<String, Long> fieldCodeToId =
                com.ryuqq.marketplace.domain.legacy.notice.vo.LegacyNoticeFieldMapping
                        .FIELD_CODE_TO_ID;
        long noticeCategoryId =
                com.ryuqq.marketplace.domain.legacy.notice.vo.LegacyNoticeFieldMapping
                        .LEGACY_NOTICE_CATEGORY_ID;

        if (request == null) {
            return new UpdateProductNoticeCommand(productGroupId, noticeCategoryId, List.of());
        }

        List<UpdateProductNoticeCommand.NoticeEntryCommand> entries = new ArrayList<>();
        addNoticeEntry(entries, fieldCodeToId, "material", request.material());
        addNoticeEntry(entries, fieldCodeToId, "color", request.color());
        addNoticeEntry(entries, fieldCodeToId, "size", request.size());
        addNoticeEntry(entries, fieldCodeToId, "maker", request.maker());
        addNoticeEntry(entries, fieldCodeToId, "origin", request.origin());
        addNoticeEntry(entries, fieldCodeToId, "washingMethod", request.washingMethod());
        addNoticeEntry(entries, fieldCodeToId, "yearMonth", request.yearMonth());
        addNoticeEntry(entries, fieldCodeToId, "assuranceStandard", request.assuranceStandard());
        addNoticeEntry(entries, fieldCodeToId, "asPhone", request.asPhone());

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

    /**
     * LegacyCreateProductGroupRequest + LegacyProductContext → RegisterProductGroupCommand (표준
     * 커맨드).
     *
     * <p>레거시 API 요청을 표준 등록 커맨드로 변환합니다. LegacyProductContext에서 리졸빙된 표준 ID와 정책 ID를 사용합니다.
     */
    public RegisterProductGroupCommand toRegisterCommand(
            LegacyCreateProductGroupRequest request, LegacyProductContext context) {

        optionValidator.validateForRegister(
                request.optionType().trim().toUpperCase(), request.productOptions());

        List<RegisterProductGroupCommand.ImageCommand> images =
                IntStream.range(0, request.productImageList().size())
                        .mapToObj(
                                i -> {
                                    var img = request.productImageList().get(i);
                                    return new RegisterProductGroupCommand.ImageCommand(
                                            convertLegacyImageType(img.type()),
                                            img.originUrl(),
                                            i);
                                })
                        .toList();

        List<RegisterProductGroupCommand.OptionGroupCommand> optionGroups =
                buildOptionGroups(request.productOptions());

        List<RegisterProductGroupCommand.ProductCommand> products =
                request.productOptions().stream()
                        .map(
                                opt ->
                                        new RegisterProductGroupCommand.ProductCommand(
                                                "",
                                                (int) request.price().regularPrice(),
                                                (int) request.price().currentPrice(),
                                                opt.quantity(),
                                                0,
                                                opt.options().stream()
                                                        .map(d -> new SelectedOption(
                                                                d.optionName(), d.optionValue()))
                                                        .toList()))
                        .toList();

        RegisterProductGroupCommand.DescriptionCommand description =
                new RegisterProductGroupCommand.DescriptionCommand(
                        request.detailDescription(), List.of());

        RegisterProductGroupCommand.NoticeCommand notice = null;
        if (context.noticeCategory() != null) {
            notice = toRegisterNoticeCommand(request.productNotice(), context.noticeCategory());
        }

        return new RegisterProductGroupCommand(
                context.internalSellerId(),
                context.internalBrandId(),
                context.internalCategoryId(),
                context.shippingPolicyId(),
                context.refundPolicyId(),
                request.productGroupName(),
                convertLegacyOptionType(request.optionType()),
                images,
                optionGroups,
                products,
                description,
                notice);
    }

    /** 레거시 이미지 타입(MAIN, DETAIL) → 표준 이미지 타입(THUMBNAIL, DETAIL) 변환. */
    private String convertLegacyImageType(String legacyImageType) {
        if ("MAIN".equals(legacyImageType)) return "THUMBNAIL";
        return "DETAIL";
    }

    /** 레거시 옵션 타입(OPTION_ONE, OPTION_TWO, SINGLE) → 표준 옵션 타입(SINGLE, COMBINATION, NONE) 변환. */
    private String convertLegacyOptionType(String legacyOptionType) {
        return com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType.valueOf(
                        legacyOptionType.trim().toUpperCase())
                .toInternalOptionType()
                .name();
    }

    private List<RegisterProductGroupCommand.OptionGroupCommand> buildOptionGroups(
            List<LegacyCreateOptionRequest> productOptions) {
        Map<String, List<String>> groupToValues = new java.util.LinkedHashMap<>();
        for (LegacyCreateOptionRequest opt : productOptions) {
            for (LegacyCreateOptionRequest.OptionDetail detail : opt.options()) {
                groupToValues
                        .computeIfAbsent(detail.optionName(), k -> new ArrayList<>())
                        .add(detail.optionValue());
            }
        }

        List<RegisterProductGroupCommand.OptionGroupCommand> result = new ArrayList<>();
        for (var entry : groupToValues.entrySet()) {
            List<String> uniqueValues = entry.getValue().stream().distinct().toList();
            List<RegisterProductGroupCommand.OptionValueCommand> valueCommands =
                    IntStream.range(0, uniqueValues.size())
                            .mapToObj(
                                    i ->
                                            new RegisterProductGroupCommand.OptionValueCommand(
                                                    uniqueValues.get(i), null, i))
                            .toList();
            result.add(
                    new RegisterProductGroupCommand.OptionGroupCommand(
                            entry.getKey(), null, "PREDEFINED", valueCommands));
        }
        return result;
    }

    private RegisterProductGroupCommand.NoticeCommand toRegisterNoticeCommand(
            LegacyCreateProductNoticeRequest request, NoticeCategory noticeCategory) {
        Map<String, Long> fieldCodeToId =
                com.ryuqq.marketplace.domain.legacy.notice.vo.LegacyNoticeFieldMapping
                        .FIELD_CODE_TO_ID;
        long noticeCategoryId =
                com.ryuqq.marketplace.domain.legacy.notice.vo.LegacyNoticeFieldMapping
                        .LEGACY_NOTICE_CATEGORY_ID;

        List<RegisterProductGroupCommand.NoticeEntryCommand> entries = new ArrayList<>();
        addRegisterNoticeEntry(entries, fieldCodeToId, "material", request.material());
        addRegisterNoticeEntry(entries, fieldCodeToId, "color", request.color());
        addRegisterNoticeEntry(entries, fieldCodeToId, "size", request.size());
        addRegisterNoticeEntry(entries, fieldCodeToId, "maker", request.maker());
        addRegisterNoticeEntry(entries, fieldCodeToId, "origin", request.origin());
        addRegisterNoticeEntry(entries, fieldCodeToId, "washingMethod", request.washingMethod());
        addRegisterNoticeEntry(entries, fieldCodeToId, "yearMonth", request.yearMonth());
        addRegisterNoticeEntry(
                entries, fieldCodeToId, "assuranceStandard", request.assuranceStandard());
        addRegisterNoticeEntry(entries, fieldCodeToId, "asPhone", request.asPhone());

        return new RegisterProductGroupCommand.NoticeCommand(noticeCategoryId, entries);
    }

    /** LegacyUpdateProductGroupRequest → ResolveLegacyProductContextCommand (수정용). */
    public ResolveLegacyProductContextCommand toResolveContextCommand(
            long productGroupId, LegacyUpdateProductGroupRequest request) {
        return new ResolveLegacyProductContextCommand(
                request.productGroupDetails().sellerId(),
                request.productGroupDetails().brandId(),
                request.productGroupDetails().categoryId(),
                request.deliveryNotice() != null
                        ? new LegacyDeliveryData(
                                request.deliveryNotice().deliveryArea(),
                                request.deliveryNotice().deliveryFee(),
                                request.deliveryNotice().deliveryPeriodAverage())
                        : new LegacyDeliveryData("", 0, 0),
                request.refundNotice() != null
                        ? new LegacyRefundData(
                                request.refundNotice().returnMethodDomestic(),
                                request.refundNotice().returnCourierDomestic(),
                                request.refundNotice().returnChargeDomestic(),
                                request.refundNotice().returnExchangeAreaDomestic())
                        : new LegacyRefundData("", "", 0, ""));
    }

    /**
     * LegacyUpdateProductGroupRequest + LegacyProductContext → UpdateProductGroupFullCommand (표준
     * 커맨드).
     */
    public UpdateProductGroupFullCommand toUpdateFullCommand(
            long productGroupId,
            LegacyUpdateProductGroupRequest request,
            LegacyProductContext context) {

        var updateStatus = request.updateStatus();

        List<UpdateProductGroupFullCommand.ImageCommand> images =
                updateStatus.imageStatus() && request.productImageList() != null
                        ? IntStream.range(0, request.productImageList().size())
                                .mapToObj(
                                        i -> {
                                            var img = request.productImageList().get(i);
                                            return new UpdateProductGroupFullCommand.ImageCommand(
                                                    convertLegacyImageType(img.type()),
                                                    img.originUrl(),
                                                    i);
                                        })
                                .toList()
                        : List.of();

        List<UpdateProductGroupFullCommand.OptionGroupCommand> optionGroups =
                updateStatus.stockOptionStatus() && request.productOptions() != null
                        ? buildUpdateOptionGroups(request.productOptions())
                        : List.of();

        List<UpdateProductGroupFullCommand.ProductCommand> products =
                updateStatus.stockOptionStatus() && request.productOptions() != null
                        ? request.productOptions().stream()
                                .map(
                                        opt ->
                                                new UpdateProductGroupFullCommand.ProductCommand(
                                                        opt.productId(),
                                                        "",
                                                        0,
                                                        0,
                                                        opt.quantity(),
                                                        0,
                                                        opt.options().stream()
                                                                .map(
                                                                        d ->
                                                                                new SelectedOption(
                                                                                        d
                                                                                                .optionName(),
                                                                                        d
                                                                                                .optionValue()))
                                                                .toList()))
                                .toList()
                        : List.of();

        UpdateProductGroupFullCommand.DescriptionCommand description =
                updateStatus.descriptionStatus() && request.detailDescription() != null
                        ? new UpdateProductGroupFullCommand.DescriptionCommand(
                                request.detailDescription().detailDescription(), List.of())
                        : null;

        UpdateProductGroupFullCommand.NoticeCommand notice =
                updateStatus.noticeStatus() && context.noticeCategory() != null
                        ? toUpdateNoticeCommand(request.productNotice(), context.noticeCategory())
                        : null;

        return new UpdateProductGroupFullCommand(
                productGroupId,
                "",
                context.internalBrandId(),
                context.internalCategoryId(),
                context.shippingPolicyId(),
                context.refundPolicyId(),
                "",
                images,
                optionGroups,
                products,
                description,
                notice);
    }

    private List<UpdateProductGroupFullCommand.OptionGroupCommand> buildUpdateOptionGroups(
            List<LegacyCreateOptionRequest> productOptions) {
        Map<String, List<String>> groupToValues = new java.util.LinkedHashMap<>();
        for (LegacyCreateOptionRequest opt : productOptions) {
            for (LegacyCreateOptionRequest.OptionDetail detail : opt.options()) {
                groupToValues
                        .computeIfAbsent(detail.optionName(), k -> new ArrayList<>())
                        .add(detail.optionValue());
            }
        }

        List<UpdateProductGroupFullCommand.OptionGroupCommand> result = new ArrayList<>();
        for (var entry : groupToValues.entrySet()) {
            List<String> uniqueValues = entry.getValue().stream().distinct().toList();
            List<UpdateProductGroupFullCommand.OptionValueCommand> valueCommands =
                    IntStream.range(0, uniqueValues.size())
                            .mapToObj(
                                    i ->
                                            new UpdateProductGroupFullCommand.OptionValueCommand(
                                                    null, uniqueValues.get(i), null, i))
                            .toList();
            result.add(
                    new UpdateProductGroupFullCommand.OptionGroupCommand(
                            null, entry.getKey(), null, "PREDEFINED", valueCommands));
        }
        return result;
    }

    private UpdateProductGroupFullCommand.NoticeCommand toUpdateNoticeCommand(
            LegacyCreateProductNoticeRequest request, NoticeCategory noticeCategory) {
        Map<String, Long> fieldCodeToId =
                com.ryuqq.marketplace.domain.legacy.notice.vo.LegacyNoticeFieldMapping
                        .FIELD_CODE_TO_ID;
        long noticeCategoryId =
                com.ryuqq.marketplace.domain.legacy.notice.vo.LegacyNoticeFieldMapping
                        .LEGACY_NOTICE_CATEGORY_ID;

        List<UpdateProductGroupFullCommand.NoticeEntryCommand> entries = new ArrayList<>();
        addUpdateNoticeEntry(entries, fieldCodeToId, "material", request.material());
        addUpdateNoticeEntry(entries, fieldCodeToId, "color", request.color());
        addUpdateNoticeEntry(entries, fieldCodeToId, "size", request.size());
        addUpdateNoticeEntry(entries, fieldCodeToId, "maker", request.maker());
        addUpdateNoticeEntry(entries, fieldCodeToId, "origin", request.origin());
        addUpdateNoticeEntry(entries, fieldCodeToId, "washingMethod", request.washingMethod());
        addUpdateNoticeEntry(entries, fieldCodeToId, "yearMonth", request.yearMonth());
        addUpdateNoticeEntry(
                entries, fieldCodeToId, "assuranceStandard", request.assuranceStandard());
        addUpdateNoticeEntry(entries, fieldCodeToId, "asPhone", request.asPhone());

        return new UpdateProductGroupFullCommand.NoticeCommand(noticeCategoryId, entries);
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

    private void addRegisterNoticeEntry(
            List<RegisterProductGroupCommand.NoticeEntryCommand> entries,
            Map<String, Long> fieldCodeToId,
            String fieldCode,
            String fieldValue) {
        Long fieldId = fieldCodeToId.get(fieldCode);
        if (fieldId != null && fieldValue != null && !fieldValue.isBlank()) {
            entries.add(new RegisterProductGroupCommand.NoticeEntryCommand(fieldId, fieldValue));
        }
    }

    private void addUpdateNoticeEntry(
            List<UpdateProductGroupFullCommand.NoticeEntryCommand> entries,
            Map<String, Long> fieldCodeToId,
            String fieldCode,
            String fieldValue) {
        Long fieldId = fieldCodeToId.get(fieldCode);
        if (fieldId != null && fieldValue != null && !fieldValue.isBlank()) {
            entries.add(new UpdateProductGroupFullCommand.NoticeEntryCommand(fieldId, fieldValue));
        }
    }
}
