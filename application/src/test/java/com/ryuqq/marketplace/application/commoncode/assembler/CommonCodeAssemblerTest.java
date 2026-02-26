package com.ryuqq.marketplace.application.commoncode.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodeResult;
import com.ryuqq.marketplace.domain.commoncode.CommonCodeFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeAssembler 단위 테스트")
class CommonCodeAssemblerTest {

    private final CommonCodeAssembler sut = new CommonCodeAssembler();

    @Nested
    @DisplayName("toResult() - Domain → Result 변환")
    class ToResultTest {

        @Test
        @DisplayName("CommonCode를 CommonCodeResult로 변환한다")
        void toResult_ConvertsToResult() {
            // given
            CommonCode domain = CommonCodeFixtures.activeCommonCode();

            // when
            CommonCodeResult result = sut.toResult(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(domain.id().value());
            assertThat(result.code()).isEqualTo(domain.code().value());
            assertThat(result.displayName()).isEqualTo(domain.displayName().value());
        }
    }

    @Nested
    @DisplayName("toResults() - Domain List → Result List 변환")
    class ToResultsTest {

        @Test
        @DisplayName("CommonCode 목록을 CommonCodeResult 목록으로 변환한다")
        void toResults_ConvertsAllToResults() {
            // given
            List<CommonCode> domains =
                    List.of(
                            CommonCodeFixtures.activeCommonCode(),
                            CommonCodeFixtures.newCommonCode("DEBIT_CARD", "체크카드"));

            // when
            List<CommonCodeResult> results = sut.toResults(domains);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("빈 목록이면 빈 결과를 반환한다")
        void toResults_EmptyList_ReturnsEmptyList() {
            // given
            List<CommonCode> domains = Collections.emptyList();

            // when
            List<CommonCodeResult> results = sut.toResults(domains);

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
            List<CommonCode> domains =
                    List.of(
                            CommonCodeFixtures.activeCommonCode(),
                            CommonCodeFixtures.newCommonCode("DEBIT_CARD", "체크카드"));
            int page = 0;
            int size = 20;
            long totalElements = 100L;

            // when
            CommonCodePageResult result = sut.toPageResult(domains, page, size, totalElements);

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
            List<CommonCode> domains = Collections.emptyList();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            CommonCodePageResult result = sut.toPageResult(domains, page, size, totalElements);

            // then
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }
}
