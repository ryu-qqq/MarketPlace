package com.ryuqq.marketplace.application.order.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.order.port.out.command.OrderItemCommandPort;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;
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
@DisplayName("OrderItemCommandManager 단위 테스트")
class OrderItemCommandManagerTest {

    @InjectMocks private OrderItemCommandManager sut;

    @Mock private OrderItemCommandPort commandPort;

    @Nested
    @DisplayName("persistAll() - 주문상품 저장/갱신")
    class PersistAllTest {

        @Test
        @DisplayName("주문상품 목록을 persistAll 포트로 위임한다")
        void persistAll_ValidItems_DelegatesToPort() {
            // given
            List<OrderItem> orderItems = List.of(OrderFixtures.defaultOrderItem());

            // when
            sut.persistAll(orderItems);

            // then
            then(commandPort).should().persistAll(orderItems);
        }

        @Test
        @DisplayName("빈 목록도 persistAll 포트로 위임한다")
        void persistAll_EmptyList_DelegatesToPort() {
            // given
            List<OrderItem> emptyItems = List.of();

            // when
            sut.persistAll(emptyItems);

            // then
            then(commandPort).should().persistAll(emptyItems);
        }
    }

    @Nested
    @DisplayName("persistAll() - 기존 주문상품 상태 갱신 시나리오")
    class PersistAllUpdateTest {

        @Test
        @DisplayName("재구성된 주문상품 목록을 persistAll 포트로 위임한다")
        void persistAll_ReconstitutedItems_DelegatesToPort() {
            // given
            List<OrderItem> orderItems = List.of(OrderFixtures.reconstitutedOrderItem());

            // when
            sut.persistAll(orderItems);

            // then
            then(commandPort).should().persistAll(orderItems);
        }

        @Test
        @DisplayName("빈 목록도 persistAll 포트로 위임한다")
        void persistAll_EmptyReconstitutedList_DelegatesToPort() {
            // given
            List<OrderItem> emptyItems = List.of();

            // when
            sut.persistAll(emptyItems);

            // then
            then(commandPort).should().persistAll(emptyItems);
        }
    }
}
