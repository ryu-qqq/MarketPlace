package com.ryuqq.marketplace.application.productgroup.validator;

import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupValidateReadFacade;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import org.springframework.stereotype.Component;

/**
 * ProductGroup 검증기.
 *
 * <p>상품 그룹 등록/수정 시 외부 FK 존재 여부 및 비즈니스 규칙을 검증합니다.
 *
 * <p>외부 FK 검증은 {@link ProductGroupValidateReadFacade}에 위임하고, 고시정보 검증은 {@link
 * NoticeEntriesValidator}에 위임합니다.
 */
@Component
public class ProductGroupValidator {

    private final ProductGroupValidateReadFacade validateReadFacade;
    private final NoticeEntriesValidator noticeEntriesValidator;

    public ProductGroupValidator(
            ProductGroupValidateReadFacade validateReadFacade,
            NoticeEntriesValidator noticeEntriesValidator) {
        this.validateReadFacade = validateReadFacade;
        this.noticeEntriesValidator = noticeEntriesValidator;
    }

    /**
     * 상품 그룹 등록 전 검증.
     *
     * <p>1. 외부 FK 존재 여부 검증 (Seller, Brand, Category, ShippingPolicy, RefundPolicy)
     *
     * <p>2. ShippingPolicy/RefundPolicy 셀러 소유 검증
     *
     * <p>3. 고시정보 entries 검증 (카테고리 필드 일치 + 필수 필드)
     *
     * @param bundle 등록 번들
     */
    public void validateForRegistration(ProductGroupRegistrationBundle bundle) {
        ProductGroup productGroup = bundle.productGroup();

        validateReadFacade.validateExternalReferences(
                productGroup.sellerId(),
                productGroup.brandId(),
                productGroup.categoryId(),
                productGroup.shippingPolicyId(),
                productGroup.refundPolicyId());

        noticeEntriesValidator.validate(bundle.noticeEntries());
    }

    /**
     * 상품 그룹 수정 전 검증.
     *
     * <p>1. 외부 FK 존재 여부 검증 (Brand, Category, ShippingPolicy, RefundPolicy)
     *
     * <p>2. 고시정보 entries 검증
     *
     * @param bundle 수정 번들
     */
    public void validateForUpdate(ProductGroupUpdateBundle bundle) {
        validateReadFacade.validateExternalReferencesForUpdate(
                bundle.brandId(),
                bundle.categoryId(),
                bundle.shippingPolicyId(),
                bundle.refundPolicyId());

        noticeEntriesValidator.validate(bundle.noticeEntries());
    }
}
