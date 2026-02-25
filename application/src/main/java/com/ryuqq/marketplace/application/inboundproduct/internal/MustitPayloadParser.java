package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.dto.payload.MustitInboundPayload;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.MustitInboundPayload.ProductImageListPayload;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.MustitInboundPayload.ProductImagePayload;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.MustitInboundPayload.ProductOptionListPayload;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.MustitInboundPayload.ProductOptionPayload;
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
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * MUSTIT 크롤링 rawPayloadJson 파서.
 *
 * <p>MustitPayloadResolver로 역직렬화 + 검증한 페이로드를 ProductGroupRegistrationBundle로 조립합니다.
 */
@SuppressWarnings("PMD.ExcessiveImports")
@Component
public class MustitPayloadParser implements InboundProductPayloadParser {

    private static final Logger log = LoggerFactory.getLogger(MustitPayloadParser.class);
    private static final long DEFAULT_POLICY_ID = 1L;
    private static final long DEFAULT_NOTICE_CATEGORY_ID = 1L;

    private final MustitPayloadResolver payloadResolver;
    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final RefundPolicyReadManager refundPolicyReadManager;

    public MustitPayloadParser(
            MustitPayloadResolver payloadResolver,
            ShippingPolicyReadManager shippingPolicyReadManager,
            RefundPolicyReadManager refundPolicyReadManager) {
        this.payloadResolver = payloadResolver;
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
    }

    @Override
    public boolean supports(String sourceCode) {
        return "MUSTIT".equals(sourceCode);
    }

    @Override
    public ProductGroupRegistrationBundle toRegistrationBundle(InboundProduct product) {
        Instant now = product.createdAt();
        MustitInboundPayload payload =
                payloadResolver.resolve(
                        product.rawPayloadJson(), product.externalProductCodeValue());

        SellerId sellerId = SellerId.of(product.sellerId());
        BrandId brandId = BrandId.of(product.internalBrandId());
        CategoryId categoryId = CategoryId.of(product.internalCategoryId());

        ShippingPolicyId shippingPolicyId = resolveShippingPolicyId(sellerId);
        RefundPolicyId refundPolicyId = resolveRefundPolicyId(sellerId);

        OptionType optionType = resolveOptionType(product.optionType());
        ProductGroupName productGroupName = ProductGroupName.of(product.productName());

        ProductGroup productGroup =
                ProductGroup.forNew(
                        sellerId,
                        brandId,
                        categoryId,
                        shippingPolicyId,
                        refundPolicyId,
                        productGroupName,
                        optionType,
                        now);

        List<ImageEntry> images = buildImageEntries(payload);
        OptionRegistrationData optionData =
                buildOptionRegistrationData(payload, product.optionType());
        String descriptionContent = buildDescriptionContent(payload);
        NoticeRegistrationData noticeData = buildNoticeData(payload);
        List<ProductEntry> products = buildProductEntries(payload, product);

        return new ProductGroupRegistrationBundle(
                productGroup, images, optionData, descriptionContent, noticeData, products, now);
    }

    @Override
    public Optional<ProductGroupUpdateBundle> toUpdateBundle(InboundProduct product) {
        return Optional.empty();
    }

    private ShippingPolicyId resolveShippingPolicyId(SellerId sellerId) {
        Optional<ShippingPolicy> defaultPolicy =
                shippingPolicyReadManager.findDefaultBySellerId(sellerId);
        return defaultPolicy.map(ShippingPolicy::id).orElse(ShippingPolicyId.of(DEFAULT_POLICY_ID));
    }

    private RefundPolicyId resolveRefundPolicyId(SellerId sellerId) {
        Optional<RefundPolicy> defaultPolicy =
                refundPolicyReadManager.findDefaultBySellerId(sellerId);
        return defaultPolicy.map(RefundPolicy::id).orElse(RefundPolicyId.of(DEFAULT_POLICY_ID));
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

    private List<ImageEntry> buildImageEntries(MustitInboundPayload payload) {
        List<ImageEntry> entries = new ArrayList<>();
        ProductImageListPayload imageList = payload.images();
        if (imageList != null) {
            for (ProductImagePayload img : imageList.thumbnails()) {
                String imageType = img.imageType() != null ? img.imageType() : "THUMBNAIL";
                entries.add(new ImageEntry(imageType, img.url(), img.displayOrder()));
            }
            for (ProductImagePayload img : imageList.descriptionImages()) {
                String imageType = img.imageType() != null ? img.imageType() : "DESCRIPTION";
                entries.add(new ImageEntry(imageType, img.url(), img.displayOrder()));
            }
        }
        return entries;
    }

    private OptionRegistrationData buildOptionRegistrationData(
            MustitInboundPayload payload, String optionTypeFromProduct) {
        List<OptionGroupEntry> optionGroups = new ArrayList<>();
        ProductOptionListPayload optionList = payload.options();

        if (optionList != null && !optionList.options().isEmpty()) {
            Map<String, List<OptionValueEntry>> groupedOptions = new LinkedHashMap<>();

            for (ProductOptionPayload option : optionList.options()) {
                if (option.color() != null && !option.color().isBlank()) {
                    addOptionValue(groupedOptions, "색상", option.color());
                }
                if (option.size() != null && !option.size().isBlank()) {
                    addOptionValue(groupedOptions, "사이즈", option.size());
                }
            }

            for (Map.Entry<String, List<OptionValueEntry>> entry : groupedOptions.entrySet()) {
                optionGroups.add(
                        new OptionGroupEntry(entry.getKey(), null, "SELECT", entry.getValue()));
            }
        }

        String optionType =
                (optionTypeFromProduct != null && !optionTypeFromProduct.isBlank())
                        ? optionTypeFromProduct
                        : "NONE";
        return new OptionRegistrationData(OptionType.valueOf(optionType), optionGroups);
    }

    private void addOptionValue(
            Map<String, List<OptionValueEntry>> groupedOptions, String groupName, String value) {
        List<OptionValueEntry> values =
                groupedOptions.computeIfAbsent(groupName, k -> new ArrayList<>());
        boolean alreadyExists = values.stream().anyMatch(v -> v.optionValueName().equals(value));
        if (!alreadyExists) {
            values.add(new OptionValueEntry(value, null, values.size()));
        }
    }

    private String buildDescriptionContent(MustitInboundPayload payload) {
        return payload.descriptionHtml() != null ? payload.descriptionHtml() : "";
    }

    private NoticeRegistrationData buildNoticeData(MustitInboundPayload payload) {
        List<NoticeEntry> entries = new ArrayList<>();
        if (payload.originCountry() != null && !payload.originCountry().isBlank()) {
            entries.add(new NoticeEntry(5L, payload.originCountry()));
        }
        return new NoticeRegistrationData(DEFAULT_NOTICE_CATEGORY_ID, entries);
    }

    private List<ProductEntry> buildProductEntries(
            MustitInboundPayload payload, InboundProduct product) {
        List<ProductEntry> entries = new ArrayList<>();
        ProductOptionListPayload optionList = payload.options();

        if (optionList != null && !optionList.options().isEmpty()) {
            int sortOrder = 0;
            for (ProductOptionPayload option : optionList.options()) {
                List<SelectedOption> selectedOptions = new ArrayList<>();
                if (option.color() != null && !option.color().isBlank()) {
                    selectedOptions.add(new SelectedOption("색상", option.color()));
                }
                if (option.size() != null && !option.size().isBlank()) {
                    selectedOptions.add(new SelectedOption("사이즈", option.size()));
                }

                String skuCode = "MUSTIT-" + option.optionNo();

                entries.add(
                        new ProductEntry(
                                skuCode,
                                payload.regularPrice(),
                                payload.currentPrice(),
                                option.stock(),
                                sortOrder,
                                selectedOptions));
                sortOrder++;
            }
        } else {
            entries.add(
                    new ProductEntry(
                            "MUSTIT-" + product.externalProductCodeValue(),
                            payload.regularPrice(),
                            payload.currentPrice(),
                            0,
                            0,
                            List.of()));
        }

        return entries;
    }
}
