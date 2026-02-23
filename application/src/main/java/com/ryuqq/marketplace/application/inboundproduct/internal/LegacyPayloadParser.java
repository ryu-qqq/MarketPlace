package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.LegacyInboundPayload;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.LegacyInboundPayload.LegacyPayloadImage;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.LegacyInboundPayload.LegacyPayloadNotice;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.LegacyInboundPayload.LegacyPayloadOption;
import com.ryuqq.marketplace.application.inboundproduct.dto.payload.LegacyInboundPayload.LegacyPayloadOptionDetail;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand.ProductData;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand.ImageCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand.NoticeEntryCommand;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand.OptionGroupCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand.OptionValueCommand;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 세토프(레거시) rawPayloadJson 파서.
 *
 * <p>rawPayloadJson은 LegacyCreateProductGroupRequest를 ObjectMapper.writeValueAsString()한 결과이므로,
 * LegacyInboundPayload로 역직렬화하여 ProductGroupRegistrationBundle을 조립합니다.
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.AvoidDeeplyNestedIfStmts"})
@Component
public class LegacyPayloadParser implements InboundProductPayloadParser {

    private static final Logger log = LoggerFactory.getLogger(LegacyPayloadParser.class);
    private static final long DEFAULT_POLICY_ID = 1L;

    private final ObjectMapper objectMapper;
    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final RefundPolicyReadManager refundPolicyReadManager;

    public LegacyPayloadParser(
            ObjectMapper objectMapper,
            ShippingPolicyReadManager shippingPolicyReadManager,
            RefundPolicyReadManager refundPolicyReadManager) {
        this.objectMapper = objectMapper.copy();
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
    }

    @Override
    public boolean supports(ExternalSourceType sourceType) {
        return sourceType == ExternalSourceType.LEGACY;
    }

    @Override
    public ProductGroupRegistrationBundle toRegistrationBundle(InboundProduct product) {
        var now = product.createdAt();
        LegacyInboundPayload payload = parsePayload(product.rawPayloadJson());

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

        RegisterProductGroupImagesCommand imageCommand = buildImageCommand(payload);
        RegisterSellerOptionGroupsCommand optionGroupCommand = buildOptionGroupCommand(payload);
        RegisterProductGroupDescriptionCommand descriptionCommand =
                buildDescriptionCommand(payload);
        RegisterProductNoticeCommand noticeCommand = buildNoticeCommand(payload);
        RegisterProductsCommand productCommand = buildProductCommand(payload);

        return new ProductGroupRegistrationBundle(
                productGroup,
                imageCommand,
                optionGroupCommand,
                descriptionCommand,
                noticeCommand,
                productCommand,
                now);
    }

    @Override
    public Optional<ProductGroupUpdateBundle> toUpdateBundle(InboundProduct product) {
        return Optional.empty();
    }

    private LegacyInboundPayload parsePayload(String rawPayloadJson) {
        try {
            return objectMapper.readValue(rawPayloadJson, LegacyInboundPayload.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("레거시 페이로드 JSON 파싱 실패", e);
        }
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

    private RegisterProductGroupImagesCommand buildImageCommand(LegacyInboundPayload payload) {
        List<ImageCommand> images = new ArrayList<>();
        if (payload.productImageList() != null) {
            AtomicInteger sortOrder = new AtomicInteger(0);
            for (LegacyPayloadImage img : payload.productImageList()) {
                String originUrl = img.originUrl() != null ? img.originUrl() : img.imageUrl();
                images.add(
                        new ImageCommand(
                                img.productImageType(), originUrl, sortOrder.getAndIncrement()));
            }
        }
        return new RegisterProductGroupImagesCommand(0L, images);
    }

    private RegisterSellerOptionGroupsCommand buildOptionGroupCommand(
            LegacyInboundPayload payload) {
        List<OptionGroupCommand> optionGroups = new ArrayList<>();

        if (payload.productOptions() != null && !payload.productOptions().isEmpty()) {
            Map<String, List<OptionValueCommand>> groupedOptions = new LinkedHashMap<>();

            for (LegacyPayloadOption option : payload.productOptions()) {
                if (option.options() != null) {
                    for (LegacyPayloadOptionDetail detail : option.options()) {
                        String groupName =
                                detail.optionName() != null ? detail.optionName() : "기본옵션";
                        groupedOptions.computeIfAbsent(groupName, k -> new ArrayList<>());

                        OptionValueCommand valueCmd =
                                new OptionValueCommand(
                                        detail.optionValue(),
                                        null,
                                        groupedOptions.get(groupName).size());
                        if (groupedOptions.get(groupName).stream()
                                .noneMatch(v -> v.optionValueName().equals(detail.optionValue()))) {
                            groupedOptions.get(groupName).add(valueCmd);
                        }
                    }
                }
            }

            for (Map.Entry<String, List<OptionValueCommand>> entry : groupedOptions.entrySet()) {
                optionGroups.add(
                        new OptionGroupCommand(entry.getKey(), null, "SELECT", entry.getValue()));
            }
        }

        String optionType = payload.optionType() != null ? payload.optionType() : "NONE";
        return new RegisterSellerOptionGroupsCommand(0L, optionType, optionGroups);
    }

    private RegisterProductGroupDescriptionCommand buildDescriptionCommand(
            LegacyInboundPayload payload) {
        String content = payload.detailDescription() != null ? payload.detailDescription() : "";
        return new RegisterProductGroupDescriptionCommand(0L, content);
    }

    private RegisterProductNoticeCommand buildNoticeCommand(LegacyInboundPayload payload) {
        List<NoticeEntryCommand> entries = new ArrayList<>();

        LegacyPayloadNotice notice = payload.productNotice();
        if (notice != null) {
            addNoticeEntry(entries, 1L, notice.material());
            addNoticeEntry(entries, 2L, notice.color());
            addNoticeEntry(entries, 3L, notice.size());
            addNoticeEntry(entries, 4L, notice.maker());
            addNoticeEntry(entries, 5L, notice.origin());
            addNoticeEntry(entries, 6L, notice.washingMethod());
            addNoticeEntry(entries, 7L, notice.yearMonth());
            addNoticeEntry(entries, 8L, notice.assuranceStandard());
            addNoticeEntry(entries, 9L, notice.asPhone());
        }

        return new RegisterProductNoticeCommand(0L, 1L, entries);
    }

    private void addNoticeEntry(List<NoticeEntryCommand> entries, long fieldId, String value) {
        if (value != null && !value.isBlank()) {
            entries.add(new NoticeEntryCommand(fieldId, value));
        }
    }

    private RegisterProductsCommand buildProductCommand(LegacyInboundPayload payload) {
        List<ProductData> products = new ArrayList<>();

        if (payload.productOptions() != null && !payload.productOptions().isEmpty()) {
            int sortOrder = 0;
            for (LegacyPayloadOption option : payload.productOptions()) {
                int regularPrice = (int) payload.price().regularPrice();
                int currentPrice = (int) payload.price().currentPrice();

                if (option.additionalPrice() != null && option.additionalPrice().intValue() > 0) {
                    currentPrice += option.additionalPrice().intValue();
                    regularPrice += option.additionalPrice().intValue();
                }

                int stockQuantity = option.quantity() != null ? option.quantity() : 0;

                List<SelectedOption> selectedOptions = new ArrayList<>();
                if (option.options() != null) {
                    for (LegacyPayloadOptionDetail detail : option.options()) {
                        selectedOptions.add(
                                new SelectedOption(detail.optionName(), detail.optionValue()));
                    }
                }

                String skuCode =
                        option.productId() != null
                                ? String.valueOf(option.productId())
                                : "SKU-" + sortOrder;

                products.add(
                        new ProductData(
                                skuCode,
                                regularPrice,
                                currentPrice,
                                stockQuantity,
                                sortOrder,
                                selectedOptions));
                sortOrder++;
            }
        } else {
            int regularPrice = (int) payload.price().regularPrice();
            int currentPrice = (int) payload.price().currentPrice();
            products.add(
                    new ProductData("DEFAULT-SKU", regularPrice, currentPrice, 0, 0, List.of()));
        }

        return new RegisterProductsCommand(0L, products);
    }
}
