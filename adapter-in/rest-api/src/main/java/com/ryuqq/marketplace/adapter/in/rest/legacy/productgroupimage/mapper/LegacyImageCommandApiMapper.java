package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.application.legacy.image.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

/** 레거시 상품 이미지 Request → Command 변환 매퍼. */
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
}
