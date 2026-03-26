package com.ryuqq.marketplace.domain.legacy.productdescription.aggregate;

import com.ryuqq.marketplace.domain.legacy.productdescription.vo.LegacyDescriptionImageDiff;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 레거시 상품그룹 상세설명 Aggregate.
 *
 * <p>내부 ProductGroupDescription과 동일한 구조로 상세설명 콘텐츠 + 이미지를 관리한다. productGroupId를 기준으로 1:1 관계를 유지한다.
 */
public class LegacyProductGroupDescription {

    private final long productGroupId;
    private String content;
    private String cdnPath;
    private DescriptionPublishStatus publishStatus;
    private final List<LegacyDescriptionImage> images;

    private LegacyProductGroupDescription(
            long productGroupId,
            String content,
            String cdnPath,
            DescriptionPublishStatus publishStatus,
            List<LegacyDescriptionImage> images) {
        this.productGroupId = productGroupId;
        this.content = content;
        this.cdnPath = cdnPath;
        this.publishStatus = publishStatus;
        this.images = new ArrayList<>(images);
    }

    /** 신규 생성. */
    public static LegacyProductGroupDescription forNew(long productGroupId, String content) {
        return new LegacyProductGroupDescription(
                productGroupId, content, null, DescriptionPublishStatus.PENDING, List.of());
    }

    /** 영속성에서 복원. */
    public static LegacyProductGroupDescription reconstitute(
            long productGroupId,
            String content,
            String cdnPath,
            DescriptionPublishStatus publishStatus,
            List<LegacyDescriptionImage> images) {
        return new LegacyProductGroupDescription(
                productGroupId, content, cdnPath, publishStatus, images);
    }

    /**
     * 상세설명 수정 (콘텐츠 + 이미지 변경 감지).
     *
     * <p>기존 이미지와 새 이미지를 originUrl 기준으로 비교하여 추가/삭제/유지를 판단합니다. 수정 시 publishStatus를 PENDING으로 리셋하고
     * cdnPath를 초기화합니다.
     *
     * @param newContent 새 HTML 콘텐츠
     * @param newImages HTML에서 추출된 새 이미지 목록
     * @param updatedAt 수정 시각
     * @return 이미지 변경 결과
     */
    public LegacyDescriptionImageDiff update(
            String newContent, List<LegacyDescriptionImage> newImages, Instant updatedAt) {
        this.content = newContent;
        this.publishStatus = DescriptionPublishStatus.PENDING;
        this.cdnPath = null;
        return computeImageDiff(newImages, updatedAt);
    }

    private LegacyDescriptionImageDiff computeImageDiff(
            List<LegacyDescriptionImage> newImages, Instant updatedAt) {
        Map<String, LegacyDescriptionImage> existingByUrl =
                images.stream()
                        .collect(Collectors.toMap(LegacyDescriptionImage::originUrl, img -> img));

        List<LegacyDescriptionImage> added = new ArrayList<>();
        List<LegacyDescriptionImage> retained = new ArrayList<>();
        Set<String> newUrls = new HashSet<>();

        for (LegacyDescriptionImage newImage : newImages) {
            String url = newImage.originUrl();
            newUrls.add(url);

            LegacyDescriptionImage existing = existingByUrl.get(url);
            if (existing != null) {
                existing.updateSortOrder(newImage.sortOrder());
                retained.add(existing);
            } else {
                added.add(newImage);
            }
        }

        List<LegacyDescriptionImage> removed =
                images.stream().filter(img -> !newUrls.contains(img.originUrl())).toList();

        for (LegacyDescriptionImage image : removed) {
            image.delete(updatedAt);
        }

        this.images.clear();
        this.images.addAll(retained);
        this.images.addAll(added);

        return LegacyDescriptionImageDiff.of(added, removed, retained);
    }

    /** 모든 이미지 업로드 완료 시 퍼블리시 대기 상태로 전환. */
    public void markPublishReady() {
        this.publishStatus = DescriptionPublishStatus.PUBLISH_READY;
    }

    /** CDN 퍼블리시 완료 처리. */
    public void publish(String cdnPath) {
        this.cdnPath = cdnPath;
        this.publishStatus = DescriptionPublishStatus.PUBLISHED;
    }

    /** 모든 이미지가 업로드되었는지 확인. */
    public boolean isAllImagesUploaded() {
        return images.isEmpty() || images.stream().allMatch(LegacyDescriptionImage::isUploaded);
    }

    public long productGroupId() {
        return productGroupId;
    }

    public String content() {
        return content;
    }

    public String cdnPath() {
        return cdnPath;
    }

    public DescriptionPublishStatus publishStatus() {
        return publishStatus;
    }

    public List<LegacyDescriptionImage> images() {
        return Collections.unmodifiableList(images);
    }
}
