package com.ryuqq.marketplace.domain.productgroup.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageType Enum 단위 테스트")
class ImageTypeTest {

    @Nested
    @DisplayName("displayName() 테스트")
    class DisplayNameTest {

        @Test
        @DisplayName("모든 이미지 타입은 표시 이름을 가진다")
        void allTypesHaveDisplayName() {
            assertThat(ImageType.THUMBNAIL.displayName()).isEqualTo("대표 이미지");
            assertThat(ImageType.DETAIL.displayName()).isEqualTo("상세 이미지");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 이미지 타입이 존재한다")
        void allValuesExist() {
            assertThat(ImageType.values())
                    .containsExactly(ImageType.THUMBNAIL, ImageType.DETAIL);
        }
    }
}
