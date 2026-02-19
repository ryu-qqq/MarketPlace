package com.ryuqq.marketplace.application.externalbrandmapping.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.externalbrandmapping.ExternalBrandMappingQueryFixtures;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingPageResult;
import com.ryuqq.marketplace.application.externalbrandmapping.factory.ExternalBrandMappingQueryFactory;
import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingReadManager;
import com.ryuqq.marketplace.domain.externalbrandmapping.ExternalBrandMappingFixtures;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchCriteria;
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
@DisplayName("SearchExternalBrandMappingService 단위 테스트")
class SearchExternalBrandMappingServiceTest {

    @InjectMocks private SearchExternalBrandMappingService sut;

    @Mock private ExternalBrandMappingQueryFactory queryFactory;
    @Mock private ExternalBrandMappingReadManager readManager;

    @Nested
    @DisplayName("execute() - 외부 브랜드 매핑 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 매핑 목록을 조회하고 페이징 결과를 반환한다")
        void execute_ValidParams_ReturnsPageResult() {
            // given
            ExternalBrandMappingSearchParams params =
                    ExternalBrandMappingQueryFixtures.searchParams(1L);
            ExternalBrandMappingSearchCriteria criteria =
                    Mockito.mock(ExternalBrandMappingSearchCriteria.class);
            List<ExternalBrandMapping> mappings =
                    List.of(
                            ExternalBrandMappingFixtures.activeMapping(1L),
                            ExternalBrandMappingFixtures.activeMapping(2L));
            long totalElements = 2L;

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(mappings);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            ExternalBrandMappingPageResult result = sut.execute(params);

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
            ExternalBrandMappingSearchParams params =
                    ExternalBrandMappingQueryFixtures.searchParams();
            ExternalBrandMappingSearchCriteria criteria =
                    Mockito.mock(ExternalBrandMappingSearchCriteria.class);

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(List.of());
            given(readManager.countByCriteria(criteria)).willReturn(0L);

            // when
            ExternalBrandMappingPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
