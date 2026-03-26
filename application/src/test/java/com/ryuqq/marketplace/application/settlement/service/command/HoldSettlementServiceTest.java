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
@DisplayName("HoldSettlementService 단위 테스트")
class HoldSettlementServiceTest {

    @InjectMocks private HoldSettlementService sut;

    @Mock private SettlementReadManager readManager;
    @Mock private SettlementPersistenceFacade persistenceFacade;
    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("execute() - Settlement 보류 처리")
    class ExecuteTest {

        @Test
        @DisplayName("CALCULATING 상태 Settlement를 HOLD 상태로 보류 처리하고 저장한다")
        void execute_CalculatingSettlement_HoldsAndPersists() {
            // given
            String settlementId = SettlementFixtures.defaultSettlementId().value();
            String reason = "이상 거래 의심";
            Settlement settlement = SettlementFixtures.calculatingSettlement();
            Instant now = Instant.now();

            given(readManager.getById(SettlementId.of(settlementId))).willReturn(settlement);
            given(timeProvider.now()).willReturn(now);

            // when
            sut.execute(settlementId, reason);

            // then
            then(readManager).should().getById(SettlementId.of(settlementId));
            then(persistenceFacade).should().persist(settlement);
        }

        @Test
        @DisplayName("CONFIRMED 상태 Settlement를 HOLD 상태로 보류 처리하고 저장한다")
        void execute_ConfirmedSettlement_HoldsAndPersists() {
            // given
            String settlementId = SettlementFixtures.defaultSettlementId().value();
            String reason = "지급 이슈로 인한 보류";
            Settlement settlement = SettlementFixtures.confirmedSettlement();
            Instant now = Instant.now();

            given(readManager.getById(SettlementId.of(settlementId))).willReturn(settlement);
            given(timeProvider.now()).willReturn(now);

            // when
            sut.execute(settlementId, reason);

            // then
            then(persistenceFacade).should().persist(settlement);
        }
    }
}
