package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemHistoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.PaymentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderQueryDslRepository;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.id.OrderId;
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
 * OrderQueryAdapter 단위 테스트.
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
@DisplayName("OrderQueryAdapter 단위 테스트")
class OrderQueryAdapterTest {

    @Mock private OrderQueryDslRepository queryDslRepository;

    @Mock private OrderJpaEntityMapper mapper;

    @InjectMocks private OrderQueryAdapter queryAdapter;

    // ========================================================================
    // findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Order 도메인을 반환합니다")
        void findById_WithExistingId_ReturnsOrder() {
            // given
            String orderId = OrderJpaEntityFixtures.DEFAULT_ID;
            OrderId id = OrderId.of(orderId);
            OrderJpaEntity orderEntity = OrderJpaEntityFixtures.orderedEntity(orderId);
            PaymentJpaEntity paymentEntity =
                    PaymentJpaEntityFixtures.completedEntity(
                            PaymentJpaEntityFixtures.DEFAULT_ID, orderId);
            OrderItemJpaEntity itemEntity = OrderItemJpaEntityFixtures.defaultItemWithId(orderId);
            Order domain = OrderFixtures.reconstitutedOrder();

            given(queryDslRepository.findById(orderId)).willReturn(Optional.of(orderEntity));
            given(queryDslRepository.findPaymentByOrderId(orderId))
                    .willReturn(Optional.of(paymentEntity));
            given(queryDslRepository.findItemsByOrderId(orderId)).willReturn(List.of(itemEntity));
            given(queryDslRepository.findItemHistoriesByOrderItemIds(List.of(itemEntity.getId())))
                    .willReturn(List.of());
            given(mapper.toDomain(orderEntity, paymentEntity, List.of(itemEntity), List.of()))
                    .willReturn(domain);

            // when
            Optional<Order> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            String orderId = "01944b2a-9999-7fff-8888-000000000000";
            OrderId id = OrderId.of(orderId);

            given(queryDslRepository.findById(orderId)).willReturn(Optional.empty());

            // when
            Optional<Order> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findById(orderId);
        }

        @Test
        @DisplayName("OrderItem에 이력이 있을 때 이력이 포함된 Order를 반환합니다")
        void findById_WithItemHistories_ReturnsDomainWithHistories() {
            // given
            String orderId = OrderJpaEntityFixtures.DEFAULT_ID;
            OrderId id = OrderId.of(orderId);
            OrderJpaEntity orderEntity = OrderJpaEntityFixtures.orderedEntity(orderId);
            PaymentJpaEntity paymentEntity =
                    PaymentJpaEntityFixtures.completedEntity(
                            PaymentJpaEntityFixtures.DEFAULT_ID, orderId);
            OrderItemJpaEntity itemEntity = OrderItemJpaEntityFixtures.defaultItemWithId(orderId);
            OrderItemHistoryJpaEntity historyEntity =
                    OrderItemHistoryJpaEntityFixtures.creationHistory(itemEntity.getId());
            Order domain = OrderFixtures.reconstitutedOrder();

            given(queryDslRepository.findById(orderId)).willReturn(Optional.of(orderEntity));
            given(queryDslRepository.findPaymentByOrderId(orderId))
                    .willReturn(Optional.of(paymentEntity));
            given(queryDslRepository.findItemsByOrderId(orderId)).willReturn(List.of(itemEntity));
            given(queryDslRepository.findItemHistoriesByOrderItemIds(List.of(itemEntity.getId())))
                    .willReturn(List.of(historyEntity));
            given(
                            mapper.toDomain(
                                    orderEntity,
                                    paymentEntity,
                                    List.of(itemEntity),
                                    List.of(historyEntity)))
                    .willReturn(domain);

            // when
            Optional<Order> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            then(queryDslRepository)
                    .should()
                    .findItemHistoriesByOrderItemIds(List.of(itemEntity.getId()));
        }
    }

    // ========================================================================
    // findByOrderNumber 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderNumber 메서드 테스트")
    class FindByOrderNumberTest {

        @Test
        @DisplayName("존재하는 orderNumber로 조회 시 Order 도메인을 반환합니다")
        void findByOrderNumber_WithExistingOrderNumber_ReturnsOrder() {
            // given
            String orderNumber = OrderJpaEntityFixtures.DEFAULT_ORDER_NUMBER;
            String orderId = OrderJpaEntityFixtures.DEFAULT_ID;
            OrderJpaEntity orderEntity = OrderJpaEntityFixtures.orderedEntity(orderId);
            PaymentJpaEntity paymentEntity =
                    PaymentJpaEntityFixtures.completedEntity(
                            PaymentJpaEntityFixtures.DEFAULT_ID, orderId);
            Order domain = OrderFixtures.reconstitutedOrder();

            given(queryDslRepository.findByOrderNumber(orderNumber))
                    .willReturn(Optional.of(orderEntity));
            given(queryDslRepository.findPaymentByOrderId(orderId))
                    .willReturn(Optional.of(paymentEntity));
            given(queryDslRepository.findItemsByOrderId(orderId)).willReturn(List.of());
            given(queryDslRepository.findItemHistoriesByOrderItemIds(List.of()))
                    .willReturn(List.of());
            given(mapper.toDomain(orderEntity, paymentEntity, List.of(), List.of()))
                    .willReturn(domain);

            // when
            Optional<Order> result = queryAdapter.findByOrderNumber(orderNumber);

            // then
            assertThat(result).isPresent();
            then(queryDslRepository).should().findByOrderNumber(orderNumber);
        }

        @Test
        @DisplayName("존재하지 않는 orderNumber로 조회 시 빈 Optional을 반환합니다")
        void findByOrderNumber_WithNonExistingOrderNumber_ReturnsEmpty() {
            // given
            String orderNumber = "ORD-99999999-9999";

            given(queryDslRepository.findByOrderNumber(orderNumber)).willReturn(Optional.empty());

            // when
            Optional<Order> result = queryAdapter.findByOrderNumber(orderNumber);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // existsByExternalOrderNo 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByExternalOrderNo 메서드 테스트")
    class ExistsByExternalOrderNoTest {

        @Test
        @DisplayName("존재하는 외부 주문번호로 조회 시 true를 반환합니다")
        void existsByExternalOrderNo_WithExistingOrderNo_ReturnsTrue() {
            // given
            long salesChannelId = OrderJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID;
            String externalOrderNo = OrderJpaEntityFixtures.DEFAULT_EXTERNAL_ORDER_NO;

            given(queryDslRepository.existsByExternalOrderNo(salesChannelId, externalOrderNo))
                    .willReturn(true);

            // when
            boolean result = queryAdapter.existsByExternalOrderNo(salesChannelId, externalOrderNo);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 외부 주문번호로 조회 시 false를 반환합니다")
        void existsByExternalOrderNo_WithNonExistingOrderNo_ReturnsFalse() {
            // given
            long salesChannelId = 1L;
            String externalOrderNo = "EXT-NOT-EXIST-000";

            given(queryDslRepository.existsByExternalOrderNo(salesChannelId, externalOrderNo))
                    .willReturn(false);

            // when
            boolean result = queryAdapter.existsByExternalOrderNo(salesChannelId, externalOrderNo);

            // then
            assertThat(result).isFalse();
        }
    }
}
