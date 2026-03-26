package com.ryuqq.marketplace.application.settlement.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.SettlementCommandFixtures;
import com.ryuqq.marketplace.application.settlement.dto.command.AggregateSettlementCommand;
import com.ryuqq.marketplace.application.settlement.factory.SettlementCommandFactory.SettlementBundle;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
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
@DisplayName("SettlementCommandFactory 단위 테스트")
class SettlementCommandFactoryTest {

    @InjectMocks private SettlementCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createAggregateBundle() - Settlement + SETTLED Entry 번들 생성")
    class CreateAggregateBundleTest {

        @Test
        @DisplayName("커맨드, 금액, Entry 목록으로 SettlementBundle을 생성한다")
        void createAggregateBundle_ValidParams_ReturnsSettlementBundle() {
            // given
            AggregateSettlementCommand command = SettlementCommandFixtures.aggregateCommand();
            SettlementAmounts amounts =
                    com.ryuqq.marketplace.domain.settlement.SettlementFixtures
                            .defaultSettlementAmounts();
            List<SettlementEntry> entries = List.of(SettlementEntryFixtures.confirmedSalesEntry());
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SettlementBundle bundle = sut.createAggregateBundle(command, amounts, entries);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.settlement()).isNotNull();
            assertThat(bundle.settlement().sellerId()).isEqualTo(command.sellerId());
            assertThat(bundle.settledEntries()).hasSize(1);
        }

        @Test
        @DisplayName("Entry 목록이 여러 건이면 번들의 Entry 수가 일치한다")
        void createAggregateBundle_MultipleEntries_BundleContainsAllEntries() {
            // given
            AggregateSettlementCommand command = SettlementCommandFixtures.aggregateCommand();
            SettlementAmounts amounts =
                    com.ryuqq.marketplace.domain.settlement.SettlementFixtures
                            .defaultSettlementAmounts();
            List<SettlementEntry> entries =
                    List.of(
                            SettlementEntryFixtures.confirmedSalesEntry(),
                            SettlementEntryFixtures.confirmedSalesEntry(),
                            SettlementEntryFixtures.confirmedSalesEntry());
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SettlementBundle bundle = sut.createAggregateBundle(command, amounts, entries);

            // then
            assertThat(bundle.settledEntries()).hasSize(3);
            assertThat(bundle.settlement().entryCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("생성된 Settlement는 CALCULATING 상태다")
        void createAggregateBundle_CreatedSettlement_HasCalculatingStatus() {
            // given
            AggregateSettlementCommand command = SettlementCommandFixtures.aggregateCommand();
            SettlementAmounts amounts =
                    com.ryuqq.marketplace.domain.settlement.SettlementFixtures
                            .defaultSettlementAmounts();
            List<SettlementEntry> entries = List.of(SettlementEntryFixtures.confirmedSalesEntry());
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SettlementBundle bundle = sut.createAggregateBundle(command, amounts, entries);

            // then
            assertThat(bundle.settlement().status())
                    .isEqualTo(
                            com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus
                                    .CALCULATING);
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
