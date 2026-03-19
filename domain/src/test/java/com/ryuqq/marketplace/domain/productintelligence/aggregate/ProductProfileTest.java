package com.ryuqq.marketplace.domain.productintelligence.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.productintelligence.ProductIntelligenceFixtures;
import com.ryuqq.marketplace.domain.productintelligence.exception.AnalysisAlreadyCompletedException;
import com.ryuqq.marketplace.domain.productintelligence.exception.AnalysisNotAllCompletedException;
import com.ryuqq.marketplace.domain.productintelligence.exception.InvalidProfileStateException;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisSource;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisStatus;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisType;
import com.ryuqq.marketplace.domain.productintelligence.vo.ConfidenceScore;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import com.ryuqq.marketplace.domain.productintelligence.vo.InspectionDecision;
import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductProfile Aggregate 단위 테스트")
class ProductProfileTest {

    @Nested
    @DisplayName("forNew() - 신규 프로파일 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 프로파일은 PENDING 상태로 생성된다")
        void forNewCreatesWithPendingStatus() {
            Instant now = Instant.now();

            ProductProfile profile = ProductProfile.forNew(100L, null, 1, now);

            assertThat(profile.status()).isEqualTo(AnalysisStatus.PENDING);
            assertThat(profile.productGroupId()).isEqualTo(100L);
            assertThat(profile.previousProfileId()).isNull();
            assertThat(profile.profileVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("신규 프로파일은 분석 카운트가 0이다")
        void forNewHasZeroCompletedCount() {
            ProductProfile profile = ProductIntelligenceFixtures.pendingProductProfile();

            assertThat(profile.completedAnalysisCount()).isEqualTo(0);
            assertThat(profile.expectedAnalysisCount()).isEqualTo(3);
            assertThat(profile.completedAnalysisTypes()).isEmpty();
        }

        @Test
        @DisplayName("신규 프로파일은 isNew가 true이다")
        void forNewIsNew() {
            ProductProfile profile = ProductIntelligenceFixtures.pendingProductProfile();

            assertThat(profile.isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 프로파일은 분석 결과 목록이 비어있다")
        void forNewHasEmptyResultLists() {
            ProductProfile profile = ProductIntelligenceFixtures.pendingProductProfile();

            assertThat(profile.extractedAttributes()).isEmpty();
            assertThat(profile.optionSuggestions()).isEmpty();
            assertThat(profile.noticeSuggestions()).isEmpty();
            assertThat(profile.decision()).isNull();
        }
    }

    @Nested
    @DisplayName("forNewAnalyzing() - 분석 중 상태의 신규 프로파일 생성")
    class ForNewAnalyzingTest {

        @Test
        @DisplayName("forNewAnalyzing은 ANALYZING 상태로 생성된다")
        void forNewAnalyzingCreatesWithAnalyzingStatus() {
            Instant now = Instant.now();

            ProductProfile profile = ProductProfile.forNewAnalyzing(100L, null, 1, now);

            assertThat(profile.status()).isEqualTo(AnalysisStatus.ANALYZING);
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("기존 프로파일을 재구성하면 isNew가 false이다")
        void reconstitutedProfileIsNotNew() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            assertThat(profile.isNew()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태 프로파일을 재구성한다")
        void reconstitutedCompletedProfile() {
            ProductProfile profile = ProductIntelligenceFixtures.completedProductProfile();

            assertThat(profile.status()).isEqualTo(AnalysisStatus.COMPLETED);
            assertThat(profile.completedAnalysisCount()).isEqualTo(3);
            assertThat(profile.decision()).isNotNull();
        }
    }

    @Nested
    @DisplayName("startOrchestrating() - PENDING → ORCHESTRATING")
    class StartOrchestratingTest {

        @Test
        @DisplayName("PENDING 상태에서 ORCHESTRATING으로 전환한다")
        void pendingToOrchestrating() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();
            Instant now = Instant.now();

            profile.startOrchestrating(now);

            assertThat(profile.status()).isEqualTo(AnalysisStatus.ORCHESTRATING);
        }

        @Test
        @DisplayName("PENDING이 아닌 상태에서 startOrchestrating 호출 시 예외가 발생한다")
        void startOrchestratingFromNonPendingThrows() {
            ProductProfile profile = ProductIntelligenceFixtures.completedProductProfile();

            assertThatThrownBy(() -> profile.startOrchestrating(Instant.now()))
                    .isInstanceOf(InvalidProfileStateException.class);
        }
    }

    @Nested
    @DisplayName("startAnalyzing() - ORCHESTRATING → ANALYZING")
    class StartAnalyzingTest {

        @Test
        @DisplayName("ORCHESTRATING 상태에서 ANALYZING으로 전환한다")
        void orchestratingToAnalyzing() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();
            Instant now = Instant.now();
            profile.startOrchestrating(now);

            profile.startAnalyzing(now);

            assertThat(profile.status()).isEqualTo(AnalysisStatus.ANALYZING);
        }

        @Test
        @DisplayName("PENDING 상태에서 startAnalyzing 호출 시 예외가 발생한다")
        void startAnalyzingFromPendingThrows() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            assertThatThrownBy(() -> profile.startAnalyzing(Instant.now()))
                    .isInstanceOf(InvalidProfileStateException.class);
        }
    }

    @Nested
    @DisplayName("recordDescriptionAnalysis() - Description 분석 결과 기록")
    class RecordDescriptionAnalysisTest {

        @Test
        @DisplayName("ANALYZING 상태에서 Description 분석 결과를 기록한다")
        void recordDescriptionAnalysisSuccess() {
            ProductProfile profile = analyzingProfile();
            List<ExtractedAttribute> attributes =
                    List.of(ProductIntelligenceFixtures.defaultExtractedAttribute());
            Instant now = Instant.now();

            profile.recordDescriptionAnalysis(attributes, now);

            assertThat(profile.extractedAttributes()).hasSize(1);
            assertThat(profile.completedAnalysisCount()).isEqualTo(1);
            assertThat(profile.completedAnalysisTypes()).contains(AnalysisType.DESCRIPTION);
        }

        @Test
        @DisplayName("3개 분석 중 마지막 분석 완료 시 true를 반환한다")
        void lastAnalysisReturnsTrue() {
            ProductProfile profile = analyzingProfile();
            Instant now = Instant.now();

            profile.recordDescriptionAnalysis(List.of(), now);
            profile.recordOptionAnalysis(List.of(), now);
            boolean allCompleted = profile.recordNoticeAnalysis(List.of(), now);

            assertThat(allCompleted).isTrue();
        }

        @Test
        @DisplayName("마지막 분석이 아닐 때는 false를 반환한다")
        void notLastAnalysisReturnsFalse() {
            ProductProfile profile = analyzingProfile();
            Instant now = Instant.now();

            boolean allCompleted = profile.recordDescriptionAnalysis(List.of(), now);

            assertThat(allCompleted).isFalse();
        }

        @Test
        @DisplayName("ANALYZING이 아닌 상태에서 기록 시 예외가 발생한다")
        void recordAnalysisFromNonAnalyzingThrows() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            assertThatThrownBy(() -> profile.recordDescriptionAnalysis(List.of(), Instant.now()))
                    .isInstanceOf(InvalidProfileStateException.class);
        }

        @Test
        @DisplayName("이미 완료된 분석 타입을 다시 기록하면 예외가 발생한다")
        void duplicateDescriptionAnalysisThrows() {
            ProductProfile profile = analyzingProfile();
            Instant now = Instant.now();
            profile.recordDescriptionAnalysis(List.of(), now);

            assertThatThrownBy(() -> profile.recordDescriptionAnalysis(List.of(), now))
                    .isInstanceOf(AnalysisAlreadyCompletedException.class);
        }
    }

    @Nested
    @DisplayName("recordOptionAnalysis() - Option 분석 결과 기록")
    class RecordOptionAnalysisTest {

        @Test
        @DisplayName("ANALYZING 상태에서 Option 분석 결과를 기록한다")
        void recordOptionAnalysisSuccess() {
            ProductProfile profile = analyzingProfile();
            List<OptionMappingSuggestion> suggestions =
                    List.of(ProductIntelligenceFixtures.defaultOptionMappingSuggestion());
            Instant now = Instant.now();

            profile.recordOptionAnalysis(suggestions, now);

            assertThat(profile.optionSuggestions()).hasSize(1);
            assertThat(profile.completedAnalysisTypes()).contains(AnalysisType.OPTION);
        }

        @Test
        @DisplayName("이미 완료된 OPTION 분석 타입을 다시 기록하면 예외가 발생한다")
        void duplicateOptionAnalysisThrows() {
            ProductProfile profile = analyzingProfile();
            Instant now = Instant.now();
            profile.recordOptionAnalysis(List.of(), now);

            assertThatThrownBy(() -> profile.recordOptionAnalysis(List.of(), now))
                    .isInstanceOf(AnalysisAlreadyCompletedException.class);
        }
    }

    @Nested
    @DisplayName("recordNoticeAnalysis() - Notice 분석 결과 기록")
    class RecordNoticeAnalysisTest {

        @Test
        @DisplayName("ANALYZING 상태에서 Notice 분석 결과를 기록한다")
        void recordNoticeAnalysisSuccess() {
            ProductProfile profile = analyzingProfile();
            List<NoticeSuggestion> suggestions =
                    List.of(ProductIntelligenceFixtures.defaultNoticeSuggestion());
            Instant now = Instant.now();

            profile.recordNoticeAnalysis(suggestions, now);

            assertThat(profile.noticeSuggestions()).hasSize(1);
            assertThat(profile.completedAnalysisTypes()).contains(AnalysisType.NOTICE);
        }
    }

    @Nested
    @DisplayName("startAggregating() - ANALYZING → AGGREGATING")
    class StartAggregatingTest {

        @Test
        @DisplayName("모든 분석 완료 후 AGGREGATING으로 전환한다")
        void startAggregatingAfterAllAnalysis() {
            ProductProfile profile = analyzingProfile();
            Instant now = Instant.now();
            profile.recordDescriptionAnalysis(List.of(), now);
            profile.recordOptionAnalysis(List.of(), now);
            profile.recordNoticeAnalysis(List.of(), now);

            profile.startAggregating(now);

            assertThat(profile.status()).isEqualTo(AnalysisStatus.AGGREGATING);
        }

        @Test
        @DisplayName("분석이 모두 완료되지 않으면 AGGREGATING 전환 시 예외가 발생한다")
        void startAggregatingWithIncompleteAnalysisThrows() {
            ProductProfile profile = analyzingProfile();
            Instant now = Instant.now();
            profile.recordDescriptionAnalysis(List.of(), now);

            assertThatThrownBy(() -> profile.startAggregating(now))
                    .isInstanceOf(AnalysisNotAllCompletedException.class);
        }

        @Test
        @DisplayName("ANALYZING이 아닌 상태에서 AGGREGATING 전환 시 예외가 발생한다")
        void startAggregatingFromNonAnalyzingThrows() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            assertThatThrownBy(() -> profile.startAggregating(Instant.now()))
                    .isInstanceOf(InvalidProfileStateException.class);
        }
    }

    @Nested
    @DisplayName("complete() - AGGREGATING → COMPLETED")
    class CompleteTest {

        @Test
        @DisplayName("AGGREGATING 상태에서 최종 판정을 완료한다")
        void completeFromAggregating() {
            ProductProfile profile = aggregatingProfile();
            InspectionDecision decision =
                    InspectionDecision.autoApprove(0.96, List.of("조건 충족"), Instant.now());
            Instant now = Instant.now();

            profile.complete(decision, "{}", now);

            assertThat(profile.status()).isEqualTo(AnalysisStatus.COMPLETED);
            assertThat(profile.decision()).isEqualTo(decision);
            assertThat(profile.rawAnalysisJson()).isEqualTo("{}");
            assertThat(profile.errorMessage()).isNull();
        }

        @Test
        @DisplayName("AGGREGATING이 아닌 상태에서 complete 호출 시 예외가 발생한다")
        void completeFromNonAggregatingThrows() {
            ProductProfile profile = analyzingProfile();
            InspectionDecision decision =
                    InspectionDecision.autoApprove(0.96, List.of(), Instant.now());

            assertThatThrownBy(() -> profile.complete(decision, "{}", Instant.now()))
                    .isInstanceOf(InvalidProfileStateException.class);
        }
    }

    @Nested
    @DisplayName("fail() - 실패 처리")
    class FailTest {

        @Test
        @DisplayName("어떤 상태에서든 실패 처리가 가능하다")
        void failFromAnyState() {
            ProductProfile pendingProfile =
                    ProductIntelligenceFixtures.existingPendingProductProfile();
            ProductProfile analyzingProfile = analyzingProfile();
            Instant now = Instant.now();

            pendingProfile.fail("에러 메시지", now);
            analyzingProfile.fail("에러 메시지", now);

            assertThat(pendingProfile.status()).isEqualTo(AnalysisStatus.FAILED);
            assertThat(analyzingProfile.status()).isEqualTo(AnalysisStatus.FAILED);
        }

        @Test
        @DisplayName("실패 처리 시 에러 메시지가 저장된다")
        void failSavesErrorMessage() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            profile.fail("분석 실패 원인", Instant.now());

            assertThat(profile.errorMessage()).isEqualTo("분석 실패 원인");
        }
    }

    @Nested
    @DisplayName("expire() - 프로파일 만료 처리")
    class ExpireTest {

        @Test
        @DisplayName("만료 처리 후 isExpired가 true이다")
        void expireProfileIsExpired() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            assertThat(profile.isExpired()).isFalse();

            profile.expire(Instant.now());

            assertThat(profile.isExpired()).isTrue();
        }
    }

    @Nested
    @DisplayName("canExecuteAnalysis() - 분석 실행 가능 여부")
    class CanExecuteAnalysisTest {

        @Test
        @DisplayName("ANALYZING 상태이고 해당 타입이 미완료이면 실행 가능하다")
        void canExecuteWhenAnalyzingAndNotCompleted() {
            ProductProfile profile = analyzingProfile();

            assertThat(profile.canExecuteAnalysis(AnalysisType.DESCRIPTION)).isTrue();
        }

        @Test
        @DisplayName("ANALYZING 상태이지만 해당 타입이 완료되었으면 실행 불가하다")
        void cannotExecuteWhenAlreadyCompleted() {
            ProductProfile profile = analyzingProfile();
            profile.recordDescriptionAnalysis(List.of(), Instant.now());

            assertThat(profile.canExecuteAnalysis(AnalysisType.DESCRIPTION)).isFalse();
        }

        @Test
        @DisplayName("ANALYZING이 아닌 상태에서 실행 불가하다")
        void cannotExecuteWhenNotAnalyzing() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            assertThat(profile.canExecuteAnalysis(AnalysisType.DESCRIPTION)).isFalse();
        }
    }

    @Nested
    @DisplayName("hasExpectedStatus() - 기대 상태 확인")
    class HasExpectedStatusTest {

        @Test
        @DisplayName("현재 상태가 기대 상태와 일치하면 true이다")
        void matchingStatusReturnsTrue() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            assertThat(profile.hasExpectedStatus(AnalysisStatus.PENDING)).isTrue();
        }

        @Test
        @DisplayName("현재 상태가 기대 상태와 다르면 false이다")
        void nonMatchingStatusReturnsFalse() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            assertThat(profile.hasExpectedStatus(AnalysisStatus.ANALYZING)).isFalse();
        }
    }

    @Nested
    @DisplayName("shouldCarryForward() - 이전 결과 이월 여부")
    class ShouldCarryForwardTest {

        @Test
        @DisplayName("이전 결과가 있고 데이터가 변경되지 않으면 이월한다")
        void shouldCarryForwardWhenPreviousResultsExistAndNoChange() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();
            List<String> previousResults = List.of("이전결과");

            boolean result = profile.shouldCarryForward(previousResults, false);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("이전 결과가 없으면 이월하지 않는다")
        void shouldNotCarryForwardWhenNoPreviousResults() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();

            boolean result = profile.shouldCarryForward(List.of(), false);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("데이터가 변경되면 이월하지 않는다")
        void shouldNotCarryForwardWhenDataChanged() {
            ProductProfile profile = ProductIntelligenceFixtures.existingPendingProductProfile();
            List<String> previousResults = List.of("이전결과");

            boolean result = profile.shouldCarryForward(previousResults, true);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("updateOptionSuggestions() / updateNoticeSuggestions() - 제안 업데이트")
    class UpdateSuggestionsTest {

        @Test
        @DisplayName("옵션 제안 목록을 업데이트한다")
        void updateOptionSuggestions() {
            ProductProfile profile = ProductIntelligenceFixtures.completedProductProfile();
            List<OptionMappingSuggestion> newSuggestions =
                    List.of(
                            new OptionMappingSuggestion(
                                    20L,
                                    200L,
                                    "사이즈",
                                    2L,
                                    20L,
                                    "M",
                                    ConfidenceScore.of(0.95),
                                    AnalysisSource.RULE_ENGINE,
                                    true));

            profile.updateOptionSuggestions(newSuggestions);

            assertThat(profile.optionSuggestions()).hasSize(1);
            assertThat(profile.optionSuggestions().get(0).sellerOptionGroupId()).isEqualTo(20L);
        }

        @Test
        @DisplayName("고시정보 제안 목록을 업데이트한다")
        void updateNoticeSuggestions() {
            ProductProfile profile = ProductIntelligenceFixtures.completedProductProfile();
            List<NoticeSuggestion> newSuggestions =
                    List.of(
                            new NoticeSuggestion(
                                    2L,
                                    "원산지",
                                    null,
                                    "중국",
                                    ConfidenceScore.of(0.95),
                                    AnalysisSource.LLM_INFERENCE,
                                    true));

            profile.updateNoticeSuggestions(newSuggestions);

            assertThat(profile.noticeSuggestions()).hasSize(1);
            assertThat(profile.noticeSuggestions().get(0).noticeFieldId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    class ImmutabilityTest {

        @Test
        @DisplayName("extractedAttributes는 불변 리스트이다")
        void extractedAttributesIsUnmodifiable() {
            ProductProfile profile = ProductIntelligenceFixtures.completedProductProfile();

            assertThatThrownBy(
                            () ->
                                    profile.extractedAttributes()
                                            .add(
                                                    ExtractedAttribute.of(
                                                            "키",
                                                            "값",
                                                            0.9,
                                                            AnalysisSource.DESCRIPTION_TEXT,
                                                            Instant.now())))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("completedAnalysisTypes는 불변 셋이다")
        void completedAnalysisTypesIsUnmodifiable() {
            ProductProfile profile = analyzingProfile();

            assertThatThrownBy(() -> profile.completedAnalysisTypes().add(AnalysisType.DESCRIPTION))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // ────────────────────────────────────────────────
    // 헬퍼 메서드
    // ────────────────────────────────────────────────

    private ProductProfile analyzingProfile() {
        Instant now = Instant.now();
        ProductProfile profile = ProductProfile.forNewAnalyzing(100L, null, 1, now);
        return profile;
    }

    private ProductProfile aggregatingProfile() {
        ProductProfile profile = analyzingProfile();
        Instant now = Instant.now();
        profile.recordDescriptionAnalysis(List.of(), now);
        profile.recordOptionAnalysis(List.of(), now);
        profile.recordNoticeAnalysis(List.of(), now);
        profile.startAggregating(now);
        return profile;
    }
}
