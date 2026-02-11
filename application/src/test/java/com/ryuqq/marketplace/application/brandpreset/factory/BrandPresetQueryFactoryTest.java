package com.ryuqq.marketplace.application.brandpreset.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.brandpreset.BrandPresetQueryFixtures;
import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
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
@DisplayName("BrandPresetQueryFactory 단위 테스트")
class BrandPresetQueryFactoryTest {

    @InjectMocks private BrandPresetQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - SearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_ReturnsCriteria() {
            // given
            BrandPresetSearchParams params = BrandPresetQueryFixtures.searchParams();
            mockCommonVoFactory();

            // when
            BrandPresetSearchCriteria result = sut.createCriteria(params);

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
            BrandPresetSearchParams params = BrandPresetQueryFixtures.searchParams(salesChannelIds);
            mockCommonVoFactory();

            // when
            BrandPresetSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.salesChannelIds()).isEqualTo(salesChannelIds);
        }

        @Test
        @DisplayName("상태 필터가 포함된 Criteria를 생성한다")
        void createCriteria_WithStatuses_ReturnsCriteriaWithFilter() {
            // given
            List<String> statuses = List.of("ACTIVE", "INACTIVE");
            BrandPresetSearchParams params =
                    BrandPresetQueryFixtures.searchParams(null, statuses);
            mockCommonVoFactory();

            // when
            BrandPresetSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statuses()).isEqualTo(statuses);
        }

        @Test
        @DisplayName("검색어가 포함된 Criteria를 생성한다")
        void createCriteria_WithSearch_ReturnsCriteriaWithSearch() {
            // given
            BrandPresetSearchParams params =
                    BrandPresetQueryFixtures.searchParams("presetName", "테스트");
            mockCommonVoFactory();

            // when
            BrandPresetSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.searchField()).isEqualTo("presetName");
            assertThat(result.searchWord()).isEqualTo("테스트");
        }

        private void mockCommonVoFactory() {
            given(commonVoFactory.parseSortDirection(any())).willReturn(SortDirection.DESC);
            given(commonVoFactory.createPageRequest(any(Integer.class), any(Integer.class)))
                    .willAnswer(
                            invocation ->
                                    PageRequest.of(
                                            invocation.getArgument(0), invocation.getArgument(1)));
            given(commonVoFactory.createQueryContext(any(), any(), any(), any(Boolean.class)))
                    .willAnswer(
                            invocation ->
                                    QueryContext.of(
                                            invocation.getArgument(0),
                                            invocation.getArgument(1),
                                            invocation.getArgument(2)));
        }
    }
}
