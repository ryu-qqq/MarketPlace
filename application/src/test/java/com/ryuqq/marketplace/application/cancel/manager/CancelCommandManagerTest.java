package com.ryuqq.marketplace.application.cancel.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.port.out.command.CancelCommandPort;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
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
@DisplayName("CancelCommandManager 단위 테스트")
class CancelCommandManagerTest {

    @InjectMocks private CancelCommandManager sut;

    @Mock private CancelCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 Cancel 저장")
    class PersistTest {

        @Test
        @DisplayName("Cancel을 CommandPort를 통해 저장한다")
        void persist_Cancel_DelegatesToCommandPort() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();

            // when
            sut.persist(cancel);

            // then
            then(commandPort).should().persist(cancel);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다건 Cancel 저장")
    class PersistAllTest {

        @Test
        @DisplayName("Cancel 목록을 CommandPort를 통해 일괄 저장한다")
        void persistAll_CancelList_DelegatesToCommandPort() {
            // given
            List<Cancel> cancels =
                    List.of(CancelFixtures.requestedCancel(), CancelFixtures.requestedCancel());

            // when
            sut.persistAll(cancels);

            // then
            then(commandPort).should().persistAll(cancels);
        }

        @Test
        @DisplayName("빈 목록도 CommandPort를 통해 저장 시도한다")
        void persistAll_EmptyList_DelegatesToCommandPort() {
            // given
            List<Cancel> emptyList = List.of();

            // when
            sut.persistAll(emptyList);

            // then
            then(commandPort).should().persistAll(emptyList);
        }
    }
}
