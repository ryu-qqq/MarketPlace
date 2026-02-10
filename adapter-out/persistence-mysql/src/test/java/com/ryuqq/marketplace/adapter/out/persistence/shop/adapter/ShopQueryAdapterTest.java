package com.ryuqq.marketplace.adapter.out.persistence.shop.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.adapter.out.persistence.shop.ShopJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.mapper.ShopJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopQueryDslRepository;
import com.ryuqq.marketplace.domain.shop.ShopFixtures;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
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
 * ShopQueryAdapterTest - Shop Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShopQueryAdapter 단위 테스트")
class ShopQueryAdapterTest {

    @Mock private ShopQueryDslRepository queryDslRepository;

    @Mock private ShopJpaEntityMapper mapper;

    @Mock private ShopSearchCriteria criteria;

    @InjectMocks private ShopQueryAdapter queryAdapter;

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
            ShopId shopId = ShopId.of(1L);
            ShopJpaEntity entity = ShopJpaEntityFixtures.activeEntity();
            Shop domain = ShopFixtures.activeShop();

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Shop> result = queryAdapter.findById(shopId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            ShopId shopId = ShopId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<Shop> result = queryAdapter.findById(shopId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 조회 시 Domain 목록을 반환합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            ShopJpaEntity entity1 = ShopJpaEntityFixtures.activeEntity(1L);
            ShopJpaEntity entity2 = ShopJpaEntityFixtures.activeEntity(2L);
            Shop domain1 = ShopFixtures.activeShop(1L);
            Shop domain2 = ShopFixtures.activeShop(2L);

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Shop> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("조건에 맞는 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<Shop> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 개수를 조회합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(10L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(10L);
        }

        @Test
        @DisplayName("조건에 맞는 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ========================================================================
    // 4. existsBySalesChannelIdAndAccountId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySalesChannelIdAndAccountId 메서드 테스트")
    class ExistsBySalesChannelIdAndAccountIdTest {

        @Test
        @DisplayName("해당 판매채널+계정이 존재하면 true를 반환합니다")
        void existsBySalesChannelIdAndAccountId_Exists_ReturnsTrue() {
            // given
            Long salesChannelId = 1L;
            String accountId = "test-account-123";
            given(queryDslRepository.existsBySalesChannelIdAndAccountId(salesChannelId, accountId))
                    .willReturn(true);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndAccountId(salesChannelId, accountId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("해당 판매채널+계정이 존재하지 않으면 false를 반환합니다")
        void existsBySalesChannelIdAndAccountId_NotExists_ReturnsFalse() {
            // given
            Long salesChannelId = 1L;
            String accountId = "non-existing-account";
            given(queryDslRepository.existsBySalesChannelIdAndAccountId(salesChannelId, accountId))
                    .willReturn(false);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndAccountId(salesChannelId, accountId);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 5. existsBySalesChannelIdAndAccountIdExcluding 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySalesChannelIdAndAccountIdExcluding 메서드 테스트")
    class ExistsBySalesChannelIdAndAccountIdExcludingTest {

        @Test
        @DisplayName("특정 ID를 제외하고 판매채널+계정이 존재하면 true를 반환합니다")
        void existsBySalesChannelIdAndAccountIdExcluding_Exists_ReturnsTrue() {
            // given
            Long salesChannelId = 1L;
            String accountId = "test-account-123";
            ShopId excludeId = ShopId.of(1L);
            given(
                            queryDslRepository.existsBySalesChannelIdAndAccountIdExcluding(
                                    salesChannelId, accountId, 1L))
                    .willReturn(true);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndAccountIdExcluding(
                            salesChannelId, accountId, excludeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("특정 ID를 제외하고 판매채널+계정이 없으면 false를 반환합니다")
        void existsBySalesChannelIdAndAccountIdExcluding_NotExists_ReturnsFalse() {
            // given
            Long salesChannelId = 1L;
            String accountId = "unique-account";
            ShopId excludeId = ShopId.of(1L);
            given(
                            queryDslRepository.existsBySalesChannelIdAndAccountIdExcluding(
                                    salesChannelId, accountId, 1L))
                    .willReturn(false);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndAccountIdExcluding(
                            salesChannelId, accountId, excludeId);

            // then
            assertThat(result).isFalse();
        }
    }
}
