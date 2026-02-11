package com.ryuqq.marketplace.domain.productgroup.aggregate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupInvalidOptionStructureException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupInvalidStatusTransitionException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNoThumbnailException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;

/**
 * 상품 그룹 Aggregate Root.
 * 상품의 상위 개념으로, 공통 속성과 셀러 옵션 구조를 관리한다.
 * 상세설명(ProductGroupDescription)은 별도 Aggregate로 분리되어 ProductGroupId로 연결된다.
 */
public class ProductGroup {

    private final ProductGroupId id;
    private final SellerId sellerId;
    private BrandId brandId;
    private CategoryId categoryId;
    private ShippingPolicyId shippingPolicyId;
    private RefundPolicyId refundPolicyId;
    private ProductGroupName productGroupName;
    private final OptionType optionType;
    private ProductGroupStatus status;
    private final List<ProductGroupImage> images;
    private final List<SellerOptionGroup> sellerOptionGroups;
    private final Instant createdAt;
    private Instant updatedAt;

    private ProductGroup(
            ProductGroupId id,
            SellerId sellerId,
            BrandId brandId,
            CategoryId categoryId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            ProductGroupName productGroupName,
            OptionType optionType,
            ProductGroupStatus status,
            List<ProductGroupImage> images,
            List<SellerOptionGroup> sellerOptionGroups,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.shippingPolicyId = shippingPolicyId;
        this.refundPolicyId = refundPolicyId;
        this.productGroupName = productGroupName;
        this.optionType = optionType;
        this.status = status;
        this.images = new ArrayList<>(images);
        this.sellerOptionGroups = new ArrayList<>(sellerOptionGroups);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 상품 그룹 생성. DRAFT 상태로 시작. */
    public static ProductGroup forNew(
            SellerId sellerId,
            BrandId brandId,
            CategoryId categoryId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            ProductGroupName productGroupName,
            OptionType optionType,
            List<ProductGroupImage> images,
            List<SellerOptionGroup> sellerOptionGroups,
            Instant now) {
        ProductGroup productGroup = new ProductGroup(
                ProductGroupId.forNew(),
                sellerId,
                brandId,
                categoryId,
                shippingPolicyId,
                refundPolicyId,
                productGroupName,
                optionType,
                ProductGroupStatus.DRAFT,
                images,
                sellerOptionGroups,
                now,
                now);
        productGroup.validateOptionStructure();
        return productGroup;
    }

    /** 영속성에서 복원 시 사용. */
    public static ProductGroup reconstitute(
            ProductGroupId id,
            SellerId sellerId,
            BrandId brandId,
            CategoryId categoryId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            ProductGroupName productGroupName,
            OptionType optionType,
            ProductGroupStatus status,
            List<ProductGroupImage> images,
            List<SellerOptionGroup> sellerOptionGroups,
            Instant createdAt,
            Instant updatedAt) {
        return new ProductGroup(
                id, sellerId, brandId, categoryId, shippingPolicyId, refundPolicyId,
                productGroupName, optionType, status,
                images, sellerOptionGroups, createdAt, updatedAt);
    }

    // ── 비즈니스 메서드 ──

    /** 판매 활성화. THUMBNAIL 이미지 최소 1개 필수. */
    public void activate(Instant now) {
        if (!status.canActivate()) {
            throw new ProductGroupInvalidStatusTransitionException(status, ProductGroupStatus.ACTIVE);
        }
        if (!hasThumbnailImage()) {
            throw new ProductGroupNoThumbnailException(idValue());
        }
        this.status = ProductGroupStatus.ACTIVE;
        this.updatedAt = now;
    }

    /** 판매 중지. */
    public void deactivate(Instant now) {
        if (!status.isActive()) {
            throw new ProductGroupInvalidStatusTransitionException(status, ProductGroupStatus.INACTIVE);
        }
        this.status = ProductGroupStatus.INACTIVE;
        this.updatedAt = now;
    }

    /** 품절 처리. */
    public void markSoldOut(Instant now) {
        if (!status.isActive()) {
            throw new ProductGroupInvalidStatusTransitionException(status, ProductGroupStatus.SOLDOUT);
        }
        this.status = ProductGroupStatus.SOLDOUT;
        this.updatedAt = now;
    }

    /** 소프트 삭제. */
    public void delete(Instant now) {
        if (!status.canDelete()) {
            throw new ProductGroupInvalidStatusTransitionException(status, ProductGroupStatus.DELETED);
        }
        this.status = ProductGroupStatus.DELETED;
        this.updatedAt = now;
    }

    /** 기본 정보 수정. */
    public void updateBasicInfo(
            ProductGroupName productGroupName,
            BrandId brandId,
            CategoryId categoryId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            Instant now) {
        this.productGroupName = productGroupName;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.shippingPolicyId = shippingPolicyId;
        this.refundPolicyId = refundPolicyId;
        this.updatedAt = now;
    }

    /** 이미지 추가. */
    public void addImage(ProductGroupImage image) {
        this.images.add(image);
    }

    /** 이미지 전체 교체. */
    public void replaceImages(List<ProductGroupImage> images) {
        this.images.clear();
        this.images.addAll(images);
    }

    /** 셀러 옵션 그룹 추가. */
    public void addSellerOptionGroup(SellerOptionGroup optionGroup) {
        this.sellerOptionGroups.add(optionGroup);
    }

    /** 셀러 옵션 그룹 전체 교체. */
    public void replaceSellerOptionGroups(List<SellerOptionGroup> optionGroups) {
        this.sellerOptionGroups.clear();
        this.sellerOptionGroups.addAll(optionGroups);
        validateOptionStructure();
    }

    // ── 검증 메서드 ──

    /** optionType과 셀러 옵션 그룹 수 정합성 검증. */
    private void validateOptionStructure() {
        int expected = optionType.expectedOptionGroupCount();
        int actual = sellerOptionGroups.size();
        if (optionType.requiresOptionGroup() && actual != expected) {
            throw new ProductGroupInvalidOptionStructureException(optionType, expected, actual);
        }
        if (!optionType.requiresOptionGroup() && actual > 0) {
            throw new ProductGroupInvalidOptionStructureException(optionType, expected, actual);
        }
    }

    // ── 조회 메서드 ──

    /** THUMBNAIL 이미지 존재 여부. */
    public boolean hasThumbnailImage() {
        return images.stream().anyMatch(ProductGroupImage::isThumbnail);
    }

    /** 모든 셀러 옵션이 캐노니컬에 매핑되었는지 확인. */
    public boolean isFullyMappedToCanonical() {
        if (sellerOptionGroups.isEmpty()) {
            return true;
        }
        return sellerOptionGroups.stream().allMatch(SellerOptionGroup::isFullyMapped);
    }

    /** 총 옵션 값 수 (전체 그룹 합산). */
    public int totalOptionValueCount() {
        return sellerOptionGroups.stream()
                .mapToInt(SellerOptionGroup::optionValueCount)
                .sum();
    }

    // ── Accessor 메서드 ──

    public ProductGroupId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId.value();
    }

    public BrandId brandId() {
        return brandId;
    }

    public Long brandIdValue() {
        return brandId.value();
    }

    public CategoryId categoryId() {
        return categoryId;
    }

    public Long categoryIdValue() {
        return categoryId.value();
    }

    public ShippingPolicyId shippingPolicyId() {
        return shippingPolicyId;
    }

    public Long shippingPolicyIdValue() {
        return shippingPolicyId.value();
    }

    public RefundPolicyId refundPolicyId() {
        return refundPolicyId;
    }

    public Long refundPolicyIdValue() {
        return refundPolicyId.value();
    }

    public ProductGroupName productGroupName() {
        return productGroupName;
    }

    public String productGroupNameValue() {
        return productGroupName.value();
    }

    public OptionType optionType() {
        return optionType;
    }

    public ProductGroupStatus status() {
        return status;
    }

    public List<ProductGroupImage> images() {
        return Collections.unmodifiableList(images);
    }

    public List<SellerOptionGroup> sellerOptionGroups() {
        return Collections.unmodifiableList(sellerOptionGroups);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
