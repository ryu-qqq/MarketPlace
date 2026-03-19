package com.ryuqq.marketplace.application.refund.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.refund.port.out.command.RefundOutboxCommandPort;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
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
@DisplayName("RefundOutboxCommandManager 단위 테스트")
class RefundOutboxCommandManagerTest {

    @InjectMocks private RefundOutboxCommandManager sut;

    @Mock private RefundOutboxCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 RefundOutbox 저장")
    class PersistTest {

        @Test
        @DisplayName("RefundOutbox를 CommandPort를 통해 저장하고 ID를 반환한다")
        void persist_RefundOutbox_DelegatesToCommandPortAndReturnsId() {
            // given
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
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
    @DisplayName("persistAll() - 다건 RefundOutbox 저장")
    class PersistAllTest {

        @Test
        @DisplayName("RefundOutbox 목록을 CommandPort를 통해 일괄 저장한다")
        void persistAll_RefundOutboxList_DelegatesToCommandPort() {
            // given
            RefundOutbox outbox1 = Mockito.mock(RefundOutbox.class);
            RefundOutbox outbox2 = Mockito.mock(RefundOutbox.class);
            List<RefundOutbox> outboxes = List.of(outbox1, outbox2);

            // when
            sut.persistAll(outboxes);

            // then
            then(commandPort).should().persistAll(outboxes);
        }
    }
}
