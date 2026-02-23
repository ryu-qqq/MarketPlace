package com.ryuqq.marketplace.application.inboundbrandmapping.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundbrandmapping.InboundBrandMappingQueryFixtures;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.query.InboundBrandMappingSearchParams;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingPageResult;
import com.ryuqq.marketplace.application.inboundbrandmapping.factory.InboundBrandMappingQueryFactory;
import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingReadManager;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchCriteria;
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
@DisplayName("SearchInboundBrandMappingService 단위 테스트")
class SearchInboundBrandMappingServiceTest {

    @InjectMocks private SearchInboundBrandMappingService sut;

    @Mock private InboundBrandMappingQueryFactory queryFactory;
    @Mock private InboundBrandMappingReadManager readManager;

    @Nested
    @DisplayName("execute() - 외부 브랜드 매핑 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 매핑 목록을 조회하고 페이징 결과를 반환한다")
        void execute_ValidParams_ReturnsPageResult() {
            // given
            InboundBrandMappingSearchParams params =
                    InboundBrandMappingQueryFixtures.searchParams(1L);
            InboundBrandMappingSearchCriteria criteria =
                    Mockito.mock(InboundBrandMappingSearchCriteria.class);
            List<InboundBrandMapping> mappings =
                    List.of(
                            InboundBrandMappingFixtures.activeMapping(1L),
                            InboundBrandMappingFixtures.activeMapping(2L));
            long totalElements = 2L;

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(mappings);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            InboundBrandMappingPageResult result = sut.execute(params);

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
            InboundBrandMappingSearchParams params =
                    InboundBrandMappingQueryFixtures.searchParams();
            InboundBrandMappingSearchCriteria criteria =
                    Mockito.mock(InboundBrandMappingSearchCriteria.class);

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(List.of());
            given(readManager.countByCriteria(criteria)).willReturn(0L);

            // when
            InboundBrandMappingPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
