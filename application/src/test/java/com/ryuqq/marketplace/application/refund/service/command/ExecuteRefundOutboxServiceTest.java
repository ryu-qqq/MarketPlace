package com.ryuqq.marketplace.application.refund.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;

import com.ryuqq.marketplace.application.claimsync.manager.ExternalOrderItemMappingReadManager;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.refund.RefundCommandFixtures;
import com.ryuqq.marketplace.application.refund.dto.command.ExecuteRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxReadManager;
import com.ryuqq.marketplace.application.refund.port.out.client.RefundClaimSyncStrategy;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ExecuteRefundOutboxService 단위 테스트")
class ExecuteRefundOutboxServiceTest {

    @InjectMocks private ExecuteRefundOutboxService sut;

    @Mock private RefundOutboxReadManager outboxReadManager;
    @Mock private RefundOutboxCommandManager outboxCommandManager;
    @Mock private RefundClaimSyncStrategy claimSyncStrategy;
    @Mock private RefundCommandFactory commandFactory;
    @Mock private ExternalOrderItemMappingReadManager mappingReadManager;
    @Mock private ShopReadManager shopReadManager;

    @Nested
    @DisplayName("execute() - 환불 Outbox 실행")
    class ExecuteTest {

        @Test
        @DisplayName("외부 API 호출 성공 시 Outbox를 COMPLETED로 전환한다")
        void execute_SyncSuccess_CompletesOutbox() {
            // given
            Long outboxId = 1L;
            ExecuteRefundOutboxCommand command =
                    RefundCommandFixtures.executeRefundOutboxCommand(outboxId, "REQUEST");
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            RefundOutbox freshOutbox = Mockito.mock(RefundOutbox.class);
            ExternalOrderItemMapping mapping = Mockito.mock(ExternalOrderItemMapping.class);
            Shop shop = Mockito.mock(Shop.class);
            Instant now = Instant.now();

            when(outboxReadManager.getById(outboxId)).thenReturn(outbox).thenReturn(freshOutbox);
            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.orderItemIdValue()).willReturn(1001L);
            given(mappingReadManager.findByOrderItemId(1001L)).willReturn(mapping);
            given(mapping.salesChannelId()).willReturn(100L);
            given(shopReadManager.findActiveBySalesChannelId(100L)).willReturn(List.of(shop));
            given(claimSyncStrategy.execute(outbox, shop)).willReturn(OutboxSyncResult.success());
            given(commandFactory.createOutboxChangeContext(outboxId))
                    .willReturn(new StatusChangeContext<>(outboxId, now));

            // when
            sut.execute(command);

            // then
            then(freshOutbox).should().complete(Mockito.any());
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 API 호출 실패 시 Outbox에 실패를 기록한다")
        void execute_SyncFailure_RecordsFailure() {
            // given
            Long outboxId = 2L;
            ExecuteRefundOutboxCommand command =
                    RefundCommandFixtures.executeRefundOutboxCommand(outboxId, "REQUEST");
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            RefundOutbox freshOutbox = Mockito.mock(RefundOutbox.class);
            OutboxSyncResult failureResult = OutboxSyncResult.failure(true, "외부 API 오류");
            ExternalOrderItemMapping mapping = Mockito.mock(ExternalOrderItemMapping.class);
            Shop shop = Mockito.mock(Shop.class);
            Instant now = Instant.now();

            when(outboxReadManager.getById(outboxId)).thenReturn(outbox).thenReturn(freshOutbox);
            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.orderItemIdValue()).willReturn(1002L);
            given(mappingReadManager.findByOrderItemId(1002L)).willReturn(mapping);
            given(mapping.salesChannelId()).willReturn(100L);
            given(shopReadManager.findActiveBySalesChannelId(100L)).willReturn(List.of(shop));
            given(claimSyncStrategy.execute(outbox, shop)).willReturn(failureResult);
            given(commandFactory.createOutboxChangeContext(outboxId))
                    .willReturn(new StatusChangeContext<>(outboxId, now));

            // when
            sut.execute(command);

            // then
            then(freshOutbox)
                    .should()
                    .recordFailure(Mockito.eq(true), Mockito.eq("외부 API 오류"), Mockito.any());
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 서비스 일시 장애 시 Outbox를 PENDING으로 복구한다")
        void execute_ExternalServiceUnavailable_DefersRetry() {
            // given
            Long outboxId = 3L;
            ExecuteRefundOutboxCommand command =
                    RefundCommandFixtures.executeRefundOutboxCommand(outboxId, "REQUEST");
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            RefundOutbox freshOutbox = Mockito.mock(RefundOutbox.class);
            ExternalOrderItemMapping mapping = Mockito.mock(ExternalOrderItemMapping.class);
            Shop shop = Mockito.mock(Shop.class);
            Instant now = Instant.now();

            when(outboxReadManager.getById(outboxId)).thenReturn(outbox).thenReturn(freshOutbox);
            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.orderItemIdValue()).willReturn(1003L);
            given(mappingReadManager.findByOrderItemId(1003L)).willReturn(mapping);
            given(mapping.salesChannelId()).willReturn(100L);
            given(shopReadManager.findActiveBySalesChannelId(100L)).willReturn(List.of(shop));
            given(claimSyncStrategy.execute(outbox, shop))
                    .willThrow(new ExternalServiceUnavailableException("서비스 일시 장애"));
            given(commandFactory.createOutboxChangeContext(outboxId))
                    .willReturn(new StatusChangeContext<>(outboxId, now));

            // when
            sut.execute(command);

            // then
            then(freshOutbox).should().recoverFromTimeout(Mockito.any());
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("예기치 않은 예외 발생 시 재시도 가능한 실패로 기록한다")
        void execute_UnexpectedException_RecordsRetryableFailure() {
            // given
            Long outboxId = 4L;
            ExecuteRefundOutboxCommand command =
                    RefundCommandFixtures.executeRefundOutboxCommand(outboxId, "REQUEST");
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            RefundOutbox freshOutbox = Mockito.mock(RefundOutbox.class);
            ExternalOrderItemMapping mapping = Mockito.mock(ExternalOrderItemMapping.class);
            Shop shop = Mockito.mock(Shop.class);
            Instant now = Instant.now();

            when(outboxReadManager.getById(outboxId)).thenReturn(outbox).thenReturn(freshOutbox);
            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.orderItemIdValue()).willReturn(1004L);
            given(mappingReadManager.findByOrderItemId(1004L)).willReturn(mapping);
            given(mapping.salesChannelId()).willReturn(100L);
            given(shopReadManager.findActiveBySalesChannelId(100L)).willReturn(List.of(shop));
            willThrow(new RuntimeException("예기치 않은 오류"))
                    .given(claimSyncStrategy)
                    .execute(outbox, shop);
            given(commandFactory.createOutboxChangeContext(outboxId))
                    .willReturn(new StatusChangeContext<>(outboxId, now));

            // when
            sut.execute(command);

            // then
            then(freshOutbox)
                    .should()
                    .recordFailure(Mockito.eq(true), Mockito.contains("실행 중 예외"), Mockito.any());
            then(outboxCommandManager).should().persist(freshOutbox);
        }
    }
}
