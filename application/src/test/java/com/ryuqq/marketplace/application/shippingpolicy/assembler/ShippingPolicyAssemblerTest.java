package com.ryuqq.marketplace.application.shippingpolicy.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingPolicyAssembler 단위 테스트")
class ShippingPolicyAssemblerTest {

    private final ShippingPolicyAssembler sut = new ShippingPolicyAssembler();

    @Nested
    @DisplayName("toResult() - Domain → Result 변환")
    class ToResultTest {

        @Test
        @DisplayName("ShippingPolicy를 ShippingPolicyResult로 변환한다")
        void toResult_ConvertsToResult() {
            // given
            ShippingPolicy domain = ShippingPolicyFixtures.activeShippingPolicy();

            // when
            ShippingPolicyResult result = sut.toResult(domain);

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
        @DisplayName("ShippingPolicy 목록을 ShippingPolicyResult 목록으로 변환한다")
        void toResults_ConvertsAllToResults() {
            // given
            List<ShippingPolicy> domains =
                    List.of(
                            ShippingPolicyFixtures.activeShippingPolicy(),
                            ShippingPolicyFixtures.newPaidShippingPolicy());

            // when
            List<ShippingPolicyResult> results = sut.toResults(domains);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("빈 목록이면 빈 결과를 반환한다")
        void toResults_EmptyList_ReturnsEmptyList() {
            // given
            List<ShippingPolicy> domains = Collections.emptyList();

            // when
            List<ShippingPolicyResult> results = sut.toResults(domains);

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
            List<ShippingPolicy> domains =
                    List.of(
                            ShippingPolicyFixtures.activeShippingPolicy(),
                            ShippingPolicyFixtures.newPaidShippingPolicy());
            int page = 0;
            int size = 20;
            long totalElements = 100L;

            // when
            ShippingPolicyPageResult result = sut.toPageResult(domains, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
        }

        @Test
        @DisplayName("빈 목록으로 PageResult를 생성한다")
        void toPageResult_EmptyList_CreatesEmptyPageResult() {
            // given
            List<ShippingPolicy> domains = Collections.emptyList();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            ShippingPolicyPageResult result = sut.toPageResult(domains, page, size, totalElements);

            // then
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }
}
