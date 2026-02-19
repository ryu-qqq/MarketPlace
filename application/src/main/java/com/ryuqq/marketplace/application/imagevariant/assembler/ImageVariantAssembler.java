package com.ryuqq.marketplace.application.imagevariant.assembler;

import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import java.util.List;
import org.springframework.stereotype.Component;

/** ImageVariant Assembler. */
@Component
public class ImageVariantAssembler {

    public ImageVariantResult toResult(ImageVariant variant) {
        return ImageVariantResult.from(variant);
    }

    public List<ImageVariantResult> toResults(List<ImageVariant> variants) {
        return variants.stream().map(this::toResult).toList();
    }
}
