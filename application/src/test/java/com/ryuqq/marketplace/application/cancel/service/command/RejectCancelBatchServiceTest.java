package com.ryuqq.marketplace.application.cancel.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.CancelCommandFixtures;
import com.ryuqq.marketplace.application.cancel.dto.command.RejectCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceBundle;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceFacade;
import com.ryuqq.marketplace.application.cancel.validator.CancelBatchValidator;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import java.time.Instant;
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
@DisplayName("RejectCancelBatchService 단위 테스트")
class RejectCancelBatchServiceTest {

    @InjectMocks private RejectCancelBatchService sut;

    @Mock private CancelBatchValidator validator;
    @Mock private CancelCommandFactory commandFactory;
    @Mock private CancelPersistenceFacade persistenceFacade;
    @Mock private CancelOutbox cancelOutbox;

    @Nested
    @DisplayName("execute() - 취소 거절 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 취소 목록을 거절하고 성공 결과를 반환한다")
        void execute_ValidCancels_ReturnsSuccessResult() {
            // given
            RejectCancelBatchCommand command = CancelCommandFixtures.rejectBatchCommand();
            Cancel cancel = CancelFixtures.requestedCancel();
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();
            Instant now = Instant.now();
            OutboxWithHistory bundle = new OutboxWithHistory(cancelOutbox, history, now);

            given(validator.validateForReject(command.cancelIds(), command.sellerId()))
                    .willReturn(List.of(cancel));
            given(commandFactory.createRejectBundle(cancel, command.processedBy()))
                    .willReturn(bundle);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(0);
            then(persistenceFacade).should().persistAll(any(CancelPersistenceBundle.class));
        }

        @Test
        @DisplayName("빈 취소 목록이면 저장을 수행하지 않는다")
        void execute_EmptyCancelList_NoPersistence() {
            // given
            RejectCancelBatchCommand command = CancelCommandFixtures.rejectBatchCommand();

            given(validator.validateForReject(command.cancelIds(), command.sellerId()))
                    .willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("여러 취소 중 일부 실패해도 성공한 건만 저장한다")
        void execute_PartialSuccess_PersistsOnlySuccessItems() {
            // given
            RejectCancelBatchCommand command =
                    CancelCommandFixtures.rejectBatchCommand(
                            List.of(
                                    "01900000-0000-7000-8000-000000000001",
                                    "01900000-0000-7000-8000-000000000002"));
            Cancel validCancel = CancelFixtures.requestedCancel();
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();
            Instant now = Instant.now();
            OutboxWithHistory bundle = new OutboxWithHistory(cancelOutbox, history, now);

            given(validator.validateForReject(command.cancelIds(), command.sellerId()))
                    .willReturn(List.of(validCancel));
            given(commandFactory.createRejectBundle(validCancel, command.processedBy()))
                    .willReturn(bundle);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
        }
    }
}
