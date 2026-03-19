package com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingStatus;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingUpdateData;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundBrandMapping Aggregate 단위 테스트")
class InboundBrandMappingTest {

    @Nested
    @DisplayName("forNew() - 신규 InboundBrandMapping 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 매핑은 ACTIVE 상태로 생성된다")
        void createNewMappingWithActiveStatus() {
            InboundBrandMapping mapping = InboundBrandMappingFixtures.newMapping();

            assertThat(mapping.status()).isEqualTo(InboundBrandMappingStatus.ACTIVE);
            assertThat(mapping.isActive()).isTrue();
        }

        @Test
        @DisplayName("신규 매핑은 isNew()가 true이다")
        void createNewMappingIsNew() {
            InboundBrandMapping mapping = InboundBrandMappingFixtures.newMapping();

            assertThat(mapping.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 매핑의 기본 정보가 올바르게 설정된다")
        void createNewMappingHasCorrectInfo() {
            InboundBrandMapping mapping = InboundBrandMappingFixtures.newMapping();

            assertThat(mapping.inboundSourceId())
                    .isEqualTo(InboundBrandMappingFixtures.DEFAULT_EXTERNAL_SOURCE_ID);
            assertThat(mapping.externalBrandCode())
                    .isEqualTo(InboundBrandMappingFixtures.DEFAULT_EXTERNAL_BRAND_CODE);
            assertThat(mapping.externalBrandName())
                    .isEqualTo(InboundBrandMappingFixtures.DEFAULT_EXTERNAL_BRAND_NAME);
            assertThat(mapping.internalBrandId())
                    .isEqualTo(InboundBrandMappingFixtures.DEFAULT_INTERNAL_BRAND_ID);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("ACTIVE 상태로 복원된 매핑은 isActive()가 true이다")
        void reconstitutedActiveMappingIsActive() {
            InboundBrandMapping mapping = InboundBrandMappingFixtures.activeMapping();

            assertThat(mapping.isActive()).isTrue();
            assertThat(mapping.id().isNew()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE 상태로 복원된 매핑은 isActive()가 false이다")
        void reconstitutedInactiveMappingIsNotActive() {
            InboundBrandMapping mapping = InboundBrandMappingFixtures.inactiveMapping();

            assertThat(mapping.isActive()).isFalse();
            assertThat(mapping.status()).isEqualTo(InboundBrandMappingStatus.INACTIVE);
        }

        @Test
        @DisplayName("복원된 매핑의 idValue()가 올바르다")
        void reconstitutedMappingHasCorrectIdValue() {
            InboundBrandMapping mapping = InboundBrandMappingFixtures.activeMapping(5L);

            assertThat(mapping.idValue()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("update() - 매핑 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("매핑 정보를 수정하면 필드가 업데이트된다")
        void updateMappingUpdatesFields() {
            InboundBrandMapping mapping = InboundBrandMappingFixtures.activeMapping();
            InboundBrandMappingUpdateData updateData =
                    InboundBrandMappingUpdateData.of(
                            "수정된 브랜드 이름", 999L, InboundBrandMappingStatus.INACTIVE);
            Instant now = CommonVoFixtures.now();

            mapping.update(updateData, now);

            assertThat(mapping.externalBrandName()).isEqualTo("수정된 브랜드 이름");
            assertThat(mapping.internalBrandId()).isEqualTo(999L);
            assertThat(mapping.status()).isEqualTo(InboundBrandMappingStatus.INACTIVE);
            assertThat(mapping.isActive()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE로 수정하면 isActive()가 false가 된다")
        void updateToInactiveSetsInactive() {
            InboundBrandMapping mapping = InboundBrandMappingFixtures.activeMapping();
            InboundBrandMappingUpdateData updateData =
                    InboundBrandMappingUpdateData.of(
                            mapping.externalBrandName(),
                            mapping.internalBrandId(),
                            InboundBrandMappingStatus.INACTIVE);

            mapping.update(updateData, CommonVoFixtures.now());

            assertThat(mapping.isActive()).isFalse();
        }

        @Test
        @DisplayName("수정 후 updatedAt이 갱신된다")
        void updateRefreshesUpdatedAt() {
            InboundBrandMapping mapping = InboundBrandMappingFixtures.activeMapping();
            Instant before = mapping.updatedAt();
            Instant now = CommonVoFixtures.now();
            InboundBrandMappingUpdateData updateData =
                    InboundBrandMappingUpdateData.of("변경", 1L, InboundBrandMappingStatus.ACTIVE);

            mapping.update(updateData, now);

            assertThat(mapping.updatedAt()).isEqualTo(now);
        }
    }
}
