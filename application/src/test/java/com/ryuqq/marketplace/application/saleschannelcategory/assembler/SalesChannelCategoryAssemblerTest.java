package com.ryuqq.marketplace.application.saleschannelcategory.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryResult;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategoryAssembler 단위 테스트")
class SalesChannelCategoryAssemblerTest {

    private final SalesChannelCategoryAssembler sut = new SalesChannelCategoryAssembler();

    @Nested
    @DisplayName("toResult() - SalesChannelCategory Domain → SalesChannelCategoryResult 변환")
    class ToResultTest {

        @Test
        @DisplayName("활성 SalesChannelCategory를 SalesChannelCategoryResult로 변환한다")
        void toResult_ActiveCategory_ReturnsResult() {
            // given
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();

            // when
            SalesChannelCategoryResult result = sut.toResult(category);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(category.idValue());
            assertThat(result.salesChannelId()).isEqualTo(category.salesChannelId());
            assertThat(result.externalCategoryCode()).isEqualTo(category.externalCategoryCode());
            assertThat(result.externalCategoryName()).isEqualTo(category.externalCategoryName());
            assertThat(result.parentId()).isEqualTo(category.parentId());
            assertThat(result.depth()).isEqualTo(category.depth());
            assertThat(result.path()).isEqualTo(category.path());
            assertThat(result.sortOrder()).isEqualTo(category.sortOrder());
            assertThat(result.leaf()).isEqualTo(category.isLeaf());
            assertThat(result.status()).isEqualTo(category.status().name());
            assertThat(result.createdAt()).isEqualTo(category.createdAt());
            assertThat(result.updatedAt()).isEqualTo(category.updatedAt());
        }

        @Test
        @DisplayName("비활성 SalesChannelCategory를 변환하면 status가 INACTIVE이다")
        void toResult_InactiveCategory_ReturnsInactiveResult() {
            // given
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.inactiveSalesChannelCategory();

            // when
            SalesChannelCategoryResult result = sut.toResult(category);

            // then
            assertThat(result.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("말단 카테고리를 변환하면 leaf가 true이다")
        void toResult_LeafCategory_ReturnsLeafResult() {
            // given
            SalesChannelCategory category = SalesChannelCategoryFixtures.leafCategory();

            // when
            SalesChannelCategoryResult result = sut.toResult(category);

            // then
            assertThat(result.leaf()).isTrue();
            assertThat(result.depth()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("toResults() - SalesChannelCategory List → SalesChannelCategoryResult List 변환")
    class ToResultsTest {

        @Test
        @DisplayName("SalesChannelCategory 목록을 SalesChannelCategoryResult 목록으로 변환한다")
        void toResults_ReturnsList() {
            // given
            List<SalesChannelCategory> categories =
                    List.of(
                            SalesChannelCategoryFixtures.activeSalesChannelCategory(1L),
                            SalesChannelCategoryFixtures.activeSalesChannelCategory(2L),
                            SalesChannelCategoryFixtures.inactiveSalesChannelCategory());

            // when
            List<SalesChannelCategoryResult> results = sut.toResults(categories);

            // then
            assertThat(results).hasSize(3);
            assertThat(results.get(0).id()).isEqualTo(1L);
            assertThat(results.get(1).id()).isEqualTo(2L);
            assertThat(results.get(2).status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("빈 목록을 변환하면 빈 결과를 반환한다")
        void toResults_EmptyList_ReturnsEmpty() {
            // given
            List<SalesChannelCategory> categories = List.of();

            // when
            List<SalesChannelCategoryResult> results = sut.toResults(categories);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 생성")
    class ToPageResultTest {

        @Test
        @DisplayName("SalesChannelCategory 목록으로 PageResult를 생성한다")
        void toPageResult_ReturnsPageResult() {
            // given
            List<SalesChannelCategory> categories =
                    List.of(SalesChannelCategoryFixtures.activeSalesChannelCategory());
            int page = 0;
            int size = 20;
            long totalElements = 1L;

            // when
            SalesChannelCategoryPageResult result =
                    sut.toPageResult(categories, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(1);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("빈 목록으로 빈 PageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            List<SalesChannelCategory> categories = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            SalesChannelCategoryPageResult result =
                    sut.toPageResult(categories, page, size, totalElements);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("다음 페이지가 있으면 hasNext가 true이다")
        void toPageResult_HasMorePages_HasNextIsTrue() {
            // given
            List<SalesChannelCategory> categories =
                    List.of(
                            SalesChannelCategoryFixtures.activeSalesChannelCategory(1L),
                            SalesChannelCategoryFixtures.activeSalesChannelCategory(2L));
            int page = 0;
            int size = 2;
            long totalElements = 10L;

            // when
            SalesChannelCategoryPageResult result =
                    sut.toPageResult(categories, page, size, totalElements);

            // then
            assertThat(result.pageMeta().hasNext()).isTrue();
        }

        @Test
        @DisplayName("마지막 페이지이면 hasNext가 false이다")
        void toPageResult_LastPage_HasNextIsFalse() {
            // given
            List<SalesChannelCategory> categories =
                    List.of(SalesChannelCategoryFixtures.activeSalesChannelCategory(1L));
            int page = 4;
            int size = 2;
            long totalElements = 10L;

            // when
            SalesChannelCategoryPageResult result =
                    sut.toPageResult(categories, page, size, totalElements);

            // then
            assertThat(result.pageMeta().hasNext()).isFalse();
        }
    }
}
