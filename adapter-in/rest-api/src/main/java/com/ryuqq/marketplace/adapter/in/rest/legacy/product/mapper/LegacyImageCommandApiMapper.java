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

    /** productGroupId + Request → LegacyUpdateImagesCommand. */
    public LegacyUpdateImagesCommand toLegacyUpdateImagesCommand(
            long productGroupId, List<LegacyCreateProductImageRequest> request) {
        List<LegacyUpdateImagesCommand.ImageEntry> entries =
                request.stream()
                        .map(
                                img ->
                                        new LegacyUpdateImagesCommand.ImageEntry(
                                                img.type(), img.productImageUrl(), img.originUrl()))
                        .toList();
        return new LegacyUpdateImagesCommand(productGroupId, entries);
    }

    /** productGroupId + Request → LegacyUpdateDescriptionCommand. */
    public LegacyUpdateDescriptionCommand toLegacyUpdateDescriptionCommand(
            long productGroupId, LegacyUpdateProductDescriptionRequest request) {
        return new LegacyUpdateDescriptionCommand(productGroupId, request.detailDescription());
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
