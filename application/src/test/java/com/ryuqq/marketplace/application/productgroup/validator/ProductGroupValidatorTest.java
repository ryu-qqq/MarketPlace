package com.ryuqq.marketplace.application.productgroup.validator;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupValidateReadFacade;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupValidator 단위 테스트")
class ProductGroupValidatorTest {

    @InjectMocks private ProductGroupValidator sut;

    @Mock private ProductGroupValidateReadFacade validateReadFacade;

    @Nested
    @DisplayName("validateForRegistration() - 등록 전 검증")
    class ValidateForRegistrationTest {

        @Test
        @DisplayName("신규 ProductGroup 등록 전 외부 FK 검증을 위임한다")
        void validateForRegistration_NewProductGroup_DelegatesToFacade() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroup();

            // when
            sut.validateForRegistration(productGroup);

            // then
            then(validateReadFacade)
                    .should()
                    .validateExternalReferences(
                            productGroup.sellerId(),
                            productGroup.brandId(),
                            productGroup.categoryId(),
                            productGroup.shippingPolicyId(),
                            productGroup.refundPolicyId());
        }

        @Test
        @DisplayName("단일 옵션 ProductGroup 등록 전에도 외부 FK 검증을 위임한다")
        void validateForRegistration_SingleOptionProductGroup_DelegatesToFacade() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroupWithSingleOption();

            // when
            sut.validateForRegistration(productGroup);

            // then
            then(validateReadFacade)
                    .should()
                    .validateExternalReferences(
                            productGroup.sellerId(),
                            productGroup.brandId(),
                            productGroup.categoryId(),
                            productGroup.shippingPolicyId(),
                            productGroup.refundPolicyId());
        }
    }

    @Nested
    @DisplayName("validateForUpdate() - 수정 전 검증")
    class ValidateForUpdateTest {

        @Test
        @DisplayName("수정 데이터로 외부 FK 검증을 위임한다")
        void validateForUpdate_ValidUpdateData_DelegatesToFacade() {
            // given
            ProductGroupUpdateData updateData = createUpdateData(1L);

            // when
            sut.validateForUpdate(updateData);

            // then
            then(validateReadFacade)
                    .should()
                    .validateExternalReferencesForUpdate(
                            updateData.brandId(),
                            updateData.categoryId(),
                            updateData.shippingPolicyId(),
                            updateData.refundPolicyId());
        }

        @Test
        @DisplayName("다른 브랜드로 수정 시에도 외부 FK 검증을 위임한다")
        void validateForUpdate_DifferentBrand_DelegatesToFacade() {
            // given
            ProductGroupUpdateData updateData = createUpdateData(2L);

            // when
            sut.validateForUpdate(updateData);

            // then
            then(validateReadFacade)
                    .should()
                    .validateExternalReferencesForUpdate(
                            updateData.brandId(),
                            updateData.categoryId(),
                            updateData.shippingPolicyId(),
                            updateData.refundPolicyId());
        }
    }

    private ProductGroupUpdateData createUpdateData(long productGroupId) {
        return ProductGroupUpdateData.of(
                ProductGroupId.of(productGroupId),
                ProductGroupFixtures.defaultProductGroupName(),
                com.ryuqq.marketplace.domain.brand.id.BrandId.of(
                        ProductGroupFixtures.DEFAULT_BRAND_ID),
                com.ryuqq.marketplace.domain.category.id.CategoryId.of(
                        ProductGroupFixtures.DEFAULT_CATEGORY_ID),
                com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId.of(
                        ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID),
                com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId.of(
                        ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID),
                Instant.now());
    }
}
