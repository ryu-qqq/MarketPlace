package com.ryuqq.marketplace.application.order.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.OrderCommandFixtures;
import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import java.time.Instant;
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
@DisplayName("OrderCommandFactory 단위 테스트")
class OrderCommandFactoryTest {

    @InjectMocks private OrderCommandFactory sut;

    @Mock private TimeProvider timeProvider;
    @Mock private IdGeneratorPort idGeneratorPort;

    @Nested
    @DisplayName("createOrder() - Order 도메인 객체 생성")
    class CreateOrderTest {

        @Test
        @DisplayName("CreateOrderCommand로 Order 도메인 객체를 생성한다")
        void createOrder_ValidCommand_ReturnsOrder() {
            // given
            CreateOrderCommand command = OrderCommandFixtures.createOrderCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);
            given(idGeneratorPort.generate()).willReturn("01900000-0000-7000-8000-000000000001");

            // when
            Order result = sut.createOrder(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.orderNumber()).isNotNull();
            then(timeProvider).should().now();
            then(idGeneratorPort).should(times(2)).generate();
        }

        @Test
        @DisplayName("PaymentNumber가 PAY- 형식으로 생성된다")
        void createOrder_GeneratesPaymentNumberWithCorrectFormat() {
            // given
            CreateOrderCommand command = OrderCommandFixtures.createOrderCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);
            given(idGeneratorPort.generate()).willReturn("01900000-0000-7000-8000-000000000001");

            // when
            Order result = sut.createOrder(command);

            // then
            String paymentNumber = result.paymentInfo().paymentNumber().value();
            assertThat(paymentNumber).isNotNull();
            assertThat(paymentNumber).matches("PAY-\\d{8}-\\d{4}");
        }

        @Test
        @DisplayName("OrderNumber가 ORD- 형식으로 생성된다")
        void createOrder_GeneratesOrderNumberWithCorrectFormat() {
            // given
            CreateOrderCommand command = OrderCommandFixtures.createOrderCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);
            given(idGeneratorPort.generate()).willReturn("01900000-0000-7000-8000-000000000001");

            // when
            Order result = sut.createOrder(command);

            // then
            String orderNumber = result.orderNumberValue();
            assertThat(orderNumber).isNotNull();
            assertThat(orderNumber).startsWith("ORD-");
        }

        @Test
        @DisplayName("Command의 구매자명이 Order에 반영된다")
        void createOrder_BuyerNameIsReflected() {
            // given
            CreateOrderCommand command = OrderCommandFixtures.createOrderCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);
            given(idGeneratorPort.generate()).willReturn("01900000-0000-7000-8000-000000000001");

            // when
            Order result = sut.createOrder(command);

            // then
            assertThat(result.buyerInfo().buyerName().value()).isEqualTo(command.buyerName());
        }

        @Test
        @DisplayName("Command의 아이템 수만큼 OrderItem이 생성된다")
        void createOrder_ItemsAreCreated() {
            // given
            CreateOrderCommand command = OrderCommandFixtures.createOrderCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);
            given(idGeneratorPort.generate()).willReturn("01900000-0000-7000-8000-000000000001");

            // when
            Order result = sut.createOrder(command);

            // then
            assertThat(result.items()).hasSize(command.items().size());
        }

        @Test
        @DisplayName("Command의 결제 수단이 PaymentInfo에 반영된다")
        void createOrder_PaymentMethodIsReflected() {
            // given
            CreateOrderCommand command = OrderCommandFixtures.createOrderCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);
            given(idGeneratorPort.generate()).willReturn("01900000-0000-7000-8000-000000000001");

            // when
            Order result = sut.createOrder(command);

            // then
            assertThat(result.paymentInfo().paymentMethod()).isEqualTo(command.paymentMethod());
        }

        @Test
        @DisplayName("선택적 필드(shopCode, paymentMethod)가 null인 Command로도 Order를 생성한다")
        void createOrder_WithoutOptionals_ReturnsOrder() {
            // given
            CreateOrderCommand command = OrderCommandFixtures.createOrderCommandWithoutOptionals();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);
            given(idGeneratorPort.generate()).willReturn("01900000-0000-7000-8000-000000000002");

            // when
            Order result = sut.createOrder(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.orderNumberValue()).startsWith("ORD-");
        }

        @Test
        @DisplayName("createOrder 호출마다 PaymentNumber가 정해진 형식으로 생성된다")
        void createOrder_EachCallGeneratesPaymentNumberWithFormat() {
            // given
            CreateOrderCommand command = OrderCommandFixtures.createOrderCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);
            given(idGeneratorPort.generate()).willReturn("01900000-0000-7000-8000-000000000001");

            // when
            Order result = sut.createOrder(command);

            // then
            assertThat(result.paymentInfo().paymentNumber().value()).matches("PAY-\\d{8}-\\d{4}");
        }
    }

    @Nested
    @DisplayName("createStatusContext() - 상태 전이 컨텍스트 생성")
    class CreateStatusContextTest {

        @Test
        @DisplayName("orderId 문자열로 StatusChangeContext를 생성한다")
        void createStatusContext_ValidOrderId_ReturnsContext() {
            // given
            String orderId = "01900000-0000-7000-8000-000000000001";
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            var result = sut.createStatusContext(orderId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(orderId);
            assertThat(result.changedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createStatusContextWithReason() - 사유 포함 상태 전이 컨텍스트 생성")
    class CreateStatusContextWithReasonTest {

        @Test
        @DisplayName("orderId, 사유, 변경자로 OrderStatusChangeWithReasonContext를 생성한다")
        void createStatusContextWithReason_ValidParams_ReturnsContext() {
            // given
            String orderId = "01900000-0000-7000-8000-000000000001";
            String reason = "재고 부족";
            String changedBy = "admin";
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            var result = sut.createStatusContextWithReason(orderId, reason, changedBy);

            // then
            assertThat(result).isNotNull();
            assertThat(result.orderId().value()).isEqualTo(orderId);
            assertThat(result.reason()).isEqualTo(reason);
            assertThat(result.changedBy()).isEqualTo(changedBy);
            assertThat(result.changedAt()).isEqualTo(now);
        }
    }
}
