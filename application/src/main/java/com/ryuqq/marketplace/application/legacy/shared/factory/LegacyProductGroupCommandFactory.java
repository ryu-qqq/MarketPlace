package com.ryuqq.marketplace.application.legacy.shared.factory;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.legacy.productgroupimage.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.legacy.productnotice.dto.command.LegacyUpdateNoticeCommand;
import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdatePriceCommand;
import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.bundle.LegacyProductRegistrationBundle.ImageEntry;
import com.ryuqq.marketplace.domain.legacy.optiondetail.id.LegacyOptionDetailId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyDescriptionImage;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productdescription.vo.LegacyProductDescription;
import com.ryuqq.marketplace.domain.legacy.productnotice.aggregate.LegacyProductNotice;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import com.ryuqq.marketplace.domain.legacy.productimage.vo.ProductGroupImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Legacy ProductGroup Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class LegacyProductGroupCommandFactory {

    private final TimeProvider timeProvider;
    private final Set<String> excludeDomains;

    public LegacyProductGroupCommandFactory(
            TimeProvider timeProvider, @Value("${fileflow.cdn-domain:}") String cdnDomain) {
        this.timeProvider = timeProvider;
        this.excludeDomains =
                (cdnDomain == null || cdnDomain.isBlank()) ? Set.of() : Set.of(cdnDomain);
    }

    /** 가격 수정 컨텍스트 생성. */
    public StatusChangeContext<LegacyProductGroupId> createPriceUpdateContext(
            LegacyUpdatePriceCommand command) {
        return new StatusChangeContext<>(
                LegacyProductGroupId.of(command.productGroupId()), timeProvider.now());
    }

    /** 전시 상태 변경 컨텍스트 생성. */
    public StatusChangeContext<LegacyProductGroupId> createDisplayStatusChangeContext(
            LegacyUpdateDisplayStatusCommand command) {
        return new StatusChangeContext<>(
                LegacyProductGroupId.of(command.productGroupId()), timeProvider.now());
    }

    /** 품절 처리 컨텍스트 생성. */
    public StatusChangeContext<LegacyProductGroupId> createMarkOutOfStockContext(
            LegacyMarkOutOfStockCommand command) {
        return new StatusChangeContext<>(
                LegacyProductGroupId.of(command.productGroupId()), timeProvider.now());
    }

    /** 고시정보 수정 컨텍스트 생성. */
    public UpdateContext<LegacyProductGroupId, LegacyProductNotice> createNoticeUpdateContext(
            LegacyUpdateNoticeCommand command) {
        LegacyProductNotice notice =
                new LegacyProductNotice(
                        command.material(),
                        command.color(),
                        command.size(),
                        command.maker(),
                        command.origin(),
                        command.washingMethod(),
                        command.yearMonthDay(),
                        command.assuranceStandard(),
                        command.asPhone());
        return new UpdateContext<>(
                LegacyProductGroupId.of(command.productGroupId()), notice, timeProvider.now());
    }

    /** 상세설명 수정 컨텍스트 생성. */
    public UpdateContext<LegacyProductGroupId, LegacyProductDescription>
            createDescriptionUpdateContext(LegacyUpdateDescriptionCommand command) {
        LegacyProductDescription description =
                new LegacyProductDescription(command.detailDescription());
        return new UpdateContext<>(
                LegacyProductGroupId.of(command.productGroupId()), description, timeProvider.now());
    }

    /** HTML 콘텐츠에서 이미지 URL을 추출하여 LegacyDescriptionImage 목록을 생성합니다. */
    public List<LegacyDescriptionImage> extractDescriptionImages(
            long productGroupId, String content) {
        DescriptionHtml html = DescriptionHtml.of(content);
        List<String> imageUrls = html.extractImageUrls(excludeDomains);
        return IntStream.range(0, imageUrls.size())
                .mapToObj(i -> LegacyDescriptionImage.forNew(productGroupId, imageUrls.get(i), i))
                .toList();
    }

    /** 이미지 수정용 도메인 객체 목록 생성. */
    public List<LegacyProductImage> createImagesForUpdate(
            LegacyProductGroupId groupId, List<LegacyUpdateImagesCommand.ImageEntry> entries) {
        return IntStream.range(0, entries.size())
                .mapToObj(
                        i -> {
                            LegacyUpdateImagesCommand.ImageEntry entry = entries.get(i);
                            return LegacyProductImage.forNew(
                                    groupId,
                                    ProductGroupImageType.valueOf(entry.imageType()),
                                    entry.imageUrl(),
                                    entry.originUrl(),
                                    i + 1);
                        })
                .toList();
    }

    /** 전체 수정 Command의 이미지를 도메인 객체 목록으로 변환. */
    public List<LegacyProductImage> createImagesFromFullUpdate(
            LegacyProductGroupId groupId,
            List<LegacyUpdateProductGroupCommand.ImageCommand> commands) {
        List<LegacyUpdateImagesCommand.ImageEntry> entries =
                commands.stream()
                        .map(
                                cmd ->
                                        new LegacyUpdateImagesCommand.ImageEntry(
                                                cmd.imageType(), cmd.imageUrl(), cmd.originUrl()))
                        .toList();
        return createImagesForUpdate(groupId, entries);
    }

    /** 이미지 등록용 도메인 객체 목록 생성. */
    public List<LegacyProductImage> createImagesForRegistration(
            LegacyProductGroupId groupId, List<ImageEntry> entries) {
        return IntStream.range(0, entries.size())
                .mapToObj(
                        i -> {
                            ImageEntry entry = entries.get(i);
                            return LegacyProductImage.forNew(
                                    groupId,
                                    ProductGroupImageType.valueOf(entry.imageType()),
                                    entry.imageUrl(),
                                    entry.originUrl(),
                                    i + 1);
                        })
                .toList();
    }

    /** APP-TIM-001: 현재 시각 제공 (TimeProvider 위임). */
    public Instant now() {
        return timeProvider.now();
    }

    /** 전체 수정 Command의 옵션을 도메인 객체 목록으로 변환. */
    public List<LegacyProduct> createProductsFromFullUpdate(
            LegacyProductGroupId groupId,
            List<LegacyUpdateProductGroupCommand.OptionCommand> commands) {
        List<LegacyUpdateProductsCommand.SkuEntry> skuEntries =
                commands.stream()
                        .map(
                                cmd ->
                                        new LegacyUpdateProductsCommand.SkuEntry(
                                                cmd.productId(),
                                                cmd.quantity(),
                                                cmd.additionalPrice(),
                                                cmd.optionDetails().stream()
                                                        .map(
                                                                d ->
                                                                        new LegacyUpdateProductsCommand
                                                                                .OptionEntry(
                                                                                d.optionGroupId(),
                                                                                d.optionDetailId(),
                                                                                d.optionName(),
                                                                                d.optionValue()))
                                                        .toList()))
                        .toList();
        return createProductsForOptionUpdate(groupId, skuEntries);
    }

    /**
     * 옵션 수정용 상품 도메인 객체 목록 생성.
     *
     * <p>SkuEntry에 productId가 있으면 기존 상품 수정, 없으면 신규 상품으로 처리합니다.
     */
    public List<LegacyProduct> createProductsForOptionUpdate(
            LegacyProductGroupId groupId, List<LegacyUpdateProductsCommand.SkuEntry> skuEntries) {
        return skuEntries.stream()
                .map(
                        sku -> {
                            List<LegacyProductOption> options =
                                    sku.options().stream()
                                            .map(
                                                    opt ->
                                                            LegacyProductOption.forNew(
                                                                    sku.productId() != null
                                                                            ? LegacyProductId.of(
                                                                                    sku.productId())
                                                                            : LegacyProductId
                                                                                    .forNew(),
                                                                    LegacyOptionGroupId.of(
                                                                            opt.optionGroupId()),
                                                                    LegacyOptionDetailId.of(
                                                                            opt.optionDetailId()),
                                                                    sku.additionalPrice()))
                                            .toList();

                            if (sku.productId() != null) {
                                return LegacyProduct.reconstitute(
                                        sku.productId(),
                                        groupId.value(),
                                        "N",
                                        "Y",
                                        sku.stockQuantity(),
                                        options,
                                        com.ryuqq.marketplace.domain.common.vo.DeletionStatus
                                                .active());
                            }
                            return LegacyProduct.forNew(
                                    groupId, "N", "Y", sku.stockQuantity(), options);
                        })
                .toList();
    }
}
