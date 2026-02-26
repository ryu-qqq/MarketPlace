package com.ryuqq.marketplace.application.inboundcategorymapping.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundcategorymapping.InboundCategoryMappingQueryFixtures;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.query.InboundCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingPageResult;
import com.ryuqq.marketplace.application.inboundcategorymapping.factory.InboundCategoryMappingQueryFactory;
import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingReadManager;
import com.ryuqq.marketplace.domain.inboundcategorymapping.InboundCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchCriteria;
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
@DisplayName("SearchInboundCategoryMappingService 단위 테스트")
class SearchInboundCategoryMappingServiceTest {

    @InjectMocks private SearchInboundCategoryMappingService sut;

    @Mock private InboundCategoryMappingQueryFactory queryFactory;
    @Mock private InboundCategoryMappingReadManager readManager;

    @Nested
    @DisplayName("execute() - 외부 카테고리 매핑 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 매핑 목록을 조회하고 페이징 결과를 반환한다")
        void execute_ValidParams_ReturnsPageResult() {
            // given
            InboundCategoryMappingSearchParams params =
                    InboundCategoryMappingQueryFixtures.searchParams(1L);
            InboundCategoryMappingSearchCriteria criteria =
                    Mockito.mock(InboundCategoryMappingSearchCriteria.class);
            List<InboundCategoryMapping> mappings =
                    List.of(
                            InboundCategoryMappingFixtures.activeMapping(1L),
                            InboundCategoryMappingFixtures.activeMapping(2L));
            long totalElements = 2L;

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(mappings);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            InboundCategoryMappingPageResult result = sut.execute(params);

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
            InboundCategoryMappingSearchParams params =
                    InboundCategoryMappingQueryFixtures.searchParams();
            InboundCategoryMappingSearchCriteria criteria =
                    Mockito.mock(InboundCategoryMappingSearchCriteria.class);

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(List.of());
            given(readManager.countByCriteria(criteria)).willReturn(0L);

            // when
            InboundCategoryMappingPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
