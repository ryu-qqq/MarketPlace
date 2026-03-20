package com.ryuqq.marketplace.application.refund.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.RefundCommandFixtures;
import com.ryuqq.marketplace.application.refund.dto.command.RejectRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceBundle;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceFacade;
import com.ryuqq.marketplace.application.refund.validator.RefundBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
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
@DisplayName("RejectRefundBatchService 단위 테스트")
class RejectRefundBatchServiceTest {

    @InjectMocks private RejectRefundBatchService sut;

    @Mock private RefundBatchValidator validator;
    @Mock private RefundCommandFactory commandFactory;
    @Mock private RefundPersistenceFacade persistenceFacade;
    @Mock private RefundOutbox refundOutbox;

    @Nested
    @DisplayName("execute() - 환불 거절 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 환불 목록을 거절하고 성공 결과를 반환한다")
        void execute_ValidRefunds_ReturnsSuccessResult() {
            // given
            RejectRefundBatchCommand command = RefundCommandFixtures.rejectBatchCommand();
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();
            OutboxWithHistory bundle = new OutboxWithHistory(refundOutbox, history);

            given(validator.validateAndGet(command.refundClaimIds(), command.sellerId()))
                    .willReturn(List.of(claim));
            given(commandFactory.createRejectBundle(claim, command.processedBy()))
                    .willReturn(bundle);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(0);
            then(persistenceFacade).should().persistAll(any(RefundPersistenceBundle.class));
        }

        @Test
        @DisplayName("빈 환불 목록이면 저장을 수행하지 않는다")
        void execute_EmptyRefundList_NoPersistence() {
            // given
            RejectRefundBatchCommand command = RefundCommandFixtures.rejectBatchCommand();

            given(validator.validateAndGet(command.refundClaimIds(), command.sellerId()))
                    .willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("여러 환불 중 일부 실패해도 성공한 건만 저장한다")
        void execute_PartialSuccess_PersistsOnlySuccessItems() {
            // given
            RejectRefundBatchCommand command =
                    RefundCommandFixtures.rejectBatchCommand(
                            List.of(
                                    "01900000-0000-7000-8000-000000000010",
                                    "01900000-0000-7000-8000-000000000011"));
            RefundClaim validClaim = RefundFixtures.requestedRefundClaim();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();
            OutboxWithHistory bundle = new OutboxWithHistory(refundOutbox, history);

            given(validator.validateAndGet(command.refundClaimIds(), command.sellerId()))
                    .willReturn(List.of(validClaim));
            given(commandFactory.createRejectBundle(validClaim, command.processedBy()))
                    .willReturn(bundle);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
        }
    }
}
