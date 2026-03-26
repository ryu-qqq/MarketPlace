package com.ryuqq.marketplace.application.settlement.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.settlement.port.out.command.SettlementCommandPort;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
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
@DisplayName("SettlementCommandManager 단위 테스트")
class SettlementCommandManagerTest {

    @InjectMocks private SettlementCommandManager sut;

    @Mock private SettlementCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 Settlement 저장")
    class PersistTest {

        @Test
        @DisplayName("Settlement을 CommandPort를 통해 저장한다")
        void persist_Settlement_DelegatesToCommandPort() {
            // given
            Settlement settlement = SettlementFixtures.calculatingSettlement();

            // when
            sut.persist(settlement);

            // then
            then(commandPort).should().persist(settlement);
        }
    }
}
