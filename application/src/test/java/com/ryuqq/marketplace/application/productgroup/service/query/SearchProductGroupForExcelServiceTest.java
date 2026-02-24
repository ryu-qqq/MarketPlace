package com.ryuqq.marketplace.application.productgroup.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroup.ProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.productgroup.assembler.ProductGroupAssembler;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupQueryFactory;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.util.List;
import java.util.Map;
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
@DisplayName("SearchProductGroupForExcelService 단위 테스트")
class SearchProductGroupForExcelServiceTest {

    @InjectMocks private SearchProductGroupForExcelService sut;

    @Mock private ProductGroupReadFacade readFacade;
    @Mock private ProductGroupQueryFactory queryFactory;
    @Mock private ProductGroupAssembler assembler;

    @Nested
    @DisplayName("execute() - 엑셀 다운로드용 상품 그룹 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 엑셀 번들을 조회하고 결과 목록을 반환한다")
        void execute_ValidSearchParams_ReturnsExcelResults() {
            // given
            ProductGroupSearchParams params = ProductGroupQueryFixtures.searchParams();
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            ProductGroupExcelBundle bundle =
                    new ProductGroupExcelBundle(
                            List.of(), List.of(), Map.of(), Map.of(), Map.of(), Map.of(), 0L);
            List<ProductGroupExcelCompositeResult> expected = List.of();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getExcelBundle(criteria)).willReturn(bundle);
            given(assembler.toExcelResults(bundle)).willReturn(expected);

            // when
            List<ProductGroupExcelCompositeResult> result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expected);
            then(queryFactory).should().createCriteria(params);
            then(readFacade).should().getExcelBundle(criteria);
            then(assembler).should().toExcelResults(bundle);
        }

        @Test
        @DisplayName("셀러 ID 필터로 검색하면 해당 셀러의 엑셀 결과를 반환한다")
        void execute_WithSellerFilter_ReturnsFilteredExcelResults() {
            // given
            Long sellerId = 1L;
            ProductGroupSearchParams params = ProductGroupQueryFixtures.searchParams(sellerId);
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            ProductGroupExcelBundle bundle =
                    new ProductGroupExcelBundle(
                            List.of(), List.of(), Map.of(), Map.of(), Map.of(), Map.of(), 0L);
            List<ProductGroupExcelCompositeResult> expected = List.of();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getExcelBundle(criteria)).willReturn(bundle);
            given(assembler.toExcelResults(bundle)).willReturn(expected);

            // when
            List<ProductGroupExcelCompositeResult> result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            then(queryFactory).should().createCriteria(params);
            then(readFacade).should().getExcelBundle(criteria);
        }

        @Test
        @DisplayName("상태 필터로 검색하면 해당 상태의 엑셀 결과를 반환한다")
        void execute_WithStatusFilter_ReturnsFilteredExcelResults() {
            // given
            ProductGroupSearchParams params =
                    ProductGroupQueryFixtures.searchParams(List.of("ACTIVE"));
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            ProductGroupExcelBundle bundle =
                    new ProductGroupExcelBundle(
                            List.of(), List.of(), Map.of(), Map.of(), Map.of(), Map.of(), 0L);
            List<ProductGroupExcelCompositeResult> expected = List.of();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getExcelBundle(criteria)).willReturn(bundle);
            given(assembler.toExcelResults(bundle)).willReturn(expected);

            // when
            List<ProductGroupExcelCompositeResult> result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            then(queryFactory).should().createCriteria(params);
        }
    }
}
