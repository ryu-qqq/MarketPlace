package com.ryuqq.marketplace.domain.inboundbrandmapping.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundBrandMappingUpdateData 단위 테스트")
class InboundBrandMappingUpdateDataTest {

    @Nested
    @DisplayName("of() 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 UpdateData를 생성한다")
        void createWithValidValues() {
            InboundBrandMappingUpdateData updateData =
                    InboundBrandMappingUpdateData.of(
                            "새 브랜드 이름", 200L, InboundBrandMappingStatus.ACTIVE);

            assertThat(updateData.externalBrandName()).isEqualTo("새 브랜드 이름");
            assertThat(updateData.internalBrandId()).isEqualTo(200L);
            assertThat(updateData.status()).isEqualTo(InboundBrandMappingStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("동일한 필드를 가진 UpdateData는 같다")
        void sameDataAreEqual() {
            InboundBrandMappingUpdateData data1 =
                    InboundBrandMappingUpdateData.of(
                            "브랜드 A", 100L, InboundBrandMappingStatus.ACTIVE);
            InboundBrandMappingUpdateData data2 =
                    InboundBrandMappingUpdateData.of(
                            "브랜드 A", 100L, InboundBrandMappingStatus.ACTIVE);

            assertThat(data1).isEqualTo(data2);
        }
    }
}
