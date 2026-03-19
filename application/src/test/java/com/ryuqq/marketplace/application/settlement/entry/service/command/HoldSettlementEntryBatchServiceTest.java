package com.ryuqq.marketplace.application.settlement.entry.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.SettlementEntryCommandFixtures;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.HoldSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.internal.SettlementEntryPersistenceFacade;
import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryReadManager;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
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
@DisplayName("HoldSettlementEntryBatchService 단위 테스트")
class HoldSettlementEntryBatchServiceTest {

    @InjectMocks private HoldSettlementEntryBatchService sut;

    @Mock private SettlementEntryReadManager readManager;
    @Mock private SettlementEntryPersistenceFacade persistenceFacade;
    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("execute() - 정산 원장 일괄 보류(HOLD) 처리")
    class ExecuteTest {

        @Test
        @DisplayName("entryIds에 해당하는 Entry들을 HOLD로 변환하고 저장한다")
        void execute_ValidCommand_HoldsEntriesAndPersistsAll() {
            // given
            HoldSettlementEntryBatchCommand command =
                    SettlementEntryCommandFixtures.holdSettlementEntryBatchCommand();
            Instant now = Instant.now();
            List<SettlementEntry> entries =
                    List.of(
                            SettlementEntryFixtures.salesEntry(),
                            SettlementEntryFixtures.salesEntry());

            given(timeProvider.now()).willReturn(now);
            given(readManager.findByIdIn(command.entryIds())).willReturn(entries);

            // when
            sut.execute(command);

            // then
            then(readManager).should().findByIdIn(command.entryIds());
            then(persistenceFacade).should().persistAll(entries);
        }

        @Test
        @DisplayName("보류 사유를 포함한 커맨드로 Entry들을 HOLD 처리한다")
        void execute_CommandWithHoldReason_HoldsEntries() {
            // given
            HoldSettlementEntryBatchCommand command =
                    SettlementEntryCommandFixtures.holdSettlementEntryBatchCommand(
                            List.of("entry-001"), "계좌 오류로 인한 보류");
            Instant now = Instant.now();
            List<SettlementEntry> entries = List.of(SettlementEntryFixtures.salesEntry());

            given(timeProvider.now()).willReturn(now);
            given(readManager.findByIdIn(command.entryIds())).willReturn(entries);

            // when
            sut.execute(command);

            // then
            then(readManager).should().findByIdIn(command.entryIds());
            then(persistenceFacade).should().persistAll(entries);
        }
    }
}
