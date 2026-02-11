package com.ryuqq.marketplace.domain.productgroup.aggregate;

import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.CdnPath;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 상품 그룹 상세설명 Aggregate Root. ProductGroupId로 ProductGroup과 연결되며, 독립적인 생명주기를 갖는다. 대용량 HTML 콘텐츠를 별도
 * Aggregate로 분리하여 목록 조회 시 불필요한 로드를 방지.
 */
public class ProductGroupDescription {

    private final ProductGroupDescriptionId id;
    private final ProductGroupId productGroupId;
    private DescriptionHtml content;
    private CdnPath cdnPath;
    private final List<DescriptionImage> images;

    private ProductGroupDescription(
            ProductGroupDescriptionId id,
            ProductGroupId productGroupId,
            DescriptionHtml content,
            CdnPath cdnPath,
            List<DescriptionImage> images) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.content = content;
        this.cdnPath = cdnPath;
        this.images = new ArrayList<>(images);
    }

    /** 신규 상세설명 생성. */
    public static ProductGroupDescription forNew(
            ProductGroupId productGroupId, DescriptionHtml content) {
        return new ProductGroupDescription(
                ProductGroupDescriptionId.forNew(), productGroupId, content, null, List.of());
    }

    /** 영속성에서 복원 시 사용. */
    public static ProductGroupDescription reconstitute(
            ProductGroupDescriptionId id,
            ProductGroupId productGroupId,
            DescriptionHtml content,
            CdnPath cdnPath,
            List<DescriptionImage> images) {
        return new ProductGroupDescription(id, productGroupId, content, cdnPath, images);
    }

    /** 상세설명 내용 수정. */
    public void updateContent(DescriptionHtml content) {
        this.content = content;
    }

    /** CDN 경로 설정. */
    public void updateCdnPath(CdnPath cdnPath) {
        this.cdnPath = cdnPath;
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

    public List<DescriptionImage> images() {
        return Collections.unmodifiableList(images);
    }
}
