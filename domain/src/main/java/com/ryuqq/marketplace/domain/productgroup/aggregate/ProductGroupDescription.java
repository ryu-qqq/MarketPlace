package com.ryuqq.marketplace.domain.productgroup.aggregate;

import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.CdnPath;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionImageDiff;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionUpdateData;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 상품 그룹 상세설명 Aggregate Root. ProductGroupId로 ProductGroup과 연결되며, 독립적인 생명주기를 갖는다. 대용량 HTML 콘텐츠를 별도
 * Aggregate로 분리하여 목록 조회 시 불필요한 로드를 방지.
 */
public class ProductGroupDescription {

    private final ProductGroupDescriptionId id;
    private final ProductGroupId productGroupId;
    private DescriptionHtml content;
    private CdnPath cdnPath;
    private DescriptionPublishStatus publishStatus;
    private final List<DescriptionImage> images;
    private final Instant createdAt;
    private Instant updatedAt;

    private ProductGroupDescription(
            ProductGroupDescriptionId id,
            ProductGroupId productGroupId,
            DescriptionHtml content,
            CdnPath cdnPath,
            DescriptionPublishStatus publishStatus,
            List<DescriptionImage> images,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.content = content;
        this.cdnPath = cdnPath;
        this.publishStatus = publishStatus;
        this.images = new ArrayList<>(images);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 상세설명 생성. 이미지가 없으면 즉시 PUBLISH_READY. */
    public static ProductGroupDescription forNew(
            ProductGroupId productGroupId, DescriptionHtml content, Instant now) {
        return new ProductGroupDescription(
                ProductGroupDescriptionId.forNew(),
                productGroupId,
                content,
                null,
                DescriptionPublishStatus.PENDING,
                List.of(),
                now,
                now);
    }

    /** 신규 상세설명 생성 (이미지 포함). */
    public static ProductGroupDescription forNew(
            ProductGroupId productGroupId,
            DescriptionHtml content,
            List<DescriptionImage> images,
            Instant now) {
        return new ProductGroupDescription(
                ProductGroupDescriptionId.forNew(),
                productGroupId,
                content,
                null,
                DescriptionPublishStatus.PENDING,
                images,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static ProductGroupDescription reconstitute(
            ProductGroupDescriptionId id,
            ProductGroupId productGroupId,
            DescriptionHtml content,
            CdnPath cdnPath,
            DescriptionPublishStatus publishStatus,
            List<DescriptionImage> images,
            Instant createdAt,
            Instant updatedAt) {
        return new ProductGroupDescription(
                id, productGroupId, content, cdnPath, publishStatus, images, createdAt, updatedAt);
    }

    /** 영속화 후 발급된 ID를 할당하고, 소유 images에도 전파한다. */
    public void assignId(ProductGroupDescriptionId assignedId) {
        for (DescriptionImage image : images) {
            image.assignProductGroupDescriptionId(assignedId);
        }
    }

    /** 상세설명 내용 수정. */
    public void updateContent(DescriptionHtml content) {
        this.content = content;
    }

    /**
     * 상세설명 수정 (컨텐츠 + 이미지 변경 감지).
     *
     * <p>기존 이미지와 새 이미지를 originUrl 기준으로 비교하여 추가/삭제/유지를 판단합니다. 수정 시 publishStatus를 PENDING으로 리셋하고
     * cdnPath를 초기화합니다.
     *
     * @param updateData 수정 데이터 (컨텐츠, 새 이미지, 수정 시각)
     * @return 이미지 변경 결과 (added, removed, retained)
     */
    public DescriptionImageDiff update(DescriptionUpdateData updateData) {
        this.content = updateData.content();
        this.updatedAt = updateData.updatedAt();
        this.publishStatus = DescriptionPublishStatus.PENDING;
        this.cdnPath = null;
        return computeImageDiff(
                updateData.newImages(), updateData.excludeDomains(), updateData.updatedAt());
    }

    private DescriptionImageDiff computeImageDiff(
            List<DescriptionImage> newImages, Set<String> excludeDomains, Instant updatedAt) {
        Map<String, DescriptionImage> existingByUrl =
                images.stream()
                        .collect(
                                Collectors.toMap(
                                        DescriptionImage::originUrlValue,
                                        img -> img,
                                        (existing, duplicate) -> existing));

        Set<String> hiddenUrls =
                content != null ? content.extractHiddenImageUrls(excludeDomains) : Set.of();

        List<DescriptionImage> added = new ArrayList<>();
        List<DescriptionImage> retained = new ArrayList<>();
        Set<String> newUrls = new HashSet<>();

        for (DescriptionImage newImage : newImages) {
            String url = newImage.originUrlValue();
            newUrls.add(url);

            DescriptionImage existing = existingByUrl.get(url);
            if (existing != null) {
                existing.updateSortOrder(newImage.sortOrder());
                retained.add(existing);
            } else {
                newImage.assignProductGroupDescriptionId(this.id);
                added.add(newImage);
            }
        }

        List<DescriptionImage> removed =
                images.stream()
                        .filter(
                                img ->
                                        !newUrls.contains(img.originUrlValue())
                                                && !hiddenUrls.contains(img.originUrlValue()))
                        .toList();

        for (DescriptionImage image : removed) {
            image.delete(updatedAt);
        }

        this.images.clear();
        this.images.addAll(retained);
        this.images.addAll(added);

        return DescriptionImageDiff.of(added, removed, retained);
    }

    /** 모든 이미지 업로드 완료 시 퍼블리시 대기 상태로 전환. */
    public void markPublishReady() {
        this.publishStatus = DescriptionPublishStatus.PUBLISH_READY;
    }

    /**
     * CDN 퍼블리시 완료 처리.
     *
     * @param cdnPath CDN 업로드 경로
     */
    public void publish(CdnPath cdnPath) {
        this.cdnPath = cdnPath;
        this.publishStatus = DescriptionPublishStatus.PUBLISHED;
    }

    /**
     * 이미지 원본 URL을 업로드된 CDN URL로 치환한 퍼블리시용 HTML을 생성한다.
     *
     * @return 이미지 URL이 치환된 DescriptionHtml
     */
    public DescriptionHtml buildPublishableHtml() {
        Map<String, String> urlMapping = new HashMap<>();
        for (DescriptionImage image : images) {
            if (image.uploadedUrlValue() != null) {
                urlMapping.put(image.originUrlValue(), image.uploadedUrlValue());
            }
        }
        return content.replaceImageUrls(urlMapping);
    }

    /** 퍼블리시 시 사용할 파일명을 반환한다. */
    public String publishFilename() {
        return idValue() + ".html";
    }

    /** 상세설명 이미지 전체 교체. */
    public void replaceImages(List<DescriptionImage> images) {
        this.images.clear();
        this.images.addAll(images);
    }

    /** 상세설명 이미지 추가. */
    public void addImage(DescriptionImage image) {
        this.images.add(image);
    }

    /** 이미지 수. */
    public int imageCount() {
        return images.size();
    }

    /** 모든 이미지가 S3에 업로드되었는지 확인. */
    public boolean isAllImagesUploaded() {
        return images.isEmpty() || images.stream().allMatch(DescriptionImage::isUploaded);
    }

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }

    public ProductGroupDescriptionId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public ProductGroupId productGroupId() {
        return productGroupId;
    }

    public Long productGroupIdValue() {
        return productGroupId.value();
    }

    public DescriptionHtml content() {
        return content;
    }

    public String contentValue() {
        return content != null ? content.value() : null;
    }

    public CdnPath cdnPath() {
        return cdnPath;
    }

    public String cdnPathValue() {
        return cdnPath != null ? cdnPath.value() : null;
    }

    public DescriptionPublishStatus publishStatus() {
        return publishStatus;
    }

    public List<DescriptionImage> images() {
        return Collections.unmodifiableList(images);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
