package com.ryuqq.marketplace.application.inboundqna.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaCommandManager;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.application.inboundqna.port.out.client.SalesChannelQnaClient;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PollExternalQnasService 단위 테스트")
class PollExternalQnasServiceTest {

    // List<SalesChannelQnaClient>는 @InjectMocks가 처리하지 못하므로 직접 생성
    private PollExternalQnasService sut;

    @Mock private SalesChannelQnaClient qnaClient;
    @Mock private InboundQnaReadManager readManager;
    @Mock private InboundQnaCommandManager commandManager;
    @Mock private InboundQnaConversionProcessor conversionProcessor;

    @BeforeEach
    void setUp() {
        sut =
                new PollExternalQnasService(
                        List.of(qnaClient), readManager, commandManager, conversionProcessor);
    }

    @Nested
    @DisplayName("execute() - 외부 QnA 폴링")
    class ExecuteTest {

        @Test
        @DisplayName("지원하지 않는 채널이면 0을 반환하고 persist를 호출하지 않는다")
        void execute_UnsupportedChannel_ReturnsZeroWithoutPersist() {
            // given
            long salesChannelId = 99L;
            int batchSize = 10;
            given(qnaClient.supports(String.valueOf(salesChannelId))).willReturn(false);

            // when
            int result = sut.execute(salesChannelId, batchSize);

            // then
            assertThat(result).isZero();
            then(commandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("외부에서 조회한 QnA가 없으면 0을 반환하고 persist를 호출하지 않는다")
        void execute_NoExternalQnas_ReturnsZeroWithoutPersist() {
            // given
            long salesChannelId = 1L;
            int batchSize = 10;
            given(qnaClient.supports(String.valueOf(salesChannelId))).willReturn(true);
            given(
                            qnaClient.fetchNewQnas(
                                    Mockito.eq(salesChannelId),
                                    Mockito.any(Instant.class),
                                    Mockito.any(Instant.class),
                                    Mockito.eq(batchSize)))
                    .willReturn(List.of());

            // when
            int result = sut.execute(salesChannelId, batchSize);

            // then
            assertThat(result).isZero();
            then(commandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("신규 QnA를 수신하면 중복 체크 후 저장하고 저장 건수를 반환한다")
        void execute_NewExternalQnas_PersistsAndReturnsCount() {
            // given
            long salesChannelId = 1L;
            int batchSize = 10;
            ExternalQnaPayload payload =
                    new ExternalQnaPayload(
                            "EXT-QNA-NEW-001",
                            "PRODUCT",
                            "사이즈가 어떻게 되나요?",
                            "구매자A",
                            null,
                            null,
                            "{\"externalQnaId\":\"EXT-QNA-NEW-001\"}");

            given(qnaClient.supports(String.valueOf(salesChannelId))).willReturn(true);
            given(
                            qnaClient.fetchNewQnas(
                                    Mockito.eq(salesChannelId),
                                    Mockito.any(Instant.class),
                                    Mockito.any(Instant.class),
                                    Mockito.eq(batchSize)))
                    .willReturn(List.of(payload));
            given(
                            readManager.existsBySalesChannelIdAndExternalQnaId(
                                    salesChannelId, "EXT-QNA-NEW-001"))
                    .willReturn(false);

            // when
            int result = sut.execute(salesChannelId, batchSize);

            // then
            assertThat(result).isEqualTo(1);
            then(commandManager).should().persistAll(Mockito.argThat(list -> list.size() == 1));
        }

        @Test
        @DisplayName("이미 존재하는 QnA는 중복 저장하지 않고 0을 반환한다")
        void execute_DuplicateExternalQna_SkipsDuplicate() {
            // given
            long salesChannelId = 1L;
            int batchSize = 10;
            ExternalQnaPayload duplicatePayload =
                    new ExternalQnaPayload(
                            "EXT-QNA-001",
                            "PRODUCT",
                            "사이즈가 어떻게 되나요?",
                            "구매자A",
                            null,
                            null,
                            "{\"externalQnaId\":\"EXT-QNA-001\"}");

            given(qnaClient.supports(String.valueOf(salesChannelId))).willReturn(true);
            given(
                            qnaClient.fetchNewQnas(
                                    Mockito.eq(salesChannelId),
                                    Mockito.any(Instant.class),
                                    Mockito.any(Instant.class),
                                    Mockito.eq(batchSize)))
                    .willReturn(List.of(duplicatePayload));
            given(readManager.existsBySalesChannelIdAndExternalQnaId(salesChannelId, "EXT-QNA-001"))
                    .willReturn(true);

            // when
            int result = sut.execute(salesChannelId, batchSize);

            // then
            assertThat(result).isZero();
            then(commandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("신규와 중복 QnA가 섞여 있으면 신규만 저장하고 신규 건수를 반환한다")
        void execute_MixedNewAndDuplicateQnas_PersistsOnlyNewOnes() {
            // given
            long salesChannelId = 1L;
            int batchSize = 10;
            ExternalQnaPayload newPayload =
                    new ExternalQnaPayload(
                            "EXT-QNA-NEW-001",
                            "PRODUCT",
                            "색상이 몇 가지 있나요?",
                            "구매자B",
                            null,
                            null,
                            "{\"externalQnaId\":\"EXT-QNA-NEW-001\"}");
            ExternalQnaPayload duplicatePayload =
                    new ExternalQnaPayload(
                            "EXT-QNA-001",
                            "PRODUCT",
                            "사이즈가 어떻게 되나요?",
                            "구매자A",
                            null,
                            null,
                            "{\"externalQnaId\":\"EXT-QNA-001\"}");

            given(qnaClient.supports(String.valueOf(salesChannelId))).willReturn(true);
            given(
                            qnaClient.fetchNewQnas(
                                    Mockito.eq(salesChannelId),
                                    Mockito.any(Instant.class),
                                    Mockito.any(Instant.class),
                                    Mockito.eq(batchSize)))
                    .willReturn(List.of(newPayload, duplicatePayload));
            given(
                            readManager.existsBySalesChannelIdAndExternalQnaId(
                                    salesChannelId, "EXT-QNA-NEW-001"))
                    .willReturn(false);
            given(readManager.existsBySalesChannelIdAndExternalQnaId(salesChannelId, "EXT-QNA-001"))
                    .willReturn(true);

            // when
            int result = sut.execute(salesChannelId, batchSize);

            // then
            assertThat(result).isEqualTo(1);
            then(commandManager)
                    .should()
                    .persistAll(
                            Mockito.argThat(
                                    (List<InboundQna> list) ->
                                            list.size() == 1
                                                    && "EXT-QNA-NEW-001"
                                                            .equals(list.get(0).externalQnaId())));
        }

        @Test
        @DisplayName("알 수 없는 QnaType 문자열은 ETC로 처리하여 저장한다")
        void execute_UnknownQnaType_ParsesAsEtcAndPersists() {
            // given
            long salesChannelId = 1L;
            int batchSize = 10;
            ExternalQnaPayload payloadWithUnknownType =
                    new ExternalQnaPayload(
                            "EXT-QNA-UNKNOWN-001",
                            "UNKNOWN_TYPE",
                            "배송은 언제 오나요?",
                            "구매자C",
                            null,
                            null,
                            "{\"externalQnaId\":\"EXT-QNA-UNKNOWN-001\"}");

            given(qnaClient.supports(String.valueOf(salesChannelId))).willReturn(true);
            given(
                            qnaClient.fetchNewQnas(
                                    Mockito.eq(salesChannelId),
                                    Mockito.any(Instant.class),
                                    Mockito.any(Instant.class),
                                    Mockito.eq(batchSize)))
                    .willReturn(List.of(payloadWithUnknownType));
            given(
                            readManager.existsBySalesChannelIdAndExternalQnaId(
                                    salesChannelId, "EXT-QNA-UNKNOWN-001"))
                    .willReturn(false);

            // when
            int result = sut.execute(salesChannelId, batchSize);

            // then
            assertThat(result).isEqualTo(1);
            then(commandManager).should().persistAll(Mockito.argThat(list -> !list.isEmpty()));
        }
    }
}
