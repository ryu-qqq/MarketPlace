package com.ryuqq.marketplace.application.externalcategorymapping.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.externalcategorymapping.port.out.command.ExternalCategoryMappingCommandPort;
import com.ryuqq.marketplace.domain.externalcategorymapping.ExternalCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
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
@DisplayName("ExternalCategoryMappingCommandManager 단위 테스트")
class ExternalCategoryMappingCommandManagerTest {

    @InjectMocks private ExternalCategoryMappingCommandManager sut;

    @Mock private ExternalCategoryMappingCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 외부 카테고리 매핑 저장")
    class PersistTest {

        @Test
        @DisplayName("ExternalCategoryMapping을 저장하고 ID를 반환한다")
        void persist_ReturnsMappingId() {
            // given
            ExternalCategoryMapping mapping = ExternalCategoryMappingFixtures.newMapping();
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
        @DisplayName("ExternalCategoryMapping 목록을 저장하고 ID 목록을 반환한다")
        void persistAll_ReturnsMappingIds() {
            // given
            List<ExternalCategoryMapping> mappings =
                    List.of(
                            ExternalCategoryMappingFixtures.newMapping(1L, "CAT_SHOES_001", 100L),
                            ExternalCategoryMappingFixtures.newMapping(1L, "CAT_BAG_001", 200L));
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
