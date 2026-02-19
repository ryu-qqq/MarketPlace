package com.ryuqq.marketplace.application.externalcategorymapping.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.externalcategorymapping.ExternalCategoryMappingQueryFixtures;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingPageResult;
import com.ryuqq.marketplace.application.externalcategorymapping.factory.ExternalCategoryMappingQueryFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingReadManager;
import com.ryuqq.marketplace.domain.externalcategorymapping.ExternalCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSearchCriteria;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchExternalCategoryMappingService 단위 테스트")
class SearchExternalCategoryMappingServiceTest {

    @InjectMocks private SearchExternalCategoryMappingService sut;

    @Mock private ExternalCategoryMappingQueryFactory queryFactory;
    @Mock private ExternalCategoryMappingReadManager readManager;

    @Nested
    @DisplayName("execute() - 외부 카테고리 매핑 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 매핑 목록을 조회하고 페이징 결과를 반환한다")
        void execute_ValidParams_ReturnsPageResult() {
            // given
            ExternalCategoryMappingSearchParams params =
                    ExternalCategoryMappingQueryFixtures.searchParams(1L);
            ExternalCategoryMappingSearchCriteria criteria =
                    Mockito.mock(ExternalCategoryMappingSearchCriteria.class);
            List<ExternalCategoryMapping> mappings =
                    List.of(
                            ExternalCategoryMappingFixtures.activeMapping(1L),
                            ExternalCategoryMappingFixtures.activeMapping(2L));
            long totalElements = 2L;

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(mappings);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            ExternalCategoryMappingPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(2);
            then(queryFactory).should().createSearchCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이징 결과를 반환한다")
        void execute_EmptyResult_ReturnsEmptyPageResult() {
            // given
            ExternalCategoryMappingSearchParams params =
                    ExternalCategoryMappingQueryFixtures.searchParams();
            ExternalCategoryMappingSearchCriteria criteria =
                    Mockito.mock(ExternalCategoryMappingSearchCriteria.class);

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(List.of());
            given(readManager.countByCriteria(criteria)).willReturn(0L);

            // when
            ExternalCategoryMappingPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
