package com.ryuqq.marketplace.application.settlement.entry.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.settlement.entry.port.out.command.SettlementEntryCommandPort;
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
@DisplayName("SettlementEntryCommandManager 단위 테스트")
class SettlementEntryCommandManagerTest {

    @InjectMocks private SettlementEntryCommandManager sut;

    @Mock private SettlementEntryCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 SettlementEntry 저장")
    class PersistTest {

        @Test
        @DisplayName("SettlementEntry를 CommandPort를 통해 저장한다")
        void persist_Entry_DelegatesToCommandPort() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();

            // when
            sut.persist(entry);

            // then
            then(commandPort).should().persist(entry);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다건 SettlementEntry 저장")
    class PersistAllTest {

        @Test
        @DisplayName("SettlementEntry 목록을 CommandPort를 통해 일괄 저장한다")
        void persistAll_EntryList_DelegatesToCommandPort() {
            // given
            List<SettlementEntry> entries =
                    List.of(
                            SettlementEntryFixtures.salesEntry(),
                            SettlementEntryFixtures.cancelReversalEntry());

            // when
            sut.persistAll(entries);

            // then
            then(commandPort).should().persistAll(entries);
        }

        @Test
        @DisplayName("빈 목록도 CommandPort를 통해 저장 시도한다")
        void persistAll_EmptyList_DelegatesToCommandPort() {
            // given
            List<SettlementEntry> emptyList = List.of();

            // when
            sut.persistAll(emptyList);

            // then
            then(commandPort).should().persistAll(emptyList);
        }
    }
}
