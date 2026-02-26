package com.ryuqq.marketplace.application.productgroup.validator;

import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupValidateReadFacade;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import org.springframework.stereotype.Component;

/**
 * ProductGroup 검증기.
 *
 * <p>상품 그룹 등록/수정 시 외부 FK 존재 여부를 검증합니다.
 *
 * <p>외부 FK 검증은 {@link ProductGroupValidateReadFacade}에 위임합니다.
 *
 * <p>고시정보 entries 검증은 각 도메인 Coordinator에서 처리합니다.
 */
@Component
public class ProductGroupValidator {

    private final ProductGroupValidateReadFacade validateReadFacade;

    public ProductGroupValidator(ProductGroupValidateReadFacade validateReadFacade) {
        this.validateReadFacade = validateReadFacade;
    }

    /**
     * 상품 그룹 등록 전 검증.
     *
     * <p>외부 FK 존재 여부 검증 (Seller, Brand, Category, ShippingPolicy, RefundPolicy)
     *
     * @param productGroup 등록 대상 ProductGroup
     */
    public void validateForRegistration(ProductGroup productGroup) {
        validateReadFacade.validateExternalReferences(
                productGroup.sellerId(),
                productGroup.brandId(),
                productGroup.categoryId(),
                productGroup.shippingPolicyId(),
                productGroup.refundPolicyId());
    }

    /**
     * 상품 그룹 수정 전 검증.
     *
     * <p>외부 FK 존재 여부 검증 (Brand, Category, ShippingPolicy, RefundPolicy)
     *
     * @param updateData 수정 데이터 (도메인 VO)
     */
    public void validateForUpdate(ProductGroupUpdateData updateData) {
        validateReadFacade.validateExternalReferencesForUpdate(
                updateData.brandId(),
                updateData.categoryId(),
                updateData.shippingPolicyId(),
                updateData.refundPolicyId());
    }
}
