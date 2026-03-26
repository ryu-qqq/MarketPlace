package com.ryuqq.marketplace.application.outboundproduct.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.assembler.OmsProductDetailAssembler;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatusSummary;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
@DisplayName("GetOmsProductDetailService 단위 테스트")
class GetOmsProductDetailServiceTest {

    @InjectMocks private GetOmsProductDetailService sut;

    @Mock private ProductGroupReadFacade productGroupReadFacade;
    @Mock private OutboundSyncOutboxReadManager outboundSyncOutboxReadManager;
    @Mock private OmsProductDetailAssembler assembler;

    @Nested
    @DisplayName("execute() - OMS 상품 상세 조회")
    class ExecuteTest {

        @Test
        @DisplayName("상품 그룹 ID로 상품 상세와 연동 통계를 조회하여 반환한다")
        void execute_ValidProductGroupId_ReturnsDetailResult() {
            // given
            Long productGroupId = 1L;
            ProductGroupDetailBundle bundle = createDetailBundle();
            SyncStatusSummary summary = new SyncStatusSummary(8L, 1L, 1L, Instant.now());
            OmsProductDetailResult expected = OmsProductQueryFixtures.omsProductDetailResult();

            given(productGroupReadFacade.getDetailBundle(productGroupId)).willReturn(bundle);
            given(outboundSyncOutboxReadManager.getSyncSummary(productGroupId)).willReturn(summary);
            given(assembler.toDetailResult(bundle, summary)).willReturn(expected);

            // when
            OmsProductDetailResult result = sut.execute(productGroupId);

            // then
            assertThat(result).isEqualTo(expected);
            then(productGroupReadFacade).should().getDetailBundle(productGroupId);
            then(outboundSyncOutboxReadManager).should().getSyncSummary(productGroupId);
            then(assembler).should().toDetailResult(bundle, summary);
        }

        @Test
        @DisplayName("연동 이력이 없는 상품도 상세 조회가 가능하다")
        void execute_ProductWithNoSyncHistory_ReturnsDetailWithEmptySummary() {
            // given
            Long productGroupId = 2L;
            ProductGroupDetailBundle bundle = createDetailBundle();
            SyncStatusSummary emptySummary = new SyncStatusSummary(0L, 0L, 0L, null);
            OmsProductDetailResult expected =
                    new OmsProductDetailResult(
                            null, OmsProductQueryFixtures.emptySyncSummaryResult());

            given(productGroupReadFacade.getDetailBundle(productGroupId)).willReturn(bundle);
            given(outboundSyncOutboxReadManager.getSyncSummary(productGroupId))
                    .willReturn(emptySummary);
            given(assembler.toDetailResult(bundle, emptySummary)).willReturn(expected);

            // when
            OmsProductDetailResult result = sut.execute(productGroupId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.syncSummary().totalSyncCount()).isZero();
            then(assembler).should().toDetailResult(bundle, emptySummary);
        }

        @Test
        @DisplayName("Facade → Manager → Assembler 순으로 협력 객체를 호출한다")
        void execute_CallsCollaboratorsInOrder() {
            // given
            Long productGroupId = 3L;
            ProductGroupDetailBundle bundle = createDetailBundle();
            SyncStatusSummary summary = new SyncStatusSummary(4L, 1L, 0L, Instant.now());
            OmsProductDetailResult expected = OmsProductQueryFixtures.omsProductDetailResult();

            given(productGroupReadFacade.getDetailBundle(productGroupId)).willReturn(bundle);
            given(outboundSyncOutboxReadManager.getSyncSummary(productGroupId)).willReturn(summary);
            given(assembler.toDetailResult(bundle, summary)).willReturn(expected);

            // when
            sut.execute(productGroupId);

            // then
            then(productGroupReadFacade).should().getDetailBundle(productGroupId);
            then(outboundSyncOutboxReadManager).should().getSyncSummary(productGroupId);
            then(assembler).should().toDetailResult(bundle, summary);
            then(productGroupReadFacade).shouldHaveNoMoreInteractions();
            then(outboundSyncOutboxReadManager).shouldHaveNoMoreInteractions();
            then(assembler).shouldHaveNoMoreInteractions();
        }
    }

    private ProductGroupDetailBundle createDetailBundle() {
        return new ProductGroupDetailBundle(
                null,
                null,
                List.of(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of());
    }
}
