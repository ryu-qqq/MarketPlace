package com.ryuqq.marketplace.domain.productgroup.vo;

import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Description 수정 데이터.
 *
 * <p>수정할 컨텐츠, 새 이미지 목록, 수정 시각, 제외 도메인을 불변으로 보관합니다.
 */
public class DescriptionUpdateData {

    private final DescriptionHtml content;
    private final List<DescriptionImage> newImages;
    private final Set<String> excludeDomains;
    private final Instant updatedAt;

    private DescriptionUpdateData(
            DescriptionHtml content,
            List<DescriptionImage> newImages,
            Set<String> excludeDomains,
            Instant updatedAt) {
        this.content = content;
        this.newImages = newImages;
        this.excludeDomains = excludeDomains;
        this.updatedAt = updatedAt;
    }

    public static DescriptionUpdateData of(
            DescriptionHtml content, List<DescriptionImage> newImages, Instant updatedAt) {
        return of(content, newImages, Set.of(), updatedAt);
    }

    public static DescriptionUpdateData of(
            DescriptionHtml content,
            List<DescriptionImage> newImages,
            Set<String> excludeDomains,
            Instant updatedAt) {
        return new DescriptionUpdateData(
                content, List.copyOf(newImages), Set.copyOf(excludeDomains), updatedAt);
    }

    public DescriptionHtml content() {
        return content;
    }

    public List<DescriptionImage> newImages() {
        return newImages;
    }

    public Set<String> excludeDomains() {
        return excludeDomains;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
