package com.ryuqq.marketplace.domain.sellerapplication.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ApplicationStatus 테스트")
class ApplicationStatusTest {

    @Nested
    @DisplayName("상태 판별 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("PENDING은 isPending이 true이다")
        void pendingStatus() {
            assertThat(ApplicationStatus.PENDING.isPending()).isTrue();
            assertThat(ApplicationStatus.PENDING.isApproved()).isFalse();
            assertThat(ApplicationStatus.PENDING.isRejected()).isFalse();
            assertThat(ApplicationStatus.PENDING.isProcessed()).isFalse();
        }

        @Test
        @DisplayName("APPROVED는 isApproved이 true이고 isProcessed도 true이다")
        void approvedStatus() {
            assertThat(ApplicationStatus.APPROVED.isApproved()).isTrue();
            assertThat(ApplicationStatus.APPROVED.isPending()).isFalse();
            assertThat(ApplicationStatus.APPROVED.isRejected()).isFalse();
            assertThat(ApplicationStatus.APPROVED.isProcessed()).isTrue();
        }

        @Test
        @DisplayName("REJECTED는 isRejected이 true이고 isProcessed도 true이다")
        void rejectedStatus() {
            assertThat(ApplicationStatus.REJECTED.isRejected()).isTrue();
            assertThat(ApplicationStatus.REJECTED.isPending()).isFalse();
            assertThat(ApplicationStatus.REJECTED.isApproved()).isFalse();
            assertThat(ApplicationStatus.REJECTED.isProcessed()).isTrue();
        }
    }

    @Nested
    @DisplayName("isProcessed() - 처리 완료 여부")
    class IsProcessedTest {

        @Test
        @DisplayName("APPROVED와 REJECTED만 처리 완료 상태이다")
        void processedStatuses() {
            assertThat(ApplicationStatus.PENDING.isProcessed()).isFalse();
            assertThat(ApplicationStatus.APPROVED.isProcessed()).isTrue();
            assertThat(ApplicationStatus.REJECTED.isProcessed()).isTrue();
        }
    }
}
