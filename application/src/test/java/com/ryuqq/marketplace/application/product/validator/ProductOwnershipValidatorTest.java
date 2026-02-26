package com.ryuqq.marketplace.application.product.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.exception.ProductNotFoundException;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupOwnershipViolationException;
import java.util.List;
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
@DisplayName("ProductOwnershipValidator 단위 테스트")
class ProductOwnershipValidatorTest {

    @InjectMocks private ProductOwnershipValidator sut;

    @Mock private ProductReadManager productReadManager;
    @Mock private ProductGroupReadManager productGroupReadManager;

    @Nested
    @DisplayName("validateAndGet() - 소유권 검증 및 상품 조회")
    class ValidateAndGetTest {

        @Test
        @DisplayName("소유권이 유효하면 조회된 상품 목록을 반환한다")
        void validateAndGet_ValidOwnership_ReturnsProducts() {
            // given
            long sellerId = 100L;
            List<ProductId> productIds = List.of(ProductId.of(1L), ProductId.of(2L));
            List<Product> products =
                    List.of(ProductFixtures.activeProduct(1L), ProductFixtures.activeProduct(2L));

            given(productReadManager.getByIds(productIds)).willReturn(products);

            // when
            List<Product> result = sut.validateAndGet(productIds, sellerId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(products);
            then(productReadManager).should().getByIds(productIds);
            then(productGroupReadManager).should().getByIdsAndSellerId(anyList(), anyLong());
        }

        @Test
        @DisplayName("단일 상품의 소유권 검증도 정상 처리된다")
        void validateAndGet_SingleProduct_ReturnsProduct() {
            // given
            long sellerId = 100L;
            List<ProductId> productIds = List.of(ProductId.of(1L));
            List<Product> products = List.of(ProductFixtures.activeProduct(1L));

            given(productReadManager.getByIds(productIds)).willReturn(products);

            // when
            List<Product> result = sut.validateAndGet(productIds, sellerId);

            // then
            assertThat(result).hasSize(1);
            then(productGroupReadManager).should().getByIdsAndSellerId(anyList(), anyLong());
        }

        @Test
        @DisplayName("상품을 찾을 수 없으면 ProductNotFoundException을 던진다")
        void validateAndGet_ProductNotFound_ThrowsException() {
            // given
            long sellerId = 100L;
            List<ProductId> productIds = List.of(ProductId.of(999L));

            given(productReadManager.getByIds(productIds))
                    .willThrow(new ProductNotFoundException(999L));

            // when & then
            assertThatThrownBy(() -> sut.validateAndGet(productIds, sellerId))
                    .isInstanceOf(ProductNotFoundException.class);
        }

        @Test
        @DisplayName("소유권 검증 실패 시 ProductGroupOwnershipViolationException을 던진다")
        void validateAndGet_OwnershipViolation_ThrowsException() {
            // given
            long sellerId = 999L;
            List<ProductId> productIds = List.of(ProductId.of(1L));
            List<Product> products = List.of(ProductFixtures.activeProduct(1L));

            given(productReadManager.getByIds(productIds)).willReturn(products);
            given(productGroupReadManager.getByIdsAndSellerId(anyList(), anyLong()))
                    .willThrow(new ProductGroupOwnershipViolationException(sellerId, 1, 0));

            // when & then
            assertThatThrownBy(() -> sut.validateAndGet(productIds, sellerId))
                    .isInstanceOf(ProductGroupOwnershipViolationException.class);
        }
    }
}
