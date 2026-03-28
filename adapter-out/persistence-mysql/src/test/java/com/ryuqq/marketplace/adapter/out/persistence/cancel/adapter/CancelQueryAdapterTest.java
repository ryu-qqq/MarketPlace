package com.ryuqq.marketplace.adapter.out.persistence.cancel.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.CancelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.mapper.CancelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelQueryDslRepository;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
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
 * CancelQueryAdapter 단위 테스트.
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
@DisplayName("CancelQueryAdapter 단위 테스트")
class CancelQueryAdapterTest {

    @Mock private CancelQueryDslRepository cancelRepository;
    @Mock private CancelJpaEntityMapper mapper;

    @InjectMocks private CancelQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Cancel 도메인을 반환합니다")
        void findById_WithExistingId_ReturnsCancel() {
            // given
            String id = CancelJpaEntityFixtures.DEFAULT_ID;
            CancelId cancelId = CancelId.of(id);
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            id,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);
            Cancel domain = CancelFixtures.requestedCancel();

            given(cancelRepository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Cancel> result = queryAdapter.findById(cancelId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            String id = "non-existent-cancel-id";
            CancelId cancelId = CancelId.of(id);

            given(cancelRepository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<Cancel> result = queryAdapter.findById(cancelId);

            // then
            assertThat(result).isEmpty();
            then(cancelRepository).should().findById(id);
        }

        @Test
        @DisplayName("findById 호출 시 repository에 ID 값이 전달됩니다")
        void findById_DelegatesToRepositoryWithIdValue() {
            // given
            String id = CancelJpaEntityFixtures.DEFAULT_ID;
            CancelId cancelId = CancelId.of(id);

            given(cancelRepository.findById(id)).willReturn(Optional.empty());

            // when
            queryAdapter.findById(cancelId);

            // then
            then(cancelRepository).should().findById(id);
        }
    }

    // ========================================================================
    // 2. findByOrderItemId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemId 메서드 테스트")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("존재하는 orderItemId로 조회 시 Cancel 도메인을 반환합니다")
        void findByOrderItemId_WithExistingOrderItemId_ReturnsCancel() {
            // given
            Long orderItemId = CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;
            OrderItemId id = OrderItemId.of(orderItemId);
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            orderItemId,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);
            Cancel domain = CancelFixtures.requestedCancel();

            given(cancelRepository.findByOrderItemId(orderItemId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Cancel> result = queryAdapter.findByOrderItemId(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 orderItemId로 조회 시 빈 Optional을 반환합니다")
        void findByOrderItemId_WithNonExistingOrderItemId_ReturnsEmpty() {
            // given
            Long orderItemId = 9999L;
            OrderItemId id = OrderItemId.of(orderItemId);

            given(cancelRepository.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            Optional<Cancel> result = queryAdapter.findByOrderItemId(id);

            // then
            assertThat(result).isEmpty();
            then(cancelRepository).should().findByOrderItemId(orderItemId);
        }
    }

    // ========================================================================
    // 3. findByOrderItemIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemIds 메서드 테스트")
    class FindByOrderItemIdsTest {

        @Test
        @DisplayName("orderItemId 목록으로 조회 시 Cancel 목록을 반환합니다")
        void findByOrderItemIds_WithExistingIds_ReturnsCancelList() {
            // given
            Long orderItemId1 = 2001L;
            Long orderItemId2 = 2002L;
            List<OrderItemId> ids =
                    List.of(OrderItemId.of(orderItemId1), OrderItemId.of(orderItemId2));
            CancelJpaEntity entity1 =
                    CancelJpaEntityFixtures.requestedEntity(
                            "cancel-id-" + orderItemId1,
                            orderItemId1,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);
            CancelJpaEntity entity2 =
                    CancelJpaEntityFixtures.requestedEntity(
                            "cancel-id-" + orderItemId2,
                            orderItemId2,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);
            Cancel domain1 = CancelFixtures.requestedCancel();
            Cancel domain2 = CancelFixtures.approvedCancel();

            given(cancelRepository.findByOrderItemIds(List.of(orderItemId1, orderItemId2)))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Cancel> result = queryAdapter.findByOrderItemIds(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("일치하는 orderItemId가 없으면 빈 리스트를 반환합니다")
        void findByOrderItemIds_WithNoResults_ReturnsEmptyList() {
            // given
            List<OrderItemId> ids = List.of(OrderItemId.of(9999L));

            given(cancelRepository.findByOrderItemIds(List.of(9999L))).willReturn(List.of());

            // when
            List<Cancel> result = queryAdapter.findByOrderItemIds(ids);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("OrderItemId 목록이 String 값 목록으로 변환되어 repository에 전달됩니다")
        void findByOrderItemIds_DelegatesToRepositoryWithStringValues() {
            // given
            Long orderItemId = CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;
            List<OrderItemId> ids = List.of(OrderItemId.of(orderItemId));

            given(cancelRepository.findByOrderItemIds(List.of(orderItemId))).willReturn(List.of());

            // when
            queryAdapter.findByOrderItemIds(ids);

            // then
            then(cancelRepository).should().findByOrderItemIds(List.of(orderItemId));
        }
    }

    // ========================================================================
    // 4. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("criteria로 조회 시 Cancel 목록을 반환합니다")
        void findByCriteria_WithCriteria_ReturnsCancelList() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);
            Cancel domain = CancelFixtures.requestedCancel();

            given(cancelRepository.findByCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<Cancel> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            given(cancelRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<Cancel> result = queryAdapter.findByCriteria(criteria);

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
        @DisplayName("criteria로 카운트 조회 시 정확한 개수를 반환합니다")
        void countByCriteria_WithCriteria_ReturnsCount() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            given(cancelRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
            then(cancelRepository).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();

            given(cancelRepository.countByCriteria(criteria)).willReturn(0L);

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
        @DisplayName("상태별 카운트 조회 시 Map을 반환합니다")
        void countByStatus_ReturnsStatusCountMap() {
            // given
            Map<CancelStatus, Long> statusCounts = new EnumMap<>(CancelStatus.class);
            statusCounts.put(CancelStatus.REQUESTED, 3L);
            statusCounts.put(CancelStatus.APPROVED, 2L);

            given(cancelRepository.countByStatus()).willReturn(statusCounts);

            // when
            Map<CancelStatus, Long> result = queryAdapter.countByStatus();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(CancelStatus.REQUESTED)).isEqualTo(3L);
            assertThat(result.get(CancelStatus.APPROVED)).isEqualTo(2L);
        }

        @Test
        @DisplayName("데이터가 없으면 빈 Map을 반환합니다")
        void countByStatus_WithNoData_ReturnsEmptyMap() {
            // given
            given(cancelRepository.countByStatus()).willReturn(Map.of());

            // when
            Map<CancelStatus, Long> result = queryAdapter.countByStatus();

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
        @DisplayName("cancelId 목록과 sellerId로 조회 시 Cancel 목록을 반환합니다")
        void findByIdIn_WithIdsAndSellerId_ReturnsCancelList() {
            // given
            List<String> cancelIds = List.of("cancel-id-001", "cancel-id-002");
            Long sellerId = CancelJpaEntityFixtures.DEFAULT_SELLER_ID;
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            "cancel-id-001",
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            sellerId);
            Cancel domain = CancelFixtures.requestedCancel();

            given(cancelRepository.findByIdIn(cancelIds, sellerId)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<Cancel> result = queryAdapter.findByIdIn(cancelIds, sellerId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("일치하는 데이터가 없으면 빈 리스트를 반환합니다")
        void findByIdIn_WithNoResults_ReturnsEmptyList() {
            // given
            List<String> cancelIds = List.of("non-exist-id");
            Long sellerId = 999L;

            given(cancelRepository.findByIdIn(cancelIds, sellerId)).willReturn(List.of());

            // when
            List<Cancel> result = queryAdapter.findByIdIn(cancelIds, sellerId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findByIdIn 호출 시 파라미터가 repository에 그대로 전달됩니다")
        void findByIdIn_DelegatesToRepositoryWithCorrectParams() {
            // given
            List<String> cancelIds = List.of("cancel-id-001");
            Long sellerId = CancelJpaEntityFixtures.DEFAULT_SELLER_ID;

            given(cancelRepository.findByIdIn(cancelIds, sellerId)).willReturn(List.of());

            // when
            queryAdapter.findByIdIn(cancelIds, sellerId);

            // then
            then(cancelRepository).should().findByIdIn(cancelIds, sellerId);
        }
    }
}
