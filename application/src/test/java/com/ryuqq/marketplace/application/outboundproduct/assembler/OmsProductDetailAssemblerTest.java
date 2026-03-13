package com.ryuqq.marketplace.application.outboundproduct.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncSummaryResult;
import com.ryuqq.marketplace.application.productgroup.assembler.ProductGroupAssembler;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatusSummary;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OmsProductDetailAssembler 단위 테스트")
class OmsProductDetailAssemblerTest {

    private OmsProductDetailAssembler sut;

    @Mock private ProductGroupAssembler productGroupAssembler;

    @BeforeEach
    void setUp() {
        sut = new OmsProductDetailAssembler(productGroupAssembler);
    }

    @Nested
    @DisplayName("toDetailResult() - Bundle + SyncStatusSummary → OmsProductDetailResult 변환")
    class ToDetailResultTest {

        @Test
        @DisplayName("ProductGroupDetailBundle과 SyncStatusSummary를 OmsProductDetailResult로 조립한다")
        void toDetailResult_ValidInputs_ReturnsDetailResult() {
            // given
            ProductGroupDetailBundle bundle = createDetailBundle();
            SyncStatusSummary summary = new SyncStatusSummary(8L, 1L, 1L, Instant.now());
            ProductGroupDetailCompositeResult compositeResult = createCompositeResult();

            given(productGroupAssembler.toDetailResult(bundle)).willReturn(compositeResult);

            // when
            OmsProductDetailResult result = sut.toDetailResult(bundle, summary);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroup()).isEqualTo(compositeResult);
            assertThat(result.syncSummary()).isNotNull();
            then(productGroupAssembler).should().toDetailResult(bundle);
        }

        @Test
        @DisplayName("SyncStatusSummary의 값이 SyncSummaryResult에 올바르게 매핑된다")
        void toDetailResult_SyncSummaryMappedCorrectly() {
            // given
            Instant lastSyncAt = Instant.now();
            long completedCount = 8L;
            long failedCount = 2L;
            long pendingCount = 1L;
            SyncStatusSummary summary =
                    new SyncStatusSummary(completedCount, failedCount, pendingCount, lastSyncAt);

            ProductGroupDetailBundle bundle = createDetailBundle();
            ProductGroupDetailCompositeResult compositeResult = createCompositeResult();

            given(productGroupAssembler.toDetailResult(bundle)).willReturn(compositeResult);

            // when
            OmsProductDetailResult result = sut.toDetailResult(bundle, summary);

            // then
            SyncSummaryResult syncSummary = result.syncSummary();
            assertThat(syncSummary.totalSyncCount()).isEqualTo(summary.totalCount());
            assertThat(syncSummary.successCount()).isEqualTo(completedCount);
            assertThat(syncSummary.failCount()).isEqualTo(failedCount);
            assertThat(syncSummary.pendingCount()).isEqualTo(pendingCount);
            assertThat(syncSummary.lastSyncAt()).isEqualTo(lastSyncAt);
        }

        @Test
        @DisplayName("연동 이력이 전혀 없는 상품의 경우 totalCount는 0이다")
        void toDetailResult_NoSyncHistory_TotalCountIsZero() {
            // given
            SyncStatusSummary emptySummary = new SyncStatusSummary(0L, 0L, 0L, null);
            ProductGroupDetailBundle bundle = createDetailBundle();
            ProductGroupDetailCompositeResult compositeResult = createCompositeResult();

            given(productGroupAssembler.toDetailResult(bundle)).willReturn(compositeResult);

            // when
            OmsProductDetailResult result = sut.toDetailResult(bundle, emptySummary);

            // then
            SyncSummaryResult syncSummary = result.syncSummary();
            assertThat(syncSummary.totalSyncCount()).isZero();
            assertThat(syncSummary.successCount()).isZero();
            assertThat(syncSummary.failCount()).isZero();
            assertThat(syncSummary.pendingCount()).isZero();
            assertThat(syncSummary.lastSyncAt()).isNull();
        }

        @Test
        @DisplayName(
                "SyncStatusSummary의 totalCount는 completedCount + failedCount + pendingCount 합산이다")
        void toDetailResult_TotalCountIsSum() {
            // given
            long completedCount = 5L;
            long failedCount = 3L;
            long pendingCount = 2L;
            SyncStatusSummary summary =
                    new SyncStatusSummary(completedCount, failedCount, pendingCount, Instant.now());

            ProductGroupDetailBundle bundle = createDetailBundle();
            ProductGroupDetailCompositeResult compositeResult = createCompositeResult();

            given(productGroupAssembler.toDetailResult(bundle)).willReturn(compositeResult);

            // when
            OmsProductDetailResult result = sut.toDetailResult(bundle, summary);

            // then
            assertThat(result.syncSummary().totalSyncCount())
                    .isEqualTo(completedCount + failedCount + pendingCount);
        }

        @Test
        @DisplayName("ProductGroupAssembler에 번들 조립을 위임한다")
        void toDetailResult_DelegatesToProductGroupAssembler() {
            // given
            ProductGroupDetailBundle bundle = createDetailBundle();
            SyncStatusSummary summary = new SyncStatusSummary(0L, 0L, 0L, null);
            ProductGroupDetailCompositeResult compositeResult = createCompositeResult();

            given(productGroupAssembler.toDetailResult(bundle)).willReturn(compositeResult);

            // when
            sut.toDetailResult(bundle, summary);

            // then
            then(productGroupAssembler).should().toDetailResult(bundle);
            then(productGroupAssembler).shouldHaveNoMoreInteractions();
        }
    }

    private ProductGroupDetailBundle createDetailBundle() {
        return new ProductGroupDetailBundle(
                null, null, List.of(), Optional.empty(), Optional.empty(), Map.of());
    }

    private ProductGroupDetailCompositeResult createCompositeResult() {
        Instant now = Instant.now();
        return new ProductGroupDetailCompositeResult(
                1L,
                1L,
                "테스트 셀러",
                1L,
                "테스트 브랜드",
                1L,
                "테스트 카테고리",
                "패션의류 > 테스트 카테고리",
                "1/100",
                "테스트 상품 그룹",
                "SINGLE",
                "ACTIVE",
                now,
                now,
                List.of(),
                null,
                null,
                null,
                null,
                null);
    }
}
