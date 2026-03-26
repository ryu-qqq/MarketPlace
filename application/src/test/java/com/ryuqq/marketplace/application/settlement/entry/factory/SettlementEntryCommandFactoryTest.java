package com.ryuqq.marketplace.application.settlement.entry.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.SettlementEntryCommandFixtures;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateReversalEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateSalesEntryCommand;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryType;
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
@DisplayName("SettlementEntryCommandFactory 단위 테스트")
class SettlementEntryCommandFactoryTest {

    @InjectMocks private SettlementEntryCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createSalesEntry() - 판매 Entry 생성")
    class CreateSalesEntryTest {

        @Test
        @DisplayName("CreateSalesEntryCommand로 SALES 타입 SettlementEntry를 생성한다")
        void createSalesEntry_ValidCommand_ReturnsSalesEntry() {
            // given
            CreateSalesEntryCommand command =
                    SettlementEntryCommandFixtures.createSalesEntryCommand();
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SettlementEntry entry = sut.createSalesEntry(command);

            // then
            assertThat(entry).isNotNull();
            assertThat(entry.sellerId()).isEqualTo(command.sellerId());
            assertThat(entry.entryType()).isEqualTo(EntryType.SALES);
            assertThat(entry.amounts().salesAmount().value()).isEqualTo(command.salesAmount());
        }

        @Test
        @DisplayName("생성된 판매 Entry는 eligibleAt이 now + 7일로 설정된다")
        void createSalesEntry_ValidCommand_EligibleAtIsSevenDaysLater() {
            // given
            CreateSalesEntryCommand command =
                    SettlementEntryCommandFixtures.createSalesEntryCommand();
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SettlementEntry entry = sut.createSalesEntry(command);

            // then
            assertThat(entry.eligibleAt()).isAfter(now);
        }
    }

    @Nested
    @DisplayName("createReversalEntry() - 역분개 Entry 생성")
    class CreateReversalEntryTest {

        @Test
        @DisplayName("CANCEL 클레임 타입으로 CANCEL 타입 역분개 Entry를 생성한다")
        void createReversalEntry_CancelClaimType_ReturnsCancelReversalEntry() {
            // given
            CreateReversalEntryCommand command =
                    SettlementEntryCommandFixtures.createCancelReversalCommand();
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SettlementEntry entry = sut.createReversalEntry(command);

            // then
            assertThat(entry).isNotNull();
            assertThat(entry.entryType()).isEqualTo(EntryType.CANCEL);
            assertThat(entry.sellerId()).isEqualTo(command.sellerId());
            assertThat(entry.entryType().isReversal()).isTrue();
        }

        @Test
        @DisplayName("REFUND 클레임 타입으로 REFUND 타입 역분개 Entry를 생성한다")
        void createReversalEntry_RefundClaimType_ReturnsRefundReversalEntry() {
            // given
            CreateReversalEntryCommand command =
                    SettlementEntryCommandFixtures.createRefundReversalCommand();
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SettlementEntry entry = sut.createReversalEntry(command);

            // then
            assertThat(entry.entryType()).isEqualTo(EntryType.REFUND);
        }

        @Test
        @DisplayName("EXCHANGE_OUT 클레임 타입으로 EXCHANGE_OUT 타입 역분개 Entry를 생성한다")
        void createReversalEntry_ExchangeOutClaimType_ReturnsExchangeOutReversalEntry() {
            // given
            CreateReversalEntryCommand command =
                    SettlementEntryCommandFixtures.createExchangeOutReversalCommand();
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SettlementEntry entry = sut.createReversalEntry(command);

            // then
            assertThat(entry.entryType()).isEqualTo(EntryType.EXCHANGE_OUT);
        }

        @Test
        @DisplayName("EXCHANGE_IN 클레임 타입으로 EXCHANGE_IN 타입 역분개 Entry를 생성한다")
        void createReversalEntry_ExchangeInClaimType_ReturnsExchangeInReversalEntry() {
            // given
            CreateReversalEntryCommand command =
                    SettlementEntryCommandFixtures.createExchangeInReversalCommand();
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SettlementEntry entry = sut.createReversalEntry(command);

            // then
            assertThat(entry.entryType()).isEqualTo(EntryType.EXCHANGE_IN);
        }

        @Test
        @DisplayName("지원하지 않는 클레임 타입이면 IllegalArgumentException이 발생한다")
        void createReversalEntry_UnsupportedClaimType_ThrowsIllegalArgumentException() {
            // given
            CreateReversalEntryCommand command =
                    SettlementEntryCommandFixtures.createReversalEntryCommand("UNKNOWN");
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when & then
            assertThatThrownBy(() -> sut.createReversalEntry(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("지원하지 않는 클레임 유형");
        }
    }

    @Nested
    @DisplayName("now() - 현재 시간 반환")
    class NowTest {

        @Test
        @DisplayName("TimeProvider에서 현재 시간을 반환한다")
        void now_ReturnsCurrentInstant() {
            // given
            Instant expected = Instant.now();
            given(timeProvider.now()).willReturn(expected);

            // when
            Instant result = sut.now();

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
