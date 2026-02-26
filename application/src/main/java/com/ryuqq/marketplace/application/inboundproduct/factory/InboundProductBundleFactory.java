package com.ryuqq.marketplace.application.inboundproduct.factory;

import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundNoticeEntry;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * InboundProduct를 내부 상품 등록/수정 Command로 변환하는 팩토리.
 *
 * <p>InboundProduct의 pre-resolved 데이터를 {@link RegisterProductGroupCommand} 또는 {@link
 * UpdateProductGroupFullCommand}로 변환하고, 번들 생성은 {@link ProductGroupBundleFactory}에 위임합니다.
 */
@Component
public class InboundProductBundleFactory {

    private static final Logger log = LoggerFactory.getLogger(InboundProductBundleFactory.class);

    private final ProductGroupBundleFactory productGroupBundleFactory;

    public InboundProductBundleFactory(ProductGroupBundleFactory productGroupBundleFactory) {
        this.productGroupBundleFactory = productGroupBundleFactory;
    }

    /** InboundProduct → RegisterProductGroupCommand → 등록 번들. */
    public ProductGroupRegistrationBundle toRegistrationBundle(InboundProduct product) {
        RegisterProductGroupCommand command = toRegisterCommand(product);
        return productGroupBundleFactory.createProductGroupBundle(command);
    }

    /** InboundProduct → UpdateProductGroupFullCommand → 수정 번들. */
    public Optional<ProductGroupUpdateBundle> toUpdateBundle(InboundProduct product) {
        Long productGroupIdValue = product.internalProductGroupId();
        if (productGroupIdValue == null) {
            return Optional.empty();
        }
        UpdateProductGroupFullCommand command = toUpdateCommand(product, productGroupIdValue);
        return Optional.of(productGroupBundleFactory.createUpdateBundle(command));
    }

    // === InboundProduct → RegisterProductGroupCommand 변환 ===

    private RegisterProductGroupCommand toRegisterCommand(InboundProduct product) {
        InboundProductPayload payload = product.payload();
        String optionType = resolveOptionType(product.optionType());

        long noticeCategoryId =
                product.resolvedNoticeCategoryId() != null
                        ? product.resolvedNoticeCategoryId()
                        : 0L;

        return new RegisterProductGroupCommand(
                product.sellerId(),
                product.internalBrandId(),
                product.internalCategoryId(),
                product.resolvedShippingPolicyId(),
                product.resolvedRefundPolicyId(),
                product.productName(),
                optionType,
                toRegisterImages(payload),
                toRegisterOptionGroups(payload),
                toRegisterProducts(payload),
                new RegisterProductGroupCommand.DescriptionCommand(
                        product.descriptionHtml(), List.of()),
                new RegisterProductGroupCommand.NoticeCommand(
                        noticeCategoryId, toRegisterNoticeEntries(payload.noticeEntries())));
    }

    private List<RegisterProductGroupCommand.ImageCommand> toRegisterImages(
            InboundProductPayload payload) {
        return payload.images().stream()
                .map(
                        img ->
                                new RegisterProductGroupCommand.ImageCommand(
                                        img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private List<RegisterProductGroupCommand.OptionGroupCommand> toRegisterOptionGroups(
            InboundProductPayload payload) {
        return payload.optionGroups().stream()
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
            InboundProductPayload payload) {
        return payload.products().stream()
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

    private List<RegisterProductGroupCommand.NoticeEntryCommand> toRegisterNoticeEntries(
            List<InboundNoticeEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return List.of();
        }
        return entries.stream()
                .filter(InboundNoticeEntry::isResolved)
                .map(
                        e ->
                                new RegisterProductGroupCommand.NoticeEntryCommand(
                                        e.resolvedFieldId(), e.fieldValue()))
                .toList();
    }

    // === InboundProduct → UpdateProductGroupFullCommand 변환 ===

    private UpdateProductGroupFullCommand toUpdateCommand(
            InboundProduct product, long productGroupId) {
        InboundProductPayload payload = product.payload();
        String optionType = resolveOptionType(product.optionType());

        long noticeCategoryId =
                product.resolvedNoticeCategoryId() != null
                        ? product.resolvedNoticeCategoryId()
                        : 0L;

        return new UpdateProductGroupFullCommand(
                productGroupId,
                product.productName(),
                product.internalBrandId(),
                product.internalCategoryId(),
                product.resolvedShippingPolicyId(),
                product.resolvedRefundPolicyId(),
                optionType,
                toUpdateImages(payload),
                toUpdateOptionGroups(payload),
                toUpdateProducts(payload),
                new UpdateProductGroupFullCommand.DescriptionCommand(
                        product.descriptionHtml(), List.of()),
                new UpdateProductGroupFullCommand.NoticeCommand(
                        noticeCategoryId, toUpdateNoticeEntries(payload.noticeEntries())));
    }

    private List<UpdateProductGroupFullCommand.ImageCommand> toUpdateImages(
            InboundProductPayload payload) {
        return payload.images().stream()
                .map(
                        img ->
                                new UpdateProductGroupFullCommand.ImageCommand(
                                        img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private List<UpdateProductGroupFullCommand.OptionGroupCommand> toUpdateOptionGroups(
            InboundProductPayload payload) {
        return payload.optionGroups().stream()
                .map(
                        og -> {
                            List<UpdateProductGroupFullCommand.OptionValueCommand> values =
                                    og.optionValues().stream()
                                            .map(
                                                    ov ->
                                                            new UpdateProductGroupFullCommand
                                                                    .OptionValueCommand(
                                                                    null,
                                                                    ov.optionValueName(),
                                                                    null,
                                                                    ov.sortOrder()))
                                            .toList();
                            return new UpdateProductGroupFullCommand.OptionGroupCommand(
                                    null, og.optionGroupName(), null, og.inputType(), values);
                        })
                .toList();
    }

    private List<UpdateProductGroupFullCommand.ProductCommand> toUpdateProducts(
            InboundProductPayload payload) {
        return payload.products().stream()
                .map(
                        p ->
                                new UpdateProductGroupFullCommand.ProductCommand(
                                        null,
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        toSelectedOptions(p.selectedOptions())))
                .toList();
    }

    private List<UpdateProductGroupFullCommand.NoticeEntryCommand> toUpdateNoticeEntries(
            List<InboundNoticeEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return List.of();
        }
        return entries.stream()
                .filter(InboundNoticeEntry::isResolved)
                .map(
                        e ->
                                new UpdateProductGroupFullCommand.NoticeEntryCommand(
                                        e.resolvedFieldId(), e.fieldValue()))
                .toList();
    }

    // === 공통 유틸 ===

    private List<SelectedOption> toSelectedOptions(
            List<InboundProductPayload.InboundProductData.InboundSelectedOption> options) {
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
