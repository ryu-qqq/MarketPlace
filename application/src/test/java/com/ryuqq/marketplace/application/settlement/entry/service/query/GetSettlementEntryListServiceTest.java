package com.ryuqq.marketplace.application.settlement.entry.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.settlement.SettlementEntryQueryFixtures;
import com.ryuqq.marketplace.application.settlement.dto.response.SettlementEntryPageResult;
import com.ryuqq.marketplace.application.settlement.entry.assembler.SettlementEntryAssembler;
import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;
import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryReadManager;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
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
@DisplayName("GetSettlementEntryListService 단위 테스트")
class GetSettlementEntryListServiceTest {

    @InjectMocks private GetSettlementEntryListService sut;

    @Mock private SettlementEntryReadManager readManager;
    @Mock private SettlementEntryAssembler assembler;

    @Nested
    @DisplayName("execute() - 정산 원장 목록 조회")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 정산 원장 목록 및 페이지 결과를 반환한다")
        void execute_ValidParams_ReturnsSettlementEntryPageResult() {
            // given
            SettlementEntrySearchParams params = SettlementEntryQueryFixtures.searchParams();
            List<SettlementEntry> entries =
                    List.of(
                            SettlementEntryFixtures.salesEntry(),
                            SettlementEntryFixtures.cancelReversalEntry());
            long totalElements = 2L;
            PageMeta pageMeta = PageMeta.of(params.page(), params.size(), totalElements);
            SettlementEntryPageResult expectedResult =
                    new SettlementEntryPageResult(List.of(), pageMeta);

            given(readManager.findByCriteria(params)).willReturn(entries);
            given(readManager.countByCriteria(params)).willReturn(totalElements);
            given(assembler.toPageResult(entries, pageMeta)).willReturn(expectedResult);

            // when
            SettlementEntryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(readManager).should().findByCriteria(params);
            then(readManager).should().countByCriteria(params);
            then(assembler).should().toPageResult(entries, pageMeta);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 목록과 totalElements 0으로 결과를 반환한다")
        void execute_NoEntries_ReturnsEmptyPageResult() {
            // given
            SettlementEntrySearchParams params = SettlementEntryQueryFixtures.emptySearchParams();
            List<SettlementEntry> emptyEntries = List.of();
            long totalElements = 0L;
            PageMeta pageMeta = PageMeta.of(params.page(), params.size(), totalElements);
            SettlementEntryPageResult expectedResult =
                    new SettlementEntryPageResult(List.of(), pageMeta);

            given(readManager.findByCriteria(params)).willReturn(emptyEntries);
            given(readManager.countByCriteria(params)).willReturn(totalElements);
            given(assembler.toPageResult(emptyEntries, pageMeta)).willReturn(expectedResult);

            // when
            SettlementEntryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.entries()).isEmpty();
        }

        @Test
        @DisplayName("페이지 파라미터에 따라 올바른 PageMeta를 생성하여 조립한다")
        void execute_WithPagination_UsesCorrectPageMeta() {
            // given
            SettlementEntrySearchParams params = SettlementEntryQueryFixtures.searchParams(1, 10);
            List<SettlementEntry> entries = List.of(SettlementEntryFixtures.salesEntry());
            long totalElements = 15L;
            PageMeta pageMeta = PageMeta.of(1, 10, totalElements);
            SettlementEntryPageResult expectedResult =
                    new SettlementEntryPageResult(List.of(), pageMeta);

            given(readManager.findByCriteria(params)).willReturn(entries);
            given(readManager.countByCriteria(params)).willReturn(totalElements);
            given(assembler.toPageResult(entries, pageMeta)).willReturn(expectedResult);

            // when
            SettlementEntryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(assembler).should().toPageResult(entries, pageMeta);
        }
    }
}
