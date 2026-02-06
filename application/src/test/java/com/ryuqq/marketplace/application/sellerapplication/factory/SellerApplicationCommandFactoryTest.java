package com.ryuqq.marketplace.application.sellerapplication.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApplySellerApplicationCommand;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
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
@DisplayName("SellerApplicationCommandFactory 단위 테스트")
class SellerApplicationCommandFactoryTest {

    @InjectMocks private SellerApplicationCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    private static ApplySellerApplicationCommand minimalCommandWithSettlement(
            String settlementCycle, Integer settlementDay) {
        return new ApplySellerApplicationCommand(
                new ApplySellerApplicationCommand.SellerInfoCommand("테스트셀러", "테스트 브랜드", null, null),
                new ApplySellerApplicationCommand.BusinessInfoCommand(
                        "123-45-67890",
                        "테스트컴퍼니",
                        "홍길동",
                        null,
                        new ApplySellerApplicationCommand.AddressCommand("12345", "서울시 강남구", "")),
                new ApplySellerApplicationCommand.CsContactCommand(
                        "02-1234-5678", "cs@example.com", null),
                new ApplySellerApplicationCommand.ContactInfoCommand(
                        "홍길동", "02-9876-5432", "contact@example.com"),
                new ApplySellerApplicationCommand.SettlementInfoCommand(
                        "088", "신한은행", "110123456789", "홍길동", settlementCycle, settlementDay));
    }

    @Nested
    @DisplayName("create() - ApplySellerApplicationCommand → SellerApplication")
    class CreateTest {

        @Test
        @DisplayName("정산 주기·정산일이 있으면 그대로 도메인에 반영된다")
        void create_WhenSettlementCycleAndDayPresent_UseAsIs() {
            // given
            ApplySellerApplicationCommand command = minimalCommandWithSettlement("WEEKLY", 15);
            Instant now = Instant.parse("2025-01-23T01:30:00Z");
            given(timeProvider.now()).willReturn(now);

            // when
            SellerApplication result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.settlementCycle()).isEqualTo(SettlementCycle.WEEKLY);
            assertThat(result.settlementDay()).isEqualTo(15);
        }
    }
}
