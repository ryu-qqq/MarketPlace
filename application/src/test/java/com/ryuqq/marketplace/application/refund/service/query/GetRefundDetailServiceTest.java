package com.ryuqq.marketplace.application.refund.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryReadManager;
import com.ryuqq.marketplace.application.refund.RefundQueryFixtures;
import com.ryuqq.marketplace.application.refund.assembler.RefundAssembler;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
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
@DisplayName("GetRefundDetailService 단위 테스트")
class GetRefundDetailServiceTest {

    @InjectMocks private GetRefundDetailService sut;

    @Mock private RefundReadManager refundReadManager;
    @Mock private RefundAssembler assembler;
    @Mock private ClaimHistoryReadManager historyReadManager;

    @Nested
    @DisplayName("execute() - 환불 상세 조회")
    class ExecuteTest {

        @Test
        @DisplayName("환불 ID로 상세 정보와 이력을 함께 조회한다")
        void execute_ValidRefundClaimId_ReturnsRefundDetailResult() {
            // given
            String refundClaimId = "01900000-0000-7000-8000-000000000010";
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.refundStatusChangeHistory());
            RefundDetailResult expectedResult =
                    RefundQueryFixtures.refundDetailResult(refundClaimId);

            given(refundReadManager.getById(RefundClaimId.of(refundClaimId))).willReturn(claim);
            given(historyReadManager.findByClaimId(ClaimType.REFUND, refundClaimId))
                    .willReturn(histories);
            given(assembler.toDetailResult(claim, histories)).willReturn(expectedResult);

            // when
            RefundDetailResult result = sut.execute(refundClaimId);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(refundReadManager).should().getById(RefundClaimId.of(refundClaimId));
            then(historyReadManager).should().findByClaimId(ClaimType.REFUND, refundClaimId);
            then(assembler).should().toDetailResult(claim, histories);
        }

        @Test
        @DisplayName("이력이 없어도 상세 결과를 반환한다")
        void execute_NoHistories_ReturnsRefundDetailWithEmptyHistories() {
            // given
            String refundClaimId = "01900000-0000-7000-8000-000000000010";
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            RefundDetailResult expectedResult =
                    RefundQueryFixtures.refundDetailResult(refundClaimId);

            given(refundReadManager.getById(RefundClaimId.of(refundClaimId))).willReturn(claim);
            given(historyReadManager.findByClaimId(ClaimType.REFUND, refundClaimId))
                    .willReturn(List.of());
            given(assembler.toDetailResult(claim, List.of())).willReturn(expectedResult);

            // when
            RefundDetailResult result = sut.execute(refundClaimId);

            // then
            assertThat(result).isNotNull();
        }
    }
}
