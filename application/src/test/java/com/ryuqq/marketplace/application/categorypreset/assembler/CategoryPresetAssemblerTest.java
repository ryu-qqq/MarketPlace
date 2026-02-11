package com.ryuqq.marketplace.application.categorypreset.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetQueryFixtures;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPresetAssembler 단위 테스트")
class CategoryPresetAssemblerTest {

    private final CategoryPresetAssembler sut = new CategoryPresetAssembler();

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 생성")
    class ToPageResultTest {

        @Test
        @DisplayName("Result 목록으로 PageResult를 생성한다")
        void toPageResult_ReturnsPageResult() {
            // given
            List<CategoryPresetResult> results =
                    List.of(
                            CategoryPresetQueryFixtures.categoryPresetResult(1L),
                            CategoryPresetQueryFixtures.categoryPresetResult(2L));
            int page = 0;
            int size = 20;
            long totalElements = 2L;

            // when
            CategoryPresetPageResult result = sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
        }

        @Test
        @DisplayName("빈 목록으로 빈 PageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            List<CategoryPresetResult> results = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            CategoryPresetPageResult result = sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("다음 페이지가 있는지 확인한다")
        void toPageResult_HasMorePages_ChecksCorrectly() {
            // given
            List<CategoryPresetResult> results =
                    List.of(
                            CategoryPresetQueryFixtures.categoryPresetResult(1L),
                            CategoryPresetQueryFixtures.categoryPresetResult(2L));
            int page = 0;
            int size = 2;
            long totalElements = 10L;

            // when
            CategoryPresetPageResult result = sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(result.pageMeta().hasNext()).isTrue();
        }

        @Test
        @DisplayName("마지막 페이지인지 확인한다")
        void toPageResult_LastPage_ChecksCorrectly() {
            // given
            List<CategoryPresetResult> results =
                    List.of(CategoryPresetQueryFixtures.categoryPresetResult(1L));
            int page = 4;
            int size = 2;
            long totalElements = 10L;

            // when
            CategoryPresetPageResult result = sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(result.pageMeta().hasNext()).isFalse();
        }
    }
}
