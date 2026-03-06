package com.ryuqq.marketplace.domain.productgroup.aggregate;

import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.productgroup.event.ProductGroupActivatedEvent;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupInvalidStatusTransitionException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 상품 그룹 Aggregate Root. 상품의 상위 개념으로, 공통 속성과 셀러 옵션 구조를 관리한다. 상세설명(ProductGroupDescription)은 별도
 * Aggregate로 분리되어 ProductGroupId로 연결된다.
 */
public class ProductGroup {

    private final ProductGroupId id;
    private final SellerId sellerId;
    private BrandId brandId;
    private CategoryId categoryId;
    private ShippingPolicyId shippingPolicyId;
    private RefundPolicyId refundPolicyId;
    private ProductGroupName productGroupName;
    private OptionType optionType;
    private ProductGroupStatus status;
    private ProductGroupImages productGroupImages;
    private SellerOptionGroups sellerOptionGroups;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<DomainEvent> events = new ArrayList<>();

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
            ProductGroupImages productGroupImages,
            SellerOptionGroups sellerOptionGroups,
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
        this.productGroupImages = productGroupImages;
        this.sellerOptionGroups = sellerOptionGroups;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 상품 그룹 생성. DRAFT 상태로 시작. 이미지/옵션은 별도 persist. */
    public static ProductGroup forNew(
            SellerId sellerId,
            BrandId brandId,
            CategoryId categoryId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            ProductGroupName productGroupName,
            OptionType optionType,
            Instant now) {
        return new ProductGroup(
                ProductGroupId.forNew(),
                sellerId,
                brandId,
                categoryId,
                shippingPolicyId,
                refundPolicyId,
                productGroupName,
                optionType,
                ProductGroupStatus.DRAFT,
                ProductGroupImages.reconstitute(List.of()),
                SellerOptionGroups.reconstitute(List.of()),
                now,
                now);
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
                id,
                sellerId,
                brandId,
                categoryId,
                shippingPolicyId,
                refundPolicyId,
                productGroupName,
                optionType,
                status,
                ProductGroupImages.reconstitute(images),
                SellerOptionGroups.reconstitute(sellerOptionGroups),
                createdAt,
                updatedAt);
    }

    // ── 비즈니스 메서드 ──

    /** targetStatus에 따라 적절한 상태 전이 메서드를 호출한다. */
    public void changeStatus(ProductGroupStatus targetStatus, Instant now) {
        switch (targetStatus) {
            case PROCESSING -> startProcessing(now);
            case ACTIVE -> activate(now);
            case INACTIVE -> deactivate(now);
            case SOLD_OUT -> markSoldOut(now);
            case PENDING_REVIEW -> pendingReview(now);
            case REJECTED -> reject(now);
            case DELETED -> delete(now);
            default -> throw new IllegalArgumentException("지원하지 않는 상태 변경입니다: " + targetStatus);
        }
    }

    /** 검수 시작. DRAFT → PROCESSING. */
    public void startProcessing(Instant now) {
        if (!status.canProcess()) {
            throw new ProductGroupInvalidStatusTransitionException(
                    status, ProductGroupStatus.PROCESSING);
        }
        this.status = ProductGroupStatus.PROCESSING;
        this.updatedAt = now;
    }

    /** 검수 대기. PROCESSING → PENDING_REVIEW. 분석 판정이 HUMAN_REVIEW일 때. */
    public void pendingReview(Instant now) {
        if (!status.canPendingReview()) {
            throw new ProductGroupInvalidStatusTransitionException(
                    status, ProductGroupStatus.PENDING_REVIEW);
        }
        this.status = ProductGroupStatus.PENDING_REVIEW;
        this.updatedAt = now;
    }

    /** 검수 반려. PROCESSING → REJECTED. */
    public void reject(Instant now) {
        if (!status.canReject()) {
            throw new ProductGroupInvalidStatusTransitionException(
                    status, ProductGroupStatus.REJECTED);
        }
        this.status = ProductGroupStatus.REJECTED;
        this.updatedAt = now;
    }

    /** 판매 활성화. ProductGroupImages VO가 THUMBNAIL 존재를 보장. 활성화 이벤트를 자동 등록. */
    public void activate(Instant now) {
        if (!status.canActivate()) {
            throw new ProductGroupInvalidStatusTransitionException(
                    status, ProductGroupStatus.ACTIVE);
        }
        this.status = ProductGroupStatus.ACTIVE;
        this.updatedAt = now;
        registerEvent(ProductGroupActivatedEvent.of(id, sellerId, now));
    }

    /** 판매 중지. */
    public void deactivate(Instant now) {
        if (!status.isActive()) {
            throw new ProductGroupInvalidStatusTransitionException(
                    status, ProductGroupStatus.INACTIVE);
        }
        this.status = ProductGroupStatus.INACTIVE;
        this.updatedAt = now;
    }

    /** 품절 처리. */
    public void markSoldOut(Instant now) {
        if (!status.isActive()) {
            throw new ProductGroupInvalidStatusTransitionException(
                    status, ProductGroupStatus.SOLD_OUT);
        }
        this.status = ProductGroupStatus.SOLD_OUT;
        this.updatedAt = now;
    }

    /** 소프트 삭제. */
    public void delete(Instant now) {
        if (!status.canDelete()) {
            throw new ProductGroupInvalidStatusTransitionException(
                    status, ProductGroupStatus.DELETED);
        }
        this.status = ProductGroupStatus.DELETED;
        this.updatedAt = now;
    }

    /** 기본 정보 수정. */
    public void update(ProductGroupUpdateData updateData) {
        this.productGroupName = updateData.productGroupName();
        this.brandId = updateData.brandId();
        this.categoryId = updateData.categoryId();
        this.shippingPolicyId = updateData.shippingPolicyId();
        this.refundPolicyId = updateData.refundPolicyId();
        this.optionType = updateData.optionType();
        this.updatedAt = updateData.updatedAt();
    }

    /** 이미지 전체 교체. ProductGroupImages VO가 검증/정렬을 보장. */
    public void replaceImages(ProductGroupImages images) {
        this.productGroupImages = images;
    }

    /** 셀러 옵션 그룹 전체 교체. SellerOptionGroups VO가 불변식을 보장. */
    public void replaceSellerOptionGroups(SellerOptionGroups optionGroups) {
        this.sellerOptionGroups = optionGroups;
        validateOptionStructure();
    }

    // ── 이벤트 메서드 ──

    protected void registerEvent(DomainEvent event) {
        this.events.add(event);
    }

    public List<DomainEvent> pollEvents() {
        List<DomainEvent> polled = new ArrayList<>(this.events);
        this.events.clear();
        return Collections.unmodifiableList(polled);
    }

    // ── 검증 메서드 ──

    /** optionType과 내부 셀러 옵션 그룹 수 정합성 검증. */
    private void validateOptionStructure() {
        sellerOptionGroups.validateStructure(optionType);
    }

    // ── 조회 메서드 ──

    /** 모든 셀러 옵션이 캐노니컬에 매핑되었는지 확인. */
    public boolean isFullyMappedToCanonical() {
        return sellerOptionGroups.isFullyMappedToCanonical();
    }

    /** 총 옵션 값 수 (전체 그룹 합산). */
    public int totalOptionValueCount() {
        return sellerOptionGroups.totalOptionValueCount();
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
        return productGroupImages.toList();
    }

    public List<SellerOptionGroup> sellerOptionGroups() {
        return sellerOptionGroups.groups();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
