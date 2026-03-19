package com.ryuqq.marketplace.adapter.out.persistence.refund.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.refund.RefundClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refund.mapper.RefundPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimQueryDslRepository;
import com.ryuqq.marketplace.application.claim.port.out.query.ClaimShipmentQueryPort;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
 * RefundQueryAdapterTest - 환불 클레임 Query Adapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefundQueryAdapter 단위 테스트")
class RefundQueryAdapterTest {

    @Mock private RefundClaimQueryDslRepository repository;
    @Mock private RefundPersistenceMapper mapper;
    @Mock private ClaimShipmentQueryPort claimShipmentQueryPort;
    @Mock private RefundSearchCriteria criteria;

    @InjectMocks private RefundQueryAdapter queryAdapter;

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
            RefundClaimId claimId = RefundFixtures.defaultRefundClaimId();
            // claimShipmentId가 null인 entity 사용 -> claimShipmentQueryPort 호출 없음
            RefundClaimJpaEntity entity = RefundClaimJpaEntityFixtures.requestedEntity();
            RefundClaim domain = RefundFixtures.requestedRefundClaim();

            given(repository.findById(claimId.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity, null)).willReturn(domain);

            // when
            Optional<RefundClaim> result = queryAdapter.findById(claimId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            RefundClaimId claimId = RefundFixtures.refundClaimId("non-existent-id");
            given(repository.findById("non-existent-id")).willReturn(Optional.empty());

            // when
            Optional<RefundClaim> result = queryAdapter.findById(claimId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findById 호출 시 repository에 ID 값이 전달됩니다")
        void findById_DelegatesToRepositoryWithIdValue() {
            // given
            RefundClaimId claimId = RefundFixtures.defaultRefundClaimId();
            given(repository.findById(claimId.value())).willReturn(Optional.empty());

            // when
            queryAdapter.findById(claimId);

            // then
            then(repository).should().findById(claimId.value());
        }
    }

    // ========================================================================
    // 2. findByOrderItemId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemId 메서드 테스트")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("존재하는 orderItemId로 조회 시 Domain을 반환합니다")
        void findByOrderItemId_WithExistingOrderItemId_ReturnsDomain() {
            // given
            String orderItemId = RefundClaimJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;
            // claimShipmentId가 null인 entity 사용 -> claimShipmentQueryPort 호출 없음
            RefundClaimJpaEntity entity = RefundClaimJpaEntityFixtures.requestedEntity();
            RefundClaim domain = RefundFixtures.requestedRefundClaim();

            given(repository.findByOrderItemId(orderItemId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity, null)).willReturn(domain);

            // when
            Optional<RefundClaim> result = queryAdapter.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 orderItemId로 조회 시 빈 Optional을 반환합니다")
        void findByOrderItemId_WithNonExistingOrderItemId_ReturnsEmpty() {
            // given
            String orderItemId = "non-existent-id";
            given(repository.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            Optional<RefundClaim> result = queryAdapter.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByOrderItemIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemIds 메서드 테스트")
    class FindByOrderItemIdsTest {

        @Test
        @DisplayName("orderItemId 목록으로 복수 Domain을 반환합니다")
        void findByOrderItemIds_WithMultipleIds_ReturnsDomainList() {
            // given
            String id1 = "01900000-0000-7000-0000-000000000010";
            String id2 = "01900000-0000-7000-0000-000000000011";
            List<String> orderItemIds = List.of(id1, id2);

            // claimShipmentId가 null인 entity 사용 -> claimShipmentQueryPort 호출 없음
            RefundClaimJpaEntity entity1 =
                    RefundClaimJpaEntityFixtures.requestedEntity("rid-001", id1, 10L);
            RefundClaimJpaEntity entity2 =
                    RefundClaimJpaEntityFixtures.requestedEntity("rid-002", id2, 10L);
            RefundClaim domain1 = RefundFixtures.requestedRefundClaim();
            RefundClaim domain2 = RefundFixtures.collectingRefundClaim();

            given(repository.findByOrderItemIds(orderItemIds))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1, null)).willReturn(domain1);
            given(mapper.toDomain(entity2, null)).willReturn(domain2);

            // when
            List<RefundClaim> result = queryAdapter.findByOrderItemIds(orderItemIds);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findByOrderItemIds_WithNoResults_ReturnsEmptyList() {
            // given
            List<String> orderItemIds = List.of("non-existent-id");
            given(repository.findByOrderItemIds(orderItemIds)).willReturn(List.of());

            // when
            List<RefundClaim> result = queryAdapter.findByOrderItemIds(orderItemIds);

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
        @DisplayName("검색 조건으로 환불 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            // claimShipmentId가 null인 entity 사용 -> claimShipmentQueryPort 호출 없음
            RefundClaimJpaEntity entity1 = RefundClaimJpaEntityFixtures.requestedEntity("id-1");
            RefundClaimJpaEntity entity2 = RefundClaimJpaEntityFixtures.requestedEntity("id-2");
            RefundClaim domain1 = RefundFixtures.requestedRefundClaim();
            RefundClaim domain2 = RefundFixtures.collectingRefundClaim();

            given(repository.findByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1, null)).willReturn(domain1);
            given(mapper.toDomain(entity2, null)).willReturn(domain2);

            // when
            List<RefundClaim> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(repository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(repository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<RefundClaim> result = queryAdapter.findByCriteria(criteria);

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
        @DisplayName("검색 조건으로 환불 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(repository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ========================================================================
    // 6. countByStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByStatus 메서드 테스트")
    class CountByStatusTest {

        @Test
        @DisplayName("상태별 환불 개수 맵을 반환합니다")
        void countByStatus_ReturnsStatusCountMap() {
            // given
            Map<RefundStatus, Long> statusCounts = new EnumMap<>(RefundStatus.class);
            statusCounts.put(RefundStatus.REQUESTED, 10L);
            statusCounts.put(RefundStatus.COLLECTING, 5L);
            statusCounts.put(RefundStatus.COMPLETED, 3L);

            given(repository.countByStatus()).willReturn(statusCounts);

            // when
            Map<RefundStatus, Long> result = queryAdapter.countByStatus();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(RefundStatus.REQUESTED)).isEqualTo(10L);
            assertThat(result.get(RefundStatus.COLLECTING)).isEqualTo(5L);
            assertThat(result.get(RefundStatus.COMPLETED)).isEqualTo(3L);
        }

        @Test
        @DisplayName("환불이 없으면 빈 맵을 반환합니다")
        void countByStatus_WithNoRefunds_ReturnsEmptyMap() {
            // given
            given(repository.countByStatus()).willReturn(new EnumMap<>(RefundStatus.class));

            // when
            Map<RefundStatus, Long> result = queryAdapter.countByStatus();

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 7. findByIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByIdIn 메서드 테스트")
    class FindByIdInTest {

        @Test
        @DisplayName("ID 목록과 sellerId로 환불 목록을 조회합니다")
        void findByIdIn_WithValidIdsAndSellerId_ReturnsDomainList() {
            // given
            List<String> ids =
                    List.of(
                            "01900000-0000-7000-0000-000000000101",
                            "01900000-0000-7000-0000-000000000102");
            Long sellerId = RefundClaimJpaEntityFixtures.DEFAULT_SELLER_ID;

            RefundClaimJpaEntity entity1 = RefundClaimJpaEntityFixtures.requestedEntity(ids.get(0));
            RefundClaimJpaEntity entity2 = RefundClaimJpaEntityFixtures.requestedEntity(ids.get(1));
            RefundClaim domain1 = RefundFixtures.requestedRefundClaim();
            RefundClaim domain2 = RefundFixtures.collectingRefundClaim();

            // claimShipmentId가 null인 entity 사용 -> claimShipmentQueryPort 호출 없음
            given(repository.findByIdIn(ids, sellerId)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1, null)).willReturn(domain1);
            given(mapper.toDomain(entity2, null)).willReturn(domain2);

            // when
            List<RefundClaim> result = queryAdapter.findByIdIn(ids, sellerId);

            // then
            assertThat(result).hasSize(2);
            then(repository).should().findByIdIn(ids, sellerId);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findByIdIn_WithNoResults_ReturnsEmptyList() {
            // given
            List<String> ids = List.of("non-existent-id");
            Long sellerId = 10L;
            given(repository.findByIdIn(ids, sellerId)).willReturn(List.of());

            // when
            List<RefundClaim> result = queryAdapter.findByIdIn(ids, sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
