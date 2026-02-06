package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.mapper.ShippingPolicyJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.repository.ShippingPolicyQueryDslRepository;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySortKey;
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
 * ShippingPolicyQueryAdapterTest - 배송 정책 Query Adapter 단위 테스트.
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
@DisplayName("ShippingPolicyQueryAdapter 단위 테스트")
class ShippingPolicyQueryAdapterTest {

    @Mock private ShippingPolicyQueryDslRepository queryDslRepository;

    @Mock private ShippingPolicyJpaEntityMapper mapper;

    @InjectMocks private ShippingPolicyQueryAdapter queryAdapter;

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
            ShippingPolicyId id = ShippingPolicyId.of(1L);
            ShippingPolicyJpaEntity entity =
                    ShippingPolicyJpaEntityFixtures.activeConditionalFreeEntity();
            ShippingPolicy domain = ShippingPolicyFixtures.activeShippingPolicy();

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<ShippingPolicy> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findById(1L);
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            ShippingPolicyId id = ShippingPolicyId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<ShippingPolicy> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findById(999L);
        }
    }

    // ========================================================================
    // 2. findByIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByIds 메서드 테스트")
    class FindByIdsTest {

        @Test
        @DisplayName("여러 ID로 조회 시 Domain 목록을 반환합니다")
        void findByIds_WithMultipleIds_ReturnsDomainList() {
            // given
            List<ShippingPolicyId> ids = List.of(ShippingPolicyId.of(1L), ShippingPolicyId.of(2L));
            ShippingPolicyJpaEntity entity1 = ShippingPolicyJpaEntityFixtures.activeEntity(1L);
            ShippingPolicyJpaEntity entity2 = ShippingPolicyJpaEntityFixtures.activeEntity(2L);
            ShippingPolicy domain1 =
                    ShippingPolicyFixtures.activeShippingPolicy(
                            1L, CommonVoFixtures.defaultSellerId());
            ShippingPolicy domain2 =
                    ShippingPolicyFixtures.activeShippingPolicy(
                            2L, CommonVoFixtures.defaultSellerId());

            given(queryDslRepository.findByIds(List.of(1L, 2L)))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ShippingPolicy> result = queryAdapter.findByIds(ids);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).contains(domain1, domain2);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 목록을 반환합니다")
        void findByIds_WithEmptyIds_ReturnsEmptyList() {
            // given
            List<ShippingPolicyId> emptyIds = List.of();
            given(queryDslRepository.findByIds(List.of())).willReturn(List.of());

            // when
            List<ShippingPolicy> result = queryAdapter.findByIds(emptyIds);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findDefaultBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findDefaultBySellerId 메서드 테스트")
    class FindDefaultBySellerIdTest {

        @Test
        @DisplayName("셀러의 기본 정책이 존재하면 반환합니다")
        void findDefaultBySellerId_WithExistingDefault_ReturnsDomain() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicyJpaEntity entity =
                    ShippingPolicyJpaEntityFixtures.activeConditionalFreeEntity();
            ShippingPolicy domain = ShippingPolicyFixtures.activeShippingPolicy();

            given(queryDslRepository.findDefaultBySellerId(sellerId.value()))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<ShippingPolicy> result = queryAdapter.findDefaultBySellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("셀러의 기본 정책이 없으면 빈 Optional을 반환합니다")
        void findDefaultBySellerId_WithNoDefault_ReturnsEmpty() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            given(queryDslRepository.findDefaultBySellerId(sellerId.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<ShippingPolicy> result = queryAdapter.findDefaultBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findBySellerIdAndId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerIdAndId 메서드 테스트")
    class FindBySellerIdAndIdTest {

        @Test
        @DisplayName("셀러 ID와 정책 ID로 조회 시 Domain을 반환합니다")
        void findBySellerIdAndId_WithValidIds_ReturnsDomain() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicyId policyId = ShippingPolicyId.of(1L);
            ShippingPolicyJpaEntity entity =
                    ShippingPolicyJpaEntityFixtures.activeConditionalFreeEntity();
            ShippingPolicy domain = ShippingPolicyFixtures.activeShippingPolicy();

            given(queryDslRepository.findBySellerIdAndId(sellerId.value(), policyId.value()))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<ShippingPolicy> result = queryAdapter.findBySellerIdAndId(sellerId, policyId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("다른 셀러의 정책 조회 시 빈 Optional을 반환합니다")
        void findBySellerIdAndId_WithDifferentSeller_ReturnsEmpty() {
            // given
            SellerId sellerId = SellerId.of(999L);
            ShippingPolicyId policyId = ShippingPolicyId.of(1L);

            given(queryDslRepository.findBySellerIdAndId(sellerId.value(), policyId.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<ShippingPolicy> result = queryAdapter.findBySellerIdAndId(sellerId, policyId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 조회 시 Domain 목록을 반환합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(
                            CommonVoFixtures.defaultSellerId(),
                            QueryContext.of(
                                    ShippingPolicySortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 10)));

            ShippingPolicyJpaEntity entity =
                    ShippingPolicyJpaEntityFixtures.activeConditionalFreeEntity();
            ShippingPolicy domain = ShippingPolicyFixtures.activeShippingPolicy();

            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ShippingPolicy> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).contains(domain);
        }
    }

    // ========================================================================
    // 6. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 개수 조회 시 카운트를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(
                            CommonVoFixtures.defaultSellerId(),
                            QueryContext.of(
                                    ShippingPolicySortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 10)));

            given(queryDslRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }
    }

    // ========================================================================
    // 7. countActiveBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("countActiveBySellerId 메서드 테스트")
    class CountActiveBySellerIdTest {

        @Test
        @DisplayName("셀러의 활성 정책 개수를 반환합니다")
        void countActiveBySellerId_WithValidSellerId_ReturnsCount() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            given(queryDslRepository.countActiveBySellerId(sellerId.value())).willReturn(3L);

            // when
            long result = queryAdapter.countActiveBySellerId(sellerId);

            // then
            assertThat(result).isEqualTo(3L);
        }

        @Test
        @DisplayName("활성 정책이 없으면 0을 반환합니다")
        void countActiveBySellerId_WithNoActive_ReturnsZero() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            given(queryDslRepository.countActiveBySellerId(sellerId.value())).willReturn(0L);

            // when
            long result = queryAdapter.countActiveBySellerId(sellerId);

            // then
            assertThat(result).isZero();
        }
    }
}
