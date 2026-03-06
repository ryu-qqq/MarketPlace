package com.ryuqq.marketplace.application.outboundproduct.dto.result;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ManualSyncResult 단위 테스트")
class ManualSyncResultTest {

    @Nested
    @DisplayName("of() - 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("create, update, skip 카운트를 전달하면 ACCEPTED 상태의 결과를 반환한다")
        void of_GivenCounts_ReturnsAcceptedResult() {
            // when
            ManualSyncResult result = ManualSyncResult.of(3, 2, 1);

            // then
            assertThat(result.createCount()).isEqualTo(3);
            assertThat(result.updateCount()).isEqualTo(2);
            assertThat(result.skippedCount()).isEqualTo(1);
            assertThat(result.status()).isEqualTo("ACCEPTED");
        }

        @Test
        @DisplayName("모든 카운트가 0이어도 ACCEPTED 상태를 반환한다")
        void of_AllZeroCounts_ReturnsAccepted() {
            // when
            ManualSyncResult result = ManualSyncResult.of(0, 0, 0);

            // then
            assertThat(result.createCount()).isZero();
            assertThat(result.updateCount()).isZero();
            assertThat(result.skippedCount()).isZero();
            assertThat(result.status()).isEqualTo("ACCEPTED");
        }
    }

    @Nested
    @DisplayName("totalOutboxCount() - 전체 아웃박스 수")
    class TotalOutboxCountTest {

        @Test
        @DisplayName("createCount와 updateCount의 합을 반환한다")
        void totalOutboxCount_ReturnsSumOfCreateAndUpdate() {
            // given
            ManualSyncResult result = ManualSyncResult.of(3, 2, 5);

            // when & then
            assertThat(result.totalOutboxCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("create만 있을 때 createCount를 반환한다")
        void totalOutboxCount_OnlyCreate_ReturnsCreateCount() {
            // given
            ManualSyncResult result = ManualSyncResult.of(4, 0, 0);

            // when & then
            assertThat(result.totalOutboxCount()).isEqualTo(4);
        }

        @Test
        @DisplayName("update만 있을 때 updateCount를 반환한다")
        void totalOutboxCount_OnlyUpdate_ReturnsUpdateCount() {
            // given
            ManualSyncResult result = ManualSyncResult.of(0, 3, 0);

            // when & then
            assertThat(result.totalOutboxCount()).isEqualTo(3);
        }
    }
}
