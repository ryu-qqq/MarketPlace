package com.ryuqq.marketplace.domain.product.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.exception.ProductInvalidPriceException;
import com.ryuqq.marketplace.domain.product.exception.ProductInvalidStatusTransitionException;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Product Aggregate 테스트")
class ProductTest {

    @Nested
    @DisplayName("forNew() - 신규 상품 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 상품을 생성한다")
        void createNewProductWithRequiredFields() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            SkuCode skuCode = SkuCode.of("SKU-001");
            Money regularPrice = CommonVoFixtures.money(100000);
            Money currentPrice = CommonVoFixtures.money(80000);
            int stockQuantity = 100;
            Instant now = CommonVoFixtures.now();

            // when
            Product product =
                    Product.forNew(
                            productGroupId,
                            skuCode,
                            regularPrice,
                            currentPrice,
                            stockQuantity,
                            1,
                            Collections.emptyList(),
                            now);

            // then
            assertThat(product.id().isNew()).isTrue();
            assertThat(product.productGroupId()).isEqualTo(productGroupId);
            assertThat(product.skuCode()).isEqualTo(skuCode);
            assertThat(product.status()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(product.regularPrice()).isEqualTo(regularPrice);
            assertThat(product.currentPrice()).isEqualTo(currentPrice);
            assertThat(product.salePrice()).isEqualTo(currentPrice);
            assertThat(product.discountRate())
                    .isEqualTo(Money.discountRate(regularPrice, currentPrice));
            assertThat(product.stockQuantity()).isEqualTo(stockQuantity);
            assertThat(product.createdAt()).isEqualTo(now);
            assertThat(product.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("currentPrice가 regularPrice보다 크면 예외가 발생한다")
        void createProduct_WhenCurrentPriceGreaterThanRegularPrice_ThrowsException() {
            // given
            Money regularPrice = CommonVoFixtures.money(80000);
            Money currentPrice = CommonVoFixtures.money(100000); // regularPrice보다 큼

            // when & then
            assertThatThrownBy(
                            () ->
                                    Product.forNew(
                                            ProductGroupId.of(1L),
                                            SkuCode.of("SKU-001"),
                                            regularPrice,
                                            currentPrice,
                                            100,
                                            1,
                                            Collections.emptyList(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ProductInvalidPriceException.class);
        }

        @Test
        @DisplayName("stockQuantity가 음수면 예외가 발생한다")
        void createProduct_WhenStockQuantityNegative_ThrowsException() {
            // given
            int invalidStockQuantity = -10;

            // when & then
            assertThatThrownBy(
                            () ->
                                    Product.forNew(
                                            ProductGroupId.of(1L),
                                            SkuCode.of("SKU-001"),
                                            CommonVoFixtures.money(100000),
                                            CommonVoFixtures.money(80000),
                                            invalidStockQuantity,
                                            1,
                                            Collections.emptyList(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고 수량은 0 이상");
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 상품을 복원한다")
        void reconstituteActiveProduct() {
            // when
            Product product = ProductFixtures.activeProduct();

            // then
            assertThat(product.idValue()).isEqualTo(1L);
            assertThat(product.status()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(product.stockQuantity()).isEqualTo(ProductFixtures.DEFAULT_STOCK_QUANTITY);
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 상품을 복원한다")
        void reconstituteInactiveProduct() {
            // when
            Product product = ProductFixtures.inactiveProduct();

            // then
            assertThat(product.status()).isEqualTo(ProductStatus.INACTIVE);
        }

        @Test
        @DisplayName("영속성에서 품절 상태의 상품을 복원한다")
        void reconstituteSoldOutProduct() {
            // when
            Product product = ProductFixtures.soldOutProduct();

            // then
            assertThat(product.status()).isEqualTo(ProductStatus.SOLD_OUT);
            assertThat(product.stockQuantity()).isZero();
        }
    }

    @Nested
    @DisplayName("activate() - 판매 재개")
    class ActivateTest {

        @Test
        @DisplayName("INACTIVE 상태에서 ACTIVE로 전환한다")
        void activateFromInactive() {
            // given
            Product product = ProductFixtures.inactiveProduct();
            Instant now = CommonVoFixtures.now();

            // when
            product.activate(now);

            // then
            assertThat(product.status()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(product.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("SOLD_OUT 상태에서 ACTIVE로 전환한다")
        void activateFromSoldOut() {
            // given
            Product product = ProductFixtures.soldOutProduct();
            Instant now = CommonVoFixtures.now();

            // when
            product.activate(now);

            // then
            assertThat(product.status()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(product.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("ACTIVE 상태에서 activate하면 예외가 발생한다")
        void activateFromActive_ThrowsException() {
            // given
            Product product = ProductFixtures.activeProduct();

            // when & then
            assertThatThrownBy(() -> product.activate(CommonVoFixtures.now()))
                    .isInstanceOf(ProductInvalidStatusTransitionException.class);
        }

        @Test
        @DisplayName("DELETED 상태에서 activate하면 예외가 발생한다")
        void activateFromDeleted_ThrowsException() {
            // given
            Product product = ProductFixtures.deletedProduct();

            // when & then
            assertThatThrownBy(() -> product.activate(CommonVoFixtures.now()))
                    .isInstanceOf(ProductInvalidStatusTransitionException.class);
        }
    }

    @Nested
    @DisplayName("deactivate() - 판매 중지")
    class DeactivateTest {

        @Test
        @DisplayName("ACTIVE 상태에서 INACTIVE로 전환한다")
        void deactivateFromActive() {
            // given
            Product product = ProductFixtures.activeProduct();
            Instant now = CommonVoFixtures.now();

            // when
            product.deactivate(now);

            // then
            assertThat(product.status()).isEqualTo(ProductStatus.INACTIVE);
            assertThat(product.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("INACTIVE 상태에서 deactivate하면 예외가 발생한다")
        void deactivateFromInactive_ThrowsException() {
            // given
            Product product = ProductFixtures.inactiveProduct();

            // when & then
            assertThatThrownBy(() -> product.deactivate(CommonVoFixtures.now()))
                    .isInstanceOf(ProductInvalidStatusTransitionException.class);
        }
    }

    @Nested
    @DisplayName("markSoldOut() - 품절 처리")
    class MarkSoldOutTest {

        @Test
        @DisplayName("ACTIVE 상태에서 SOLD_OUT로 전환한다")
        void markSoldOutFromActive() {
            // given
            Product product = ProductFixtures.activeProduct();
            Instant now = CommonVoFixtures.now();

            // when
            product.markSoldOut(now);

            // then
            assertThat(product.status()).isEqualTo(ProductStatus.SOLD_OUT);
            assertThat(product.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("INACTIVE 상태에서 markSoldOut하면 예외가 발생한다")
        void markSoldOutFromInactive_ThrowsException() {
            // given
            Product product = ProductFixtures.inactiveProduct();

            // when & then
            assertThatThrownBy(() -> product.markSoldOut(CommonVoFixtures.now()))
                    .isInstanceOf(ProductInvalidStatusTransitionException.class);
        }
    }

    @Nested
    @DisplayName("delete() - 소프트 삭제")
    class DeleteTest {

        @Test
        @DisplayName("ACTIVE 상태에서 DELETED로 전환한다")
        void deleteFromActive() {
            // given
            Product product = ProductFixtures.activeProduct();
            Instant now = CommonVoFixtures.now();

            // when
            product.delete(now);

            // then
            assertThat(product.status()).isEqualTo(ProductStatus.DELETED);
            assertThat(product.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("INACTIVE 상태에서 DELETED로 전환한다")
        void deleteFromInactive() {
            // given
            Product product = ProductFixtures.inactiveProduct();
            Instant now = CommonVoFixtures.now();

            // when
            product.delete(now);

            // then
            assertThat(product.status()).isEqualTo(ProductStatus.DELETED);
        }

        @Test
        @DisplayName("DELETED 상태에서 delete하면 예외가 발생한다")
        void deleteFromDeleted_ThrowsException() {
            // given
            Product product = ProductFixtures.deletedProduct();

            // when & then
            assertThatThrownBy(() -> product.delete(CommonVoFixtures.now()))
                    .isInstanceOf(ProductInvalidStatusTransitionException.class);
        }
    }

    @Nested
    @DisplayName("updatePrice() - 가격 수정")
    class UpdatePriceTest {

        @Test
        @DisplayName("정상적인 가격 체계로 수정한다")
        void updatePriceWithValidPrices() {
            // given
            Product product = ProductFixtures.activeProduct();
            Money newRegularPrice = CommonVoFixtures.money(120000);
            Money newCurrentPrice = CommonVoFixtures.money(100000);
            Instant now = CommonVoFixtures.now();

            // when
            product.updatePrice(newRegularPrice, newCurrentPrice, now);

            // then
            assertThat(product.regularPrice()).isEqualTo(newRegularPrice);
            assertThat(product.currentPrice()).isEqualTo(newCurrentPrice);
            assertThat(product.salePrice()).isEqualTo(newCurrentPrice);
            assertThat(product.discountRate())
                    .isEqualTo(Money.discountRate(newRegularPrice, newCurrentPrice));
            assertThat(product.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("currentPrice > regularPrice이면 예외가 발생한다")
        void updatePrice_WhenCurrentPriceGreaterThanRegularPrice_ThrowsException() {
            // given
            Product product = ProductFixtures.activeProduct();
            Money regularPrice = CommonVoFixtures.money(80000);
            Money currentPrice = CommonVoFixtures.money(100000);

            // when & then
            assertThatThrownBy(
                            () ->
                                    product.updatePrice(
                                            regularPrice, currentPrice, CommonVoFixtures.now()))
                    .isInstanceOf(ProductInvalidPriceException.class);
        }
    }

    @Nested
    @DisplayName("updateStock() - 재고 수정")
    class UpdateStockTest {

        @Test
        @DisplayName("정상적인 재고 수량으로 수정한다")
        void updateStockWithValidQuantity() {
            // given
            Product product = ProductFixtures.activeProduct();
            int newStockQuantity = 200;
            Instant now = CommonVoFixtures.now();

            // when
            product.updateStock(newStockQuantity, now);

            // then
            assertThat(product.stockQuantity()).isEqualTo(newStockQuantity);
            assertThat(product.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("재고 수량이 음수면 예외가 발생한다")
        void updateStock_WhenQuantityNegative_ThrowsException() {
            // given
            Product product = ProductFixtures.activeProduct();

            // when & then
            assertThatThrownBy(() -> product.updateStock(-10, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고 수량은 0 이상");
        }
    }

    @Nested
    @DisplayName("쿼리 메서드 테스트")
    class QueryMethodTest {

        @Test
        @DisplayName("hasStock()은 재고가 있으면 true를 반환한다")
        void hasStockReturnsTrueWhenStockExists() {
            // given
            Product product = ProductFixtures.activeProduct();

            // when & then
            assertThat(product.hasStock()).isTrue();
        }

        @Test
        @DisplayName("hasStock()은 재고가 0이면 false를 반환한다")
        void hasStockReturnsFalseWhenStockZero() {
            // given
            Product product = ProductFixtures.soldOutProduct();

            // when & then
            assertThat(product.hasStock()).isFalse();
        }

        @Test
        @DisplayName("isOnSale()은 세일 중이면 true를 반환한다")
        void isOnSaleReturnsTrueWhenOnSale() {
            // given
            Product product = ProductFixtures.activeProduct();

            // when & then
            assertThat(product.isOnSale()).isTrue();
        }

        @Test
        @DisplayName("isOnSale()은 세일 중이 아니면 false를 반환한다")
        void isOnSaleReturnsFalseWhenNotOnSale() {
            // given
            Product product = ProductFixtures.productWithoutSale();

            // when & then
            assertThat(product.isOnSale()).isFalse();
        }

        @Test
        @DisplayName("effectivePrice()는 세일 중이면 salePrice를 반환한다")
        void effectivePriceReturnsSalePriceWhenOnSale() {
            // given
            Product product = ProductFixtures.activeProduct();

            // when
            Money effectivePrice = product.effectivePrice();

            // then
            assertThat(effectivePrice).isEqualTo(product.salePrice());
        }

        @Test
        @DisplayName("effectivePrice()는 세일 중이 아니면 currentPrice를 반환한다")
        void effectivePriceReturnsCurrentPriceWhenNotOnSale() {
            // given
            Product product = ProductFixtures.productWithoutSale();

            // when
            Money effectivePrice = product.effectivePrice();

            // then
            assertThat(effectivePrice).isEqualTo(product.currentPrice());
        }
    }

    @Nested
    @DisplayName("Accessor 메서드 테스트")
    class AccessorTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            Product product = ProductFixtures.activeProduct(100L);

            // when & then
            assertThat(product.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("productGroupIdValue()는 ProductGroupId의 값을 반환한다")
        void productGroupIdValueReturnsValue() {
            // given
            Product product = ProductFixtures.activeProduct();

            // when & then
            assertThat(product.productGroupIdValue())
                    .isEqualTo(ProductFixtures.DEFAULT_PRODUCT_GROUP_ID);
        }

        @Test
        @DisplayName("skuCodeValue()는 SkuCode의 값을 반환한다")
        void skuCodeValueReturnsValue() {
            // given
            Product product = ProductFixtures.activeProduct();

            // when & then
            assertThat(product.skuCodeValue()).isEqualTo(ProductFixtures.DEFAULT_SKU_CODE);
        }
    }
}
