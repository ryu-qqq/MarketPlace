package com.ryuqq.marketplace.adapter.out.persistence.composite.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderCompositeProjectionDtoFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.PaymentProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.ProductOrderDetailProjectionDto;
import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrderCompositeMapper 단위 테스트.
 *
 * <p>Payment 리팩토링 이후 paymentId(String), paymentNumber 필드 변경 검증. 검증 포인트: -
 * toPaymentResult(PaymentProjectionDto): paymentId(String), paymentNumber 매핑 -
 * toDetailData(ProductOrderDetailProjectionDto): PaymentResult paymentId/paymentNumber 확인
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("OrderCompositeMapper 단위 테스트")
class OrderCompositeMapperTest {

    private final OrderCompositeMapper sut = new OrderCompositeMapper();

    // ========================================================================
    // toPaymentResult(PaymentProjectionDto) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toPaymentResult(PaymentProjectionDto) 메서드 테스트")
    class ToPaymentResultFromProjectionTest {

        @Test
        @DisplayName("paymentId(String)가 PaymentResult.paymentId 필드에 그대로 매핑됩니다")
        void toPaymentResult_WithStringPaymentId_MapsCorrectly() {
            // given
            PaymentProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.completedPaymentProjection();

            // when
            PaymentResult result = sut.toPaymentResult(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.paymentId())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_ID);
        }

        @Test
        @DisplayName("paymentNumber가 PaymentResult.paymentNumber 필드에 매핑됩니다")
        void toPaymentResult_WithPaymentNumber_MapsCorrectly() {
            // given
            PaymentProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.completedPaymentProjection();

            // when
            PaymentResult result = sut.toPaymentResult(dto);

            // then
            assertThat(result.paymentNumber())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_NUMBER);
        }

        @Test
        @DisplayName("paymentStatus, paymentMethod, paymentAmount가 올바르게 매핑됩니다")
        void toPaymentResult_WithAllFields_MapsAllFieldsCorrectly() {
            // given
            PaymentProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.completedPaymentProjection();

            // when
            PaymentResult result = sut.toPaymentResult(dto);

            // then
            assertThat(result.paymentStatus())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_STATUS);
            assertThat(result.paymentMethod())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_METHOD);
            assertThat(result.paymentAgencyId())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_AGENCY_ID);
            assertThat(result.paymentAmount())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_AMOUNT);
        }

        @Test
        @DisplayName("특정 paymentId와 paymentNumber를 가진 DTO가 올바르게 변환됩니다")
        void toPaymentResult_WithSpecificPaymentIdAndNumber_MapsCorrectly() {
            // given
            String specificPaymentId = "01956f4a-cccc-7fff-9999-000000000001";
            String specificPaymentNumber = "PAY-20261001-9999";
            PaymentProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.paymentProjection(
                            specificPaymentId,
                            specificPaymentNumber,
                            OrderCompositeProjectionDtoFixtures.DEFAULT_ORDER_ID);

            // when
            PaymentResult result = sut.toPaymentResult(dto);

            // then
            assertThat(result.paymentId()).isEqualTo(specificPaymentId);
            assertThat(result.paymentNumber()).isEqualTo(specificPaymentNumber);
        }

        @Test
        @DisplayName("canceledAt이 있는 취소 DTO를 변환하면 canceledAt이 매핑됩니다")
        void toPaymentResult_WithCanceledPayment_MapsCanceledAtCorrectly() {
            // given
            String paymentId = "01944b2a-aaaa-7fff-8888-000000000099";
            PaymentProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.canceledPaymentProjection(paymentId);

            // when
            PaymentResult result = sut.toPaymentResult(dto);

            // then
            assertThat(result.canceledAt()).isNotNull();
            assertThat(result.paymentId()).isEqualTo(paymentId);
        }

        @Test
        @DisplayName("null DTO를 전달하면 null을 반환합니다")
        void toPaymentResult_WithNullDto_ReturnsNull() {
            // when
            PaymentResult result = sut.toPaymentResult((PaymentProjectionDto) null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // toDetailData(ProductOrderDetailProjectionDto) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDetailData 메서드 테스트")
    class ToDetailDataTest {

        @Test
        @DisplayName("결제 정보가 있을 때 PaymentResult.paymentId(String)가 올바르게 매핑됩니다")
        void toDetailData_WithPaymentInfo_MapsPaymentIdAsString() {
            // given
            ProductOrderDetailProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.defaultDetailProjection();

            // when
            ProductOrderDetailData data = sut.toDetailData(dto);

            // then
            assertThat(data.payment()).isNotNull();
            assertThat(data.payment().paymentId())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_ID);
            assertThat(data.payment().paymentId()).isInstanceOf(String.class);
        }

        @Test
        @DisplayName("결제 정보가 있을 때 PaymentResult.paymentNumber가 올바르게 매핑됩니다")
        void toDetailData_WithPaymentInfo_MapsPaymentNumberCorrectly() {
            // given
            ProductOrderDetailProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.defaultDetailProjection();

            // when
            ProductOrderDetailData data = sut.toDetailData(dto);

            // then
            assertThat(data.payment().paymentNumber())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_NUMBER);
        }

        @Test
        @DisplayName("결제 정보가 있을 때 paymentStatus, paymentMethod, paymentAmount가 매핑됩니다")
        void toDetailData_WithPaymentInfo_MapsPaymentFieldsCorrectly() {
            // given
            ProductOrderDetailProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.defaultDetailProjection();

            // when
            ProductOrderDetailData data = sut.toDetailData(dto);

            // then
            PaymentResult payment = data.payment();
            assertThat(payment.paymentStatus())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_STATUS);
            assertThat(payment.paymentMethod())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_METHOD);
            assertThat(payment.paymentAmount())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_PAYMENT_AMOUNT);
        }

        @Test
        @DisplayName("paymentId가 null이면 PaymentResult도 null입니다")
        void toDetailData_WithNullPaymentId_ReturnsNullPaymentResult() {
            // given
            ProductOrderDetailProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.detailProjectionWithoutPayment();

            // when
            ProductOrderDetailData data = sut.toDetailData(dto);

            // then
            assertThat(data.payment()).isNull();
        }

        @Test
        @DisplayName("OrderItemResult와 OrderListResult도 함께 포함됩니다")
        void toDetailData_WithFullProjection_ContainsItemAndOrderResult() {
            // given
            ProductOrderDetailProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.defaultDetailProjection();

            // when
            ProductOrderDetailData data = sut.toDetailData(dto);

            // then
            assertThat(data.item()).isNotNull();
            assertThat(data.item().orderId())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_ORDER_ID);
            assertThat(data.order()).isNotNull();
            assertThat(data.order().orderId())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_ORDER_ID);
        }

        @Test
        @DisplayName("OrderItemResult의 orderItemId가 올바르게 매핑됩니다")
        void toDetailData_WithOrderItemId_MapsOrderItemIdCorrectly() {
            // given
            ProductOrderDetailProjectionDto dto =
                    OrderCompositeProjectionDtoFixtures.defaultDetailProjection();

            // when
            ProductOrderDetailData data = sut.toDetailData(dto);

            // then
            assertThat(data.item().orderItemId())
                    .isEqualTo(OrderCompositeProjectionDtoFixtures.DEFAULT_ORDER_ITEM_ID);
        }
    }
}
