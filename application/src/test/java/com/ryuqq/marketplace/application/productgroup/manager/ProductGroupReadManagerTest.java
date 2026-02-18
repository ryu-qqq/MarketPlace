package com.ryuqq.marketplace.application.productgroup.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupQueryPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupOwnershipViolationException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
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
@DisplayName("ProductGroupReadManager 단위 테스트")
class ProductGroupReadManagerTest {

    @InjectMocks private ProductGroupReadManager sut;

    @Mock private ProductGroupQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 단건 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 ProductGroup을 조회한다")
        void getById_Exists_ReturnsProductGroup() {
            // given
            ProductGroupId id = ProductGroupFixtures.defaultProductGroupId();
            ProductGroup expected = ProductGroupFixtures.activeProductGroup();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            ProductGroup result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            ProductGroupId id = ProductGroupId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(ProductGroupNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByIdsAndSellerId() - 셀러 소유 배치 조회")
    class GetByIdsAndSellerIdTest {

        @Test
        @DisplayName("셀러 소유의 상품 그룹 목록을 배치 조회한다")
        void getByIdsAndSellerId_AllOwned_ReturnsProductGroups() {
            // given
            long sellerId = ProductGroupFixtures.DEFAULT_SELLER_ID;
            List<ProductGroupId> ids = List.of(ProductGroupId.of(1L), ProductGroupId.of(2L));
            List<ProductGroup> expected =
                    List.of(
                            ProductGroupFixtures.activeProductGroup(),
                            ProductGroupFixtures.activeProductGroup());

            given(queryPort.findByIdsAndSellerId(ids, sellerId)).willReturn(expected);

            // when
            List<ProductGroup> result = sut.getByIdsAndSellerId(ids, sellerId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByIdsAndSellerId(ids, sellerId);
        }

        @Test
        @DisplayName("요청 수와 조회 수가 다를 때 소유권 위반 예외가 발생한다")
        void getByIdsAndSellerId_CountMismatch_ThrowsOwnershipViolationException() {
            // given
            long sellerId = ProductGroupFixtures.DEFAULT_SELLER_ID;
            List<ProductGroupId> ids =
                    List.of(ProductGroupId.of(1L), ProductGroupId.of(2L), ProductGroupId.of(3L));
            List<ProductGroup> foundGroups = List.of(ProductGroupFixtures.activeProductGroup());

            given(queryPort.findByIdsAndSellerId(ids, sellerId)).willReturn(foundGroups);

            // when & then
            assertThatThrownBy(() -> sut.getByIdsAndSellerId(ids, sellerId))
                    .isInstanceOf(ProductGroupOwnershipViolationException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 ProductGroup 목록을 조회한다")
        void findByCriteria_ValidCriteria_ReturnsProductGroups() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            List<ProductGroup> expected = List.of(ProductGroupFixtures.activeProductGroup());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<ProductGroup> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void findByCriteria_NoResults_ReturnsEmptyList() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();

            given(queryPort.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<ProductGroup> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 ProductGroup 수를 반환한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            long expected = 42L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }
    }
}
