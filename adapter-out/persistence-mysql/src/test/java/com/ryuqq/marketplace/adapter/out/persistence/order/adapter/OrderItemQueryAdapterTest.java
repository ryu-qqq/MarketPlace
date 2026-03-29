package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
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
 * OrderItemQueryAdapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 JpaRepository를 통해 단순 조회.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemQueryAdapter 단위 테스트")
class OrderItemQueryAdapterTest {

    @Mock private OrderItemJpaRepository itemRepository;

    @Mock private OrderJpaEntityMapper mapper;

    @InjectMocks private OrderItemQueryAdapter queryAdapter;

    // ========================================================================
    // findAllByIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findAllByIds 메서드 테스트")
    class FindAllByIdsTest {

        @Test
        @DisplayName("유효한 ID 목록으로 조회 시 OrderItem 도메인 목록을 반환합니다")
        void findAllByIds_WithValidIds_ReturnsOrderItems() {
            // given
            String orderId = OrderJpaEntityFixtures.DEFAULT_ID;
            OrderItemJpaEntity entity1 = OrderItemJpaEntityFixtures.defaultItemWithId(orderId);
            OrderItemJpaEntity entity2 = OrderItemJpaEntityFixtures.defaultItemWithId(orderId);
            List<Long> rawIds = List.of(entity1.getId(), entity2.getId());
            List<OrderItemId> orderItemIds = rawIds.stream().map(OrderItemId::of).toList();
            OrderItem domain1 = OrderFixtures.reconstitutedOrderItem();
            OrderItem domain2 = OrderFixtures.reconstitutedOrderItem();

            given(itemRepository.findAllById(rawIds)).willReturn(List.of(entity1, entity2));
            given(mapper.toOrderItem(entity1)).willReturn(domain1);
            given(mapper.toOrderItem(entity2)).willReturn(domain2);

            // when
            List<OrderItem> result = queryAdapter.findAllByIds(orderItemIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(itemRepository).should().findAllById(rawIds);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 리스트를 반환합니다")
        void findAllByIds_WithEmptyIds_ReturnsEmptyList() {
            // given
            List<OrderItemId> emptyIds = List.of();

            given(itemRepository.findAllById(List.of())).willReturn(List.of());

            // when
            List<OrderItem> result = queryAdapter.findAllByIds(emptyIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID를 포함한 목록 조회 시 조회된 Entity만 반환합니다")
        void findAllByIds_WithPartiallyExistingIds_ReturnsFoundEntities() {
            // given
            String orderId = OrderJpaEntityFixtures.DEFAULT_ID;
            OrderItemJpaEntity entity = OrderItemJpaEntityFixtures.defaultItemWithId(orderId);
            Long nonExistingId = 99999L;
            List<Long> rawIds = List.of(entity.getId(), nonExistingId);
            List<OrderItemId> orderItemIds = rawIds.stream().map(OrderItemId::of).toList();
            OrderItem domain = OrderFixtures.reconstitutedOrderItem();

            given(itemRepository.findAllById(rawIds)).willReturn(List.of(entity));
            given(mapper.toOrderItem(entity)).willReturn(domain);

            // when
            List<OrderItem> result = queryAdapter.findAllByIds(orderItemIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(domain);
        }
    }
}
