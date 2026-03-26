package com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.InboundCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingUpdateData;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundCategoryMapping Aggregate 단위 테스트")
class InboundCategoryMappingTest {

    @Nested
    @DisplayName("forNew() - 신규 InboundCategoryMapping 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 매핑은 ACTIVE 상태로 생성된다")
        void createNewMappingWithActiveStatus() {
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.newMapping();

            assertThat(mapping.status()).isEqualTo(InboundCategoryMappingStatus.ACTIVE);
            assertThat(mapping.isActive()).isTrue();
        }

        @Test
        @DisplayName("신규 매핑은 isNew()가 true이다")
        void createNewMappingIsNew() {
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.newMapping();

            assertThat(mapping.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 매핑의 기본 정보가 올바르게 설정된다")
        void createNewMappingHasCorrectInfo() {
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.newMapping();

            assertThat(mapping.inboundSourceId())
                    .isEqualTo(InboundCategoryMappingFixtures.DEFAULT_EXTERNAL_SOURCE_ID);
            assertThat(mapping.externalCategoryCode())
                    .isEqualTo(InboundCategoryMappingFixtures.DEFAULT_EXTERNAL_CATEGORY_CODE);
            assertThat(mapping.externalCategoryName())
                    .isEqualTo(InboundCategoryMappingFixtures.DEFAULT_EXTERNAL_CATEGORY_NAME);
            assertThat(mapping.internalCategoryId())
                    .isEqualTo(InboundCategoryMappingFixtures.DEFAULT_INTERNAL_CATEGORY_ID);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("ACTIVE 상태로 복원된 매핑은 isActive()가 true이다")
        void reconstitutedActiveMappingIsActive() {
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.activeMapping();

            assertThat(mapping.isActive()).isTrue();
            assertThat(mapping.id().isNew()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE 상태로 복원된 매핑은 isActive()가 false이다")
        void reconstitutedInactiveMappingIsNotActive() {
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.inactiveMapping();

            assertThat(mapping.isActive()).isFalse();
            assertThat(mapping.status()).isEqualTo(InboundCategoryMappingStatus.INACTIVE);
        }

        @Test
        @DisplayName("복원된 매핑의 idValue()가 올바르다")
        void reconstitutedMappingHasCorrectIdValue() {
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.activeMapping(7L);

            assertThat(mapping.idValue()).isEqualTo(7L);
        }
    }

    @Nested
    @DisplayName("update() - 매핑 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("매핑 정보를 수정하면 필드가 업데이트된다")
        void updateMappingUpdatesFields() {
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.activeMapping();
            InboundCategoryMappingUpdateData updateData =
                    InboundCategoryMappingUpdateData.of(
                            "수정된 카테고리 이름", 999L, InboundCategoryMappingStatus.INACTIVE);
            Instant now = CommonVoFixtures.now();

            mapping.update(updateData, now);

            assertThat(mapping.externalCategoryName()).isEqualTo("수정된 카테고리 이름");
            assertThat(mapping.internalCategoryId()).isEqualTo(999L);
            assertThat(mapping.status()).isEqualTo(InboundCategoryMappingStatus.INACTIVE);
            assertThat(mapping.isActive()).isFalse();
        }

        @Test
        @DisplayName("수정 후 updatedAt이 갱신된다")
        void updateRefreshesUpdatedAt() {
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.activeMapping();
            Instant now = CommonVoFixtures.now();
            InboundCategoryMappingUpdateData updateData =
                    InboundCategoryMappingUpdateData.of(
                            "변경", 1L, InboundCategoryMappingStatus.ACTIVE);

            mapping.update(updateData, now);

            assertThat(mapping.updatedAt()).isEqualTo(now);
        }
    }
}
