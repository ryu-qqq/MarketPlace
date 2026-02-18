package com.ryuqq.marketplace.application.productgroupinspection.validator;

import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 검수 실행 선행 조건 검증기.
 *
 * <p>이미지 업로드, 상세설명 이미지 업로드 등 비동기 처리가 완료되었는지 확인합니다. 선행 조건이 충족되지 않으면 검수를 스킵하고 다음 스케줄러 사이클에서 재시도합니다.
 */
@Component
public class InspectionReadinessValidator {

    private final ProductGroupImageReadManager imageReadManager;
    private final ProductGroupDescriptionReadManager descriptionReadManager;

    public InspectionReadinessValidator(
            ProductGroupImageReadManager imageReadManager,
            ProductGroupDescriptionReadManager descriptionReadManager) {
        this.imageReadManager = imageReadManager;
        this.descriptionReadManager = descriptionReadManager;
    }

    /**
     * 검수 실행 가능 여부를 확인합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 모든 선행 조건이 충족되면 true
     */
    public boolean isReady(Long productGroupId) {
        return isImageUploadComplete(productGroupId) && isDescriptionUploadComplete(productGroupId);
    }

    private boolean isImageUploadComplete(Long productGroupId) {
        ProductGroupImages images =
                imageReadManager.getByProductGroupId(ProductGroupId.of(productGroupId));
        if (images.isEmpty()) {
            return true;
        }
        return images.toList().stream().allMatch(img -> img.uploadedUrl() != null);
    }

    private boolean isDescriptionUploadComplete(Long productGroupId) {
        Optional<ProductGroupDescription> descOpt =
                descriptionReadManager.findByProductGroupId(ProductGroupId.of(productGroupId));
        return descOpt.map(ProductGroupDescription::isAllImagesUploaded).orElse(true);
    }
}
