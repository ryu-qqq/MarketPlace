package com.ryuqq.marketplace.application.brandpreset.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.brandpreset.BrandPresetQueryFixtures;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandPresetAssembler 단위 테스트")
class BrandPresetAssemblerTest {

    private BrandPresetAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new BrandPresetAssembler();
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 변환")
    class ToPageResultTest {

        @Test
        @DisplayName("Result 목록을 PageResult로 변환한다")
        void toPageResult_ValidResults_ReturnsPageResult() {
            // given
            List<BrandPresetResult> results =
                    List.of(
                            BrandPresetQueryFixtures.brandPresetResult(1L),
                            BrandPresetQueryFixtures.brandPresetResult(2L));
            int page = 0;
            int size = 20;
            long totalElements = 2L;

            // when
            BrandPresetPageResult result = sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("빈 목록으로 빈 PageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            List<BrandPresetResult> emptyResults = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            BrandPresetPageResult result =
                    sut.toPageResult(emptyResults, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).isEmpty();
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.size()).isZero();
        }

        @Test
        @DisplayName("페이지네이션 정보가 올바르게 설정된다")
        void toPageResult_WithPagination_SetsCorrectPageMeta() {
            // given
            List<BrandPresetResult> results =
                    List.of(BrandPresetQueryFixtures.brandPresetResult(1L));
            int page = 2;
            int size = 10;
            long totalElements = 25L;

            // when
            BrandPresetPageResult result = sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
            assertThat(result.pageMeta().totalPages()).isEqualTo(3);
        }
    }
}
