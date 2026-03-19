package com.ryuqq.marketplace.application.settlement.internal;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryCommandManager;
import com.ryuqq.marketplace.application.settlement.manager.SettlementCommandManager;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
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
@DisplayName("SettlementPersistenceFacade 단위 테스트")
class SettlementPersistenceFacadeTest {

    @InjectMocks private SettlementPersistenceFacade sut;

    @Mock private SettlementCommandManager settlementCommandManager;
    @Mock private SettlementEntryCommandManager entryCommandManager;

    @Nested
    @DisplayName("persist() - 단건 Settlement 저장")
    class PersistTest {

        @Test
        @DisplayName("Settlement를 SettlementCommandManager를 통해 저장한다")
        void persist_Settlement_DelegatesToSettlementCommandManager() {
            // given
            Settlement settlement = SettlementFixtures.calculatingSettlement();

            // when
            sut.persist(settlement);

            // then
            then(settlementCommandManager).should().persist(settlement);
            then(entryCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistWithSettledEntries() - Settlement 생성 + Entry SETTLED 일괄 저장")
    class PersistWithSettledEntriesTest {

        @Test
        @DisplayName("Settlement와 SETTLED 상태 Entry 목록을 함께 저장한다")
        void persistWithSettledEntries_SavesSettlementAndEntries() {
            // given
            Settlement settlement = SettlementFixtures.newSettlement();
            List<SettlementEntry> entries =
                    List.of(
                            SettlementEntryFixtures.confirmedSalesEntry(),
                            SettlementEntryFixtures.confirmedSalesEntry());

            // when
            sut.persistWithSettledEntries(settlement, entries);

            // then
            then(settlementCommandManager).should().persist(settlement);
            then(entryCommandManager).should().persistAll(entries);
        }

        @Test
        @DisplayName("Entry 목록이 비어있어도 Settlement를 저장한다")
        void persistWithSettledEntries_EmptyEntries_StillSavesSettlement() {
            // given
            Settlement settlement = SettlementFixtures.newSettlement();
            List<SettlementEntry> emptyEntries = List.of();

            // when
            sut.persistWithSettledEntries(settlement, emptyEntries);

            // then
            then(settlementCommandManager).should().persist(settlement);
            then(entryCommandManager).should().persistAll(emptyEntries);
        }
    }
}
