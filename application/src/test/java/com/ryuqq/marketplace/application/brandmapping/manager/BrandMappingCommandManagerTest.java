package com.ryuqq.marketplace.application.brandmapping.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brandmapping.port.out.command.BrandMappingCommandPort;
import com.ryuqq.marketplace.domain.brandmapping.BrandMappingFixtures;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
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
@DisplayName("BrandMappingCommandManager 단위 테스트")
class BrandMappingCommandManagerTest {

    @InjectMocks private BrandMappingCommandManager sut;

    @Mock private BrandMappingCommandPort commandPort;

    @Nested
    @DisplayName("persist() - BrandMapping 저장")
    class PersistTest {

        @Test
        @DisplayName("BrandMapping을 저장하고 ID를 반환한다")
        void persist_ReturnsBrandMappingId() {
            // given
            BrandMapping brandMapping = BrandMappingFixtures.newBrandMapping();
            Long expectedId = 1L;

            given(commandPort.persist(brandMapping)).willReturn(expectedId);

            // when
            Long result = sut.persist(brandMapping);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(brandMapping);
        }
    }

    @Nested
    @DisplayName("persistAll() - BrandMapping 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("BrandMapping 목록을 저장하고 ID 목록을 반환한다")
        void persistAll_SavesAllBrandMappingsAndReturnsIds() {
            // given
            List<BrandMapping> brandMappings =
                    List.of(
                            BrandMappingFixtures.newBrandMapping(),
                            BrandMappingFixtures.newBrandMapping());
            List<Long> expectedIds = List.of(1L, 2L);

            given(commandPort.persistAll(brandMappings)).willReturn(expectedIds);

            // when
            List<Long> result = sut.persistAll(brandMappings);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(commandPort).should().persistAll(brandMappings);
        }
    }

    @Nested
    @DisplayName("deleteAllByPresetId() - PresetId로 BrandMapping 일괄 삭제")
    class DeleteAllByPresetIdTest {

        @Test
        @DisplayName("PresetId로 모든 BrandMapping을 삭제한다")
        void deleteAllByPresetId_DeletesAllBrandMappings() {
            // given
            Long presetId = 1L;

            // when
            sut.deleteAllByPresetId(presetId);

            // then
            then(commandPort).should().deleteAllByPresetId(presetId);
        }
    }

    @Nested
    @DisplayName("deleteAllByPresetIds() - PresetId 목록으로 BrandMapping 일괄 삭제")
    class DeleteAllByPresetIdsTest {

        @Test
        @DisplayName("PresetId 목록으로 모든 BrandMapping을 삭제한다")
        void deleteAllByPresetIds_DeletesAllBrandMappings() {
            // given
            List<Long> presetIds = List.of(1L, 2L, 3L);

            // when
            sut.deleteAllByPresetIds(presetIds);

            // then
            then(commandPort).should().deleteAllByPresetIds(presetIds);
        }
    }
}
