package com.ryuqq.marketplace.application.categorypreset.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.categorypreset.port.out.command.CategoryPresetCommandPort;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
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
@DisplayName("CategoryPresetCommandManager 단위 테스트")
class CategoryPresetCommandManagerTest {

    @InjectMocks private CategoryPresetCommandManager sut;

    @Mock private CategoryPresetCommandPort commandPort;

    @Nested
    @DisplayName("persist() - CategoryPreset 저장")
    class PersistTest {

        @Test
        @DisplayName("CategoryPreset을 저장하고 ID를 반환한다")
        void persist_ReturnsCategoryPresetId() {
            // given
            CategoryPreset categoryPreset = CategoryPresetFixtures.newCategoryPreset();
            Long expectedId = 1L;

            given(commandPort.persist(categoryPreset)).willReturn(expectedId);

            // when
            Long result = sut.persist(categoryPreset);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(categoryPreset);
        }
    }

    @Nested
    @DisplayName("persistAll() - CategoryPreset 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("CategoryPreset 목록을 저장하고 ID 목록을 반환한다")
        void persistAll_SavesAllCategoryPresets() {
            // given
            List<CategoryPreset> categoryPresets =
                    List.of(
                            CategoryPresetFixtures.newCategoryPreset(),
                            CategoryPresetFixtures.newCategoryPreset());
            List<Long> expectedIds = List.of(1L, 2L);

            given(commandPort.persistAll(categoryPresets)).willReturn(expectedIds);

            // when
            List<Long> result = sut.persistAll(categoryPresets);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(commandPort).should().persistAll(categoryPresets);
        }
    }
}
