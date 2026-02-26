package com.ryuqq.marketplace.domain.productgroup.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupStatus Enum 단위 테스트")
class ProductGroupStatusTest {

    @Nested
    @DisplayName("isActive() 테스트")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVE는 isActive() true를 반환한다")
        void activeIsActive() {
            assertThat(ProductGroupStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("DRAFT는 isActive() false를 반환한다")
        void draftIsNotActive() {
            assertThat(ProductGroupStatus.DRAFT.isActive()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE는 isActive() false를 반환한다")
        void inactiveIsNotActive() {
            assertThat(ProductGroupStatus.INACTIVE.isActive()).isFalse();
        }

        @Test
        @DisplayName("SOLDOUT는 isActive() false를 반환한다")
        void soldoutIsNotActive() {
            assertThat(ProductGroupStatus.SOLDOUT.isActive()).isFalse();
        }

        @Test
        @DisplayName("DELETED는 isActive() false를 반환한다")
        void deletedIsNotActive() {
            assertThat(ProductGroupStatus.DELETED.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("isDeleted() 테스트")
    class IsDeletedTest {

        @Test
        @DisplayName("DELETED는 isDeleted() true를 반환한다")
        void deletedIsDeleted() {
            assertThat(ProductGroupStatus.DELETED.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("DELETED가 아닌 상태는 isDeleted() false를 반환한다")
        void nonDeletedIsNotDeleted() {
            assertThat(ProductGroupStatus.DRAFT.isDeleted()).isFalse();
            assertThat(ProductGroupStatus.ACTIVE.isDeleted()).isFalse();
            assertThat(ProductGroupStatus.INACTIVE.isDeleted()).isFalse();
            assertThat(ProductGroupStatus.SOLDOUT.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("canActivate() 테스트")
    class CanActivateTest {

        @Test
        @DisplayName("DRAFT는 활성화 가능하다")
        void draftCanActivate() {
            assertThat(ProductGroupStatus.DRAFT.canActivate()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 활성화 가능하다")
        void inactiveCanActivate() {
            assertThat(ProductGroupStatus.INACTIVE.canActivate()).isTrue();
        }

        @Test
        @DisplayName("SOLDOUT는 활성화 가능하다")
        void soldoutCanActivate() {
            assertThat(ProductGroupStatus.SOLDOUT.canActivate()).isTrue();
        }

        @Test
        @DisplayName("ACTIVE는 재검수 통과 시 멱등 활성화를 허용한다")
        void activeCanActivate() {
            assertThat(ProductGroupStatus.ACTIVE.canActivate()).isTrue();
        }

        @Test
        @DisplayName("DELETED는 활성화 불가능하다")
        void deletedCannotActivate() {
            assertThat(ProductGroupStatus.DELETED.canActivate()).isFalse();
        }
    }

    @Nested
    @DisplayName("canDelete() 테스트")
    class CanDeleteTest {

        @Test
        @DisplayName("DRAFT는 삭제 가능하다")
        void draftCanDelete() {
            assertThat(ProductGroupStatus.DRAFT.canDelete()).isTrue();
        }

        @Test
        @DisplayName("ACTIVE는 삭제 가능하다")
        void activeCanDelete() {
            assertThat(ProductGroupStatus.ACTIVE.canDelete()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 삭제 가능하다")
        void inactiveCanDelete() {
            assertThat(ProductGroupStatus.INACTIVE.canDelete()).isTrue();
        }

        @Test
        @DisplayName("SOLDOUT는 삭제 가능하다")
        void soldoutCanDelete() {
            assertThat(ProductGroupStatus.SOLDOUT.canDelete()).isTrue();
        }

        @Test
        @DisplayName("DELETED는 삭제 불가능하다")
        void deletedCannotDelete() {
            assertThat(ProductGroupStatus.DELETED.canDelete()).isFalse();
        }
    }

    @Nested
    @DisplayName("displayName() 테스트")
    class DisplayNameTest {

        @Test
        @DisplayName("모든 상태는 표시 이름을 가진다")
        void allStatusesHaveDisplayName() {
            assertThat(ProductGroupStatus.DRAFT.displayName()).isEqualTo("임시저장");
            assertThat(ProductGroupStatus.ACTIVE.displayName()).isEqualTo("판매중");
            assertThat(ProductGroupStatus.INACTIVE.displayName()).isEqualTo("판매중지");
            assertThat(ProductGroupStatus.SOLDOUT.displayName()).isEqualTo("품절");
            assertThat(ProductGroupStatus.DELETED.displayName()).isEqualTo("삭제");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allValuesExist() {
            assertThat(ProductGroupStatus.values())
                    .containsExactly(
                            ProductGroupStatus.DRAFT,
                            ProductGroupStatus.PROCESSING,
                            ProductGroupStatus.PENDING_REVIEW,
                            ProductGroupStatus.ACTIVE,
                            ProductGroupStatus.INACTIVE,
                            ProductGroupStatus.SOLDOUT,
                            ProductGroupStatus.REJECTED,
                            ProductGroupStatus.DELETED);
        }
    }
}
