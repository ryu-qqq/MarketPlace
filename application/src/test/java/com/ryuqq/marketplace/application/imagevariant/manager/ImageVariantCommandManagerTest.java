package com.ryuqq.marketplace.application.imagevariant.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagevariant.port.out.command.ImageVariantCommandPort;
import com.ryuqq.marketplace.domain.imagevariant.ImageVariantFixtures;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
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
@DisplayName("ImageVariantCommandManager 단위 테스트")
class ImageVariantCommandManagerTest {

    @InjectMocks private ImageVariantCommandManager sut;

    @Mock private ImageVariantCommandPort commandPort;

    @Nested
    @DisplayName("persist() - ImageVariant 저장")
    class PersistTest {

        @Test
        @DisplayName("ImageVariant를 저장하고 ID를 반환한다")
        void persist_ValidVariant_ReturnsId() {
            // given
            ImageVariant variant = ImageVariantFixtures.newSmallWebpVariant();
            Long expectedId = 1L;

            given(commandPort.persist(variant)).willReturn(expectedId);

            // when
            Long result = sut.persist(variant);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(variant);
        }
    }
}
