package com.ryuqq.marketplace.domain.brandmapping.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.brandmapping.BrandMappingFixtures;
import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import com.ryuqq.marketplace.domain.brandmapping.vo.BrandMappingStatus;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandMapping Aggregate 테스트")
class BrandMappingTest {

    @Nested
    @DisplayName("forNew() - 신규 브랜드 매핑 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 브랜드 매핑을 생성한다")
        void createNewBrandMapping() {
            // given
            Long presetId = 1L;
            Long salesChannelBrandId = 100L;
            Long internalBrandId = 10L;
            Instant now = CommonVoFixtures.now();

            // when
            BrandMapping mapping =
                    BrandMapping.forNew(presetId, salesChannelBrandId, internalBrandId, now);

            // then
            assertThat(mapping.id().isNew()).isTrue();
            assertThat(mapping.presetId()).isEqualTo(presetId);
            assertThat(mapping.salesChannelBrandId()).isEqualTo(salesChannelBrandId);
            assertThat(mapping.internalBrandId()).isEqualTo(internalBrandId);
            assertThat(mapping.status()).isEqualTo(BrandMappingStatus.ACTIVE);
            assertThat(mapping.isActive()).isTrue();
            assertThat(mapping.createdAt()).isEqualTo(now);
            assertThat(mapping.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 브랜드 매핑을 복원한다")
        void reconstituteActiveBrandMapping() {
            // given
            BrandMappingId id = BrandMappingId.of(1L);
            Instant createdAt = CommonVoFixtures.yesterday();

            // when
            BrandMapping mapping =
                    BrandMapping.reconstitute(
                            id, 1L, 100L, 10L, BrandMappingStatus.ACTIVE, createdAt, createdAt);

            // then
            assertThat(mapping.id()).isEqualTo(id);
            assertThat(mapping.idValue()).isEqualTo(1L);
            assertThat(mapping.isActive()).isTrue();
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 브랜드 매핑을 복원한다")
        void reconstituteInactiveBrandMapping() {
            BrandMapping mapping = BrandMappingFixtures.inactiveBrandMapping();

            assertThat(mapping.isActive()).isFalse();
            assertThat(mapping.status()).isEqualTo(BrandMappingStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("activate() / deactivate() - 상태 변경")
    class StatusChangeTest {

        @Test
        @DisplayName("비활성 매핑을 활성화한다")
        void activateInactiveMapping() {
            // given
            BrandMapping mapping = BrandMappingFixtures.inactiveBrandMapping();
            Instant now = CommonVoFixtures.now();

            // when
            mapping.activate(now);

            // then
            assertThat(mapping.isActive()).isTrue();
            assertThat(mapping.status()).isEqualTo(BrandMappingStatus.ACTIVE);
            assertThat(mapping.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성 매핑을 비활성화한다")
        void deactivateActiveMapping() {
            // given
            BrandMapping mapping = BrandMappingFixtures.activeBrandMapping();
            Instant now = CommonVoFixtures.now();

            // when
            mapping.deactivate(now);

            // then
            assertThat(mapping.isActive()).isFalse();
            assertThat(mapping.status()).isEqualTo(BrandMappingStatus.INACTIVE);
            assertThat(mapping.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            BrandMapping mapping = BrandMappingFixtures.activeBrandMapping(100L);
            assertThat(mapping.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("presetId()는 프리셋 ID를 반환한다")
        void presetIdReturnsPresetId() {
            BrandMapping mapping = BrandMappingFixtures.activeBrandMapping();
            assertThat(mapping.presetId()).isEqualTo(BrandMappingFixtures.DEFAULT_PRESET_ID);
        }
    }
}
