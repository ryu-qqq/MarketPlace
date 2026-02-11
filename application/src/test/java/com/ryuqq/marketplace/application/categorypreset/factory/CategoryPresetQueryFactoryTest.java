package com.ryuqq.marketplace.application.categorypreset.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetQueryFixtures;
import com.ryuqq.marketplace.application.categorypreset.dto.query.CategoryPresetSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryPresetQueryFactory 단위 테스트")
class CategoryPresetQueryFactoryTest {

    @InjectMocks private CategoryPresetQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - SearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_ReturnsCriteria() {
            // given
            CategoryPresetSearchParams params = CategoryPresetQueryFixtures.searchParams();
            mockCommonVoFactory();

            // when
            CategoryPresetSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext().pageRequest().page()).isEqualTo(params.page());
            assertThat(result.queryContext().pageRequest().size()).isEqualTo(params.size());
        }

        @Test
        @DisplayName("판매채널 필터가 포함된 Criteria를 생성한다")
        void createCriteria_WithSalesChannels_ReturnsCriteriaWithFilter() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L);
            CategoryPresetSearchParams params =
                    CategoryPresetQueryFixtures.searchParams(salesChannelIds);
            mockCommonVoFactory();

            // when
            CategoryPresetSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.salesChannelIds()).isEqualTo(salesChannelIds);
        }

        @Test
        @DisplayName("상태 필터가 포함된 Criteria를 생성한다")
        void createCriteria_WithStatuses_ReturnsCriteriaWithFilter() {
            // given
            List<String> statuses = List.of("ACTIVE", "INACTIVE");
            CategoryPresetSearchParams params =
                    CategoryPresetQueryFixtures.searchParams(null, statuses);
            mockCommonVoFactory();

            // when
            CategoryPresetSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statuses()).isEqualTo(statuses);
        }

        @Test
        @DisplayName("검색어가 포함된 Criteria를 생성한다")
        void createCriteria_WithSearch_ReturnsCriteriaWithSearch() {
            // given
            CategoryPresetSearchParams params =
                    CategoryPresetQueryFixtures.searchParams("presetName", "테스트");
            mockCommonVoFactory();

            // when
            CategoryPresetSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.searchField()).isEqualTo("presetName");
            assertThat(result.searchWord()).isEqualTo("테스트");
        }

        @Test
        @DisplayName("페이징 정보가 포함된 Criteria를 생성한다")
        void createCriteria_WithPaging_ReturnsCriteriaWithPaging() {
            // given
            int page = 2;
            int size = 50;
            CategoryPresetSearchParams params = CategoryPresetQueryFixtures.searchParams(page, size);
            mockCommonVoFactory(page, size);

            // when
            CategoryPresetSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext().pageRequest().page()).isEqualTo(page);
            assertThat(result.queryContext().pageRequest().size()).isEqualTo(size);
        }

        private void mockCommonVoFactory() {
            mockCommonVoFactory(0, 20);
        }

        private void mockCommonVoFactory(int page, int size) {
            given(commonVoFactory.parseSortDirection(any())).willReturn(SortDirection.DESC);
            given(commonVoFactory.createPageRequest(page, size))
                    .willReturn(PageRequest.of(page, size));
            given(commonVoFactory.createQueryContext(
                            any(CategoryPresetSortKey.class),
                            any(SortDirection.class),
                            any(PageRequest.class),
                            any(Boolean.class)))
                    .willReturn(
                            QueryContext.of(
                                    CategoryPresetSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(page, size)));
        }
    }
}
