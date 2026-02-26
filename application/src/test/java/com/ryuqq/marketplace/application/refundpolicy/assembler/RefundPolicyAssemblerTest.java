package com.ryuqq.marketplace.application.refundpolicy.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundPolicyAssembler 단위 테스트")
class RefundPolicyAssemblerTest {

    private final RefundPolicyAssembler sut = new RefundPolicyAssembler();

    @Nested
    @DisplayName("toResult() - Domain → Result 변환")
    class ToResultTest {

        @Test
        @DisplayName("RefundPolicy를 RefundPolicyResult로 변환한다")
        void toResult_ConvertsToResult() {
            // given
            RefundPolicy domain = RefundPolicyFixtures.activeRefundPolicy();

            // when
            RefundPolicyResult result = sut.toResult(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.policyId()).isEqualTo(domain.id().value());
            assertThat(result.policyName()).isEqualTo(domain.policyName().value());
        }
    }

    @Nested
    @DisplayName("toResults() - Domain List → Result List 변환")
    class ToResultsTest {

        @Test
        @DisplayName("RefundPolicy 목록을 RefundPolicyResult 목록으로 변환한다")
        void toResults_ConvertsAllToResults() {
            // given
            List<RefundPolicy> domains =
                    List.of(
                            RefundPolicyFixtures.activeRefundPolicy(),
                            RefundPolicyFixtures.newRefundPolicy(7, 14));

            // when
            List<RefundPolicyResult> results = sut.toResults(domains);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("빈 목록이면 빈 결과를 반환한다")
        void toResults_EmptyList_ReturnsEmptyList() {
            // given
            List<RefundPolicy> domains = Collections.emptyList();

            // when
            List<RefundPolicyResult> results = sut.toResults(domains);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - Domain List → PageResult 변환")
    class ToPageResultTest {

        @Test
        @DisplayName("Domain 목록과 페이징 정보로 PageResult를 생성한다")
        void toPageResult_CreatesPageResult() {
            // given
            List<RefundPolicy> domains =
                    List.of(
                            RefundPolicyFixtures.activeRefundPolicy(),
                            RefundPolicyFixtures.newRefundPolicy(7, 14));
            int page = 0;
            int size = 20;
            long totalElements = 100L;

            // when
            RefundPolicyPageResult result = sut.toPageResult(domains, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("빈 목록으로 PageResult를 생성한다")
        void toPageResult_EmptyList_CreatesEmptyPageResult() {
            // given
            List<RefundPolicy> domains = Collections.emptyList();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            RefundPolicyPageResult result = sut.toPageResult(domains, page, size, totalElements);

            // then
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
