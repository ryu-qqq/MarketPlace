package com.ryuqq.marketplace.application.inboundproduct.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundImageData;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundNoticeEntry;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundOptionGroupData;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundOptionGroupData.InboundOptionValueData;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundProductData;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundProductData.InboundSelectedOption;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductUpdateData;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InboundProductCommandFactory {

    private final TimeProvider timeProvider;

    public InboundProductCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public InboundProduct create(ReceiveInboundProductCommand command) {
        Instant now = timeProvider.now();
        InboundProductPayload payload = toPayload(command);
        String descriptionHtml =
                command.description() != null ? command.description().content() : null;

        return InboundProduct.forNew(
                command.inboundSourceId(),
                ExternalProductCode.of(command.externalProductCode()),
                command.productName(),
                command.externalBrandCode(),
                command.externalCategoryCode(),
                command.sellerId(),
                command.regularPrice(),
                command.currentPrice(),
                command.optionType(),
                descriptionHtml,
                payload,
                now);
    }

    public InboundProductUpdateData toUpdateData(ReceiveInboundProductCommand command) {
        InboundProductPayload payload = toPayload(command);
        String descriptionHtml =
                command.description() != null ? command.description().content() : null;

        return InboundProductUpdateData.of(
                command.productName(),
                command.externalBrandCode(),
                command.externalCategoryCode(),
                command.regularPrice(),
                command.currentPrice(),
                command.optionType(),
                descriptionHtml,
                payload);
    }

    /** Command의 직접 필드들을 도메인 InboundProductPayload VO로 변환한다. */
    private InboundProductPayload toPayload(ReceiveInboundProductCommand command) {
        List<InboundImageData> images = mapImages(command.images());
        List<InboundOptionGroupData> optionGroups = mapOptionGroups(command.optionGroups());
        List<InboundProductData> products = mapProducts(command.products());
        List<InboundNoticeEntry> noticeEntries = mapNoticeEntries(command.notice());
        return new InboundProductPayload(images, optionGroups, products, noticeEntries);
    }

    private List<InboundImageData> mapImages(
            List<ReceiveInboundProductCommand.ImageCommand> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream()
                .map(i -> new InboundImageData(i.imageType(), i.originUrl(), i.sortOrder()))
                .toList();
    }

    private List<InboundOptionGroupData> mapOptionGroups(
            List<ReceiveInboundProductCommand.OptionGroupCommand> optionGroups) {
        if (optionGroups == null) {
            return List.of();
        }
        return optionGroups.stream()
                .map(
                        og -> {
                            List<InboundOptionValueData> values =
                                    og.optionValues() != null
                                            ? og.optionValues().stream()
                                                    .map(
                                                            ov ->
                                                                    new InboundOptionValueData(
                                                                            ov.optionValueName(),
                                                                            ov.sortOrder()))
                                                    .toList()
                                            : List.of();
                            return new InboundOptionGroupData(
                                    og.optionGroupName(), og.inputType(), values);
                        })
                .toList();
    }

    private List<InboundProductData> mapProducts(
            List<ReceiveInboundProductCommand.ProductCommand> products) {
        if (products == null) {
            return List.of();
        }
        return products.stream()
                .map(
                        p -> {
                            List<InboundSelectedOption> selectedOptions =
                                    p.selectedOptions() != null
                                            ? p.selectedOptions().stream()
                                                    .map(
                                                            so ->
                                                                    new InboundSelectedOption(
                                                                            so.optionGroupName(),
                                                                            so.optionValueName()))
                                                    .toList()
                                            : List.of();
                            return new InboundProductData(
                                    p.skuCode(),
                                    p.regularPrice(),
                                    p.currentPrice(),
                                    p.stockQuantity(),
                                    p.sortOrder(),
                                    selectedOptions);
                        })
                .toList();
    }

    private List<InboundNoticeEntry> mapNoticeEntries(
            ReceiveInboundProductCommand.NoticeCommand notice) {
        if (notice == null || notice.entries() == null) {
            return List.of();
        }
        return notice.entries().stream()
                .map(e -> InboundNoticeEntry.ofRaw(e.fieldCode(), e.fieldValue()))
                .toList();
    }
}
