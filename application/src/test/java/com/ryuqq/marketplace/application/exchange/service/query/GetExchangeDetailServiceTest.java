package com.ryuqq.marketplace.application.exchange.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryReadManager;
import com.ryuqq.marketplace.application.exchange.ExchangeQueryFixtures;
import com.ryuqq.marketplace.application.exchange.assembler.ExchangeAssembler;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
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
@DisplayName("GetExchangeDetailService 단위 테스트")
class GetExchangeDetailServiceTest {

    @InjectMocks private GetExchangeDetailService sut;

    @Mock private ExchangeReadManager exchangeReadManager;
    @Mock private ExchangeAssembler assembler;
    @Mock private ClaimHistoryReadManager historyReadManager;

    @Nested
    @DisplayName("execute() - 교환 상세 조회")
    class ExecuteTest {

        @Test
        @DisplayName("교환 클레임 ID로 상세 정보와 이력을 함께 조회한다")
        void execute_ValidClaimId_ReturnsDetailResult() {
            // given
            String claimId = "01900000-0000-7000-0000-000000000001";
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();
            List<ClaimHistory> histories = List.of();
            ExchangeDetailResult expectedResult =
                    ExchangeQueryFixtures.exchangeDetailResult(claimId);

            given(exchangeReadManager.getById(ExchangeClaimId.of(claimId))).willReturn(claim);
            given(historyReadManager.findByClaimId(ClaimType.EXCHANGE, claimId))
                    .willReturn(histories);
            given(assembler.toDetailResult(claim, histories)).willReturn(expectedResult);

            // when
            ExchangeDetailResult result = sut.execute(claimId);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResult);
            then(exchangeReadManager).should().getById(ExchangeClaimId.of(claimId));
            then(historyReadManager).should().findByClaimId(ClaimType.EXCHANGE, claimId);
            then(assembler).should().toDetailResult(claim, histories);
        }
    }
}
