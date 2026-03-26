package com.ryuqq.marketplace.application.claimhistory.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

import com.ryuqq.marketplace.application.claimhistory.ClaimHistoryCommandFixtures;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
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
@DisplayName("AddClaimHistoryMemoService 단위 테스트")
class AddClaimHistoryMemoServiceTest {

    @InjectMocks private AddClaimHistoryMemoService sut;

    @Mock private ClaimHistoryFactory historyFactory;
    @Mock private ClaimHistoryCommandManager historyCommandManager;

    @Nested
    @DisplayName("execute() - 수기 메모 등록")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 수기 메모를 등록하고 historyId를 반환한다")
        void execute_ValidCommand_ReturnsHistoryId() {
            // given
            AddClaimHistoryMemoCommand command = ClaimHistoryCommandFixtures.addMemoCommand();
            ClaimHistory history = ClaimHistoryFixtures.manualClaimHistory();

            given(
                            historyFactory.createManualMemo(
                                    command.claimType(),
                                    command.claimId(),
                                    command.message(),
                                    command.actorId(),
                                    command.actorName()))
                    .willReturn(history);
            doNothing().when(historyCommandManager).persist(history);

            // when
            String result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(history.idValue());
            then(historyFactory)
                    .should()
                    .createManualMemo(
                            command.claimType(),
                            command.claimId(),
                            command.message(),
                            command.actorId(),
                            command.actorName());
            then(historyCommandManager).should().persist(history);
        }

        @Test
        @DisplayName("교환 클레임에 대한 수기 메모를 등록한다")
        void execute_ExchangeClaimType_ReturnsHistoryId() {
            // given
            AddClaimHistoryMemoCommand command =
                    ClaimHistoryCommandFixtures.addMemoCommand(
                            com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType.EXCHANGE,
                            "exchange-claim-001");
            ClaimHistory history = ClaimHistoryFixtures.manualClaimHistory();

            given(
                            historyFactory.createManualMemo(
                                    command.claimType(),
                                    command.claimId(),
                                    command.message(),
                                    command.actorId(),
                                    command.actorName()))
                    .willReturn(history);
            doNothing().when(historyCommandManager).persist(history);

            // when
            String result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(history.idValue());
        }

        @Test
        @DisplayName("환불 클레임에 대한 수기 메모를 등록한다")
        void execute_RefundClaimType_ReturnsHistoryId() {
            // given
            AddClaimHistoryMemoCommand command =
                    ClaimHistoryCommandFixtures.addMemoCommand(
                            com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType.REFUND,
                            "refund-claim-001");
            ClaimHistory history = ClaimHistoryFixtures.manualClaimHistory();

            given(
                            historyFactory.createManualMemo(
                                    command.claimType(),
                                    command.claimId(),
                                    command.message(),
                                    command.actorId(),
                                    command.actorName()))
                    .willReturn(history);
            doNothing().when(historyCommandManager).persist(history);

            // when
            String result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
        }
    }
}
