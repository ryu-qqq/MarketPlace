package com.ryuqq.marketplace.application.productgroupdescription.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionUpdateData;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDescription Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class ProductGroupDescriptionCommandFactory {

    private final TimeProvider timeProvider;
    private final Set<String> excludeDomains;

    public ProductGroupDescriptionCommandFactory(
            TimeProvider timeProvider, @Value("${fileflow.cdn-domain:}") String cdnDomain) {
        this.timeProvider = timeProvider;
        this.excludeDomains =
                (cdnDomain == null || cdnDomain.isBlank()) ? Set.of() : Set.of(cdnDomain);
    }

    /**
     * 신규 상세 설명을 생성합니다.
     *
     * @param command 등록 Command
     * @return 생성된 ProductGroupDescription
     */
    public ProductGroupDescription create(RegisterProductGroupDescriptionCommand command) {
        DescriptionHtml content = DescriptionHtml.of(command.content());
        List<String> imageUrls = content.extractImageUrls(excludeDomains);

        List<DescriptionImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            images.add(DescriptionImage.forNew(ImageUrl.of(imageUrls.get(i)), i));
        }

        return ProductGroupDescription.forNew(
                ProductGroupId.of(command.productGroupId()), content, images, timeProvider.now());
    }

    /**
     * 상세 설명을 생성하거나 기존 설명을 업데이트합니다.
     *
     * @param command 수정 Command
     * @param existingOpt 기존 상세 설명 (Optional)
     * @return 생성 또는 수정된 ProductGroupDescription
     */
    public ProductGroupDescription createOrUpdateDescription(
            UpdateProductGroupDescriptionCommand command,
            Optional<ProductGroupDescription> existingOpt) {

        DescriptionHtml content = DescriptionHtml.of(command.content());

        if (existingOpt.isPresent()) {
            ProductGroupDescription existing = existingOpt.get();
            existing.updateContent(content);
            return existing;
        } else {
            List<String> imageUrls = content.extractImageUrls(excludeDomains);
            List<DescriptionImage> images = new ArrayList<>();
            for (int i = 0; i < imageUrls.size(); i++) {
                images.add(DescriptionImage.forNew(ImageUrl.of(imageUrls.get(i)), i));
            }
            return ProductGroupDescription.forNew(
                    ProductGroupId.of(command.productGroupId()),
                    content,
                    images,
                    timeProvider.now());
        }
    }

    /**
     * 수정 Command로 DescriptionUpdateData를 생성합니다.
     *
     * @param command 수정 Command
     * @return DescriptionUpdateData (컨텐츠, 새 이미지 목록, 수정 시각)
     */
    public DescriptionUpdateData createUpdateData(UpdateProductGroupDescriptionCommand command) {
        DescriptionHtml content = DescriptionHtml.of(command.content());
        List<String> imageUrls = content.extractImageUrls(excludeDomains);

        List<DescriptionImage> newImages = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            newImages.add(DescriptionImage.forNew(ImageUrl.of(imageUrls.get(i)), i));
        }

        return DescriptionUpdateData.of(content, newImages, excludeDomains, timeProvider.now());
    }

    /**
     * Description 이미지 ID로 ImageUploadOutbox 목록 생성.
     *
     * @param imageIds 저장된 Description 이미지 ID 목록
     * @param descriptionImages Description 이미지 도메인 목록
     * @param createdAt 생성 시각
     * @return ImageUploadOutbox 목록
     */
    public List<ImageUploadOutbox> createDescriptionImageOutboxes(
            List<Long> imageIds, List<DescriptionImage> descriptionImages, Instant createdAt) {
        List<ImageUploadOutbox> outboxes = new ArrayList<>();
        for (int i = 0; i < imageIds.size(); i++) {
            outboxes.add(
                    ImageUploadOutbox.forNew(
                            imageIds.get(i),
                            ImageSourceType.DESCRIPTION_IMAGE,
                            descriptionImages.get(i).originUrlValue(),
                            createdAt));
        }
        return outboxes;
    }
}
