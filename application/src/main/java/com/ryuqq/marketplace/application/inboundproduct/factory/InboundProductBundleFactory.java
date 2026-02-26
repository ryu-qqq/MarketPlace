package com.ryuqq.marketplace.application.inboundproduct.factory;

import com.ryuqq.marketplace.application.notice.resolver.CategoryNoticeResolver;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.ImageEntry;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.NoticeRegistrationData;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.NoticeRegistrationData.NoticeEntry;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.OptionRegistrationData;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.OptionRegistrationData.OptionGroupEntry;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.OptionRegistrationData.OptionGroupEntry.OptionValueEntry;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.ProductEntry;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand.ImageCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand.NoticeEntryCommand;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand.OptionGroupCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand.OptionValueCommand;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundNoticeEntry;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.exception.DefaultRefundPolicyNotFoundException;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.DefaultShippingPolicyNotFoundException;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * InboundProduct payload를 등록/수정 번들로 변환하는 팩토리.
 *
 * <p>기존 소스별 파서(MustitPayloadParser 등)를 대체합니다. InboundProductPayload VO에서 직접 번들 컴포넌트를 조립하므로 JSON 파싱이
 * 불필요합니다.
 */
@SuppressWarnings("PMD.ExcessiveImports")
@Component
public class InboundProductBundleFactory {

    private static final Logger log = LoggerFactory.getLogger(InboundProductBundleFactory.class);
    private static final String DEFAULT_NOTICE_VALUE = "상세설명 참고";

    private final CategoryNoticeResolver categoryNoticeResolver;
    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final RefundPolicyReadManager refundPolicyReadManager;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring-managed bean injected via constructor")
    public InboundProductBundleFactory(
            CategoryNoticeResolver categoryNoticeResolver,
            ShippingPolicyReadManager shippingPolicyReadManager,
            RefundPolicyReadManager refundPolicyReadManager) {
        this.categoryNoticeResolver = categoryNoticeResolver;
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
    }

    /** InboundProduct를 신규 등록 번들로 변환한다. */
    public ProductGroupRegistrationBundle toRegistrationBundle(InboundProduct product) {
        Instant now = product.createdAt();
        InboundProductPayload payload = product.payload();
        SellerId sellerId = SellerId.of(product.sellerId());
        OptionType optionType = resolveOptionType(product.optionType());

        ProductGroup productGroup =
                ProductGroup.forNew(
                        sellerId,
                        BrandId.of(product.internalBrandId()),
                        CategoryId.of(product.internalCategoryId()),
                        resolveShippingPolicyId(sellerId),
                        resolveRefundPolicyId(sellerId),
                        ProductGroupName.of(product.productName()),
                        optionType,
                        now);

        return new ProductGroupRegistrationBundle(
                productGroup,
                resolveImages(payload),
                resolveOptions(payload, optionType),
                product.descriptionHtml(),
                resolveNotice(payload, product.internalCategoryId()),
                resolveProducts(payload),
                now);
    }

    /** InboundProduct를 수정 번들로 변환한다. */
    public Optional<ProductGroupUpdateBundle> toUpdateBundle(InboundProduct product) {
        Long productGroupIdValue = product.internalProductGroupId();
        if (productGroupIdValue == null) {
            return Optional.empty();
        }

        InboundProductPayload payload = product.payload();
        long pgId = productGroupIdValue;
        SellerId sellerId = SellerId.of(product.sellerId());
        OptionType optionType = resolveOptionType(product.optionType());
        Instant now = Instant.now();

        ProductGroupUpdateData basicInfo =
                ProductGroupUpdateData.of(
                        ProductGroupId.of(pgId),
                        ProductGroupName.of(product.productName()),
                        BrandId.of(product.internalBrandId()),
                        CategoryId.of(product.internalCategoryId()),
                        resolveShippingPolicyId(sellerId),
                        resolveRefundPolicyId(sellerId),
                        now);

        return Optional.of(
                new ProductGroupUpdateBundle(
                        basicInfo,
                        toImageCommand(pgId, resolveImages(payload)),
                        toOptionCommand(pgId, resolveOptions(payload, optionType)),
                        new UpdateProductGroupDescriptionCommand(pgId, product.descriptionHtml()),
                        toNoticeCommand(pgId, resolveNotice(payload, product.internalCategoryId())),
                        toProductDiffEntries(resolveProducts(payload))));
    }

    private List<ImageEntry> resolveImages(InboundProductPayload payload) {
        return payload.images().stream()
                .map(img -> new ImageEntry(img.imageType(), img.originUrl(), img.sortOrder()))
                .toList();
    }

    private OptionRegistrationData resolveOptions(
            InboundProductPayload payload, OptionType optionType) {
        List<OptionGroupEntry> groups =
                payload.optionGroups().stream()
                        .map(
                                og -> {
                                    List<OptionValueEntry> values =
                                            og.optionValues().stream()
                                                    .map(
                                                            ov ->
                                                                    new OptionValueEntry(
                                                                            ov.optionValueName(),
                                                                            null,
                                                                            ov.sortOrder()))
                                                    .toList();
                                    return new OptionGroupEntry(
                                            og.optionGroupName(), null, og.inputType(), values);
                                })
                        .toList();
        return new OptionRegistrationData(optionType, groups);
    }

    private NoticeRegistrationData resolveNotice(
            InboundProductPayload payload, long internalCategoryId) {
        return categoryNoticeResolver
                .resolve(internalCategoryId)
                .map(
                        noticeCategory -> {
                            List<NoticeEntry> entries =
                                    mapNoticeEntries(payload.noticeEntries(), noticeCategory);
                            return new NoticeRegistrationData(noticeCategory.idValue(), entries);
                        })
                .orElseGet(
                        () -> {
                            log.warn(
                                    "카테고리 ID {}에 해당하는 고시정보 카테고리 없음, 빈 고시정보 생성", internalCategoryId);
                            return new NoticeRegistrationData(0L, List.of());
                        });
    }

    private List<NoticeEntry> mapNoticeEntries(
            List<InboundNoticeEntry> inboundEntries, NoticeCategory noticeCategory) {
        return noticeCategory.fields().stream()
                .map(
                        field -> {
                            String value =
                                    findByFieldCode(inboundEntries, field.fieldCodeValue())
                                            .orElse(DEFAULT_NOTICE_VALUE);
                            return new NoticeEntry(field.idValue(), value);
                        })
                .toList();
    }

    private Optional<String> findByFieldCode(List<InboundNoticeEntry> entries, String fieldCode) {
        if (entries == null || entries.isEmpty()) {
            return Optional.empty();
        }
        return entries.stream()
                .filter(e -> e.fieldCode().equals(fieldCode))
                .map(InboundNoticeEntry::fieldValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst();
    }

    private List<ProductEntry> resolveProducts(InboundProductPayload payload) {
        return payload.products().stream()
                .map(
                        p -> {
                            List<SelectedOption> selectedOptions =
                                    p.selectedOptions().stream()
                                            .map(
                                                    so ->
                                                            new SelectedOption(
                                                                    so.optionGroupName(),
                                                                    so.optionValueName()))
                                            .toList();
                            return new ProductEntry(
                                    p.skuCode(),
                                    p.regularPrice(),
                                    p.currentPrice(),
                                    p.stockQuantity(),
                                    p.sortOrder(),
                                    selectedOptions);
                        })
                .toList();
    }

    private OptionType resolveOptionType(String optionTypeStr) {
        if (optionTypeStr == null || optionTypeStr.isBlank()) {
            return OptionType.NONE;
        }
        try {
            return OptionType.valueOf(optionTypeStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.warn("알 수 없는 OptionType '{}', NONE으로 설정", optionTypeStr);
            return OptionType.NONE;
        }
    }

    private ShippingPolicyId resolveShippingPolicyId(SellerId sellerId) {
        return shippingPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(ShippingPolicy::id)
                .orElseThrow(() -> new DefaultShippingPolicyNotFoundException(sellerId.value()));
    }

    private RefundPolicyId resolveRefundPolicyId(SellerId sellerId) {
        return refundPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(RefundPolicy::id)
                .orElseThrow(() -> new DefaultRefundPolicyNotFoundException(sellerId.value()));
    }

    private UpdateProductGroupImagesCommand toImageCommand(
            long productGroupId, List<ImageEntry> images) {
        List<ImageCommand> commands =
                images.stream()
                        .map(e -> new ImageCommand(e.imageType(), e.originUrl(), e.sortOrder()))
                        .toList();
        return new UpdateProductGroupImagesCommand(productGroupId, commands);
    }

    private UpdateSellerOptionGroupsCommand toOptionCommand(
            long productGroupId, OptionRegistrationData optionData) {
        List<OptionGroupCommand> commands =
                optionData.groups().stream()
                        .map(
                                g -> {
                                    List<OptionValueCommand> values =
                                            g.optionValues().stream()
                                                    .map(
                                                            v ->
                                                                    new OptionValueCommand(
                                                                            null,
                                                                            v.optionValueName(),
                                                                            v
                                                                                    .canonicalOptionValueId(),
                                                                            v.sortOrder()))
                                                    .toList();
                                    return new OptionGroupCommand(
                                            null,
                                            g.optionGroupName(),
                                            g.canonicalOptionGroupId(),
                                            g.inputType(),
                                            values);
                                })
                        .toList();
        return new UpdateSellerOptionGroupsCommand(productGroupId, commands);
    }

    private UpdateProductNoticeCommand toNoticeCommand(
            long productGroupId, NoticeRegistrationData noticeData) {
        List<NoticeEntryCommand> commands =
                noticeData.entries().stream()
                        .map(e -> new NoticeEntryCommand(e.noticeFieldId(), e.fieldValue()))
                        .toList();
        return new UpdateProductNoticeCommand(
                productGroupId, noticeData.noticeCategoryId(), commands);
    }

    private List<ProductDiffUpdateEntry> toProductDiffEntries(List<ProductEntry> products) {
        return products.stream()
                .map(
                        p ->
                                new ProductDiffUpdateEntry(
                                        null,
                                        p.skuCode(),
                                        p.regularPrice(),
                                        p.currentPrice(),
                                        p.stockQuantity(),
                                        p.sortOrder(),
                                        p.selectedOptions()))
                .toList();
    }
}
