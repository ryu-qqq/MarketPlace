package com.ryuqq.marketplace.domain.category.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Category Aggregate 단위 테스트. */
@DisplayName("Category")
class CategoryTest {

    private static final Instant NOW = Instant.now();

    @Nested
    @DisplayName("forNew")
    class ForNew {

        @Test
        @DisplayName("새 카테고리를 생성할 때 CategoryGroup이 설정된다")
        void createWithCategoryGroup() {
            Category category =
                    Category.forNew(
                            CategoryCode.of("FASHION"),
                            CategoryName.of("패션", "Fashion"),
                            null,
                            CategoryDepth.of(0),
                            CategoryPath.of("1"),
                            SortOrder.of(1),
                            Department.FASHION,
                            CategoryGroup.CLOTHING,
                            NOW);

            assertThat(category.categoryGroup()).isEqualTo(CategoryGroup.CLOTHING);
            assertThat(category.isLeaf()).isTrue();
            assertThat(category.status()).isEqualTo(CategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("CategoryGroup이 null이면 ETC로 기본 설정된다")
        void defaultsToEtcWhenNull() {
            Category category =
                    Category.forNew(
                            CategoryCode.of("TEST"),
                            CategoryName.of("테스트", "Test"),
                            null,
                            CategoryDepth.of(0),
                            CategoryPath.of("1"),
                            SortOrder.of(1),
                            Department.LIVING,
                            null,
                            NOW);

            assertThat(category.categoryGroup()).isEqualTo(CategoryGroup.ETC);
        }
    }

    @Nested
    @DisplayName("reconstitute")
    class Reconstitute {

        @Test
        @DisplayName("저장된 카테고리를 복원할 때 CategoryGroup이 유지된다")
        void restoresWithCategoryGroup() {
            Category category =
                    Category.reconstitute(
                            CategoryId.of(1L),
                            CategoryCode.of("SHOES"),
                            CategoryName.of("신발", "Shoes"),
                            null,
                            CategoryDepth.of(1),
                            CategoryPath.of("1/2"),
                            SortOrder.of(2),
                            true,
                            CategoryStatus.ACTIVE,
                            Department.FASHION,
                            CategoryGroup.SHOES,
                            null,
                            NOW.minusSeconds(3600),
                            NOW);

            assertThat(category.categoryGroup()).isEqualTo(CategoryGroup.SHOES);
            assertThat(category.idValue()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("CategoryGroup을 포함한 카테고리 정보를 수정한다")
        void updatesCategoryGroup() {
            Category category = createSampleCategory(CategoryGroup.CLOTHING);

            CategoryUpdateData updateData =
                    new CategoryUpdateData(
                            CategoryName.of("의류 수정", "Clothing Updated"),
                            SortOrder.of(10),
                            CategoryStatus.ACTIVE,
                            Department.FASHION,
                            CategoryGroup.SHOES);

            Instant updateTime = NOW.plusSeconds(3600);
            category.update(updateData, updateTime);

            assertThat(category.categoryGroup()).isEqualTo(CategoryGroup.SHOES);
            assertThat(category.nameKo()).isEqualTo("의류 수정");
            assertThat(category.updatedAt()).isEqualTo(updateTime);
        }
    }

    @Nested
    @DisplayName("requiresNoticeInfo")
    class RequiresNoticeInfo {

        @Test
        @DisplayName("CLOTHING 그룹은 고시정보가 필요하다")
        void clothingRequiresNotice() {
            Category category = createSampleCategory(CategoryGroup.CLOTHING);
            assertThat(category.categoryGroup().requiresNoticeInfo()).isTrue();
        }

        @Test
        @DisplayName("DIGITAL 그룹은 고시정보가 필요하다")
        void digitalRequiresNotice() {
            Category category = createSampleCategory(CategoryGroup.DIGITAL);
            assertThat(category.categoryGroup().requiresNoticeInfo()).isTrue();
        }

        @Test
        @DisplayName("ETC 그룹은 고시정보가 필요하지 않다")
        void etcDoesNotRequireNotice() {
            Category category = createSampleCategory(CategoryGroup.ETC);
            assertThat(category.categoryGroup().requiresNoticeInfo()).isFalse();
        }
    }

    @Nested
    @DisplayName("lifecycle")
    class Lifecycle {

        @Test
        @DisplayName("카테고리 활성화/비활성화 시 CategoryGroup은 유지된다")
        void categoryGroupRemainsAfterStatusChange() {
            Category category = createSampleCategory(CategoryGroup.BAGS);

            category.deactivate(NOW.plusSeconds(100));
            assertThat(category.categoryGroup()).isEqualTo(CategoryGroup.BAGS);
            assertThat(category.status()).isEqualTo(CategoryStatus.INACTIVE);

            category.activate(NOW.plusSeconds(200));
            assertThat(category.categoryGroup()).isEqualTo(CategoryGroup.BAGS);
            assertThat(category.status()).isEqualTo(CategoryStatus.ACTIVE);
        }

        @Test
        @DisplayName("카테고리 삭제/복원 시 CategoryGroup은 유지된다")
        void categoryGroupRemainsAfterDeletion() {
            Category category = createSampleCategory(CategoryGroup.JEWELRY);

            category.delete(NOW.plusSeconds(100));
            assertThat(category.categoryGroup()).isEqualTo(CategoryGroup.JEWELRY);
            assertThat(category.isDeleted()).isTrue();

            category.restore(NOW.plusSeconds(200));
            assertThat(category.categoryGroup()).isEqualTo(CategoryGroup.JEWELRY);
            assertThat(category.isDeleted()).isFalse();
        }
    }

    private Category createSampleCategory(CategoryGroup categoryGroup) {
        return Category.forNew(
                CategoryCode.of("TEST"),
                CategoryName.of("테스트", "Test"),
                null,
                CategoryDepth.of(0),
                CategoryPath.of("1"),
                SortOrder.of(1),
                Department.FASHION,
                categoryGroup,
                NOW);
    }
}
