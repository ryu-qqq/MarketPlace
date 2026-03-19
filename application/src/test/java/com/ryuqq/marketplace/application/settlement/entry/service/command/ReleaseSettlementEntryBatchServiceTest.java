package com.ryuqq.marketplace.application.settlement.entry.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.SettlementEntryCommandFixtures;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.ReleaseSettlementEntryBatchCommand;
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
@DisplayName("ReleaseSettlementEntryBatchService 단위 테스트")
class ReleaseSettlementEntryBatchServiceTest {

    @InjectMocks private ReleaseSettlementEntryBatchService sut;

    @Mock private SettlementEntryReadManager readManager;
    @Mock private SettlementEntryPersistenceFacade persistenceFacade;
    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("execute() - 정산 원장 일괄 보류 해제(PENDING) 처리")
    class ExecuteTest {

        @Test
        @DisplayName("entryIds에 해당하는 Entry들을 HOLD에서 PENDING으로 해제하고 저장한다")
        void execute_ValidCommand_ReleasesHoldEntriesAndPersistsAll() {
            // given
            ReleaseSettlementEntryBatchCommand command =
                    SettlementEntryCommandFixtures.releaseSettlementEntryBatchCommand();
            Instant now = Instant.now();
            SettlementEntry holdEntry1 = SettlementEntryFixtures.salesEntry();
            SettlementEntry holdEntry2 = SettlementEntryFixtures.salesEntry();
            holdEntry1.hold(now);
            holdEntry2.hold(now);
            List<SettlementEntry> entries = List.of(holdEntry1, holdEntry2);

            given(timeProvider.now()).willReturn(now);
            given(readManager.findByIdIn(command.entryIds())).willReturn(entries);

            // when
            sut.execute(command);

            // then
            then(readManager).should().findByIdIn(command.entryIds());
            then(persistenceFacade).should().persistAll(entries);
        }

        @Test
        @DisplayName("단건 entryId의 보류 해제도 정상 처리한다")
        void execute_SingleEntryId_ReleasesHoldAndPersists() {
            // given
            ReleaseSettlementEntryBatchCommand command =
                    SettlementEntryCommandFixtures.releaseSettlementEntryBatchCommand(
                            List.of("entry-hold-001"));
            Instant now = Instant.now();
            SettlementEntry holdEntry = SettlementEntryFixtures.salesEntry();
            holdEntry.hold(now);
            List<SettlementEntry> entries = List.of(holdEntry);

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
