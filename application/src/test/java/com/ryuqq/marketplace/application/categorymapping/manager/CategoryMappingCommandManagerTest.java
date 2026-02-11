package com.ryuqq.marketplace.application.categorymapping.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.categorymapping.port.out.command.CategoryMappingCommandPort;
import com.ryuqq.marketplace.domain.categorymapping.CategoryMappingFixtures;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
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
@DisplayName("CategoryMappingCommandManager 단위 테스트")
class CategoryMappingCommandManagerTest {

    @InjectMocks private CategoryMappingCommandManager sut;

    @Mock private CategoryMappingCommandPort commandPort;

    @Nested
    @DisplayName("persist() - CategoryMapping 저장")
    class PersistTest {

        @Test
        @DisplayName("CategoryMapping을 저장하고 ID를 반환한다")
        void persist_ReturnsCategoryMappingId() {
            // given
            CategoryMapping categoryMapping = CategoryMappingFixtures.newCategoryMapping();
            Long expectedId = 1L;

            given(commandPort.persist(categoryMapping)).willReturn(expectedId);

            // when
            Long result = sut.persist(categoryMapping);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(categoryMapping);
        }
    }

    @Nested
    @DisplayName("persistAll() - CategoryMapping 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("CategoryMapping 목록을 저장하고 ID 목록을 반환한다")
        void persistAll_SavesAllCategoryMappingsAndReturnsIds() {
            // given
            List<CategoryMapping> categoryMappings =
                    List.of(
                            CategoryMappingFixtures.newCategoryMapping(),
                            CategoryMappingFixtures.newCategoryMapping());
            List<Long> expectedIds = List.of(1L, 2L);

            given(commandPort.persistAll(categoryMappings)).willReturn(expectedIds);

            // when
            List<Long> result = sut.persistAll(categoryMappings);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(commandPort).should().persistAll(categoryMappings);
        }
    }

    @Nested
    @DisplayName("deleteAllByPresetId() - PresetId로 CategoryMapping 일괄 삭제")
    class DeleteAllByPresetIdTest {

        @Test
        @DisplayName("PresetId로 모든 CategoryMapping을 삭제한다")
        void deleteAllByPresetId_DeletesAllCategoryMappings() {
            // given
            Long presetId = 1L;

            // when
            sut.deleteAllByPresetId(presetId);

            // then
            then(commandPort).should().deleteAllByPresetId(presetId);
        }
    }

    @Nested
    @DisplayName("deleteAllByPresetIds() - PresetId 목록으로 CategoryMapping 일괄 삭제")
    class DeleteAllByPresetIdsTest {

        @Test
        @DisplayName("PresetId 목록으로 모든 CategoryMapping을 삭제한다")
        void deleteAllByPresetIds_DeletesAllCategoryMappings() {
            // given
            List<Long> presetIds = List.of(1L, 2L, 3L);

            // when
            sut.deleteAllByPresetIds(presetIds);

            // then
            then(commandPort).should().deleteAllByPresetIds(presetIds);
        }
    }
}
