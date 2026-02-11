package com.ryuqq.marketplace.application.brand.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brand.BrandQueryFixtures;
import com.ryuqq.marketplace.application.brand.assembler.BrandAssembler;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchParams;
import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.factory.BrandQueryFactory;
import com.ryuqq.marketplace.application.brand.manager.BrandReadManager;
import com.ryuqq.marketplace.domain.brand.BrandFixtures;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
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
@DisplayName("SearchBrandByOffsetService 단위 테스트")
class SearchBrandByOffsetServiceTest {

    @InjectMocks private SearchBrandByOffsetService sut;

    @Mock private BrandReadManager readManager;
    @Mock private BrandQueryFactory queryFactory;
    @Mock private BrandAssembler assembler;

    @Nested
    @DisplayName("execute() - 브랜드 검색 실행")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 브랜드 페이지 결과를 조회한다")
        void execute_ValidParams_ReturnsPageResult() {
            // given
            BrandSearchParams params = BrandQueryFixtures.searchParams();
            BrandSearchCriteria criteria = null; // mock 대상
            List<Brand> brands =
                    List.of(BrandFixtures.activeBrand(1L), BrandFixtures.activeBrand(2L));
            long totalElements = 2L;
            BrandPageResult expectedResult =
                    BrandQueryFixtures.brandPageResult(params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(brands);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(brands, params.page(), params.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            BrandPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(brands, params.page(), params.size(), totalElements);
        }

        @Test
        @DisplayName("조건에 맞는 브랜드가 없으면 빈 페이지 결과를 반환한다")
        void execute_NoMatches_ReturnsEmptyPageResult() {
            // given
            BrandSearchParams params = BrandQueryFixtures.searchParams();
            BrandSearchCriteria criteria = null;
            List<Brand> emptyBrands = List.of();
            long totalElements = 0L;
            BrandPageResult emptyResult = BrandQueryFixtures.emptyBrandPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyBrands);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(emptyBrands, params.page(), params.size(), totalElements))
                    .willReturn(emptyResult);

            // when
            BrandPageResult result = sut.execute(params);

            // then
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.results()).isEmpty();
        }

        @Test
        @DisplayName("페이지 크기와 페이지 번호를 올바르게 전달한다")
        void execute_CustomPageSize_UsesCorrectPagination() {
            // given
            int customPage = 1;
            int customSize = 10;
            BrandSearchParams params = BrandQueryFixtures.searchParams(customPage, customSize);
            BrandSearchCriteria criteria = null;
            List<Brand> brands = List.of(BrandFixtures.activeBrand(1L));
            long totalElements = 1L;
            BrandPageResult expectedResult =
                    BrandQueryFixtures.brandPageResult(customPage, customSize, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(brands);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(brands, customPage, customSize, totalElements))
                    .willReturn(expectedResult);

            // when
            BrandPageResult result = sut.execute(params);

            // then
            assertThat(result.pageMeta().page()).isEqualTo(customPage);
            assertThat(result.pageMeta().size()).isEqualTo(customSize);
            then(assembler).should().toPageResult(brands, customPage, customSize, totalElements);
        }

        @Test
        @DisplayName("검색어가 포함된 검색 파라미터로 조회한다")
        void execute_WithSearchWord_ReturnsFilteredResult() {
            // given
            String searchWord = "테스트";
            BrandSearchParams params = BrandQueryFixtures.searchParams(searchWord);
            BrandSearchCriteria criteria = null;
            List<Brand> brands = List.of(BrandFixtures.activeBrand(1L));
            long totalElements = 1L;
            BrandPageResult expectedResult =
                    BrandQueryFixtures.brandPageResult(params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(brands);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(brands, params.page(), params.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            BrandPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            then(queryFactory).should().createCriteria(params);
        }
    }
}
