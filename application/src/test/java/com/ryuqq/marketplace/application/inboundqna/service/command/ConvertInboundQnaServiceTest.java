package com.ryuqq.marketplace.application.inboundqna.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundqna.internal.InboundQnaConversionProcessor;
import com.ryuqq.marketplace.application.inboundqna.manager.InboundQnaReadManager;
import com.ryuqq.marketplace.domain.inboundqna.InboundQnaFixtures;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
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
@DisplayName("ConvertInboundQnaService 단위 테스트")
class ConvertInboundQnaServiceTest {

    @InjectMocks private ConvertInboundQnaService sut;

    @Mock private InboundQnaReadManager readManager;
    @Mock private InboundQnaConversionProcessor conversionProcessor;

    @Nested
    @DisplayName("execute() - InboundQna 단건 변환")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 inboundQnaId로 조회하고 conversionProcessor를 호출한다")
        void execute_ValidInboundQnaId_CallsConversionProcessor() {
            // given
            long inboundQnaId = 1L;
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(inboundQnaId);
            given(readManager.getById(inboundQnaId)).willReturn(inboundQna);

            // when
            sut.execute(inboundQnaId);

            // then
            then(readManager).should().getById(inboundQnaId);
            then(conversionProcessor).should().convert(inboundQna, null, null);
        }

        @Test
        @DisplayName("externalProductId와 externalOrderId를 null로 전달한다")
        void execute_ValidInboundQnaId_PassesNullExternalIds() {
            // given
            long inboundQnaId = 2L;
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(inboundQnaId);
            given(readManager.getById(inboundQnaId)).willReturn(inboundQna);

            // when
            sut.execute(inboundQnaId);

            // then
            then(conversionProcessor).should().convert(inboundQna, null, null);
        }
    }
}
