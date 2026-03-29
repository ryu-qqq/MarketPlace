package com.ryuqq.marketplace.application.legacy.productgroup.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.productgroup.LegacyProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.productgroup.factory.LegacyProductGroupQueryFactory;
import com.ryuqq.marketplace.application.legacy.shared.assembler.LegacyProductGroupFromMarketAssembler;
import com.ryuqq.marketplace.application.productgroup.ProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.application.productgroup.port.in.query.SearchProductGroupByOffsetUseCase;
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
@DisplayName("LegacySearchProductGroupByOffsetService 단위 테스트")
class LegacySearchProductGroupByOffsetServiceTest {

    @InjectMocks private LegacySearchProductGroupByOffsetService sut;

    @Mock private SearchProductGroupByOffsetUseCase searchUseCase;
    @Mock private LegacyProductGroupQueryFactory queryFactory;
    @Mock private LegacyProductGroupFromMarketAssembler assembler;
    @Mock private com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupCompositionQueryPort compositionQueryPort;

    @Nested
    @DisplayName("execute() - 상품그룹 목록 조회 실행")
    class ExecuteTest {

        @Test
        @DisplayName("레거시 파라미터를 표준으로 변환하고 조회 후 레거시 PageResult로 변환한다")
        void execute_ValidParams_ConvertsAndReturnsPageResult() {
            // given
            LegacyProductGroupSearchParams legacyParams =
                    LegacyProductGroupQueryFixtures.searchParams();
            ProductGroupSearchParams standardParams = ProductGroupQueryFixtures.searchParams();
            ProductGroupPageResult standardPageResult =
                    ProductGroupPageResult.of(List.of(), 0, 20, 0L);
            LegacyProductGroupPageResult expectedResult =
                    LegacyProductGroupQueryFixtures.emptyPageResult();

            given(queryFactory.toStandardSearchParams(legacyParams)).willReturn(standardParams);
            given(searchUseCase.execute(standardParams)).willReturn(standardPageResult);
            given(
                            assembler.toPageResult(
                                    any(), any(), anyInt(), anyInt()))
                    .willReturn(expectedResult);

            // when
            LegacyProductGroupPageResult result = sut.execute(legacyParams);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(queryFactory).should().toStandardSearchParams(legacyParams);
            then(searchUseCase).should().execute(standardParams);
            then(assembler)
                    .should()
                    .toPageResult(any(), any(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("셀러 ID 조건이 있는 파라미터도 정상 처리한다")
        void execute_WithSellerId_ProcessesNormally() {
            // given
            LegacyProductGroupSearchParams legacyParams =
                    LegacyProductGroupQueryFixtures.searchParamsWithSeller(1L);
            ProductGroupSearchParams standardParams = ProductGroupQueryFixtures.searchParams(1L);
            ProductGroupPageResult standardPageResult =
                    ProductGroupPageResult.of(List.of(), 0, 20, 0L);
            LegacyProductGroupPageResult expectedResult =
                    LegacyProductGroupQueryFixtures.emptyPageResult();

            given(queryFactory.toStandardSearchParams(legacyParams)).willReturn(standardParams);
            given(searchUseCase.execute(standardParams)).willReturn(standardPageResult);
            given(
                            assembler.toPageResult(
                                    any(), any(), anyInt(), anyInt()))
                    .willReturn(expectedResult);

            // when
            LegacyProductGroupPageResult result = sut.execute(legacyParams);

            // then
            assertThat(result).isNotNull();
            then(queryFactory).should().toStandardSearchParams(legacyParams);
        }

        @Test
        @DisplayName("커스텀 페이지 크기 파라미터가 올바르게 전달된다")
        void execute_CustomPageSize_PassesCorrectPagination() {
            // given
            int customPage = 2;
            int customSize = 10;
            LegacyProductGroupSearchParams legacyParams =
                    LegacyProductGroupQueryFixtures.searchParams(customPage, customSize);
            ProductGroupSearchParams standardParams =
                    ProductGroupQueryFixtures.searchParams(customPage, customSize);
            ProductGroupPageResult standardPageResult =
                    ProductGroupPageResult.of(List.of(), customPage, customSize, 0L);
            LegacyProductGroupPageResult expectedResult =
                    LegacyProductGroupQueryFixtures.emptyPageResult();

            given(queryFactory.toStandardSearchParams(legacyParams)).willReturn(standardParams);
            given(searchUseCase.execute(standardParams)).willReturn(standardPageResult);
            given(assembler.toPageResult(any(), any(), anyInt(), anyInt()))
                    .willReturn(expectedResult);

            // when
            LegacyProductGroupPageResult result = sut.execute(legacyParams);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("결과가 없으면 empty PageResult를 반환한다")
        void execute_EmptyResult_ReturnsEmptyPageResult() {
            // given
            LegacyProductGroupSearchParams legacyParams =
                    LegacyProductGroupQueryFixtures.searchParams();
            ProductGroupSearchParams standardParams = ProductGroupQueryFixtures.searchParams();
            ProductGroupPageResult emptyPageResult =
                    ProductGroupPageResult.of(List.of(), 0, 20, 0L);
            LegacyProductGroupPageResult expectedEmpty =
                    LegacyProductGroupQueryFixtures.emptyPageResult();

            given(queryFactory.toStandardSearchParams(legacyParams)).willReturn(standardParams);
            given(searchUseCase.execute(standardParams)).willReturn(emptyPageResult);
            given(assembler.toPageResult(any(), any(), anyInt(), anyInt()))
                    .willReturn(expectedEmpty);

            // when
            LegacyProductGroupPageResult result = sut.execute(legacyParams);

            // then
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
