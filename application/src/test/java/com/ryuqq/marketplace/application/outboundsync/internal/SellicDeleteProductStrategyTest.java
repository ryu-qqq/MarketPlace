package com.ryuqq.marketplace.application.outboundsync.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundsync.OutboundSyncExecutionContextFixtures;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.application.outboundsync.manager.SalesChannelProductClientManager;
import com.ryuqq.marketplace.domain.outboundproduct.OutboundProductFixtures;
import com.ryuqq.marketplace.domain.outboundproduct.exception.OutboundProductErrorCode;
import com.ryuqq.marketplace.domain.outboundproduct.exception.OutboundProductException;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
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
@DisplayName("SellicDeleteProductStrategy 단위 테스트")
class SellicDeleteProductStrategyTest {

    @InjectMocks private SellicDeleteProductStrategy sut;

    @Mock private OutboundProductReadManager outboundProductReadManager;
    @Mock private SalesChannelProductClientManager productClientManager;

    @Nested
    @DisplayName("supports() - 채널/SyncType 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("SELLIC + DELETE 조합은 true를 반환한다")
        void supports_SellicDelete_ReturnsTrue() {
            boolean result = sut.supports("SELLIC", SyncType.DELETE);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SELLIC + CREATE 조합은 false를 반환한다")
        void supports_SellicCreate_ReturnsFalse() {
            boolean result = sut.supports("SELLIC", SyncType.CREATE);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("SELLIC + UPDATE 조합은 false를 반환한다")
        void supports_SellicUpdate_ReturnsFalse() {
            boolean result = sut.supports("SELLIC", SyncType.UPDATE);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("NAVER + DELETE 조합은 false를 반환한다")
        void supports_NaverDelete_ReturnsFalse() {
            boolean result = sut.supports("NAVER", SyncType.DELETE);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("execute() - 상품 삭제 실행")
    class ExecuteTest {

        @Test
        @DisplayName("등록된 외부 상품이 있으면 삭제 성공 결과를 반환한다")
        void execute_RegisteredOutboundProduct_ReturnsSuccessResult() {
            // given
            OutboundSyncExecutionContext context =
                    OutboundSyncExecutionContextFixtures.sellicDeleteContext();
            OutboundProduct registeredProduct = OutboundProductFixtures.registeredProduct();

            given(outboundProductReadManager.getByProductGroupIdAndSalesChannelId(
                            context.productGroupId(), context.outbox().salesChannelIdValue()))
                    .willReturn(registeredProduct);
            willDoNothing()
                    .given(productClientManager)
                    .deleteProduct(anyString(), anyString(), any());

            // when
            OutboundSyncExecutionResult result = sut.execute(context);

            // then
            assertThat(result.success()).isTrue();
            assertThat(result.externalProductId())
                    .isEqualTo(OutboundProductFixtures.DEFAULT_EXTERNAL_PRODUCT_ID);
            then(productClientManager)
                    .should()
                    .deleteProduct(
                            eq("SELLIC"),
                            eq(OutboundProductFixtures.DEFAULT_EXTERNAL_PRODUCT_ID),
                            any());
        }

        @Test
        @DisplayName("외부 상품이 미등록 상태이면 재시도 불가 실패 결과를 반환한다")
        void execute_PendingOutboundProduct_ReturnsNonRetryableFailure() {
            // given
            OutboundSyncExecutionContext context =
                    OutboundSyncExecutionContextFixtures.sellicDeleteContext();
            OutboundProduct pendingProduct = OutboundProductFixtures.pendingProduct();

            given(outboundProductReadManager.getByProductGroupIdAndSalesChannelId(
                            context.productGroupId(), context.outbox().salesChannelIdValue()))
                    .willReturn(pendingProduct);

            // when
            OutboundSyncExecutionResult result = sut.execute(context);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isFalse();
            assertThat(result.errorMessage()).isEqualTo("외부 상품 미등록 상태");
            then(productClientManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("DomainException 발생 시 재시도 불가 실패 결과를 반환한다")
        void execute_DomainExceptionThrown_ReturnsNonRetryableFailure() {
            // given
            OutboundSyncExecutionContext context =
                    OutboundSyncExecutionContextFixtures.sellicDeleteContext();

            given(outboundProductReadManager.getByProductGroupIdAndSalesChannelId(
                            context.productGroupId(), context.outbox().salesChannelIdValue()))
                    .willThrow(
                            new OutboundProductException(
                                    OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND));

            // when
            OutboundSyncExecutionResult result = sut.execute(context);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isFalse();
            assertThat(result.errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("일반 Exception 발생 시 재시도 가능 실패 결과를 반환한다")
        void execute_GeneralExceptionThrown_ReturnsRetryableFailure() {
            // given
            OutboundSyncExecutionContext context =
                    OutboundSyncExecutionContextFixtures.sellicDeleteContext();

            given(outboundProductReadManager.getByProductGroupIdAndSalesChannelId(
                            context.productGroupId(), context.outbox().salesChannelIdValue()))
                    .willThrow(new RuntimeException("네트워크 오류"));

            // when
            OutboundSyncExecutionResult result = sut.execute(context);

            // then
            assertThat(result.success()).isFalse();
            assertThat(result.retryable()).isTrue();
            assertThat(result.errorMessage()).isEqualTo("네트워크 오류");
        }
    }
}
