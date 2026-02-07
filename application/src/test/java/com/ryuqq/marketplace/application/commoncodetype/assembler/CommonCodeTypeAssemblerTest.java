package com.ryuqq.marketplace.application.commoncodetype.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypePageResult;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypeResult;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeTypeAssembler 단위 테스트")
class CommonCodeTypeAssemblerTest {

    private final CommonCodeTypeAssembler sut = new CommonCodeTypeAssembler();

    @Nested
    @DisplayName("toResult() - Domain → Result 변환")
    class ToResultTest {

        @Test
        @DisplayName("CommonCodeType을 CommonCodeTypeResult로 변환한다")
        void toResult_ReturnsCommonCodeTypeResult() {
            // given
            CommonCodeType domain = CommonCodeTypeFixtures.activeCommonCodeType();

            // when
            CommonCodeTypeResult result = sut.toResult(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(domain.idValue());
            assertThat(result.code()).isEqualTo(domain.code());
            assertThat(result.name()).isEqualTo(domain.name());
            assertThat(result.description()).isEqualTo(domain.description());
            assertThat(result.displayOrder()).isEqualTo(domain.displayOrder());
            assertThat(result.active()).isEqualTo(domain.isActive());
        }

        @Test
        @DisplayName("비활성 상태의 CommonCodeType을 변환한다")
        void toResult_InactiveDomain_ReturnsInactiveResult() {
            // given
            CommonCodeType domain = CommonCodeTypeFixtures.inactiveCommonCodeType();

            // when
            CommonCodeTypeResult result = sut.toResult(domain);

            // then
            assertThat(result.active()).isFalse();
        }
    }

    @Nested
    @DisplayName("toResults() - Domain List → Result List 변환")
    class ToResultsTest {

        @Test
        @DisplayName("CommonCodeType 목록을 CommonCodeTypeResult 목록으로 변환한다")
        void toResults_ReturnsCommonCodeTypeResults() {
            // given
            List<CommonCodeType> domains =
                    List.of(
                            CommonCodeTypeFixtures.activeCommonCodeType(1L),
                            CommonCodeTypeFixtures.activeCommonCodeType(2L),
                            CommonCodeTypeFixtures.inactiveCommonCodeType());

            // when
            List<CommonCodeTypeResult> results = sut.toResults(domains);

            // then
            assertThat(results).hasSize(3);
            assertThat(results.get(0).id()).isEqualTo(1L);
            assertThat(results.get(1).id()).isEqualTo(2L);
            assertThat(results.get(2).active()).isFalse();
        }

        @Test
        @DisplayName("빈 목록을 변환하면 빈 결과를 반환한다")
        void toResults_EmptyList_ReturnsEmptyResults() {
            // given
            List<CommonCodeType> domains = List.of();

            // when
            List<CommonCodeTypeResult> results = sut.toResults(domains);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 생성")
    class ToPageResultTest {

        @Test
        @DisplayName("Domain 목록으로 PageResult를 생성한다")
        void toPageResult_ReturnsPageResult() {
            // given
            List<CommonCodeType> domains = List.of(CommonCodeTypeFixtures.activeCommonCodeType());
            int page = 0;
            int size = 20;
            long totalElements = 1L;

            // when
            CommonCodeTypePageResult result = sut.toPageResult(domains, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("빈 Domain 목록으로 빈 PageResult를 생성한다")
        void toPageResult_EmptyDomains_ReturnsEmptyPageResult() {
            // given
            List<CommonCodeType> domains = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            CommonCodeTypePageResult result = sut.toPageResult(domains, page, size, totalElements);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("여러 페이지가 있는 경우 PageResult를 생성한다")
        void toPageResult_MultiplePages_ReturnsCorrectPageResult() {
            // given
            List<CommonCodeType> domains =
                    List.of(
                            CommonCodeTypeFixtures.activeCommonCodeType(1L),
                            CommonCodeTypeFixtures.activeCommonCodeType(2L));
            int page = 1;
            int size = 2;
            long totalElements = 10L;

            // when
            CommonCodeTypePageResult result = sut.toPageResult(domains, page, size, totalElements);

            // then
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }
    }
}
