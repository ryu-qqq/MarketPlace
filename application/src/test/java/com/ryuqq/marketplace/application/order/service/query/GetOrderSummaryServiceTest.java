package com.ryuqq.marketplace.application.order.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.order.OrderQueryFixtures;
import com.ryuqq.marketplace.application.order.assembler.OrderAssembler;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.util.Map;
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
@DisplayName("GetOrderSummaryService 단위 테스트")
class GetOrderSummaryServiceTest {

    @InjectMocks private GetOrderSummaryService sut;

    @Mock private OrderItemReadManager readManager;
    @Mock private OrderAssembler assembler;

    @Nested
    @DisplayName("execute() - 주문상품 상태별 요약 조회")
    class ExecuteTest {

        @Test
        @DisplayName("상태별 카운트를 조회하여 OrderSummaryResult를 반환한다")
        void execute_ReturnsOrderSummaryResult() {
            // given
            Map<OrderItemStatus, Long> statusCounts = OrderQueryFixtures.orderItemStatusCounts();
            OrderSummaryResult expectedResult = OrderQueryFixtures.orderSummaryResult();

            given(readManager.countByStatus()).willReturn(statusCounts);
            given(assembler.toSummaryResult(statusCounts)).willReturn(expectedResult);

            // when
            OrderSummaryResult result = sut.execute();

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(readManager).should().countByStatus();
            then(assembler).should().toSummaryResult(statusCounts);
        }

        @Test
        @DisplayName("빈 카운트 맵으로 호출하면 모든 값이 0인 요약을 반환한다")
        void execute_EmptyStatusCounts_ReturnsZeroSummary() {
            // given
            Map<OrderItemStatus, Long> emptyCounts = Map.of();
            OrderSummaryResult zeroResult = new OrderSummaryResult(0L, 0L, 0L, 0L, 0L);

            given(readManager.countByStatus()).willReturn(emptyCounts);
            given(assembler.toSummaryResult(emptyCounts)).willReturn(zeroResult);

            // when
            OrderSummaryResult result = sut.execute();

            // then
            assertThat(result.ready()).isZero();
            assertThat(result.confirmed()).isZero();
            assertThat(result.cancelled()).isZero();
            assertThat(result.returnRequested()).isZero();
            assertThat(result.returned()).isZero();
        }
    }
}
