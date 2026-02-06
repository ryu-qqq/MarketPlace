package com.ryuqq.marketplace.application.commoncodetype.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.commoncodetype.CommonCodeTypeQueryFixtures;
import com.ryuqq.marketplace.application.commoncodetype.assembler.CommonCodeTypeAssembler;
import com.ryuqq.marketplace.application.commoncodetype.dto.query.CommonCodeTypeSearchParams;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypePageResult;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypeResult;
import com.ryuqq.marketplace.application.commoncodetype.factory.CommonCodeTypeQueryFactory;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeReadManager;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
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
@DisplayName("SearchCommonCodeTypeService 단위 테스트")
class SearchCommonCodeTypeServiceTest {

    @InjectMocks private SearchCommonCodeTypeService sut;

    @Mock private CommonCodeTypeReadManager readManager;

    @Mock private CommonCodeTypeQueryFactory queryFactory;

    @Mock private CommonCodeTypeAssembler assembler;

    @Nested
    @DisplayName("execute() - 공통 코드 타입 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 공통 코드 타입 목록을 조회한다")
        void searchCommonCodeTypes_Success() {
            // given
            CommonCodeTypeSearchParams params = CommonCodeTypeQueryFixtures.searchParams();
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();
            List<CommonCodeType> domains = List.of(CommonCodeTypeFixtures.activeCommonCodeType());
            long totalElements = 1L;
            List<CommonCodeTypeResult> results = List.of(CommonCodeTypeResult.from(domains.get(0)));
            CommonCodeTypePageResult pageResult =
                    CommonCodeTypePageResult.of(
                            results, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(domains);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(domains, params.page(), params.size(), totalElements))
                    .willReturn(pageResult);

            // when
            CommonCodeTypePageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(domains, params.page(), params.size(), totalElements);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void searchCommonCodeTypes_Empty_ReturnsEmptyPage() {
            // given
            CommonCodeTypeSearchParams params =
                    CommonCodeTypeQueryFixtures.searchParams(null, "존재하지않음");
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();
            List<CommonCodeType> domains = List.of();
            long totalElements = 0L;
            CommonCodeTypePageResult pageResult =
                    CommonCodeTypePageResult.of(
                            List.of(), params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(domains);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(domains, params.page(), params.size(), totalElements))
                    .willReturn(pageResult);

            // when
            CommonCodeTypePageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("활성화 필터로 공통 코드 타입을 조회한다")
        void searchCommonCodeTypes_WithActiveFilter() {
            // given
            CommonCodeTypeSearchParams params = CommonCodeTypeQueryFixtures.searchParams(true);
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();
            List<CommonCodeType> domains = List.of(CommonCodeTypeFixtures.activeCommonCodeType());
            long totalElements = 1L;
            List<CommonCodeTypeResult> results = List.of(CommonCodeTypeResult.from(domains.get(0)));
            CommonCodeTypePageResult pageResult =
                    CommonCodeTypePageResult.of(
                            results, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(domains);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(domains, params.page(), params.size(), totalElements))
                    .willReturn(pageResult);

            // when
            CommonCodeTypePageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
        }
    }
}
