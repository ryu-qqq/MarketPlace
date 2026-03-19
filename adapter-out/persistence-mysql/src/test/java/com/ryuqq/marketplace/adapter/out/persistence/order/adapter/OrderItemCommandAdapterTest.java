package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemHistoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
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
@DisplayName("OrderItemCommandAdapter 단위 테스트")
class OrderItemCommandAdapterTest {

    @Mock private OrderItemJpaRepository itemRepository;
    @Mock private OrderItemHistoryJpaRepository itemHistoryRepository;
    @Mock private OrderJpaEntityMapper mapper;

    @InjectMocks private OrderItemCommandAdapter commandAdapter;

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("OrderItem이 존재하지 않으면 Entity 변환 후 save + 이력 저장됩니다")
        void persistAll_WithNewOrderItem_SavesEntityAndHistory() {
            // given
            String orderId = OrderJpaEntityFixtures.DEFAULT_ID;
            OrderItem orderItem = OrderFixtures.confirmedOrderItem();
            OrderItemJpaEntity entity = OrderItemJpaEntityFixtures.defaultItem(orderId);
            OrderItemHistoryJpaEntity historyEntity =
                    OrderItemHistoryJpaEntityFixtures.transitionHistory(
                            entity.getId(), "READY", "CONFIRMED");

            given(itemRepository.findById(orderItem.idValue())).willReturn(Optional.empty());
            given(mapper.toOrderItemEntity(orderItem, orderItem.idValue())).willReturn(entity);
            given(mapper.toOrderItemHistoryEntities(orderItem.histories()))
                    .willReturn(List.of(historyEntity));

            // when
            commandAdapter.persistAll(List.of(orderItem));

            // then
            then(itemRepository).should().save(entity);
            then(itemHistoryRepository).should().saveAll(List.of(historyEntity));
        }

        @Test
        @DisplayName("빈 OrderItem 목록을 전달하면 어떤 Repository도 호출되지 않습니다")
        void persistAll_WithEmptyList_CallsNoRepository() {
            // when
            commandAdapter.persistAll(List.of());

            // then
            then(itemRepository).shouldHaveNoInteractions();
            then(itemHistoryRepository).shouldHaveNoInteractions();
            then(mapper).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistAll - 기존 Entity 업데이트 시나리오 테스트")
    class PersistAllUpdateTest {

        @Test
        @DisplayName("OrderItem이 이미 존재할 때 상태를 업데이트합니다")
        void persistAll_WithExistingEntity_UpdatesStatus() {
            // given
            String orderId = OrderJpaEntityFixtures.DEFAULT_ID;
            OrderItem orderItem = OrderFixtures.confirmedOrderItem();
            OrderItemJpaEntity entity = OrderItemJpaEntityFixtures.defaultItem(orderId);
            OrderItemHistoryJpaEntity historyEntity =
                    OrderItemHistoryJpaEntityFixtures.transitionHistory(
                            entity.getId(), "READY", "CONFIRMED");

            given(itemRepository.findById(orderItem.idValue())).willReturn(Optional.of(entity));
            given(mapper.toOrderItemHistoryEntities(orderItem.histories()))
                    .willReturn(List.of(historyEntity));

            // when
            commandAdapter.persistAll(List.of(orderItem));

            // then
            then(itemRepository).should().findById(orderItem.idValue());
            then(itemHistoryRepository).should().saveAll(List.of(historyEntity));
        }

        @Test
        @DisplayName("Entity가 존재하지 않을 때 새로 저장합니다")
        void persistAll_WithNonExistingEntity_SavesNew() {
            // given
            OrderItem orderItem = OrderFixtures.confirmedOrderItem();
            OrderItemJpaEntity newEntity =
                    OrderItemJpaEntityFixtures.defaultItem(OrderJpaEntityFixtures.DEFAULT_ID);
            OrderItemHistoryJpaEntity historyEntity =
                    OrderItemHistoryJpaEntityFixtures.creationHistory(orderItem.idValue());

            given(itemRepository.findById(orderItem.idValue())).willReturn(Optional.empty());
            given(mapper.toOrderItemEntity(orderItem, orderItem.idValue())).willReturn(newEntity);
            given(mapper.toOrderItemHistoryEntities(orderItem.histories()))
                    .willReturn(List.of(historyEntity));

            // when
            commandAdapter.persistAll(List.of(orderItem));

            // then
            then(itemRepository).should().save(newEntity);
            then(itemHistoryRepository).should().saveAll(List.of(historyEntity));
        }

        @Test
        @DisplayName("빈 목록을 전달하면 어떤 Repository도 호출되지 않습니다")
        void persistAll_WithEmptyList_CallsNoRepository() {
            // when
            commandAdapter.persistAll(List.of());

            // then
            then(itemRepository).shouldHaveNoInteractions();
            then(itemHistoryRepository).shouldHaveNoInteractions();
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
