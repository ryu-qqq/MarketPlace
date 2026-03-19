package com.ryuqq.marketplace.application.settlement.entry.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
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
@DisplayName("ConfirmPendingEntriesService 단위 테스트")
class ConfirmPendingEntriesServiceTest {

    @InjectMocks private ConfirmPendingEntriesService sut;

    @Mock private SettlementEntryReadManager readManager;
    @Mock private SettlementEntryPersistenceFacade persistenceFacade;
    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("execute() - PENDING → CONFIRMED 배치 확정")
    class ExecuteTest {

        @Test
        @DisplayName("PENDING Entry가 있으면 CONFIRMED로 변환하고 건수를 반환한다")
        void execute_WithPendingEntries_ConfirmsAndReturnsCount() {
            // given
            int batchSize = 100;
            Instant now = Instant.now();
            List<SettlementEntry> entries =
                    List.of(
                            SettlementEntryFixtures.salesEntry(),
                            SettlementEntryFixtures.salesEntry());

            given(timeProvider.now()).willReturn(now);
            given(readManager.findConfirmableEntries(now, batchSize)).willReturn(entries);

            // when
            int result = sut.execute(batchSize);

            // then
            assertThat(result).isEqualTo(2);
            then(persistenceFacade).should().persistAll(entries);
        }

        @Test
        @DisplayName("PENDING Entry가 없으면 0을 반환하고 저장하지 않는다")
        void execute_NoPendingEntries_ReturnsZeroAndDoesNotPersist() {
            // given
            int batchSize = 100;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);
            given(readManager.findConfirmableEntries(now, batchSize)).willReturn(List.of());

            // when
            int result = sut.execute(batchSize);

            // then
            assertThat(result).isZero();
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("batchSize만큼 조회하여 처리한다")
        void execute_BatchSize_UsedForQuery() {
            // given
            int batchSize = 50;
            Instant now = Instant.now();
            List<SettlementEntry> entries = List.of(SettlementEntryFixtures.salesEntry());

            given(timeProvider.now()).willReturn(now);
            given(readManager.findConfirmableEntries(now, batchSize)).willReturn(entries);

            // when
            int result = sut.execute(batchSize);

            // then
            assertThat(result).isEqualTo(1);
            then(readManager).should().findConfirmableEntries(now, batchSize);
        }
    }
}
