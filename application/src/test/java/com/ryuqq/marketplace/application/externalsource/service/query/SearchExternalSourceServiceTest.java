package com.ryuqq.marketplace.application.externalsource.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.externalsource.ExternalSourceQueryFixtures;
import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourcePageResult;
import com.ryuqq.marketplace.application.externalsource.factory.ExternalSourceQueryFactory;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.domain.externalsource.ExternalSourceFixtures;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSearchCriteria;
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
@DisplayName("SearchExternalSourceService 단위 테스트")
class SearchExternalSourceServiceTest {

    @InjectMocks private SearchExternalSourceService sut;

    @Mock private ExternalSourceQueryFactory queryFactory;
    @Mock private ExternalSourceReadManager readManager;

    @Nested
    @DisplayName("execute() - 외부 소스 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 외부 소스 목록을 조회하고 페이징 결과를 반환한다")
        void execute_ValidParams_ReturnsPageResult() {
            // given
            ExternalSourceSearchParams params = ExternalSourceQueryFixtures.searchParams();
            ExternalSourceSearchCriteria criteria =
                    Mockito.mock(ExternalSourceSearchCriteria.class);
            List<ExternalSource> sources =
                    List.of(
                            ExternalSourceFixtures.activeExternalSource(1L),
                            ExternalSourceFixtures.activeExternalSource(2L));
            long totalElements = 2L;

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(sources);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            ExternalSourcePageResult result = sut.execute(params);

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
            ExternalSourceSearchParams params = ExternalSourceQueryFixtures.searchParams();
            ExternalSourceSearchCriteria criteria =
                    Mockito.mock(ExternalSourceSearchCriteria.class);

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(List.of());
            given(readManager.countByCriteria(criteria)).willReturn(0L);

            // when
            ExternalSourcePageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
