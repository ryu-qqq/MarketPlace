package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.mapper;

import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest.ImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest.NoticeEntryRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest.OptionGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest.ProductRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductConversionApiResponse;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundImageData;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundNoticeEntry;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundOptionGroupData;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundOptionGroupData.InboundOptionValueData;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundProductData;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload.InboundProductData.InboundSelectedOption;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundProduct API 요청/응답 매퍼. */
@Component
public class InboundProductCommandApiMapper {

    public ReceiveInboundProductCommand toCommand(ReceiveInboundProductApiRequest request) {
        InboundProductPayload payload = toPayload(request);
        return new ReceiveInboundProductCommand(
                request.inboundSourceId(),
                request.externalProductCode(),
                request.productName(),
                request.externalBrandCode(),
                request.externalCategoryCode(),
                request.sellerId(),
                request.regularPrice(),
                request.currentPrice(),
                request.optionType(),
                request.descriptionHtml(),
                payload);
    }

    public List<UpdateProductStockCommand> toStockCommands(
            UpdateInboundProductStockApiRequest request) {
        return request.stocks().stream()
                .map(s -> new UpdateProductStockCommand(s.productId(), s.stockQuantity()))
                .toList();
    }

    public UpdateProductGroupImagesCommand toImagesCommand(
            UpdateInboundProductImagesApiRequest request) {
        List<UpdateProductGroupImagesCommand.ImageCommand> images =
                request.images().stream()
                        .map(
                                i ->
                                        new UpdateProductGroupImagesCommand.ImageCommand(
                                                i.imageType(), i.originUrl(), i.sortOrder()))
                        .toList();
        return new UpdateProductGroupImagesCommand(0L, images);
    }

    public InboundProductConversionApiResponse toResponse(InboundProductConversionResult result) {
        return new InboundProductConversionApiResponse(
                result.inboundProductId(),
                result.internalProductGroupId(),
                result.status().name(),
                result.action().name());
    }

    private InboundProductPayload toPayload(ReceiveInboundProductApiRequest request) {
        List<InboundImageData> images = mapImages(request.images());
        List<InboundOptionGroupData> optionGroups = mapOptionGroups(request.optionGroups());
        List<InboundProductData> products = mapProducts(request.products());
        List<InboundNoticeEntry> noticeEntries = mapNoticeEntries(request.noticeEntries());
        return new InboundProductPayload(images, optionGroups, products, noticeEntries);
    }

    private List<InboundImageData> mapImages(List<ImageRequest> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream()
                .map(i -> new InboundImageData(i.imageType(), i.originUrl(), i.sortOrder()))
                .toList();
    }

    private List<InboundOptionGroupData> mapOptionGroups(List<OptionGroupRequest> optionGroups) {
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

    private List<InboundProductData> mapProducts(List<ProductRequest> products) {
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

    private List<InboundNoticeEntry> mapNoticeEntries(List<NoticeEntryRequest> noticeEntries) {
        if (noticeEntries == null) {
            return List.of();
        }
        return noticeEntries.stream()
                .map(e -> new InboundNoticeEntry(e.fieldCode(), e.fieldValue()))
                .toList();
    }
}
