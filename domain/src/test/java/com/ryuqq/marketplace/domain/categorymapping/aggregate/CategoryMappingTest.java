package com.ryuqq.marketplace.domain.categorymapping.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.categorymapping.CategoryMappingFixtures;
import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import com.ryuqq.marketplace.domain.categorymapping.vo.CategoryMappingStatus;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryMapping Aggregate 테스트")
class CategoryMappingTest {

    @Nested
    @DisplayName("forNew() - 신규 카테고리 매핑 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 카테고리 매핑을 생성한다")
        void createNewCategoryMapping() {
            Long presetId = 1L;
            Long salesChannelCategoryId = 200L;
            Long internalCategoryId = 20L;
            Instant now = CommonVoFixtures.now();

            CategoryMapping mapping =
                    CategoryMapping.forNew(
                            presetId, salesChannelCategoryId, internalCategoryId, now);

            assertThat(mapping.id().isNew()).isTrue();
            assertThat(mapping.presetId()).isEqualTo(presetId);
            assertThat(mapping.salesChannelCategoryId()).isEqualTo(salesChannelCategoryId);
            assertThat(mapping.internalCategoryId()).isEqualTo(internalCategoryId);
            assertThat(mapping.status()).isEqualTo(CategoryMappingStatus.ACTIVE);
            assertThat(mapping.isActive()).isTrue();
            assertThat(mapping.createdAt()).isEqualTo(now);
            assertThat(mapping.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 카테고리 매핑을 복원한다")
        void reconstituteActiveCategoryMapping() {
            CategoryMappingId id = CategoryMappingId.of(1L);
            Instant createdAt = CommonVoFixtures.yesterday();

            CategoryMapping mapping =
                    CategoryMapping.reconstitute(
                            id, 1L, 200L, 20L, CategoryMappingStatus.ACTIVE, createdAt, createdAt);

            assertThat(mapping.id()).isEqualTo(id);
            assertThat(mapping.idValue()).isEqualTo(1L);
            assertThat(mapping.isActive()).isTrue();
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 카테고리 매핑을 복원한다")
        void reconstituteInactiveCategoryMapping() {
            CategoryMapping mapping = CategoryMappingFixtures.inactiveCategoryMapping();

            assertThat(mapping.isActive()).isFalse();
            assertThat(mapping.status()).isEqualTo(CategoryMappingStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("activate() / deactivate() - 상태 변경")
    class StatusChangeTest {

        @Test
        @DisplayName("비활성 매핑을 활성화한다")
        void activateInactiveMapping() {
            CategoryMapping mapping = CategoryMappingFixtures.inactiveCategoryMapping();
            Instant now = CommonVoFixtures.now();

            mapping.activate(now);

            assertThat(mapping.isActive()).isTrue();
            assertThat(mapping.status()).isEqualTo(CategoryMappingStatus.ACTIVE);
            assertThat(mapping.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성 매핑을 비활성화한다")
        void deactivateActiveMapping() {
            CategoryMapping mapping = CategoryMappingFixtures.activeCategoryMapping();
            Instant now = CommonVoFixtures.now();

            mapping.deactivate(now);

            assertThat(mapping.isActive()).isFalse();
            assertThat(mapping.status()).isEqualTo(CategoryMappingStatus.INACTIVE);
            assertThat(mapping.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            CategoryMapping mapping = CategoryMappingFixtures.activeCategoryMapping(100L);
            assertThat(mapping.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("presetId()는 프리셋 ID를 반환한다")
        void presetIdReturnsPresetId() {
            CategoryMapping mapping = CategoryMappingFixtures.activeCategoryMapping();
            assertThat(mapping.presetId()).isEqualTo(CategoryMappingFixtures.DEFAULT_PRESET_ID);
        }
    }
}
