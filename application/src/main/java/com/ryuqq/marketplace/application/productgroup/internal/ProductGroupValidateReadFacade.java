package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.brand.manager.BrandReadManager;
import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 그룹 검증용 Read Facade.
 *
 * <p>외부 FK 존재 여부 검증에 필요한 조회를 하나의 트랜잭션으로 묶어 처리합니다.
 */
@Component
public class ProductGroupValidateReadFacade {

    private final SellerReadManager sellerReadManager;
    private final BrandReadManager brandReadManager;
    private final CategoryReadManager categoryReadManager;
    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final RefundPolicyReadManager refundPolicyReadManager;

    public ProductGroupValidateReadFacade(
            SellerReadManager sellerReadManager,
            BrandReadManager brandReadManager,
            CategoryReadManager categoryReadManager,
            ShippingPolicyReadManager shippingPolicyReadManager,
            RefundPolicyReadManager refundPolicyReadManager) {
        this.sellerReadManager = sellerReadManager;
        this.brandReadManager = brandReadManager;
        this.categoryReadManager = categoryReadManager;
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
    }

    /**
     * 등록 시 외부 FK 존재 여부를 검증합니다.
     *
     * <p>Seller, Brand, Category 존재 + ShippingPolicy/RefundPolicy 셀러 소유 검증을 하나의 읽기 트랜잭션으로 처리합니다.
     */
    @Transactional(readOnly = true)
    public void validateExternalReferences(
            SellerId sellerId,
            BrandId brandId,
            CategoryId categoryId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId) {
        sellerReadManager.getById(sellerId);
        brandReadManager.getById(brandId);
        categoryReadManager.getById(categoryId);
        shippingPolicyReadManager.getBySellerIdAndId(sellerId, shippingPolicyId);
        refundPolicyReadManager.getBySellerIdAndId(sellerId, refundPolicyId);
    }

    /**
     * 수정 시 외부 FK 존재 여부를 검증합니다.
     *
     * <p>Brand, Category, ShippingPolicy, RefundPolicy 존재 검증을 하나의 읽기 트랜잭션으로 처리합니다.
     */
    @Transactional(readOnly = true)
    public void validateExternalReferencesForUpdate(
            BrandId brandId,
            CategoryId categoryId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId) {
        brandReadManager.getById(brandId);
        categoryReadManager.getById(categoryId);
        shippingPolicyReadManager.getById(shippingPolicyId);
        refundPolicyReadManager.getById(refundPolicyId);
    }
}
