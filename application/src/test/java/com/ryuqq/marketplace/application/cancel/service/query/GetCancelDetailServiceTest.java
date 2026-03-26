package com.ryuqq.marketplace.application.cancel.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.CancelQueryFixtures;
import com.ryuqq.marketplace.application.cancel.assembler.CancelAssembler;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryReadManager;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
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
@DisplayName("GetCancelDetailService 단위 테스트")
class GetCancelDetailServiceTest {

    @InjectMocks private GetCancelDetailService sut;

    @Mock private CancelReadManager cancelReadManager;
    @Mock private CancelAssembler assembler;
    @Mock private ClaimHistoryReadManager historyReadManager;

    @Nested
    @DisplayName("execute() - 취소 상세 조회")
    class ExecuteTest {

        @Test
        @DisplayName("취소 ID로 상세 정보와 이력을 함께 조회한다")
        void execute_ValidCancelId_ReturnsCancelDetailResult() {
            // given
            String cancelId = "01900000-0000-7000-8000-000000000001";
            Cancel cancel = CancelFixtures.requestedCancel();
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());
            CancelDetailResult expectedResult = CancelQueryFixtures.cancelDetailResult(cancelId);

            given(cancelReadManager.getById(CancelId.of(cancelId))).willReturn(cancel);
            given(historyReadManager.findByClaimId(ClaimType.CANCEL, cancelId))
                    .willReturn(histories);
            given(assembler.toDetailResult(cancel, histories)).willReturn(expectedResult);

            // when
            CancelDetailResult result = sut.execute(cancelId);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(cancelReadManager).should().getById(CancelId.of(cancelId));
            then(historyReadManager).should().findByClaimId(ClaimType.CANCEL, cancelId);
            then(assembler).should().toDetailResult(cancel, histories);
        }

        @Test
        @DisplayName("이력이 없어도 상세 결과를 반환한다")
        void execute_NoHistories_ReturnsCancelDetailWithEmptyHistories() {
            // given
            String cancelId = "01900000-0000-7000-8000-000000000001";
            Cancel cancel = CancelFixtures.requestedCancel();
            CancelDetailResult expectedResult = CancelQueryFixtures.cancelDetailResult(cancelId);

            given(cancelReadManager.getById(CancelId.of(cancelId))).willReturn(cancel);
            given(historyReadManager.findByClaimId(ClaimType.CANCEL, cancelId))
                    .willReturn(List.of());
            given(assembler.toDetailResult(cancel, List.of())).willReturn(expectedResult);

            // when
            CancelDetailResult result = sut.execute(cancelId);

            // then
            assertThat(result).isNotNull();
        }
    }
}
