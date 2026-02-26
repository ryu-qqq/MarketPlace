package com.ryuqq.marketplace.domain.saleschannelcategory.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.saleschannelcategory.vo.SalesChannelCategoryStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategory Aggregate 테스트")
class SalesChannelCategoryTest {

    @Nested
    @DisplayName("forNew() - 신규 판매채널 카테고리 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 판매채널 카테고리를 생성한다")
        void createNewSalesChannelCategoryWithRequiredFields() {
            // given
            Long salesChannelId = 1L;
            String externalCategoryCode = "CAT001";
            String externalCategoryName = "테스트 카테고리";
            String displayPath = "테스트 카테고리";
            Instant now = CommonVoFixtures.now();

            // when
            SalesChannelCategory category =
                    SalesChannelCategory.forNew(
                            salesChannelId,
                            externalCategoryCode,
                            externalCategoryName,
                            null,
                            1,
                            "/CAT001",
                            1,
                            false,
                            displayPath,
                            now);

            // then
            assertThat(category.salesChannelId()).isEqualTo(salesChannelId);
            assertThat(category.externalCategoryCode()).isEqualTo(externalCategoryCode);
            assertThat(category.externalCategoryName()).isEqualTo(externalCategoryName);
            assertThat(category.parentId()).isNull();
            assertThat(category.depth()).isEqualTo(1);
            assertThat(category.path()).isEqualTo("/CAT001");
            assertThat(category.sortOrder()).isEqualTo(1);
            assertThat(category.isLeaf()).isFalse();
            assertThat(category.status()).isEqualTo(SalesChannelCategoryStatus.ACTIVE);
            assertThat(category.displayPath()).isEqualTo(displayPath);
            assertThat(category.createdAt()).isEqualTo(now);
            assertThat(category.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("하위 카테고리를 생성한다")
        void createChildCategory() {
            // given
            Long parentId = 100L;
            Instant now = CommonVoFixtures.now();

            // when
            SalesChannelCategory category =
                    SalesChannelCategory.forNew(
                            1L,
                            "CAT002",
                            "하위 카테고리",
                            parentId,
                            2,
                            "/CAT001/CAT002",
                            1,
                            false,
                            "상위 > 하위 카테고리",
                            now);

            // then
            assertThat(category.parentId()).isEqualTo(parentId);
            assertThat(category.depth()).isEqualTo(2);
            assertThat(category.path()).isEqualTo("/CAT001/CAT002");
        }

        @Test
        @DisplayName("말단 카테고리를 생성한다")
        void createLeafCategory() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            SalesChannelCategory category =
                    SalesChannelCategory.forNew(
                            1L,
                            "CAT003",
                            "말단 카테고리",
                            100L,
                            3,
                            "/CAT001/CAT002/CAT003",
                            1,
                            true,
                            "상위 > 중간 > 말단 카테고리",
                            now);

            // then
            assertThat(category.isLeaf()).isTrue();
            assertThat(category.depth()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 카테고리를 복원한다")
        void reconstituteActiveCategory() {
            // given
            SalesChannelCategoryId id =
                    SalesChannelCategoryFixtures.defaultSalesChannelCategoryId();
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            SalesChannelCategory category =
                    SalesChannelCategory.reconstitute(
                            id,
                            1L,
                            "CAT001",
                            "활성 카테고리",
                            null,
                            1,
                            "/CAT001",
                            1,
                            false,
                            SalesChannelCategoryStatus.ACTIVE,
                            "활성 카테고리",
                            createdAt,
                            updatedAt);

            // then
            assertThat(category.id()).isEqualTo(id);
            assertThat(category.isActive()).isTrue();
            assertThat(category.status()).isEqualTo(SalesChannelCategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 카테고리를 복원한다")
        void reconstituteInactiveCategory() {
            // given
            SalesChannelCategoryId id = SalesChannelCategoryId.of(2L);
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            SalesChannelCategory category =
                    SalesChannelCategory.reconstitute(
                            id,
                            1L,
                            "CAT002",
                            "비활성 카테고리",
                            null,
                            1,
                            "/CAT002",
                            1,
                            false,
                            SalesChannelCategoryStatus.INACTIVE,
                            "비활성 카테고리",
                            createdAt,
                            updatedAt);

            // then
            assertThat(category.id()).isEqualTo(id);
            assertThat(category.isActive()).isFalse();
            assertThat(category.status()).isEqualTo(SalesChannelCategoryStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("update() - 카테고리 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("카테고리 정보를 수정한다")
        void updateCategoryInfo() {
            // given
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();
            SalesChannelCategoryUpdateData updateData =
                    SalesChannelCategoryFixtures.salesChannelCategoryUpdateData();
            Instant now = CommonVoFixtures.now();

            // when
            category.update(updateData, now);

            // then
            assertThat(category.externalCategoryName())
                    .isEqualTo(updateData.externalCategoryName());
            assertThat(category.sortOrder()).isEqualTo(updateData.sortOrder());
            assertThat(category.isLeaf()).isEqualTo(updateData.leaf());
            assertThat(category.status()).isEqualTo(updateData.status());
            assertThat(category.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("카테고리를 말단 노드로 변경한다")
        void updateToLeafCategory() {
            // given
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();
            SalesChannelCategoryUpdateData updateData =
                    SalesChannelCategoryFixtures.salesChannelCategoryUpdateData(
                            "변경된 카테고리", 5, true, SalesChannelCategoryStatus.ACTIVE);
            Instant now = CommonVoFixtures.now();

            // when
            category.update(updateData, now);

            // then
            assertThat(category.isLeaf()).isTrue();
        }

        @Test
        @DisplayName("카테고리 상태를 비활성화로 변경한다")
        void updateToInactiveStatus() {
            // given
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();
            SalesChannelCategoryUpdateData updateData =
                    SalesChannelCategoryFixtures.salesChannelCategoryUpdateData(
                            "비활성 카테고리", 1, false, SalesChannelCategoryStatus.INACTIVE);
            Instant now = CommonVoFixtures.now();

            // when
            category.update(updateData, now);

            // then
            assertThat(category.status()).isEqualTo(SalesChannelCategoryStatus.INACTIVE);
            assertThat(category.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory(100L);

            // when
            Long idValue = category.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("isActive()는 상태가 ACTIVE이면 true를 반환한다")
        void isActiveReturnsTrueWhenStatusIsActive() {
            // given
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();

            // then
            assertThat(category.isActive()).isTrue();
        }

        @Test
        @DisplayName("isActive()는 상태가 INACTIVE이면 false를 반환한다")
        void isActiveReturnsFalseWhenStatusIsInactive() {
            // given
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.inactiveSalesChannelCategory();

            // then
            assertThat(category.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("계층 구조 테스트")
    class HierarchyTest {

        @Test
        @DisplayName("최상위 카테고리는 부모 ID가 null이다")
        void rootCategoryHasNullParentId() {
            // given & when
            SalesChannelCategory category =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();

            // then
            assertThat(category.parentId()).isNull();
            assertThat(category.depth()).isEqualTo(1);
        }

        @Test
        @DisplayName("하위 카테고리는 부모 ID를 가진다")
        void childCategoryHasParentId() {
            // given & when
            SalesChannelCategory category = SalesChannelCategoryFixtures.newChildCategory(100L);

            // then
            assertThat(category.parentId()).isNotNull();
            assertThat(category.parentId()).isEqualTo(100L);
            assertThat(category.depth()).isGreaterThan(1);
        }

        @Test
        @DisplayName("말단 카테고리는 leaf=true를 가진다")
        void leafCategoryHasLeafTrue() {
            // given & when
            SalesChannelCategory category = SalesChannelCategoryFixtures.leafCategory();

            // then
            assertThat(category.isLeaf()).isTrue();
        }
    }
}
