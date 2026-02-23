package com.ryuqq.marketplace.application.inboundcategorymapping.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundcategorymapping.port.out.command.InboundCategoryMappingCommandPort;
import com.ryuqq.marketplace.domain.inboundcategorymapping.InboundCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
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
@DisplayName("InboundCategoryMappingCommandManager 단위 테스트")
class InboundCategoryMappingCommandManagerTest {

    @InjectMocks private InboundCategoryMappingCommandManager sut;

    @Mock private InboundCategoryMappingCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 외부 카테고리 매핑 저장")
    class PersistTest {

        @Test
        @DisplayName("InboundCategoryMapping을 저장하고 ID를 반환한다")
        void persist_ReturnsMappingId() {
            // given
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.newMapping();
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
    @DisplayName("persistAll() - 외부 카테고리 매핑 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("InboundCategoryMapping 목록을 저장하고 ID 목록을 반환한다")
        void persistAll_ReturnsMappingIds() {
            // given
            List<InboundCategoryMapping> mappings =
                    List.of(
                            InboundCategoryMappingFixtures.newMapping(1L, "CAT_SHOES_001", 100L),
                            InboundCategoryMappingFixtures.newMapping(1L, "CAT_BAG_001", 200L));
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
