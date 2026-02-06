package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerQueryDslRepository;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
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

/**
 * SellerQueryAdapterTest - 셀러 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerQueryAdapter 단위 테스트")
class SellerQueryAdapterTest {

    @Mock private SellerQueryDslRepository queryDslRepository;

    @Mock private SellerJpaEntityMapper mapper;

    @Mock private SellerSearchCriteria criteria;

    @InjectMocks private SellerQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerJpaEntity entity = SellerJpaEntityFixtures.activeEntity();
            Seller domain = SellerFixtures.activeSeller();

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Seller> result = queryAdapter.findById(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            SellerId sellerId = SellerId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<Seller> result = queryAdapter.findById(sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByIds 메서드 테스트")
    class FindByIdsTest {

        @Test
        @DisplayName("ID 목록으로 조회 시 Domain 목록을 반환합니다")
        void findByIds_WithValidIds_ReturnsDomainList() {
            // given
            List<SellerId> sellerIds = List.of(SellerId.of(1L), SellerId.of(2L));
            SellerJpaEntity entity1 = SellerJpaEntityFixtures.activeEntity(1L);
            SellerJpaEntity entity2 = SellerJpaEntityFixtures.activeEntity(2L);
            Seller domain1 = SellerFixtures.activeSeller(1L);
            Seller domain2 = SellerFixtures.activeSeller(2L);

            given(queryDslRepository.findByIds(List.of(1L, 2L)))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Seller> result = queryAdapter.findByIds(sellerIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 리스트를 반환합니다")
        void findByIds_WithEmptyList_ReturnsEmptyList() {
            // given
            List<SellerId> emptyList = List.of();
            given(queryDslRepository.findByIds(List.of())).willReturn(List.of());

            // when
            List<Seller> result = queryAdapter.findByIds(emptyList);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. existsById 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsById 메서드 테스트")
    class ExistsByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 true를 반환합니다")
        void existsById_WithExistingId_ReturnsTrue() {
            // given
            SellerId sellerId = SellerId.of(1L);
            given(queryDslRepository.existsById(1L)).willReturn(true);

            // when
            boolean result = queryAdapter.existsById(sellerId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 false를 반환합니다")
        void existsById_WithNonExistingId_ReturnsFalse() {
            // given
            SellerId sellerId = SellerId.of(999L);
            given(queryDslRepository.existsById(999L)).willReturn(false);

            // when
            boolean result = queryAdapter.existsById(sellerId);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 4. existsBySellerName 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySellerName 메서드 테스트")
    class ExistsBySellerNameTest {

        @Test
        @DisplayName("존재하는 셀러명으로 조회 시 true를 반환합니다")
        void existsBySellerName_WithExistingName_ReturnsTrue() {
            // given
            String sellerName = "테스트 셀러";
            given(queryDslRepository.existsBySellerName(sellerName)).willReturn(true);

            // when
            boolean result = queryAdapter.existsBySellerName(sellerName);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 셀러명으로 조회 시 false를 반환합니다")
        void existsBySellerName_WithNonExistingName_ReturnsFalse() {
            // given
            String sellerName = "존재하지 않는 셀러";
            given(queryDslRepository.existsBySellerName(sellerName)).willReturn(false);

            // when
            boolean result = queryAdapter.existsBySellerName(sellerName);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 5. existsBySellerNameExcluding 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySellerNameExcluding 메서드 테스트")
    class ExistsBySellerNameExcludingTest {

        @Test
        @DisplayName("특정 ID 제외하고 셀러명 존재 확인 시 true를 반환합니다")
        void existsBySellerNameExcluding_WithExistingName_ReturnsTrue() {
            // given
            String sellerName = "테스트 셀러";
            SellerId excludeId = SellerId.of(1L);
            given(queryDslRepository.existsBySellerNameExcluding(sellerName, 1L)).willReturn(true);

            // when
            boolean result = queryAdapter.existsBySellerNameExcluding(sellerName, excludeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("특정 ID 제외하고 셀러명이 없으면 false를 반환합니다")
        void existsBySellerNameExcluding_WithNonExistingName_ReturnsFalse() {
            // given
            String sellerName = "고유한 셀러명";
            SellerId excludeId = SellerId.of(1L);
            given(queryDslRepository.existsBySellerNameExcluding(sellerName, 1L)).willReturn(false);

            // when
            boolean result = queryAdapter.existsBySellerNameExcluding(sellerName, excludeId);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 6. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 셀러 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            SellerJpaEntity entity1 = SellerJpaEntityFixtures.activeEntity(1L);
            SellerJpaEntity entity2 = SellerJpaEntityFixtures.activeEntity(2L);
            Seller domain1 = SellerFixtures.activeSeller(1L);
            Seller domain2 = SellerFixtures.activeSeller(2L);

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Seller> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<Seller> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 7. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 셀러 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
