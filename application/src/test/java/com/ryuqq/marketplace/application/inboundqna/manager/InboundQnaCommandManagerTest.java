package com.ryuqq.marketplace.application.inboundqna.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundqna.port.out.command.InboundQnaCommandPort;
import com.ryuqq.marketplace.domain.inboundqna.InboundQnaFixtures;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
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
@DisplayName("InboundQnaCommandManager 단위 테스트")
class InboundQnaCommandManagerTest {

    @InjectMocks private InboundQnaCommandManager sut;

    @Mock private InboundQnaCommandPort commandPort;

    @Nested
    @DisplayName("persist() - InboundQna 단건 저장")
    class PersistTest {

        @Test
        @DisplayName("InboundQna를 commandPort에 위임하여 저장한다")
        void persist_ValidInboundQna_DelegatesToCommandPort() {
            // given
            InboundQna inboundQna = InboundQnaFixtures.receivedInboundQna(1L);

            // when
            sut.persist(inboundQna);

            // then
            then(commandPort).should().persist(inboundQna);
        }
    }

    @Nested
    @DisplayName("persistAll() - InboundQna 목록 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("InboundQna 목록을 commandPort에 위임하여 일괄 저장한다")
        void persistAll_ValidList_DelegatesToCommandPort() {
            // given
            InboundQna qna1 = InboundQnaFixtures.receivedInboundQna(1L);
            InboundQna qna2 = InboundQnaFixtures.receivedInboundQna(2L);
            List<InboundQna> inboundQnas = List.of(qna1, qna2);

            // when
            sut.persistAll(inboundQnas);

            // then
            then(commandPort).should().persistAll(inboundQnas);
        }

        @Test
        @DisplayName("빈 목록도 commandPort에 위임하여 처리한다")
        void persistAll_EmptyList_DelegatesToCommandPort() {
            // given
            List<InboundQna> emptyList = List.of();

            // when
            sut.persistAll(emptyList);

            // then
            then(commandPort).should().persistAll(emptyList);
        }
    }
}
