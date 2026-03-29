package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.PaymentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.PaymentJpaRepository;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
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
 * OrderCommandAdapter 단위 테스트.
 *
 * <p>Payment 리팩토링 이후 IdGeneratorPort로 UUIDv7을 생성하여 toPaymentEntity(Order, String paymentId)에 전달하는
 * 흐름을 검증합니다. auto_increment 전환 후 saveAll 반환값으로 history에 ID를 전달하는 흐름도 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderCommandAdapter 단위 테스트")
class OrderCommandAdapterTest {

    @Mock private OrderJpaRepository orderRepository;

    @Mock private OrderItemJpaRepository itemRepository;

    @Mock private OrderItemHistoryJpaRepository itemHistoryRepository;

    @Mock private PaymentJpaRepository paymentRepository;

    @Mock private OrderJpaEntityMapper mapper;

    @Mock private IdGeneratorPort idGeneratorPort;

    @InjectMocks private OrderCommandAdapter commandAdapter;

    // ========================================================================
    // persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("IdGeneratorPort.generate()로 생성된 UUIDv7을 toPaymentEntity에 전달합니다")
        void persist_CallsIdGeneratorAndPassesIdToPaymentMapper() {
            // given
            Order order = OrderFixtures.newOrder();
            String generatedPaymentId = "01944b2a-bbbb-7fff-8888-000000000001";

            OrderJpaEntity orderEntity = OrderJpaEntityFixtures.orderedEntity(order.idValue());
            PaymentJpaEntity paymentEntity =
                    PaymentJpaEntityFixtures.completedEntity(generatedPaymentId, order.idValue());
            OrderItemJpaEntity itemEntity =
                    OrderItemJpaEntityFixtures.defaultItemWithId(order.idValue());

            given(idGeneratorPort.generate()).willReturn(generatedPaymentId);
            given(mapper.toOrderEntity(order)).willReturn(orderEntity);
            given(mapper.toPaymentEntity(order, generatedPaymentId)).willReturn(paymentEntity);
            given(mapper.toOrderItemEntities(anyList(), anyString(), any(), any()))
                    .willReturn(List.of(itemEntity));
            given(itemRepository.saveAll(List.of(itemEntity))).willReturn(List.of(itemEntity));

            // when
            commandAdapter.persist(order);

            // then
            then(idGeneratorPort).should(times(1)).generate();
            then(mapper).should().toPaymentEntity(order, generatedPaymentId);
        }

        @Test
        @DisplayName(
                "OrderJpaRepository, PaymentJpaRepository, ItemRepository, ItemHistoryRepository가"
                        + " 각각 호출됩니다")
        void persist_CallsAllRepositories() {
            // given
            Order order = OrderFixtures.newOrder();
            String generatedPaymentId = "01944b2a-bbbb-7fff-8888-000000000002";

            OrderJpaEntity orderEntity = OrderJpaEntityFixtures.orderedEntity(order.idValue());
            PaymentJpaEntity paymentEntity =
                    PaymentJpaEntityFixtures.completedEntity(generatedPaymentId, order.idValue());
            OrderItemJpaEntity itemEntity =
                    OrderItemJpaEntityFixtures.defaultItemWithId(order.idValue());

            given(idGeneratorPort.generate()).willReturn(generatedPaymentId);
            given(mapper.toOrderEntity(order)).willReturn(orderEntity);
            given(mapper.toPaymentEntity(order, generatedPaymentId)).willReturn(paymentEntity);
            given(mapper.toOrderItemEntities(anyList(), anyString(), any(), any()))
                    .willReturn(List.of(itemEntity));
            given(itemRepository.saveAll(List.of(itemEntity))).willReturn(List.of(itemEntity));

            // when
            commandAdapter.persist(order);

            // then
            then(orderRepository).should().save(orderEntity);
            then(paymentRepository).should().save(paymentEntity);
            then(itemRepository).should().saveAll(List.of(itemEntity));
            then(itemHistoryRepository).should().saveAll(anyList());
        }

        @Test
        @DisplayName("IdGenerator가 생성한 ID가 toPaymentEntity에 정확히 전달됩니다")
        void persist_PassesGeneratedIdToPaymentEntityMapper() {
            // given
            Order order = OrderFixtures.newOrder();
            String specificPaymentId = "01956f4a-cccc-7fff-9999-aabbccddeeff";

            OrderJpaEntity orderEntity = OrderJpaEntityFixtures.orderedEntity(order.idValue());
            PaymentJpaEntity paymentEntity =
                    PaymentJpaEntityFixtures.completedEntity(specificPaymentId, order.idValue());

            given(idGeneratorPort.generate()).willReturn(specificPaymentId);
            given(mapper.toOrderEntity(order)).willReturn(orderEntity);
            given(mapper.toPaymentEntity(eq(order), eq(specificPaymentId)))
                    .willReturn(paymentEntity);
            given(mapper.toOrderItemEntities(anyList(), anyString(), any(), any()))
                    .willReturn(List.of());
            given(itemRepository.saveAll(anyList())).willReturn(List.of());

            // when
            commandAdapter.persist(order);

            // then
            then(mapper).should().toPaymentEntity(order, specificPaymentId);
        }

        @Test
        @DisplayName("toOrderEntity Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsOrderMapperOnce() {
            // given
            Order order = OrderFixtures.newOrder();
            String generatedId = "01944b2a-bbbb-7fff-8888-000000000003";

            given(idGeneratorPort.generate()).willReturn(generatedId);
            given(mapper.toOrderEntity(order))
                    .willReturn(OrderJpaEntityFixtures.orderedEntity(order.idValue()));
            given(mapper.toPaymentEntity(order, generatedId))
                    .willReturn(
                            PaymentJpaEntityFixtures.completedEntity(generatedId, order.idValue()));
            given(mapper.toOrderItemEntities(anyList(), anyString(), any(), any()))
                    .willReturn(List.of());
            given(itemRepository.saveAll(anyList())).willReturn(List.of());

            // when
            commandAdapter.persist(order);

            // then
            then(mapper).should(times(1)).toOrderEntity(order);
        }
    }

    // ========================================================================
    // persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 Order를 저장할 때 Order 수만큼 IdGenerator가 호출됩니다")
        void persistAll_WithMultipleOrders_CallsIdGeneratorForEach() {
            // given
            Order order1 = OrderFixtures.newOrder();
            Order order2 = OrderFixtures.reconstitutedOrder();
            List<Order> orders = List.of(order1, order2);

            String paymentId1 = "01944b2a-bbbb-7fff-8888-000000000010";
            String paymentId2 = "01944b2a-bbbb-7fff-8888-000000000011";

            given(idGeneratorPort.generate()).willReturn(paymentId1, paymentId2);
            given(mapper.toOrderEntity(any(Order.class)))
                    .willAnswer(
                            inv ->
                                    OrderJpaEntityFixtures.orderedEntity(
                                            ((Order) inv.getArgument(0)).idValue()));
            given(mapper.toPaymentEntity(any(Order.class), anyString()))
                    .willAnswer(
                            inv ->
                                    PaymentJpaEntityFixtures.completedEntity(
                                            inv.getArgument(1),
                                            ((Order) inv.getArgument(0)).idValue()));
            given(mapper.toOrderItemEntities(anyList(), anyString(), any(), any()))
                    .willReturn(List.of());
            given(itemRepository.saveAll(anyList())).willReturn(List.of());

            // when
            commandAdapter.persistAll(orders);

            // then
            then(idGeneratorPort).should(times(2)).generate();
        }

        @Test
        @DisplayName("빈 리스트를 전달하면 어떤 Repository도 호출되지 않습니다")
        void persistAll_WithEmptyList_CallsNoRepository() {
            // when
            commandAdapter.persistAll(List.of());

            // then
            then(orderRepository).shouldHaveNoInteractions();
            then(paymentRepository).shouldHaveNoInteractions();
            then(itemRepository).shouldHaveNoInteractions();
            then(itemHistoryRepository).shouldHaveNoInteractions();
            then(idGeneratorPort).shouldHaveNoInteractions();
        }
    }
}
