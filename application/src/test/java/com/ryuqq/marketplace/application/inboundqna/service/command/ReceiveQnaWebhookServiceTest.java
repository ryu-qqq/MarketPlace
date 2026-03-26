package com.ryuqq.marketplace.application.inboundqna.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.dto.result.QnaWebhookResult;
import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaCommandManager;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
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
@DisplayName("ReceiveQnaWebhookService 단위 테스트")
class ReceiveQnaWebhookServiceTest {

    @Mock private InboundQnaReadManager readManager;
    @Mock private InboundQnaCommandManager commandManager;
    @Mock private InboundQnaConversionProcessor conversionProcessor;
    @Mock private QnaReadManager qnaReadManager;
    @Mock private QnaCommandManager qnaCommandManager;

    @InjectMocks private ReceiveQnaWebhookService sut;

    private static final long SALES_CHANNEL_ID = 1L;
    private static final long SHOP_ID = 10L;

    @Nested
    @DisplayName("execute() - QnA 웹훅 수신")
    class ExecuteTest {

        @Test
        @DisplayName("새 QnA 수신 시 InboundQna를 생성하고 conversionProcessor.convert()를 호출한다")
        void execute_NewQna_CreatesInboundQnaAndCallsConvert() {
            // given
            ExternalQnaPayload payload =
                    new ExternalQnaPayload(
                            "EXT-QNA-001",
                            null,
                            "PRODUCT",
                            "사이즈 문의",
                            "사이즈가 어떻게 되나요?",
                            "구매자A",
                            "EXT-PROD-001",
                            null,
                            "{\"externalQnaId\":\"EXT-QNA-001\"}");

            given(readManager.existsBySalesChannelIdAndExternalQnaId(SALES_CHANNEL_ID, "EXT-QNA-001"))
                    .willReturn(false);

            // when
            QnaWebhookResult result = sut.execute(List.of(payload), SALES_CHANNEL_ID, SHOP_ID);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.created()).isEqualTo(1);
            assertThat(result.duplicated()).isZero();
            assertThat(result.failed()).isZero();

            then(commandManager).should().persist(any(InboundQna.class));
            then(conversionProcessor).should().convert(any(InboundQna.class), eq("EXT-PROD-001"), eq(null));
        }

        @Test
        @DisplayName("중복 QnA는 스킵하고 duplicated 카운트를 증가시킨다")
        void execute_DuplicateQna_SkipsAndIncrementsDuplicated() {
            // given
            ExternalQnaPayload payload =
                    new ExternalQnaPayload(
                            "EXT-QNA-DUP-001",
                            null,
                            "PRODUCT",
                            "중복 문의",
                            "이미 등록된 문의입니다",
                            "구매자B",
                            "EXT-PROD-001",
                            null,
                            "{\"externalQnaId\":\"EXT-QNA-DUP-001\"}");

            given(readManager.existsBySalesChannelIdAndExternalQnaId(SALES_CHANNEL_ID, "EXT-QNA-DUP-001"))
                    .willReturn(true);

            // when
            QnaWebhookResult result = sut.execute(List.of(payload), SALES_CHANNEL_ID, SHOP_ID);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.created()).isZero();
            assertThat(result.duplicated()).isEqualTo(1);
            assertThat(result.failed()).isZero();

            then(commandManager).shouldHaveNoInteractions();
            then(conversionProcessor).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("대댓글(parentExternalQnaId 있음)이면 부모 Qna를 찾아 addFollowUp()을 호출한다")
        void execute_FollowUpQna_CallsAddFollowUpAndPersists() {
            // given
            ExternalQnaPayload payload =
                    new ExternalQnaPayload(
                            "EXT-QNA-REPLY-001",
                            "EXT-QNA-PARENT-001",
                            "PRODUCT",
                            "추가 질문",
                            "추가로 궁금한 점이 있습니다",
                            "구매자C",
                            "EXT-PROD-001",
                            null,
                            "{\"externalQnaId\":\"EXT-QNA-REPLY-001\"}");

            given(readManager.existsBySalesChannelIdAndExternalQnaId(SALES_CHANNEL_ID, "EXT-QNA-REPLY-001"))
                    .willReturn(false);

            Qna parentQna =
                    Qna.forNew(
                            100L,
                            200L,
                            null,
                            QnaType.PRODUCT,
                            new QnaSource(SALES_CHANNEL_ID, "EXT-QNA-PARENT-001"),
                            "원본 질문 제목",
                            "원본 질문 내용",
                            "구매자C",
                            Instant.now());

            given(qnaReadManager.getBySalesChannelIdAndExternalQnaId(SALES_CHANNEL_ID, "EXT-QNA-PARENT-001"))
                    .willReturn(parentQna);

            // when
            QnaWebhookResult result = sut.execute(List.of(payload), SALES_CHANNEL_ID, SHOP_ID);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.created()).isEqualTo(1);
            assertThat(result.duplicated()).isZero();
            assertThat(result.failed()).isZero();

            then(qnaCommandManager).should().persist(parentQna);
            then(commandManager).should().persist(any(InboundQna.class));
            then(conversionProcessor).should(never()).convert(any(), any(), any());
        }

        @Test
        @DisplayName("처리 중 예외가 발생하면 failed 카운트를 증가시킨다")
        void execute_ProcessingFails_IncrementsFailed() {
            // given
            ExternalQnaPayload payload =
                    new ExternalQnaPayload(
                            "EXT-QNA-FAIL-001",
                            null,
                            "PRODUCT",
                            "실패 문의",
                            "처리 실패 테스트",
                            "구매자D",
                            "EXT-PROD-001",
                            null,
                            "{\"externalQnaId\":\"EXT-QNA-FAIL-001\"}");

            given(readManager.existsBySalesChannelIdAndExternalQnaId(SALES_CHANNEL_ID, "EXT-QNA-FAIL-001"))
                    .willReturn(false);
            willThrow(new RuntimeException("저장 실패"))
                    .given(commandManager)
                    .persist(any(InboundQna.class));

            // when
            QnaWebhookResult result = sut.execute(List.of(payload), SALES_CHANNEL_ID, SHOP_ID);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.created()).isZero();
            assertThat(result.duplicated()).isZero();
            assertThat(result.failed()).isEqualTo(1);
        }
    }
}
