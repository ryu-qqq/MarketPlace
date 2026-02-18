package com.ryuqq.marketplace.application.productgroup.internal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.brand.manager.BrandReadManager;
import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
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
@DisplayName("ProductGroupValidateReadFacade 단위 테스트")
class ProductGroupValidateReadFacadeTest {

    @InjectMocks private ProductGroupValidateReadFacade sut;

    @Mock private SellerReadManager sellerReadManager;
    @Mock private BrandReadManager brandReadManager;
    @Mock private CategoryReadManager categoryReadManager;
    @Mock private ShippingPolicyReadManager shippingPolicyReadManager;
    @Mock private RefundPolicyReadManager refundPolicyReadManager;

    @Nested
    @DisplayName("validateExternalReferences() - 등록 시 외부 FK 검증")
    class ValidateExternalReferencesTest {

        @Test
        @DisplayName("모든 외부 FK 검증을 각 ReadManager에 위임한다")
        void validateExternalReferences_ValidIds_DelegatesToAllReadManagers() {
            // given
            SellerId sellerId = SellerId.of(ProductGroupFixtures.DEFAULT_SELLER_ID);
            BrandId brandId = BrandId.of(ProductGroupFixtures.DEFAULT_BRAND_ID);
            CategoryId categoryId = CategoryId.of(ProductGroupFixtures.DEFAULT_CATEGORY_ID);
            ShippingPolicyId shippingPolicyId =
                    ShippingPolicyId.of(ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID);
            RefundPolicyId refundPolicyId =
                    RefundPolicyId.of(ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID);

            // when
            sut.validateExternalReferences(
                    sellerId, brandId, categoryId, shippingPolicyId, refundPolicyId);

            // then
            then(sellerReadManager).should().getById(sellerId);
            then(brandReadManager).should().getById(brandId);
            then(categoryReadManager).should().getById(categoryId);
            then(shippingPolicyReadManager).should().getBySellerIdAndId(sellerId, shippingPolicyId);
            then(refundPolicyReadManager).should().getBySellerIdAndId(sellerId, refundPolicyId);
        }

        @Test
        @DisplayName("Seller 검증 실패 시 예외가 발생하고 이후 검증이 수행되지 않는다")
        void validateExternalReferences_SellerNotFound_ThrowsException() {
            // given
            SellerId sellerId = SellerId.of(999L);
            BrandId brandId = BrandId.of(ProductGroupFixtures.DEFAULT_BRAND_ID);
            CategoryId categoryId = CategoryId.of(ProductGroupFixtures.DEFAULT_CATEGORY_ID);
            ShippingPolicyId shippingPolicyId =
                    ShippingPolicyId.of(ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID);
            RefundPolicyId refundPolicyId =
                    RefundPolicyId.of(ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID);

            willThrow(new RuntimeException("셀러를 찾을 수 없습니다"))
                    .given(sellerReadManager)
                    .getById(sellerId);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateExternalReferences(
                                            sellerId,
                                            brandId,
                                            categoryId,
                                            shippingPolicyId,
                                            refundPolicyId))
                    .isInstanceOf(RuntimeException.class);

            then(brandReadManager).shouldHaveNoInteractions();
            then(categoryReadManager).shouldHaveNoInteractions();
            then(shippingPolicyReadManager).shouldHaveNoInteractions();
            then(refundPolicyReadManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Brand 검증 실패 시 예외가 발생한다")
        void validateExternalReferences_BrandNotFound_ThrowsException() {
            // given
            SellerId sellerId = SellerId.of(ProductGroupFixtures.DEFAULT_SELLER_ID);
            BrandId brandId = BrandId.of(999L);
            CategoryId categoryId = CategoryId.of(ProductGroupFixtures.DEFAULT_CATEGORY_ID);
            ShippingPolicyId shippingPolicyId =
                    ShippingPolicyId.of(ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID);
            RefundPolicyId refundPolicyId =
                    RefundPolicyId.of(ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID);

            willThrow(new RuntimeException("브랜드를 찾을 수 없습니다"))
                    .given(brandReadManager)
                    .getById(brandId);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateExternalReferences(
                                            sellerId,
                                            brandId,
                                            categoryId,
                                            shippingPolicyId,
                                            refundPolicyId))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("ShippingPolicy 검증 실패 시 예외가 발생한다")
        void validateExternalReferences_ShippingPolicyNotOwned_ThrowsException() {
            // given
            SellerId sellerId = SellerId.of(ProductGroupFixtures.DEFAULT_SELLER_ID);
            BrandId brandId = BrandId.of(ProductGroupFixtures.DEFAULT_BRAND_ID);
            CategoryId categoryId = CategoryId.of(ProductGroupFixtures.DEFAULT_CATEGORY_ID);
            ShippingPolicyId shippingPolicyId = ShippingPolicyId.of(999L);
            RefundPolicyId refundPolicyId =
                    RefundPolicyId.of(ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID);

            willThrow(new RuntimeException("배송 정책 소유권이 없습니다"))
                    .given(shippingPolicyReadManager)
                    .getBySellerIdAndId(sellerId, shippingPolicyId);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateExternalReferences(
                                            sellerId,
                                            brandId,
                                            categoryId,
                                            shippingPolicyId,
                                            refundPolicyId))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("validateExternalReferencesForUpdate() - 수정 시 외부 FK 검증")
    class ValidateExternalReferencesForUpdateTest {

        @Test
        @DisplayName("수정 시 Brand, Category, ShippingPolicy, RefundPolicy 검증을 각 ReadManager에 위임한다")
        void validateExternalReferencesForUpdate_ValidIds_DelegatesToAllReadManagers() {
            // given
            BrandId brandId = BrandId.of(ProductGroupFixtures.DEFAULT_BRAND_ID);
            CategoryId categoryId = CategoryId.of(ProductGroupFixtures.DEFAULT_CATEGORY_ID);
            ShippingPolicyId shippingPolicyId =
                    ShippingPolicyId.of(ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID);
            RefundPolicyId refundPolicyId =
                    RefundPolicyId.of(ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID);

            // when
            sut.validateExternalReferencesForUpdate(
                    brandId, categoryId, shippingPolicyId, refundPolicyId);

            // then
            then(brandReadManager).should().getById(brandId);
            then(categoryReadManager).should().getById(categoryId);
            then(shippingPolicyReadManager).should().getById(shippingPolicyId);
            then(refundPolicyReadManager).should().getById(refundPolicyId);
        }

        @Test
        @DisplayName("수정 시 Seller 검증을 수행하지 않는다")
        void validateExternalReferencesForUpdate_DoesNotValidateSeller() {
            // given
            BrandId brandId = BrandId.of(ProductGroupFixtures.DEFAULT_BRAND_ID);
            CategoryId categoryId = CategoryId.of(ProductGroupFixtures.DEFAULT_CATEGORY_ID);
            ShippingPolicyId shippingPolicyId =
                    ShippingPolicyId.of(ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID);
            RefundPolicyId refundPolicyId =
                    RefundPolicyId.of(ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID);

            // when
            sut.validateExternalReferencesForUpdate(
                    brandId, categoryId, shippingPolicyId, refundPolicyId);

            // then
            then(sellerReadManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Category 검증 실패 시 예외가 발생한다")
        void validateExternalReferencesForUpdate_CategoryNotFound_ThrowsException() {
            // given
            BrandId brandId = BrandId.of(ProductGroupFixtures.DEFAULT_BRAND_ID);
            CategoryId categoryId = CategoryId.of(999L);
            ShippingPolicyId shippingPolicyId =
                    ShippingPolicyId.of(ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID);
            RefundPolicyId refundPolicyId =
                    RefundPolicyId.of(ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID);

            willThrow(new RuntimeException("카테고리를 찾을 수 없습니다"))
                    .given(categoryReadManager)
                    .getById(categoryId);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateExternalReferencesForUpdate(
                                            brandId, categoryId, shippingPolicyId, refundPolicyId))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("수정 시 ShippingPolicy 조회는 getById(셀러 소유 검증 없음)를 사용한다")
        void validateExternalReferencesForUpdate_UsesGetByIdForShippingPolicy() {
            // given
            BrandId brandId = BrandId.of(ProductGroupFixtures.DEFAULT_BRAND_ID);
            CategoryId categoryId = CategoryId.of(ProductGroupFixtures.DEFAULT_CATEGORY_ID);
            ShippingPolicyId shippingPolicyId =
                    ShippingPolicyId.of(ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID);
            RefundPolicyId refundPolicyId =
                    RefundPolicyId.of(ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID);

            // when
            sut.validateExternalReferencesForUpdate(
                    brandId, categoryId, shippingPolicyId, refundPolicyId);

            // then
            then(shippingPolicyReadManager).should().getById(shippingPolicyId);
            then(shippingPolicyReadManager).shouldHaveNoMoreInteractions();
        }
    }
}
