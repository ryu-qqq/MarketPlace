package com.ryuqq.marketplace.application.brandpreset.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brandpreset.port.out.command.BrandPresetCommandPort;
import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
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
@DisplayName("BrandPresetCommandManager 단위 테스트")
class BrandPresetCommandManagerTest {

    @InjectMocks private BrandPresetCommandManager sut;

    @Mock private BrandPresetCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단일 프리셋 저장")
    class PersistTest {

        @Test
        @DisplayName("브랜드 프리셋을 저장하고 ID를 반환한다")
        void persist_ValidBrandPreset_ReturnsId() {
            // given
            BrandPreset brandPreset = BrandPresetFixtures.newBrandPreset();
            Long expectedId = 1L;

            given(commandPort.persist(brandPreset)).willReturn(expectedId);

            // when
            Long result = sut.persist(brandPreset);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(brandPreset);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다중 프리셋 벌크 저장")
    class PersistAllTest {

        @Test
        @DisplayName("여러 브랜드 프리셋을 벌크로 저장하고 ID 목록을 반환한다")
        void persistAll_ValidBrandPresets_ReturnsIds() {
            // given
            List<BrandPreset> brandPresets =
                    List.of(
                            BrandPresetFixtures.newBrandPreset(),
                            BrandPresetFixtures.newBrandPreset(),
                            BrandPresetFixtures.newBrandPreset());
            List<Long> expectedIds = List.of(1L, 2L, 3L);

            given(commandPort.persistAll(brandPresets)).willReturn(expectedIds);

            // when
            List<Long> result = sut.persistAll(brandPresets);

            // then
            assertThat(result).isEqualTo(expectedIds);
            assertThat(result).hasSize(3);
            then(commandPort).should().persistAll(brandPresets);
        }

        @Test
        @DisplayName("빈 목록으로 호출하면 빈 목록을 반환한다")
        void persistAll_EmptyList_ReturnsEmptyList() {
            // given
            List<BrandPreset> emptyList = List.of();
            List<Long> expectedIds = List.of();

            given(commandPort.persistAll(emptyList)).willReturn(expectedIds);

            // when
            List<Long> result = sut.persistAll(emptyList);

            // then
            assertThat(result).isEmpty();
            then(commandPort).should().persistAll(emptyList);
        }
    }
}
