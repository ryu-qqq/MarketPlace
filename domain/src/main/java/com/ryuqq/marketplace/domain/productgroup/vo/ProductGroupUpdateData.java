package com.ryuqq.marketplace.domain.productgroup.vo;

import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;

/**
 * ProductGroup 기본 정보 수정 데이터.
 *
 * <p>수정 대상 ID, 변경할 기본 정보, 수정 시각을 불변으로 보관합니다.
 */
public record ProductGroupUpdateData(
        ProductGroupId productGroupId,
        ProductGroupName productGroupName,
        BrandId brandId,
        CategoryId categoryId,
        ShippingPolicyId shippingPolicyId,
        RefundPolicyId refundPolicyId,
        Instant updatedAt) {

    public static ProductGroupUpdateData of(
            ProductGroupId productGroupId,
            ProductGroupName productGroupName,
            BrandId brandId,
            CategoryId categoryId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            Instant updatedAt) {
        return new ProductGroupUpdateData(
                productGroupId,
                productGroupName,
                brandId,
                categoryId,
                shippingPolicyId,
                refundPolicyId,
                updatedAt);
    }
}
