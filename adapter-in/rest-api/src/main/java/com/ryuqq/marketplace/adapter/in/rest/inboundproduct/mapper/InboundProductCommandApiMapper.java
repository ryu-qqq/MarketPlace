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
                request.descriptionHtml(),
                request.rawPayloadJson());
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
}
