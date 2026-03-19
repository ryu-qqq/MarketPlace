package com.ryuqq.marketplace.application.refund.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.RefundCommandFixtures;
import com.ryuqq.marketplace.application.refund.dto.command.CollectRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceFacade;
import com.ryuqq.marketplace.application.refund.validator.RefundBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
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
@DisplayName("CollectRefundBatchService 단위 테스트")
class CollectRefundBatchServiceTest {

    @InjectMocks private CollectRefundBatchService sut;

    @Mock private RefundBatchValidator validator;
    @Mock private RefundCommandFactory commandFactory;
    @Mock private RefundPersistenceFacade persistenceFacade;
    @Mock private RefundOutbox refundOutbox;

    @Nested
    @DisplayName("execute() - 환불 수거 완료 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 환불 목록의 수거를 완료하고 성공 결과를 반환한다")
        void execute_ValidRefunds_ReturnsSuccessResult() {
            // given
            CollectRefundBatchCommand command = RefundCommandFixtures.collectBatchCommand();
            RefundClaim claim = RefundFixtures.collectingRefundClaim();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();
            OutboxWithHistory bundle = new OutboxWithHistory(refundOutbox, history);

            given(validator.validateAndGet(command.refundClaimIds(), command.sellerId()))
                    .willReturn(List.of(claim));
            given(commandFactory.now()).willReturn(Instant.now());
            given(commandFactory.createCollectBundle(claim, command.processedBy()))
                    .willReturn(bundle);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(0);
            then(persistenceFacade)
                    .should()
                    .persistClaimsWithOutboxesAndHistories(
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList(),
                            org.mockito.ArgumentMatchers.anyList());
        }

        @Test
        @DisplayName("빈 환불 목록이면 저장을 수행하지 않는다")
        void execute_EmptyRefundList_NoPersistence() {
            // given
            CollectRefundBatchCommand command = RefundCommandFixtures.collectBatchCommand();

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
