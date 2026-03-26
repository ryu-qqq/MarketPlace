package com.ryuqq.marketplace.application.settlement.entry.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.settlement.dto.response.SettlementEntryPageResult;
import com.ryuqq.marketplace.application.settlement.entry.dto.response.SettlementEntryListResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementEntryAssembler 단위 테스트")
class SettlementEntryAssemblerTest {

    private SettlementEntryAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new SettlementEntryAssembler();
    }

    @Nested
    @DisplayName("toListResult() - SettlementEntry → SettlementEntryListResult 변환")
    class ToListResultTest {

        @Test
        @DisplayName("판매 Entry를 SettlementEntryListResult로 변환한다")
        void toListResult_SalesEntry_ReturnsListResult() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();

            // when
            SettlementEntryListResult result = sut.toListResult(entry);

            // then
            assertThat(result).isNotNull();
            assertThat(result.entryId()).isEqualTo(entry.idValue());
            assertThat(result.entryStatus()).isEqualTo(entry.status().name());
            assertThat(result.sellerId()).isEqualTo(entry.sellerId());
            assertThat(result.entryType()).isEqualTo(entry.entryType().name());
            assertThat(result.orderItemId()).isEqualTo(entry.source().orderItemId());
            assertThat(result.salesAmount()).isEqualTo(entry.amounts().salesAmount().value());
            assertThat(result.commissionRate()).isEqualTo(entry.amounts().commissionRate());
            assertThat(result.commissionAmount())
                    .isEqualTo(entry.amounts().commissionAmount().value());
            assertThat(result.settlementAmount())
                    .isEqualTo(entry.amounts().settlementAmount().value());
            assertThat(result.createdAt()).isEqualTo(entry.createdAt());
        }

        @Test
        @DisplayName("취소 역분개 Entry를 SettlementEntryListResult로 변환한다")
        void toListResult_CancelReversalEntry_ReturnsListResultWithClaimInfo() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.cancelReversalEntry();

            // when
            SettlementEntryListResult result = sut.toListResult(entry);

            // then
            assertThat(result).isNotNull();
            assertThat(result.entryType()).isEqualTo("CANCEL");
            assertThat(result.claimId()).isEqualTo(entry.source().claimId());
            assertThat(result.claimType()).isEqualTo(entry.source().claimType());
        }

        @Test
        @DisplayName("판매 Entry는 claimId와 claimType이 null이다")
        void toListResult_SalesEntry_HasNullClaimInfo() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();

            // when
            SettlementEntryListResult result = sut.toListResult(entry);

            // then
            assertThat(result.claimId()).isNull();
            assertThat(result.claimType()).isNull();
        }

        @Test
        @DisplayName("확정된 Entry도 정상 변환한다")
        void toListResult_ConfirmedEntry_ReturnsListResult() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.confirmedSalesEntry();

            // when
            SettlementEntryListResult result = sut.toListResult(entry);

            // then
            assertThat(result).isNotNull();
            assertThat(result.entryStatus()).isEqualTo("CONFIRMED");
        }
    }

    @Nested
    @DisplayName("toPageResult() - Entry 목록 + PageMeta → SettlementEntryPageResult 변환")
    class ToPageResultTest {

        @Test
        @DisplayName("Entry 목록과 PageMeta로 SettlementEntryPageResult를 생성한다")
        void toPageResult_ValidEntries_ReturnsPageResult() {
            // given
            List<SettlementEntry> entries =
                    List.of(
                            SettlementEntryFixtures.salesEntry(),
                            SettlementEntryFixtures.cancelReversalEntry());
            PageMeta pageMeta = PageMeta.of(0, 20, 2L);

            // when
            SettlementEntryPageResult result = sut.toPageResult(entries, pageMeta);

            // then
            assertThat(result).isNotNull();
            assertThat(result.entries()).hasSize(2);
            assertThat(result.pageMeta()).isEqualTo(pageMeta);
            assertThat(result.pageMeta().totalElements()).isEqualTo(2L);
        }

        @Test
        @DisplayName("빈 목록으로 빈 SettlementEntryPageResult를 생성한다")
        void toPageResult_EmptyEntries_ReturnsEmptyPageResult() {
            // given
            List<SettlementEntry> emptyEntries = List.of();
            PageMeta pageMeta = PageMeta.of(0, 20, 0L);

            // when
            SettlementEntryPageResult result = sut.toPageResult(emptyEntries, pageMeta);

            // then
            assertThat(result.entries()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("다음 페이지가 있으면 pageMeta가 hasNext를 올바르게 나타낸다")
        void toPageResult_HasMorePages_PageMetaHasNextIsTrue() {
            // given
            List<SettlementEntry> entries =
                    List.of(
                            SettlementEntryFixtures.salesEntry(),
                            SettlementEntryFixtures.salesEntry());
            PageMeta pageMeta = PageMeta.of(0, 2, 10L);

            // when
            SettlementEntryPageResult result = sut.toPageResult(entries, pageMeta);

            // then
            assertThat(result.entries()).hasSize(2);
            assertThat(result.pageMeta().hasNext()).isTrue();
        }

        @Test
        @DisplayName("마지막 페이지이면 pageMeta가 hasNext false를 나타낸다")
        void toPageResult_LastPage_PageMetaHasNextIsFalse() {
            // given
            List<SettlementEntry> entries = List.of(SettlementEntryFixtures.salesEntry());
            PageMeta pageMeta = PageMeta.of(0, 20, 1L);

            // when
            SettlementEntryPageResult result = sut.toPageResult(entries, pageMeta);

            // then
            assertThat(result.pageMeta().hasNext()).isFalse();
        }
    }
}
