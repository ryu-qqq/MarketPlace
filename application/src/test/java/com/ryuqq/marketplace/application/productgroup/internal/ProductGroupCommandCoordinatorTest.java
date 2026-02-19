package com.ryuqq.marketplace.application.productgroup.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroup.validator.ProductGroupValidator;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
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
@DisplayName("ProductGroupCommandCoordinator 단위 테스트")
class ProductGroupCommandCoordinatorTest {

    @InjectMocks private ProductGroupCommandCoordinator sut;

    @Mock private ProductGroupValidator productGroupValidator;
    @Mock private ProductGroupReadManager productGroupReadManager;
    @Mock private ProductGroupCommandManager productGroupCommandManager;

    @Nested
    @DisplayName("register() - 상품 그룹 등록 조율")
    class RegisterTest {

        @Test
        @DisplayName("검증 통과 시 상품 그룹을 저장하고 ID를 반환한다")
        void register_ValidationPassed_ReturnsSavedId() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroup();
            Long expectedId = 1L;

            given(productGroupCommandManager.persist(productGroup)).willReturn(expectedId);

            // when
            Long result = sut.register(productGroup);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(productGroupValidator).should().validateForRegistration(productGroup);
            then(productGroupCommandManager).should().persist(productGroup);
        }

        @Test
        @DisplayName("검증 실패 시 예외가 발생하고 저장이 수행되지 않는다")
        void register_ValidationFailed_ThrowsExceptionAndNoPersist() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroup();

            willThrow(new RuntimeException("검증 실패"))
                    .given(productGroupValidator)
                    .validateForRegistration(productGroup);

            // when & then
            assertThatThrownBy(() -> sut.register(productGroup))
                    .isInstanceOf(RuntimeException.class);

            then(productGroupCommandManager).should(never()).persist(productGroup);
        }
    }

    @Nested
    @DisplayName("update() - 상품 그룹 기본 정보 수정 조율")
    class UpdateTest {

        @Test
        @DisplayName("검증 통과 시 조회 + update + 저장을 수행한다")
        void update_ValidationPassed_UpdatesAndPersists() {
            // given
            long productGroupId = 1L;
            ProductGroupUpdateData updateData = createUpdateData(productGroupId);
            ProductGroup existingGroup = ProductGroupFixtures.activeProductGroup();

            given(productGroupReadManager.getById(updateData.productGroupId()))
                    .willReturn(existingGroup);

            // when
            sut.update(updateData);

            // then
            then(productGroupValidator).should().validateForUpdate(updateData);
            then(productGroupReadManager).should().getById(updateData.productGroupId());
            then(productGroupCommandManager).should().persist(existingGroup);
        }

        @Test
        @DisplayName("검증 실패 시 조회와 저장이 수행되지 않는다")
        void update_ValidationFailed_ThrowsExceptionNoReadOrPersist() {
            // given
            long productGroupId = 1L;
            ProductGroupUpdateData updateData = createUpdateData(productGroupId);

            willThrow(new RuntimeException("검증 실패"))
                    .given(productGroupValidator)
                    .validateForUpdate(updateData);

            // when & then
            assertThatThrownBy(() -> sut.update(updateData)).isInstanceOf(RuntimeException.class);

            then(productGroupReadManager).should(never()).getById(updateData.productGroupId());
            then(productGroupCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("상품 그룹을 찾지 못하면 예외가 발생한다")
        void update_ProductGroupNotFound_ThrowsNotFoundException() {
            // given
            long productGroupId = 999L;
            ProductGroupUpdateData updateData = createUpdateData(productGroupId);

            given(productGroupReadManager.getById(updateData.productGroupId()))
                    .willThrow(new ProductGroupNotFoundException(productGroupId));

            // when & then
            assertThatThrownBy(() -> sut.update(updateData))
                    .isInstanceOf(ProductGroupNotFoundException.class);

            then(productGroupCommandManager).shouldHaveNoInteractions();
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
