package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

/** 레거시 상품 이미지·설명 Request → Command 변환 매퍼. */
@Component
public class LegacyImageCommandApiMapper {

    /** setofProductGroupId + Request → LegacyUpdateImagesCommand. */
    public LegacyUpdateImagesCommand toLegacyUpdateImagesCommand(
            long setofProductGroupId, List<LegacyCreateProductImageRequest> request) {
        UpdateProductGroupImagesCommand command = toImagesCommand(0L, request);
        return new LegacyUpdateImagesCommand(setofProductGroupId, command);
    }

    /** setofProductGroupId + Request → LegacyUpdateDescriptionCommand. */
    public LegacyUpdateDescriptionCommand toLegacyUpdateDescriptionCommand(
            long setofProductGroupId, LegacyUpdateProductDescriptionRequest request) {
        UpdateProductGroupDescriptionCommand command = toDescriptionCommand(0L, request);
        return new LegacyUpdateDescriptionCommand(setofProductGroupId, command);
    }

    /** List&lt;LegacyCreateProductImageRequest&gt; → UpdateProductGroupImagesCommand. */
    public UpdateProductGroupImagesCommand toImagesCommand(
            long productGroupId, List<LegacyCreateProductImageRequest> request) {
        List<UpdateProductGroupImagesCommand.ImageCommand> images =
                IntStream.range(0, request.size())
                        .mapToObj(
                                i -> {
                                    var img = request.get(i);
                                    return new UpdateProductGroupImagesCommand.ImageCommand(
                                            img.type(), img.originUrl(), i + 1);
                                })
                        .toList();

        return new UpdateProductGroupImagesCommand(productGroupId, images);
    }

    /** LegacyUpdateProductDescriptionRequest → UpdateProductGroupDescriptionCommand. */
    public UpdateProductGroupDescriptionCommand toDescriptionCommand(
            long productGroupId, LegacyUpdateProductDescriptionRequest request) {
        return new UpdateProductGroupDescriptionCommand(
                productGroupId, request.detailDescription());
    }
}
