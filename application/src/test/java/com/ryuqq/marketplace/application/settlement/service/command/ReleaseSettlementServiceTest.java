package com.ryuqq.marketplace.application.settlement.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.internal.SettlementPersistenceFacade;
import com.ryuqq.marketplace.application.settlement.manager.SettlementReadManager;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import java.time.Instant;
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
@DisplayName("ReleaseSettlementService 단위 테스트")
class ReleaseSettlementServiceTest {

    @InjectMocks private ReleaseSettlementService sut;

    @Mock private SettlementReadManager readManager;
    @Mock private SettlementPersistenceFacade persistenceFacade;
    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("execute() - Settlement 보류 해제 처리")
    class ExecuteTest {

        @Test
        @DisplayName("HOLD 상태 Settlement를 CALCULATING 상태로 보류 해제하고 저장한다")
        void execute_HeldSettlement_ReleasesAndPersists() {
            // given
            String settlementId = SettlementFixtures.defaultSettlementId().value();
            Settlement settlement = SettlementFixtures.heldSettlement();
            Instant now = Instant.now();

            given(readManager.getById(SettlementId.of(settlementId))).willReturn(settlement);
            given(timeProvider.now()).willReturn(now);

            // when
            sut.execute(settlementId);

            // then
            then(readManager).should().getById(SettlementId.of(settlementId));
            then(persistenceFacade).should().persist(settlement);
        }
    }
}
