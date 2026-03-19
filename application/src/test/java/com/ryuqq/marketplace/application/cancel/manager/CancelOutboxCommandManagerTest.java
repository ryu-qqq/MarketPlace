package com.ryuqq.marketplace.application.cancel.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.port.out.command.CancelOutboxCommandPort;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
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
@DisplayName("CancelOutboxCommandManager 단위 테스트")
class CancelOutboxCommandManagerTest {

    @InjectMocks private CancelOutboxCommandManager sut;

    @Mock private CancelOutboxCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 CancelOutbox 저장")
    class PersistTest {

        @Test
        @DisplayName("CancelOutbox를 저장하고 ID를 반환한다")
        void persist_CancelOutbox_ReturnsOutboxId() {
            // given
            CancelOutbox outbox = org.mockito.Mockito.mock(CancelOutbox.class);
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
    @DisplayName("persistAll() - 다건 CancelOutbox 저장")
    class PersistAllTest {

        @Test
        @DisplayName("CancelOutbox 목록을 일괄 저장한다")
        void persistAll_CancelOutboxList_DelegatesToCommandPort() {
            // given
            CancelOutbox outbox1 = org.mockito.Mockito.mock(CancelOutbox.class);
            CancelOutbox outbox2 = org.mockito.Mockito.mock(CancelOutbox.class);
            List<CancelOutbox> outboxes = List.of(outbox1, outbox2);

            // when
            sut.persistAll(outboxes);

            // then
            then(commandPort).should().persistAll(outboxes);
        }
    }
}
