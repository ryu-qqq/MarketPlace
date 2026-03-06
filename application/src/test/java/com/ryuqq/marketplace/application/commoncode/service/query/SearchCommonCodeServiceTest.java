package com.ryuqq.marketplace.application.commoncode.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.commoncode.CommonCodeQueryFixtures;
import com.ryuqq.marketplace.application.commoncode.assembler.CommonCodeAssembler;
import com.ryuqq.marketplace.application.commoncode.dto.query.CommonCodeSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodeResult;
import com.ryuqq.marketplace.application.commoncode.factory.CommonCodeQueryFactory;
import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeReadManager;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.commoncode.CommonCodeFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSortKey;
import java.util.Collections;
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
@DisplayName("SearchCommonCodeService 단위 테스트")
class SearchCommonCodeServiceTest {

    @InjectMocks private SearchCommonCodeService sut;

    @Mock private CommonCodeReadManager readManager;
    @Mock private CommonCodeQueryFactory queryFactory;
    @Mock private CommonCodeAssembler assembler;

    @Nested
    @DisplayName("execute() - 공통 코드 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 공통 코드 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedResult() {
            // given
            String commonCodeTypeCode = "PAYMENT_METHOD";
            CommonCodeSearchParams params =
                    CommonCodeQueryFixtures.searchParams(commonCodeTypeCode, 0, 20);

            QueryContext<CommonCodeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeSearchCriteria criteria =
                    new CommonCodeSearchCriteria(commonCodeTypeCode, null, queryContext);

            List<CommonCode> codes =
                    List.of(
                            CommonCodeFixtures.activeCommonCode(),
                            CommonCodeFixtures.newCommonCode("DEBIT_CARD", "체크카드"));
            long totalElements = 2L;

            List<CommonCodeResult> results = codes.stream().map(CommonCodeResult::from).toList();
            CommonCodePageResult expected =
                    CommonCodePageResult.of(results, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(codes);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(codes, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            CommonCodePageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.size()).isEqualTo(2);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(codes, params.page(), params.size(), totalElements);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            String commonCodeTypeCode = "PAYMENT_METHOD";
            CommonCodeSearchParams params =
                    CommonCodeQueryFixtures.searchParams(commonCodeTypeCode);

            QueryContext<CommonCodeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeSearchCriteria criteria =
                    new CommonCodeSearchCriteria(commonCodeTypeCode, null, queryContext);

            List<CommonCode> emptyCodes = Collections.emptyList();
            long totalElements = 0L;

            CommonCodePageResult expected = CommonCodePageResult.empty(params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyCodes);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(emptyCodes, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            CommonCodePageResult result = sut.execute(params);

            // then
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.size()).isZero();
        }
    }
}
