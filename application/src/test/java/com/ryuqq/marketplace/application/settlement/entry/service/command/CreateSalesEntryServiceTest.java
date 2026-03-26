package com.ryuqq.marketplace.application.settlement.entry.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.settlement.SettlementEntryCommandFixtures;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateSalesEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.factory.SettlementEntryCommandFactory;
import com.ryuqq.marketplace.application.settlement.entry.internal.SettlementEntryPersistenceFacade;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
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
@DisplayName("CreateSalesEntryService 단위 테스트")
class CreateSalesEntryServiceTest {

    @InjectMocks private CreateSalesEntryService sut;

    @Mock private SettlementEntryCommandFactory commandFactory;
    @Mock private SettlementEntryPersistenceFacade persistenceFacade;

    @Nested
    @DisplayName("execute() - 판매 Entry 생성")
    class ExecuteTest {

        @Test
        @DisplayName("CreateSalesEntryCommand로 판매 Entry를 생성하고 저장한다")
        void execute_ValidCommand_CreatesAndPersistsSalesEntry() {
            // given
            CreateSalesEntryCommand command =
                    SettlementEntryCommandFixtures.createSalesEntryCommand();
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();

            given(commandFactory.createSalesEntry(command)).willReturn(entry);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createSalesEntry(command);
            then(persistenceFacade).should().persist(entry);
        }
    }
}
