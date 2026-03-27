package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.ClaimHistoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.ClaimHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.mapper.ClaimHistoryPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryQueryDslRepository;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ClaimHistoryQueryAdapterTest - 클레임 이력 Query Adapter 단위 테스트.
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
@DisplayName("ClaimHistoryQueryAdapter 단위 테스트")
class ClaimHistoryQueryAdapterTest {

    @Mock private ClaimHistoryQueryDslRepository claimHistoryRepository;

    @Mock private ClaimHistoryPersistenceMapper mapper;

    @InjectMocks private ClaimHistoryQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findByClaimTypeAndClaimId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByClaimTypeAndClaimId 메서드 테스트")
    class FindByClaimTypeAndClaimIdTest {

        @Test
        @DisplayName("claimType과 claimId로 이력 목록을 반환합니다")
        void findByClaimTypeAndClaimId_WithValidParams_ReturnsDomainList() {
            // given
            ClaimType claimType = ClaimType.CANCEL;
            String claimId = "cancel-001";

            ClaimHistoryJpaEntity entity1 =
                    ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(claimId);
            ClaimHistoryJpaEntity entity2 = ClaimHistoryJpaEntityFixtures.manualMemoEntity(claimId);

            ClaimHistory domain1 = ClaimHistoryFixtures.cancelStatusChangeHistory();
            ClaimHistory domain2 = ClaimHistoryFixtures.manualClaimHistory();

            given(claimHistoryRepository.findByClaimTypeAndClaimId("CANCEL", claimId))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ClaimHistory> result = queryAdapter.findByClaimTypeAndClaimId(claimType, claimId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(claimHistoryRepository).should().findByClaimTypeAndClaimId("CANCEL", claimId);
        }

        @Test
        @DisplayName("이력이 없으면 빈 리스트를 반환합니다")
        void findByClaimTypeAndClaimId_WithNoResults_ReturnsEmptyList() {
            // given
            ClaimType claimType = ClaimType.CANCEL;
            String claimId = "non-existent-001";

            given(claimHistoryRepository.findByClaimTypeAndClaimId("CANCEL", claimId))
                    .willReturn(List.of());

            // when
            List<ClaimHistory> result = queryAdapter.findByClaimTypeAndClaimId(claimType, claimId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("REFUND 타입으로 이력을 조회합니다")
        void findByClaimTypeAndClaimId_WithRefundType_ReturnsDomainList() {
            // given
            ClaimType claimType = ClaimType.REFUND;
            String claimId = "refund-001";

            ClaimHistoryJpaEntity entity =
                    ClaimHistoryJpaEntityFixtures.refundStatusChangeEntity(claimId);
            ClaimHistory domain = ClaimHistoryFixtures.refundStatusChangeHistory();

            given(claimHistoryRepository.findByClaimTypeAndClaimId("REFUND", claimId))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ClaimHistory> result = queryAdapter.findByClaimTypeAndClaimId(claimType, claimId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("EXCHANGE 타입으로 이력을 조회합니다")
        void findByClaimTypeAndClaimId_WithExchangeType_ReturnsDomainList() {
            // given
            ClaimType claimType = ClaimType.EXCHANGE;
            String claimId = "exchange-001";

            ClaimHistoryJpaEntity entity =
                    ClaimHistoryJpaEntityFixtures.exchangeStatusChangeEntity(claimId);
            ClaimHistory domain = ClaimHistoryFixtures.exchangeStatusChangeHistory();

            given(claimHistoryRepository.findByClaimTypeAndClaimId("EXCHANGE", claimId))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ClaimHistory> result = queryAdapter.findByClaimTypeAndClaimId(claimType, claimId);

            // then
            assertThat(result).hasSize(1);
        }
    }

    // ========================================================================
    // 2. findByClaimTypeAndClaimIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByClaimTypeAndClaimIds 메서드 테스트")
    class FindByClaimTypeAndClaimIdsTest {

        @Test
        @DisplayName("claimType과 claimId 목록으로 이력 목록을 반환합니다")
        void findByClaimTypeAndClaimIds_WithValidParams_ReturnsDomainList() {
            // given
            ClaimType claimType = ClaimType.CANCEL;
            List<String> claimIds = List.of("cancel-001", "cancel-002");

            ClaimHistoryJpaEntity entity1 =
                    ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("cancel-001");
            ClaimHistoryJpaEntity entity2 =
                    ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("cancel-002");

            ClaimHistory domain1 = ClaimHistoryFixtures.cancelStatusChangeHistory();
            ClaimHistory domain2 = ClaimHistoryFixtures.cancelStatusChangeHistory();

            given(claimHistoryRepository.findByClaimTypeAndClaimIds("CANCEL", claimIds))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ClaimHistory> result =
                    queryAdapter.findByClaimTypeAndClaimIds(claimType, claimIds);

            // then
            assertThat(result).hasSize(2);
            then(claimHistoryRepository).should().findByClaimTypeAndClaimIds("CANCEL", claimIds);
        }

        @Test
        @DisplayName("빈 claimId 목록으로 조회 시 빈 리스트를 반환합니다")
        void findByClaimTypeAndClaimIds_WithEmptyList_ReturnsEmptyList() {
            // given
            ClaimType claimType = ClaimType.CANCEL;
            List<String> emptyIds = List.of();

            given(claimHistoryRepository.findByClaimTypeAndClaimIds("CANCEL", emptyIds))
                    .willReturn(List.of());

            // when
            List<ClaimHistory> result =
                    queryAdapter.findByClaimTypeAndClaimIds(claimType, emptyIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 리스트를 반환합니다")
        void findByClaimTypeAndClaimIds_WithNoResults_ReturnsEmptyList() {
            // given
            ClaimType claimType = ClaimType.REFUND;
            List<String> claimIds = List.of("non-existent-001");

            given(claimHistoryRepository.findByClaimTypeAndClaimIds("REFUND", claimIds))
                    .willReturn(List.of());

            // when
            List<ClaimHistory> result =
                    queryAdapter.findByClaimTypeAndClaimIds(claimType, claimIds);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByOrderItemId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemId 메서드 테스트")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("orderItemId로 이력 목록을 반환합니다")
        void findByOrderItemId_WithValidOrderItemId_ReturnsDomainList() {
            // given
            String orderItemId = "order-item-001";

            ClaimHistoryJpaEntity entity1 =
                    ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("cancel-001");
            ClaimHistoryJpaEntity entity2 =
                    ClaimHistoryJpaEntityFixtures.orderMemoEntity(orderItemId);

            ClaimHistory domain1 = ClaimHistoryFixtures.cancelStatusChangeHistory();
            ClaimHistory domain2 = ClaimHistoryFixtures.orderManualClaimHistory();

            given(claimHistoryRepository.findByOrderItemId(orderItemId))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ClaimHistory> result = queryAdapter.findByOrderItemId(orderItemId);

            // then
            assertThat(result).hasSize(2);
            then(claimHistoryRepository).should().findByOrderItemId(orderItemId);
        }

        @Test
        @DisplayName("이력이 없으면 빈 리스트를 반환합니다")
        void findByOrderItemId_WithNoResults_ReturnsEmptyList() {
            // given
            String orderItemId = "non-existent-order-item";

            given(claimHistoryRepository.findByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            List<ClaimHistory> result = queryAdapter.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("criteria로 이력 목록을 반환합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf("order-item-001");

            ClaimHistoryJpaEntity entity = ClaimHistoryJpaEntityFixtures.defaultEntity();
            ClaimHistory domain = ClaimHistoryFixtures.reconstitutedClaimHistory();

            given(claimHistoryRepository.findByCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ClaimHistory> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(claimHistoryRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf("non-existent-order-item");

            given(claimHistoryRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<ClaimHistory> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("criteria에 해당하는 이력 수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf("order-item-001");

            given(claimHistoryRepository.countByCriteria(criteria)).willReturn(3L);

            // when
            long count = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(3L);
            then(claimHistoryRepository).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf("non-existent-order-item");

            given(claimHistoryRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long count = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }
    }
}
