package com.ryuqq.marketplace.application.refund.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.RefundCommandFixtures;
import com.ryuqq.marketplace.application.refund.dto.command.HoldRefundBatchCommand;
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
@DisplayName("HoldRefundBatchService 단위 테스트")
class HoldRefundBatchServiceTest {

    @InjectMocks private HoldRefundBatchService sut;

    @Mock private RefundBatchValidator validator;
    @Mock private RefundCommandFactory commandFactory;
    @Mock private RefundPersistenceFacade persistenceFacade;
    @Mock private RefundOutbox refundOutbox;

    @Nested
    @DisplayName("execute() - 환불 보류 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("보류 요청 시 환불을 보류 상태로 전환하고 성공 결과를 반환한다")
        void execute_HoldRequest_ReturnsSuccessResult() {
            // given
            HoldRefundBatchCommand command = RefundCommandFixtures.holdBatchCommand();
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();
            OutboxWithHistory bundle = new OutboxWithHistory(refundOutbox, history);

            given(validator.validateAndGet(command.refundClaimIds(), command.sellerId()))
                    .willReturn(List.of(claim));
            given(commandFactory.createHoldBundle(claim, command.memo(), command.processedBy()))
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
        @DisplayName("보류 해제 요청 시 환불을 보류 해제하고 성공 결과를 반환한다")
        void execute_ReleaseHoldRequest_ReturnsSuccessResult() {
            // given
            HoldRefundBatchCommand command = RefundCommandFixtures.releaseHoldBatchCommand();
            RefundClaim claim = RefundFixtures.holdRefundClaim();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();
            OutboxWithHistory bundle = new OutboxWithHistory(refundOutbox, history);

            given(validator.validateAndGet(command.refundClaimIds(), command.sellerId()))
                    .willReturn(List.of(claim));
            given(commandFactory.createReleaseHoldBundle(claim, command.processedBy()))
                    .willReturn(bundle);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.successCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("빈 환불 목록이면 저장을 수행하지 않는다")
        void execute_EmptyRefundList_NoPersistence() {
            // given
            HoldRefundBatchCommand command = RefundCommandFixtures.holdBatchCommand();

            given(validator.validateAndGet(command.refundClaimIds(), command.sellerId()))
                    .willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
        }
    }
}
