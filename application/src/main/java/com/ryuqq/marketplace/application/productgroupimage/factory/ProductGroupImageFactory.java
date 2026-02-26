package com.ryuqq.marketplace.application.productgroupimage.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImageUpdateData;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * ProductGroupImages 생성 서브 팩토리.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class ProductGroupImageFactory {

    private final TimeProvider timeProvider;

    public ProductGroupImageFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 등록 Command의 이미지 리스트로부터 ProductGroupImages 생성. */
    public ProductGroupImages createFromRegistration(
            ProductGroupId productGroupId, List<RegisterProductGroupCommand.ImageCommand> images) {
        return toProductGroupImages(
                images,
                img ->
                        ProductGroupImage.forNew(
                                productGroupId,
                                ImageUrl.of(img.originUrl()),
                                ImageType.valueOf(img.imageType()),
                                img.sortOrder()));
    }

    /** 전체 수정 Command의 이미지 리스트로부터 ProductGroupImages 생성. */
    public ProductGroupImages createFromFullUpdate(
            ProductGroupId productGroupId,
            List<UpdateProductGroupFullCommand.ImageCommand> images) {
        return toProductGroupImages(
                images,
                img ->
                        ProductGroupImage.forNew(
                                productGroupId,
                                ImageUrl.of(img.originUrl()),
                                ImageType.valueOf(img.imageType()),
                                img.sortOrder()));
    }

    /** 이미지 등록 Command로부터 ProductGroupImages 생성. */
    public ProductGroupImages createFromImageRegistration(
            ProductGroupId productGroupId,
            List<RegisterProductGroupImagesCommand.ImageCommand> images) {
        return toProductGroupImages(
                images,
                img ->
                        ProductGroupImage.forNew(
                                productGroupId,
                                ImageUrl.of(img.originUrl()),
                                ImageType.valueOf(img.imageType()),
                                img.sortOrder()));
    }

    /** 이미지 수정 Command로부터 ProductGroupImageUpdateData 생성. */
    public ProductGroupImageUpdateData createUpdateData(UpdateProductGroupImagesCommand command) {
        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());
        ProductGroupImages newImages =
                toProductGroupImages(
                        command.images(),
                        img ->
                                ProductGroupImage.forNew(
                                        productGroupId,
                                        ImageUrl.of(img.originUrl()),
                                        ImageType.valueOf(img.imageType()),
                                        img.sortOrder()));
        return ProductGroupImageUpdateData.of(newImages, timeProvider.now());
    }

    private static <T> ProductGroupImages toProductGroupImages(
            List<T> images, Function<T, ProductGroupImage> mapper) {
        List<ProductGroupImage> list = images.stream().map(mapper).toList();
        return ProductGroupImages.of(list);
    }

    /**
     * ProductGroup 이미지 ID로 ImageUploadOutbox 목록 생성.
     *
     * @param imageIds 저장된 ProductGroup 이미지 ID 목록
     * @param images ProductGroup 이미지 도메인 목록
     * @param createdAt 생성 시각
     * @return ImageUploadOutbox 목록
     */
    public List<ImageUploadOutbox> createProductGroupImageOutboxes(
            List<Long> imageIds, List<ProductGroupImage> images) {
        Instant now = timeProvider.now();
        List<ImageUploadOutbox> outboxes = new ArrayList<>();
        for (int i = 0; i < imageIds.size(); i++) {
            outboxes.add(
                    ImageUploadOutbox.forNew(
                            imageIds.get(i),
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            images.get(i).originUrlValue(),
                            now));
        }
        return outboxes;
    }
}
