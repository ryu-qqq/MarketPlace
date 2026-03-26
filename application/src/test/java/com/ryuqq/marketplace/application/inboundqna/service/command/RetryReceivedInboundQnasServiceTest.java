package com.ryuqq.marketplace.application.inboundqna.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.domain.inboundqna.InboundQnaFixtures;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
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
@DisplayName("RetryReceivedInboundQnasService 단위 테스트")
class RetryReceivedInboundQnasServiceTest {

    @InjectMocks private RetryReceivedInboundQnasService sut;

    @Mock private InboundQnaReadManager readManager;
    @Mock private InboundQnaConversionProcessor conversionProcessor;

    @Nested
    @DisplayName("execute() - RECEIVED 상태 InboundQna 일괄 재변환")
    class ExecuteTest {

        @Test
        @DisplayName("RECEIVED 상태 QnA가 없으면 0을 반환하고 변환을 시도하지 않는다")
        void execute_NoReceivedQnas_ReturnsZeroWithoutConversion() {
            // given
            int batchSize = 10;
            given(readManager.findByStatus(InboundQnaStatus.RECEIVED, batchSize))
                    .willReturn(List.of());

            // when
            int result = sut.execute(batchSize);

            // then
            assertThat(result).isZero();
            then(conversionProcessor).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("RECEIVED 상태 QnA가 있으면 각각 변환하고 변환 건수를 반환한다")
        void execute_ReceivedQnas_ConvertsEachAndReturnsCount() {
            // given
            int batchSize = 10;
            InboundQna qna1 = InboundQnaFixtures.receivedInboundQna(1L);
            InboundQna qna2 = InboundQnaFixtures.receivedInboundQna(2L);
            given(readManager.findByStatus(InboundQnaStatus.RECEIVED, batchSize))
                    .willReturn(List.of(qna1, qna2));

            // when
            int result = sut.execute(batchSize);

            // then
            assertThat(result).isEqualTo(2);
            then(conversionProcessor).should().convert(qna1, null, null);
            then(conversionProcessor).should().convert(qna2, null, null);
        }

        @Test
        @DisplayName("모든 변환이 실패해도 0을 반환하고 예외를 전파하지 않는다")
        void execute_AllConversionsFail_ReturnsZeroWithoutException() {
            // given
            int batchSize = 5;
            InboundQna qna1 = InboundQnaFixtures.receivedInboundQna(1L);
            InboundQna qna2 = InboundQnaFixtures.receivedInboundQna(2L);
            given(readManager.findByStatus(InboundQnaStatus.RECEIVED, batchSize))
                    .willReturn(List.of(qna1, qna2));
            // lenient로 모든 convert 호출에 대해 예외 발생
            org.mockito.Mockito.lenient()
                    .doThrow(new RuntimeException("변환 실패"))
                    .when(conversionProcessor)
                    .convert(any(InboundQna.class), isNull(String.class), isNull(String.class));

            // when
            int result = sut.execute(batchSize);

            // then
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("일부 변환이 실패해도 나머지는 계속 처리한다")
        void execute_SomeConversionFails_ContinuesProcessingRemainingItems() {
            // given
            int batchSize = 10;
            InboundQna qna1 = InboundQnaFixtures.receivedInboundQna(1L);
            InboundQna qna2 = InboundQnaFixtures.receivedInboundQna(2L);
            given(readManager.findByStatus(InboundQnaStatus.RECEIVED, batchSize))
                    .willReturn(List.of(qna1, qna2));
            // qna1 성공, qna2 실패
            org.mockito.Mockito.lenient()
                    .doThrow(new RuntimeException("변환 실패"))
                    .when(conversionProcessor)
                    .convert(qna2, null, null);

            // when
            int result = sut.execute(batchSize);

            // then: qna1 성공 1건, qna2 실패
            assertThat(result).isEqualTo(1);
            then(conversionProcessor).should().convert(qna1, null, null);
        }

        @Test
        @DisplayName("단일 RECEIVED QnA를 변환하면 1을 반환한다")
        void execute_SingleReceivedQna_ReturnsOne() {
            // given
            int batchSize = 10;
            InboundQna qna = InboundQnaFixtures.receivedInboundQna(1L);
            given(readManager.findByStatus(InboundQnaStatus.RECEIVED, batchSize))
                    .willReturn(List.of(qna));

            // when
            int result = sut.execute(batchSize);

            // then
            assertThat(result).isEqualTo(1);
            then(conversionProcessor).should().convert(qna, null, null);
        }
    }
}
