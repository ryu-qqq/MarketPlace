package com.ryuqq.marketplace.application.exchange.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.exchange.port.out.command.ExchangeOutboxCommandPort;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeOutboxCommandManager 단위 테스트")
class ExchangeOutboxCommandManagerTest {

    @InjectMocks private ExchangeOutboxCommandManager sut;

    @Mock private ExchangeOutboxCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 ExchangeOutbox 저장")
    class PersistTest {

        @Test
        @DisplayName("ExchangeOutbox를 CommandPort를 통해 저장하고 ID를 반환한다")
        void persist_ExchangeOutbox_DelegatesToCommandPortAndReturnsId() {
            // given
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            Long expectedId = 1L;

            given(commandPort.persist(outbox)).willReturn(expectedId);

            // when
            Long result = sut.persist(outbox);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(outbox);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다건 ExchangeOutbox 저장")
    class PersistAllTest {

        @Test
        @DisplayName("ExchangeOutbox 목록을 CommandPort를 통해 일괄 저장한다")
        void persistAll_ExchangeOutboxList_DelegatesToCommandPort() {
            // given
            ExchangeOutbox outbox1 = Mockito.mock(ExchangeOutbox.class);
            ExchangeOutbox outbox2 = Mockito.mock(ExchangeOutbox.class);
            List<ExchangeOutbox> outboxes = List.of(outbox1, outbox2);

            // when
            sut.persistAll(outboxes);

            // then
            then(commandPort).should().persistAll(outboxes);
        }
    }
}
