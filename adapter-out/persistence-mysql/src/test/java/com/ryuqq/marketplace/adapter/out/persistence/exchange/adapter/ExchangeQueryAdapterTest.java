package com.ryuqq.marketplace.adapter.out.persistence.exchange.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.ExchangeClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.mapper.ExchangePersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimQueryDslRepository;
import com.ryuqq.marketplace.application.claim.port.out.query.ClaimShipmentQueryPort;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
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
 * ExchangeQueryAdapterTest - 교환 클레임 Query Adapter 단위 테스트.
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
@DisplayName("ExchangeQueryAdapter 단위 테스트")
class ExchangeQueryAdapterTest {

    @Mock private ExchangeClaimQueryDslRepository repository;
    @Mock private ExchangePersistenceMapper mapper;
    @Mock private ClaimShipmentQueryPort claimShipmentQueryPort;
    @Mock private ExchangeSearchCriteria criteria;

    @InjectMocks private ExchangeQueryAdapter queryAdapter;

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
            ExchangeClaimId claimId = ExchangeFixtures.defaultExchangeClaimId();
            ExchangeClaimJpaEntity entity = ExchangeClaimJpaEntityFixtures.requestedEntity();
            ExchangeClaim domain = ExchangeFixtures.requestedExchangeClaim();

            given(repository.findById(claimId.value())).willReturn(Optional.of(entity));
            given(claimShipmentQueryPort.findById(org.mockito.ArgumentMatchers.any()))
                    .willReturn(Optional.empty());
            given(mapper.toDomain(entity, null)).willReturn(domain);

            // when
            Optional<ExchangeClaim> result = queryAdapter.findById(claimId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            ExchangeClaimId claimId = ExchangeFixtures.exchangeClaimId("non-existent-id");
            given(repository.findById("non-existent-id")).willReturn(Optional.empty());

            // when
            Optional<ExchangeClaim> result = queryAdapter.findById(claimId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findById 호출 시 repository에 ID 값이 전달됩니다")
        void findById_DelegatesToRepositoryWithIdValue() {
            // given
            ExchangeClaimId claimId = ExchangeFixtures.defaultExchangeClaimId();
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
            String orderItemId = ExchangeClaimJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;
            ExchangeClaimJpaEntity entity =
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            ExchangeClaimJpaEntityFixtures.DEFAULT_ID, orderItemId);
            ExchangeClaim domain = ExchangeFixtures.requestedExchangeClaim();

            given(repository.findByOrderItemId(orderItemId)).willReturn(Optional.of(entity));
            given(claimShipmentQueryPort.findById(org.mockito.ArgumentMatchers.any()))
                    .willReturn(Optional.empty());
            given(mapper.toDomain(entity, null)).willReturn(domain);

            // when
            Optional<ExchangeClaim> result =
                    queryAdapter.findByOrderItemId(OrderItemId.of(orderItemId));

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 orderItemId로 조회 시 빈 Optional을 반환합니다")
        void findByOrderItemId_WithNonExistingOrderItemId_ReturnsEmpty() {
            // given
            String orderItemId = "01900000-0000-7000-0000-000000000999";
            given(repository.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            Optional<ExchangeClaim> result =
                    queryAdapter.findByOrderItemId(OrderItemId.of(orderItemId));

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
            List<OrderItemId> orderItemIds = List.of(OrderItemId.of(id1), OrderItemId.of(id2));

            ExchangeClaimJpaEntity entity1 =
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId("id-001", id1);
            ExchangeClaimJpaEntity entity2 =
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId("id-002", id2);
            ExchangeClaim domain1 = ExchangeFixtures.requestedExchangeClaim();
            ExchangeClaim domain2 = ExchangeFixtures.collectingExchangeClaim();

            given(repository.findByOrderItemIds(List.of(id1, id2)))
                    .willReturn(List.of(entity1, entity2));
            given(claimShipmentQueryPort.findById(org.mockito.ArgumentMatchers.any()))
                    .willReturn(Optional.empty());
            given(mapper.toDomain(entity1, null)).willReturn(domain1);
            given(mapper.toDomain(entity2, null)).willReturn(domain2);

            // when
            List<ExchangeClaim> result = queryAdapter.findByOrderItemIds(orderItemIds);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findByOrderItemIds_WithNoResults_ReturnsEmptyList() {
            // given
            List<OrderItemId> orderItemIds = List.of(OrderItemId.of("non-existent-id"));
            given(repository.findByOrderItemIds(List.of("non-existent-id"))).willReturn(List.of());

            // when
            List<ExchangeClaim> result = queryAdapter.findByOrderItemIds(orderItemIds);

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
        @DisplayName("검색 조건으로 교환 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            ExchangeClaimJpaEntity entity1 = ExchangeClaimJpaEntityFixtures.requestedEntity("id-1");
            ExchangeClaimJpaEntity entity2 = ExchangeClaimJpaEntityFixtures.requestedEntity("id-2");
            ExchangeClaim domain1 = ExchangeFixtures.requestedExchangeClaim();
            ExchangeClaim domain2 = ExchangeFixtures.collectingExchangeClaim();

            given(repository.findByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(claimShipmentQueryPort.findById(org.mockito.ArgumentMatchers.any()))
                    .willReturn(Optional.empty());
            given(mapper.toDomain(entity1, null)).willReturn(domain1);
            given(mapper.toDomain(entity2, null)).willReturn(domain2);

            // when
            List<ExchangeClaim> result = queryAdapter.findByCriteria(criteria);

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
            List<ExchangeClaim> result = queryAdapter.findByCriteria(criteria);

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
        @DisplayName("검색 조건으로 교환 개수를 반환합니다")
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
        @DisplayName("상태별 교환 개수 맵을 반환합니다")
        void countByStatus_ReturnsStatusCountMap() {
            // given
            Map<ExchangeStatus, Long> statusCounts = new EnumMap<>(ExchangeStatus.class);
            statusCounts.put(ExchangeStatus.REQUESTED, 10L);
            statusCounts.put(ExchangeStatus.COLLECTING, 5L);
            statusCounts.put(ExchangeStatus.COMPLETED, 3L);

            given(repository.countByStatus()).willReturn(statusCounts);

            // when
            Map<ExchangeStatus, Long> result = queryAdapter.countByStatus();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(ExchangeStatus.REQUESTED)).isEqualTo(10L);
            assertThat(result.get(ExchangeStatus.COLLECTING)).isEqualTo(5L);
            assertThat(result.get(ExchangeStatus.COMPLETED)).isEqualTo(3L);
        }

        @Test
        @DisplayName("교환이 없으면 빈 맵을 반환합니다")
        void countByStatus_WithNoExchanges_ReturnsEmptyMap() {
            // given
            given(repository.countByStatus()).willReturn(new EnumMap<>(ExchangeStatus.class));

            // when
            Map<ExchangeStatus, Long> result = queryAdapter.countByStatus();

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
        @DisplayName("ID 목록과 sellerId로 교환 목록을 조회합니다")
        void findByIdIn_WithValidIdsAndSellerId_ReturnsDomainList() {
            // given
            List<String> ids =
                    List.of(
                            "01900000-0000-7000-0000-000000000001",
                            "01900000-0000-7000-0000-000000000002");
            Long sellerId = ExchangeClaimJpaEntityFixtures.DEFAULT_SELLER_ID;

            ExchangeClaimJpaEntity entity1 =
                    ExchangeClaimJpaEntityFixtures.requestedEntity(ids.get(0));
            ExchangeClaimJpaEntity entity2 =
                    ExchangeClaimJpaEntityFixtures.requestedEntity(ids.get(1));
            ExchangeClaim domain1 = ExchangeFixtures.requestedExchangeClaim();
            ExchangeClaim domain2 = ExchangeFixtures.collectingExchangeClaim();

            given(repository.findByIdIn(ids, sellerId)).willReturn(List.of(entity1, entity2));
            given(claimShipmentQueryPort.findById(org.mockito.ArgumentMatchers.any()))
                    .willReturn(Optional.empty());
            given(mapper.toDomain(entity1, null)).willReturn(domain1);
            given(mapper.toDomain(entity2, null)).willReturn(domain2);

            // when
            List<ExchangeClaim> result = queryAdapter.findByIdIn(ids, sellerId);

            // then
            assertThat(result).hasSize(2);
            then(repository).should().findByIdIn(ids, sellerId);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findByIdIn_WithNoResults_ReturnsEmptyList() {
            // given
            List<String> ids = List.of("non-existent-id");
            Long sellerId = 100L;
            given(repository.findByIdIn(ids, sellerId)).willReturn(List.of());

            // when
            List<ExchangeClaim> result = queryAdapter.findByIdIn(ids, sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
