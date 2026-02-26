package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ReceiveInboundProductCommand → 내부 상품 Command 변환기.
 *
 * <p>순수 데이터 변환만 담당합니다. 데이터 조회(SKU→productId 등)는 호출자(Coordinator)가 수행하여 ID 맵으로 전달합니다.
 */
@Component
public class InboundProductCommandConverter {

    private static final Logger log = LoggerFactory.getLogger(InboundProductCommandConverter.class);

    /** 신규 등록: ReceiveInboundProductCommand → RegisterProductGroupCommand. */
    public RegisterProductGroupCommand toRegisterCommand(
            ReceiveInboundProductCommand command,
            InboundProduct mapping,
            ResolvedPolicies resolved) {

        String optionType = resolveOptionType(command.optionType());

        return new RegisterProductGroupCommand(
                mapping.sellerId(),
                mapping.internalBrandId(),
                mapping.internalCategoryId(),
                resolved.shippingPolicyId(),
                resolved.refundPolicyId(),
                command.productName(),
                optionType,
                toRegisterImages(command),
                toRegisterOptionGroups(command),
                toRegisterProducts(command),
                toRegisterDescription(command),
                toRegisterNotice(resolved));
    }

    /**
     * 재수신(갱신): ReceiveInboundProductCommand → UpdateProductGroupFullCommand.
     *
     * @param idMaps 기존 내부 상품의 SKU/옵션명 → ID 매핑 (Coordinator가 조회하여 전달)
     */
    public UpdateProductGroupFullCommand toUpdateCommand(
            ReceiveInboundProductCommand command,
            InboundProduct mapping,
            ResolvedPolicies resolved,
            InboundIdMaps idMaps) {

        String optionType = resolveOptionType(command.optionType());

        return new UpdateProductGroupFullCommand(
                mapping.internalProductGroupId(),
                command.productName(),
                mapping.internalBrandId(),
                mapping.internalCategoryId(),
                resolved.shippingPolicyId(),
                resolved.refundPolicyId(),
                optionType,
                toUpdateImages(command),
                toUpdateOptionGroups(command, idMaps),
                toUpdateProducts(command, idMaps),
                toUpdateDescription(command),
                toUpdateNotice(resolved));
    }

    // === Register 변환 ===

    private List<RegisterProductGroupCommand.ImageCommand> toRegisterImages(
            ReceiveInboundProductCommand command) {
        return command.images().stream()
                .map(
                        img ->
                                new RegisterProductGroupCommand.ImageCommand(
                                        img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private List<RegisterProductGroupCommand.OptionGroupCommand> toRegisterOptionGroups(
            ReceiveInboundProductCommand command) {
        return command.optionGroups().stream()
                .map(
                        og -> {
                            List<RegisterProductGroupCommand.OptionValueCommand> values =
                                    og.optionValues().stream()
                                            .map(
                                                    ov ->
                                                            new RegisterProductGroupCommand
                                                                    .OptionValueCommand(
                                                                    ov.optionValueName(),
                                                                    null,
                                                                    ov.sortOrder()))
                                            .toList();
                            return new RegisterProductGroupCommand.OptionGroupCommand(
                                    og.optionGroupName(), null, og.inputType(), values);
                        })
                .toList();
    }

    private List<RegisterProductGroupCommand.ProductCommand> toRegisterProducts(
            ReceiveInboundProductCommand command) {
        return command.products().stream()
                .map(
                        p ->
                                new RegisterProductGroupCommand.ProductCommand(
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        toSelectedOptions(p.selectedOptions())))
                .toList();
    }

    private RegisterProductGroupCommand.DescriptionCommand toRegisterDescription(
            ReceiveInboundProductCommand command) {
        String content = command.description() != null ? command.description().content() : null;
        return new RegisterProductGroupCommand.DescriptionCommand(content, List.of());
    }

    private RegisterProductGroupCommand.NoticeCommand toRegisterNotice(ResolvedPolicies resolved) {
        long noticeCategoryId =
                resolved.noticeCategoryId() != null ? resolved.noticeCategoryId() : 0L;
        List<RegisterProductGroupCommand.NoticeEntryCommand> entries =
                resolved.resolvedNoticeEntries().stream()
                        .map(
                                e ->
                                        new RegisterProductGroupCommand.NoticeEntryCommand(
                                                e.noticeFieldId(), e.fieldValue()))
                        .toList();
        return new RegisterProductGroupCommand.NoticeCommand(noticeCategoryId, entries);
    }

    // === Update 변환 (ID 매칭 포함) ===

    private List<UpdateProductGroupFullCommand.ImageCommand> toUpdateImages(
            ReceiveInboundProductCommand command) {
        return command.images().stream()
                .map(
                        img ->
                                new UpdateProductGroupFullCommand.ImageCommand(
                                        img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private List<UpdateProductGroupFullCommand.OptionGroupCommand> toUpdateOptionGroups(
            ReceiveInboundProductCommand command, InboundIdMaps idMaps) {
        return command.optionGroups().stream()
                .map(
                        og -> {
                            Long groupId = idMaps.findOptionGroupId(og.optionGroupName());
                            Map<String, Long> valueIdMap =
                                    idMaps.findOptionValueIds(og.optionGroupName());

                            List<UpdateProductGroupFullCommand.OptionValueCommand> values =
                                    og.optionValues().stream()
                                            .map(
                                                    ov ->
                                                            new UpdateProductGroupFullCommand
                                                                    .OptionValueCommand(
                                                                    valueIdMap.get(
                                                                            ov.optionValueName()),
                                                                    ov.optionValueName(),
                                                                    null,
                                                                    ov.sortOrder()))
                                            .toList();
                            return new UpdateProductGroupFullCommand.OptionGroupCommand(
                                    groupId, og.optionGroupName(), null, og.inputType(), values);
                        })
                .toList();
    }

    private List<UpdateProductGroupFullCommand.ProductCommand> toUpdateProducts(
            ReceiveInboundProductCommand command, InboundIdMaps idMaps) {
        return command.products().stream()
                .map(
                        p ->
                                new UpdateProductGroupFullCommand.ProductCommand(
                                        idMaps.findProductId(p.skuCode()),
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        toSelectedOptions(p.selectedOptions())))
                .toList();
    }

    private UpdateProductGroupFullCommand.DescriptionCommand toUpdateDescription(
            ReceiveInboundProductCommand command) {
        String content = command.description() != null ? command.description().content() : null;
        return new UpdateProductGroupFullCommand.DescriptionCommand(content, List.of());
    }

    private UpdateProductGroupFullCommand.NoticeCommand toUpdateNotice(ResolvedPolicies resolved) {
        long noticeCategoryId =
                resolved.noticeCategoryId() != null ? resolved.noticeCategoryId() : 0L;
        List<UpdateProductGroupFullCommand.NoticeEntryCommand> entries =
                resolved.resolvedNoticeEntries().stream()
                        .map(
                                e ->
                                        new UpdateProductGroupFullCommand.NoticeEntryCommand(
                                                e.noticeFieldId(), e.fieldValue()))
                        .toList();
        return new UpdateProductGroupFullCommand.NoticeCommand(noticeCategoryId, entries);
    }

    // === 공통 ===

    private List<SelectedOption> toSelectedOptions(
            List<ReceiveInboundProductCommand.SelectedOptionCommand> options) {
        if (options == null || options.isEmpty()) {
            return List.of();
        }
        return options.stream()
                .map(so -> new SelectedOption(so.optionGroupName(), so.optionValueName()))
                .toList();
    }

    private String resolveOptionType(String optionTypeStr) {
        if (optionTypeStr == null || optionTypeStr.isBlank()) {
            return "NONE";
        }
        try {
            return com.ryuqq.marketplace.domain.productgroup.vo.OptionType.valueOf(
                            optionTypeStr.toUpperCase(Locale.ROOT))
                    .name();
        } catch (IllegalArgumentException e) {
            log.warn("알 수 없는 OptionType '{}', NONE으로 설정", optionTypeStr);
            return "NONE";
        }
    }
}
