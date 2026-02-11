package com.ryuqq.marketplace.application.brandpreset.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brandpreset.BrandPresetQueryFixtures;
import com.ryuqq.marketplace.application.brandpreset.assembler.BrandPresetAssembler;
import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetQueryFactory;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetReadManager;
import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
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
@DisplayName("SearchBrandPresetByOffsetService 단위 테스트")
class SearchBrandPresetByOffsetServiceTest {

    @InjectMocks private SearchBrandPresetByOffsetService sut;

    @Mock private BrandPresetReadManager readManager;
    @Mock private BrandPresetQueryFactory queryFactory;
    @Mock private BrandPresetAssembler assembler;

    @Nested
    @DisplayName("execute() - 브랜드 프리셋 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 브랜드 프리셋 목록을 조회한다")
        void execute_ValidParams_ReturnsBrandPresetPageResult() {
            // given
            BrandPresetSearchParams params = BrandPresetQueryFixtures.searchParams();
            BrandPresetSearchCriteria criteria = BrandPresetFixtures.defaultSearchCriteria();
            List<BrandPresetResult> results =
                    List.of(
                            BrandPresetQueryFixtures.brandPresetResult(1L),
                            BrandPresetQueryFixtures.brandPresetResult(2L));
            long totalElements = 2L;
            BrandPresetPageResult expectedResult =
                    BrandPresetQueryFixtures.brandPresetPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(results);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(results, criteria.page(), criteria.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            BrandPresetPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler).should().toPageResult(results, criteria.page(), criteria.size(), totalElements);
        }

        @Test
        @DisplayName("판매채널 ID로 필터링하여 조회한다")
        void execute_WithSalesChannelIds_ReturnsFilteredResult() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L);
            BrandPresetSearchParams params =
                    BrandPresetQueryFixtures.searchParams(salesChannelIds);
            BrandPresetSearchCriteria criteria =
                    BrandPresetFixtures.searchCriteriaWithSalesChannel(salesChannelIds);
            List<BrandPresetResult> results =
                    List.of(BrandPresetQueryFixtures.brandPresetResult(1L));
            long totalElements = 1L;
            BrandPresetPageResult expectedResult =
                    BrandPresetQueryFixtures.brandPresetPageResult(0, 20, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(results);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(results, criteria.page(), criteria.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            BrandPresetPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지 결과를 반환한다")
        void execute_NoResults_ReturnsEmptyPageResult() {
            // given
            BrandPresetSearchParams params = BrandPresetQueryFixtures.searchParams();
            BrandPresetSearchCriteria criteria = BrandPresetFixtures.defaultSearchCriteria();
            List<BrandPresetResult> emptyResults = List.of();
            long totalElements = 0L;
            BrandPresetPageResult expectedResult = BrandPresetQueryFixtures.emptyPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyResults);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(emptyResults, criteria.page(), criteria.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            BrandPresetPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
