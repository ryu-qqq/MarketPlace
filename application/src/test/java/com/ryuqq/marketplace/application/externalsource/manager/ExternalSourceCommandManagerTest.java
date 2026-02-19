package com.ryuqq.marketplace.application.externalsource.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.externalsource.port.out.command.ExternalSourceCommandPort;
import com.ryuqq.marketplace.domain.externalsource.ExternalSourceFixtures;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
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
@DisplayName("ExternalSourceCommandManager 단위 테스트")
class ExternalSourceCommandManagerTest {

    @InjectMocks private ExternalSourceCommandManager sut;

    @Mock private ExternalSourceCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 외부 소스 저장")
    class PersistTest {

        @Test
        @DisplayName("ExternalSource를 저장하고 ID를 반환한다")
        void persist_ReturnsExternalSourceId() {
            // given
            ExternalSource externalSource = ExternalSourceFixtures.newExternalSource();
            Long expectedId = 1L;

            given(commandPort.persist(externalSource)).willReturn(expectedId);

            // when
            Long result = sut.persist(externalSource);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(externalSource);
        }
    }
}
