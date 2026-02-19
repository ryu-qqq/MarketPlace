package com.ryuqq.marketplace.application.imagevariant.service.query;

import com.ryuqq.marketplace.application.imagevariant.assembler.ImageVariantAssembler;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.application.imagevariant.manager.ImageVariantReadManager;
import com.ryuqq.marketplace.application.imagevariant.port.in.query.GetImageVariantsByImageIdUseCase;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import java.util.List;
import org.springframework.stereotype.Service;

/** 이미지별 Variant 조회 서비스. */
@Service
public class GetImageVariantsByImageIdService implements GetImageVariantsByImageIdUseCase {

    private final ImageVariantReadManager variantReadManager;
    private final ImageVariantAssembler assembler;

    public GetImageVariantsByImageIdService(
            ImageVariantReadManager variantReadManager, ImageVariantAssembler assembler) {
        this.variantReadManager = variantReadManager;
        this.assembler = assembler;
    }

    @Override
    public List<ImageVariantResult> execute(Long sourceImageId) {
        List<ImageVariant> variants =
                variantReadManager.findBySourceImageId(
                        sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE);

        return assembler.toResults(variants);
    }
}
