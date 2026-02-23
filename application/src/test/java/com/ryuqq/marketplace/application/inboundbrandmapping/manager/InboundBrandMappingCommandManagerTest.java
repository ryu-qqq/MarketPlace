package com.ryuqq.marketplace.application.inboundbrandmapping.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundbrandmapping.port.out.command.InboundBrandMappingCommandPort;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
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
@DisplayName("InboundBrandMappingCommandManager 단위 테스트")
class InboundBrandMappingCommandManagerTest {

    @InjectMocks private InboundBrandMappingCommandManager sut;

    @Mock private InboundBrandMappingCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 외부 브랜드 매핑 저장")
    class PersistTest {

        @Test
        @DisplayName("InboundBrandMapping을 저장하고 ID를 반환한다")
        void persist_ReturnsMappingId() {
            // given
            InboundBrandMapping mapping = InboundBrandMappingFixtures.newMapping();
            Long expectedId = 1L;

            given(commandPort.persist(mapping)).willReturn(expectedId);

            // when
            Long result = sut.persist(mapping);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(mapping);
        }
    }

    @Nested
    @DisplayName("persistAll() - 외부 브랜드 매핑 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("InboundBrandMapping 목록을 저장하고 ID 목록을 반환한다")
        void persistAll_ReturnsMappingIds() {
            // given
            List<InboundBrandMapping> mappings =
                    List.of(
                            InboundBrandMappingFixtures.newMapping(1L, "BR001", 100L),
                            InboundBrandMappingFixtures.newMapping(1L, "BR002", 200L));
            List<Long> expectedIds = List.of(1L, 2L);

            given(commandPort.persistAll(mappings)).willReturn(expectedIds);

            // when
            List<Long> result = sut.persistAll(mappings);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expectedIds);
            then(commandPort).should().persistAll(mappings);
        }
    }
}
