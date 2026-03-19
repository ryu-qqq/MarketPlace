package com.ryuqq.marketplace.domain.settlement.entry.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("EntryStatus 상태 전이 테스트")
class EntryStatusTest {

    @Test
    @DisplayName("PENDING → CONFIRMED 전이 가능")
    void pendingToConfirmed() {
        assertThat(EntryStatus.PENDING.canTransitionTo(EntryStatus.CONFIRMED)).isTrue();
    }

    @Test
    @DisplayName("CONFIRMED → SETTLED 전이 가능")
    void confirmedToSettled() {
        assertThat(EntryStatus.CONFIRMED.canTransitionTo(EntryStatus.SETTLED)).isTrue();
    }

    @Test
    @DisplayName("PENDING → SETTLED 전이 불가")
    void pendingToSettledNotAllowed() {
        assertThat(EntryStatus.PENDING.canTransitionTo(EntryStatus.SETTLED)).isFalse();
    }

    @Test
    @DisplayName("CONFIRMED → CONFIRMED 전이 불가")
    void confirmedToConfirmedNotAllowed() {
        assertThat(EntryStatus.CONFIRMED.canTransitionTo(EntryStatus.CONFIRMED)).isFalse();
    }

    @Test
    @DisplayName("SETTLED → 어떤 상태로도 전이 불가")
    void settledIsTerminal() {
        assertThat(EntryStatus.SETTLED.canTransitionTo(EntryStatus.PENDING)).isFalse();
        assertThat(EntryStatus.SETTLED.canTransitionTo(EntryStatus.CONFIRMED)).isFalse();
        assertThat(EntryStatus.SETTLED.canTransitionTo(EntryStatus.SETTLED)).isFalse();
    }
}
