package com.ryuqq.marketplace.application.settlement.entry.internal;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryCommandManager;
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
@DisplayName("SettlementEntryPersistenceFacade 단위 테스트")
class SettlementEntryPersistenceFacadeTest {

    @InjectMocks private SettlementEntryPersistenceFacade sut;

    @Mock private SettlementEntryCommandManager entryCommandManager;

    @Nested
    @DisplayName("persist() - 단건 SettlementEntry 저장")
    class PersistTest {

        @Test
        @DisplayName("SettlementEntry를 EntryCommandManager를 통해 저장한다")
        void persist_Entry_DelegatesToEntryCommandManager() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();

            // when
            sut.persist(entry);

            // then
            then(entryCommandManager).should().persist(entry);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다건 SettlementEntry 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("SettlementEntry 목록을 EntryCommandManager를 통해 일괄 저장한다")
        void persistAll_EntryList_DelegatesToEntryCommandManager() {
            // given
            List<SettlementEntry> entries =
                    List.of(
                            SettlementEntryFixtures.salesEntry(),
                            SettlementEntryFixtures.cancelReversalEntry());

            // when
            sut.persistAll(entries);

            // then
            then(entryCommandManager).should().persistAll(entries);
        }

        @Test
        @DisplayName("빈 목록도 EntryCommandManager를 통해 저장 시도한다")
        void persistAll_EmptyList_DelegatesToEntryCommandManager() {
            // given
            List<SettlementEntry> emptyList = List.of();

            // when
            sut.persistAll(emptyList);

            // then
            then(entryCommandManager).should().persistAll(emptyList);
        }
    }
}
