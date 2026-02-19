package com.ryuqq.marketplace.application.productgroupinspection.internal.scorer;

import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionScoreType;
import java.util.List;
import org.springframework.stereotype.Component;

/** 이미지 커버리지를 평가하는 Scorer. */
@Component
public class ImageCoverageScorer implements InspectionScorer {

    private final ProductGroupImageReadManager imageReadManager;

    public ImageCoverageScorer(ProductGroupImageReadManager imageReadManager) {
        this.imageReadManager = imageReadManager;
    }

    @Override
    public InspectionScoreType type() {
        return InspectionScoreType.IMAGE_COVERAGE;
    }

    @Override
    public int score(Long productGroupId) {
        ProductGroupImages images =
                imageReadManager.getByProductGroupId(ProductGroupId.of(productGroupId));
        List<ProductGroupImage> imageList = images.toList();
        if (imageList.isEmpty()) {
            return 0;
        }

        boolean hasThumbnail =
                imageList.stream().anyMatch(img -> img.imageType() == ImageType.THUMBNAIL);
        int imageCount = imageList.size();

        int score = 0;
        if (hasThumbnail) {
            score += 50;
        }
        score += Math.min(50, imageCount * 10);
        return Math.min(100, score);
    }
}
