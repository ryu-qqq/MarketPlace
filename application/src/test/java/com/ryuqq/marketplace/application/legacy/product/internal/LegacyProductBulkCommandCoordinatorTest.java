package com.ryuqq.marketplace.application.legacy.product.internal;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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
@DisplayName("LegacyProductBulkCommandCoordinator 단위 테스트")
class LegacyProductBulkCommandCoordinatorTest {

    @InjectMocks private LegacyProductBulkCommandCoordinator sut;

    @Mock private ProductReadManager productReadManager;
    @Mock private ProductCommandManager productCommandManager;

    @Nested
    @DisplayName("updatePriceAll() - 상품그룹 내 모든 Product 가격 일괄 변경")
    class UpdatePriceAllTest {

        @Test
        @DisplayName("상품그룹 내 모든 Product에 새 가격을 적용하고 저장한다")
        void updatePriceAll_ValidParams_UpdatesAndPersistsAll() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Money regularPrice = Money.of(10000);
            Money currentPrice = Money.of(9000);
            Instant now = Instant.now();

            List<Product> products =
                    List.of(ProductFixtures.activeProduct(1L), ProductFixtures.activeProduct(2L));
            given(productReadManager.findByProductGroupId(productGroupId)).willReturn(products);

            // when
            sut.updatePriceAll(productGroupId, regularPrice, currentPrice, now);

            // then
            then(productReadManager).should().findByProductGroupId(productGroupId);
            then(productCommandManager).should().persistAll(products);
        }

        @Test
        @DisplayName("상품그룹 내 Product가 없어도 빈 리스트로 저장을 호출한다")
        void updatePriceAll_NoProducts_CallsPersistAllWithEmptyList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(999L);
            Money regularPrice = Money.of(10000);
            Money currentPrice = Money.of(9000);
            Instant now = Instant.now();

            given(productReadManager.findByProductGroupId(productGroupId))
                    .willReturn(List.of());

            // when
            sut.updatePriceAll(productGroupId, regularPrice, currentPrice, now);

            // then
            then(productCommandManager).should().persistAll(List.of());
        }
    }

    @Nested
    @DisplayName("changeStatusAll() - 상품그룹 내 모든 Product 상태 일괄 변경")
    class ChangeStatusAllTest {

        @Test
        @DisplayName("모든 Product에 ACTIVE 상태를 적용하고 저장한다")
        void changeStatusAll_ToActive_UpdatesAndPersistsAll() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            ProductStatus targetStatus = ProductStatus.ACTIVE;
            Instant now = Instant.now();

            // INACTIVE → ACTIVE, SOLD_OUT → ACTIVE 전환은 도메인에서 허용됨
            List<Product> products =
                    List.of(
                            ProductFixtures.inactiveProduct(),
                            ProductFixtures.soldOutProduct());
            given(productReadManager.findByProductGroupId(productGroupId)).willReturn(products);

            // when
            sut.changeStatusAll(productGroupId, targetStatus, now);

            // then
            then(productReadManager).should().findByProductGroupId(productGroupId);
            then(productCommandManager).should().persistAll(products);
        }

        @Test
        @DisplayName("모든 Product에 INACTIVE 상태를 적용하고 저장한다")
        void changeStatusAll_ToInactive_UpdatesAndPersistsAll() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            ProductStatus targetStatus = ProductStatus.INACTIVE;
            Instant now = Instant.now();

            List<Product> products = List.of(ProductFixtures.activeProduct(1L));
            given(productReadManager.findByProductGroupId(productGroupId)).willReturn(products);

            // when
            sut.changeStatusAll(productGroupId, targetStatus, now);

            // then
            then(productCommandManager).should().persistAll(products);
        }
    }

    @Nested
    @DisplayName("markSoldOutAll() - 상품그룹 내 모든 Product 품절 처리")
    class MarkSoldOutAllTest {

        @Test
        @DisplayName("모든 Product를 SOLD_OUT 상태와 재고 0으로 변경하고 저장한다")
        void markSoldOutAll_ValidProductGroup_MarksAllAsSoldOut() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Instant now = Instant.now();

            List<Product> products =
                    List.of(ProductFixtures.activeProduct(1L), ProductFixtures.activeProduct(2L));
            given(productReadManager.findByProductGroupId(productGroupId)).willReturn(products);

            // when
            sut.markSoldOutAll(productGroupId, now);

            // then
            then(productReadManager).should().findByProductGroupId(productGroupId);
            then(productCommandManager).should().persistAll(products);
        }
    }

    @Nested
    @DisplayName("updateStockByProductIds() - 개별 Product 재고 수정")
    class UpdateStockByProductIdsTest {

        @Test
        @DisplayName("재고 Map에 해당하는 Product만 재고를 수정하고 저장한다")
        void updateStockByProductIds_ValidMap_UpdatesMatchingProducts() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Instant now = Instant.now();

            Product product1 = ProductFixtures.activeProduct(1L);
            Product product2 = ProductFixtures.activeProduct(2L);
            List<Product> products = List.of(product1, product2);

            Map<ProductId, Integer> stockByProductId =
                    Map.of(ProductId.of(1L), 50, ProductId.of(2L), 30);

            given(productReadManager.findByProductGroupId(productGroupId)).willReturn(products);

            // when
            sut.updateStockByProductIds(productGroupId, stockByProductId, now);

            // then
            then(productReadManager).should().findByProductGroupId(productGroupId);
            then(productCommandManager).should().persistAll(products);
        }

        @Test
        @DisplayName("stockByProductId에 없는 Product는 재고를 수정하지 않는다")
        void updateStockByProductIds_UnmatchedProduct_SkipsUpdate() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Instant now = Instant.now();

            List<Product> products = List.of(ProductFixtures.activeProduct(1L));
            Map<ProductId, Integer> stockByProductId = Map.of(ProductId.of(999L), 50);

            given(productReadManager.findByProductGroupId(productGroupId)).willReturn(products);

            // when
            sut.updateStockByProductIds(productGroupId, stockByProductId, now);

            // then
            then(productCommandManager).should().persistAll(products);
        }
    }
}
