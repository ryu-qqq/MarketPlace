package com.ryuqq.marketplace.application.product.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.product.port.out.query.ProductQueryPort;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.exception.ProductNotFoundException;
import com.ryuqq.marketplace.domain.product.exception.ProductOwnershipViolationException;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import java.util.Optional;
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
@DisplayName("ProductReadManager 단위 테스트")
class ProductReadManagerTest {

    @InjectMocks private ProductReadManager sut;

    @Mock private ProductQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 상품 단건 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 상품 ID로 조회하면 상품을 반환한다")
        void getById_ExistingId_ReturnsProduct() {
            // given
            ProductId productId = ProductId.of(1L);
            Product expected = ProductFixtures.activeProduct();

            given(queryPort.findById(productId)).willReturn(Optional.of(expected));

            // when
            Product result = sut.getById(productId);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(productId);
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID로 조회하면 예외를 던진다")
        void getById_NonExistingId_ThrowsProductNotFoundException() {
            // given
            ProductId productId = ProductId.of(999L);
            given(queryPort.findById(productId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(productId))
                    .isInstanceOf(ProductNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByProductGroupId() - ProductGroupId로 상품 목록 조회")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("ProductGroupId로 상품 목록을 반환한다")
        void findByProductGroupId_ValidId_ReturnsProducts() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            List<Product> expected = List.of(ProductFixtures.activeProduct());

            given(queryPort.findByProductGroupId(productGroupId)).willReturn(expected);

            // when
            List<Product> result = sut.findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByProductGroupId(productGroupId);
        }

        @Test
        @DisplayName("상품이 없으면 빈 목록을 반환한다")
        void findByProductGroupId_NoProducts_ReturnsEmptyList() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();

            given(queryPort.findByProductGroupId(productGroupId)).willReturn(List.of());

            // when
            List<Product> result = sut.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByProductGroupIds() - 여러 ProductGroupId로 상품 배치 조회")
    class FindByProductGroupIdsTest {

        @Test
        @DisplayName("여러 ProductGroupId로 상품 목록을 배치 조회한다")
        void findByProductGroupIds_ValidIds_ReturnsProducts() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupId.of(1L), ProductGroupId.of(2L));
            List<Product> expected =
                    List.of(ProductFixtures.activeProduct(), ProductFixtures.activeProduct(2L));

            given(queryPort.findByProductGroupIdIn(productGroupIds)).willReturn(expected);

            // when
            List<Product> result = sut.findByProductGroupIds(productGroupIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByProductGroupIdIn(productGroupIds);
        }

        @Test
        @DisplayName("빈 productIds 목록이면 쿼리를 실행하지 않고 빈 목록을 반환한다")
        void findByProductGroupIds_EmptyIds_ReturnsEmptyWithoutQuery() {
            // when
            List<Product> result = sut.findByProductGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
            then(queryPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void findByProductGroupIds_NoResults_ReturnsEmptyList() {
            // given
            List<ProductGroupId> productGroupIds = List.of(ProductGroupId.of(999L));

            given(queryPort.findByProductGroupIdIn(productGroupIds)).willReturn(List.of());

            // when
            List<Product> result = sut.findByProductGroupIds(productGroupIds);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getByProductGroupIdAndIds() - 상품 그룹 내 특정 상품 배치 조회")
    class GetByProductGroupIdAndIdsTest {

        @Test
        @DisplayName("요청 수와 조회 수가 일치하면 상품 목록을 반환한다")
        void getByProductGroupIdAndIds_CountMatch_ReturnsProducts() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            List<ProductId> ids = List.of(ProductId.of(1L), ProductId.of(2L));
            List<Product> expected =
                    List.of(ProductFixtures.activeProduct(), ProductFixtures.activeProduct(2L));

            given(queryPort.findByProductGroupIdAndIdIn(productGroupId, ids)).willReturn(expected);

            // when
            List<Product> result = sut.getByProductGroupIdAndIds(productGroupId, ids);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("요청 수와 조회 수가 다르면 소유권 위반 예외를 던진다")
        void getByProductGroupIdAndIds_CountMismatch_ThrowsOwnershipViolationException() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            List<ProductId> ids = List.of(ProductId.of(1L), ProductId.of(2L), ProductId.of(3L));
            List<Product> foundProducts = List.of(ProductFixtures.activeProduct());

            given(queryPort.findByProductGroupIdAndIdIn(productGroupId, ids))
                    .willReturn(foundProducts);

            // when & then
            assertThatThrownBy(() -> sut.getByProductGroupIdAndIds(productGroupId, ids))
                    .isInstanceOf(ProductOwnershipViolationException.class);
        }
    }
}
