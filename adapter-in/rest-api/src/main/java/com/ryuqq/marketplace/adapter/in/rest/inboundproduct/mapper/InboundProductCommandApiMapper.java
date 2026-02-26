package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.mapper;

import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductConversionApiResponse;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundProduct API 요청/응답 매퍼. */
@Component
public class InboundProductCommandApiMapper {

    public ReceiveInboundProductCommand toCommand(ReceiveInboundProductApiRequest request) {
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
                mapImages(request.images()),
                mapOptionGroups(request.optionGroups()),
                mapProducts(request.products()),
                mapDescription(request.description()),
                mapNotice(request.notice()));
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

    private List<ReceiveInboundProductCommand.ImageCommand> mapImages(
            List<ReceiveInboundProductApiRequest.ImageRequest> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream()
                .map(
                        i ->
                                new ReceiveInboundProductCommand.ImageCommand(
                                        i.imageType(), i.originUrl(), i.sortOrder()))
                .toList();
    }

    private List<ReceiveInboundProductCommand.OptionGroupCommand> mapOptionGroups(
            List<ReceiveInboundProductApiRequest.OptionGroupRequest> optionGroups) {
        if (optionGroups == null) {
            return List.of();
        }
        return optionGroups.stream()
                .map(
                        og -> {
                            List<ReceiveInboundProductCommand.OptionValueCommand> values =
                                    og.optionValues() != null
                                            ? og.optionValues().stream()
                                                    .map(
                                                            ov ->
                                                                    new ReceiveInboundProductCommand
                                                                            .OptionValueCommand(
                                                                            ov.optionValueName(),
                                                                            ov.sortOrder()))
                                                    .toList()
                                            : List.of();
                            return new ReceiveInboundProductCommand.OptionGroupCommand(
                                    og.optionGroupName(), og.inputType(), values);
                        })
                .toList();
    }

    private List<ReceiveInboundProductCommand.ProductCommand> mapProducts(
            List<ReceiveInboundProductApiRequest.ProductRequest> products) {
        if (products == null) {
            return List.of();
        }
        return products.stream()
                .map(
                        p -> {
                            List<ReceiveInboundProductCommand.SelectedOptionCommand>
                                    selectedOptions =
                                            p.selectedOptions() != null
                                                    ? p.selectedOptions().stream()
                                                            .map(
                                                                    so ->
                                                                            new ReceiveInboundProductCommand
                                                                                    .SelectedOptionCommand(
                                                                                    so
                                                                                            .optionGroupName(),
                                                                                    so
                                                                                            .optionValueName()))
                                                            .toList()
                                                    : List.of();
                            return new ReceiveInboundProductCommand.ProductCommand(
                                    p.skuCode(),
                                    p.regularPrice(),
                                    p.currentPrice(),
                                    p.stockQuantity(),
                                    p.sortOrder(),
                                    selectedOptions);
                        })
                .toList();
    }

    private ReceiveInboundProductCommand.DescriptionCommand mapDescription(
            ReceiveInboundProductApiRequest.DescriptionRequest description) {
        if (description == null) {
            return new ReceiveInboundProductCommand.DescriptionCommand(null);
        }
        return new ReceiveInboundProductCommand.DescriptionCommand(description.content());
    }

    private ReceiveInboundProductCommand.NoticeCommand mapNotice(
            ReceiveInboundProductApiRequest.NoticeRequest notice) {
        if (notice == null || notice.entries() == null) {
            return new ReceiveInboundProductCommand.NoticeCommand(List.of());
        }
        List<ReceiveInboundProductCommand.NoticeEntryCommand> entries =
                notice.entries().stream()
                        .map(
                                e ->
                                        new ReceiveInboundProductCommand.NoticeEntryCommand(
                                                e.fieldCode(), e.fieldValue()))
                        .toList();
        return new ReceiveInboundProductCommand.NoticeCommand(entries);
    }
}
