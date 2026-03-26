package com.ryuqq.marketplace.application.claimhistory.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimHistoryType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimHistoryAssembler 단위 테스트")
class ClaimHistoryAssemblerTest {

    private ClaimHistoryAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ClaimHistoryAssembler();
    }

    @Nested
    @DisplayName("toResult() - 단일 이력 변환")
    class ToResultTest {

        @Test
        @DisplayName("상태 변경 이력을 ClaimHistoryResult로 변환한다")
        void toResult_StatusChangeHistory_ReturnsResult() {
            // given
            ClaimHistory history = ClaimHistoryFixtures.statusChangeClaimHistory();

            // when
            ClaimHistoryResult result = sut.toResult(history);

            // then
            assertThat(result).isNotNull();
            assertThat(result.historyId()).isEqualTo(history.idValue());
            assertThat(result.type()).isEqualTo(ClaimHistoryType.STATUS_CHANGE.name());
            assertThat(result.title()).isEqualTo(history.title());
            assertThat(result.message()).isEqualTo(history.message());
            assertThat(result.actorType()).isEqualTo(history.actor().actorType().name());
            assertThat(result.actorId()).isEqualTo(history.actor().actorId());
            assertThat(result.actorName()).isEqualTo(history.actor().actorName());
            assertThat(result.createdAt()).isEqualTo(history.createdAt());
        }

        @Test
        @DisplayName("수기 메모 이력을 ClaimHistoryResult로 변환한다")
        void toResult_ManualHistory_ReturnsResult() {
            // given
            ClaimHistory history = ClaimHistoryFixtures.manualClaimHistory();

            // when
            ClaimHistoryResult result = sut.toResult(history);

            // then
            assertThat(result).isNotNull();
            assertThat(result.historyId()).isEqualTo(history.idValue());
            assertThat(result.type()).isEqualTo(ClaimHistoryType.MANUAL.name());
            assertThat(result.title()).isEqualTo("CS 메모");
            assertThat(result.message()).isEqualTo(history.message());
        }

        @Test
        @DisplayName("DB에서 복원된 이력을 ClaimHistoryResult로 변환한다")
        void toResult_ReconstitutedHistory_ReturnsResult() {
            // given
            ClaimHistory history = ClaimHistoryFixtures.reconstitutedClaimHistory();

            // when
            ClaimHistoryResult result = sut.toResult(history);

            // then
            assertThat(result).isNotNull();
            assertThat(result.historyId()).isEqualTo(history.idValue());
            assertThat(result.type()).isEqualTo(ClaimHistoryType.STATUS_CHANGE.name());
            assertThat(result.title()).isEqualTo("승인");
            assertThat(result.message()).isEqualTo("REQUESTED → APPROVED");
        }
    }

    @Nested
    @DisplayName("toResults() - 이력 목록 변환")
    class ToResultsTest {

        @Test
        @DisplayName("이력 목록을 ClaimHistoryResult 목록으로 변환한다")
        void toResults_ValidHistories_ReturnsResults() {
            // given
            List<ClaimHistory> histories =
                    List.of(
                            ClaimHistoryFixtures.statusChangeClaimHistory(),
                            ClaimHistoryFixtures.manualClaimHistory());

            // when
            List<ClaimHistoryResult> results = sut.toResults(histories);

            // then
            assertThat(results).isNotNull();
            assertThat(results).hasSize(2);
            assertThat(results.get(0).type()).isEqualTo(ClaimHistoryType.STATUS_CHANGE.name());
            assertThat(results.get(1).type()).isEqualTo(ClaimHistoryType.MANUAL.name());
        }

        @Test
        @DisplayName("빈 목록으로 호출하면 빈 결과 목록을 반환한다")
        void toResults_EmptyList_ReturnsEmptyList() {
            // given
            List<ClaimHistory> emptyHistories = List.of();

            // when
            List<ClaimHistoryResult> results = sut.toResults(emptyHistories);

            // then
            assertThat(results).isNotNull();
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("다양한 ClaimType 이력을 모두 변환한다")
        void toResults_MultipleClaimTypes_ReturnsAllResults() {
            // given
            List<ClaimHistory> histories =
                    List.of(
                            ClaimHistoryFixtures.cancelStatusChangeHistory(),
                            ClaimHistoryFixtures.refundStatusChangeHistory(),
                            ClaimHistoryFixtures.exchangeStatusChangeHistory());

            // when
            List<ClaimHistoryResult> results = sut.toResults(histories);

            // then
            assertThat(results).hasSize(3);
            assertThat(results)
                    .allMatch(r -> r.type().equals(ClaimHistoryType.STATUS_CHANGE.name()));
        }
    }
}
