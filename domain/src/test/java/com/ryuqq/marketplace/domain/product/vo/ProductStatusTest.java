package com.ryuqq.marketplace.domain.product.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductStatus Enum 테스트")
class ProductStatusTest {

    @Nested
    @DisplayName("displayName() - 표시 이름")
    class DisplayNameTest {

        @Test
        @DisplayName("ACTIVE의 표시 이름은 '판매중'이다")
        void activeDisplayName() {
            assertThat(ProductStatus.ACTIVE.displayName()).isEqualTo("판매중");
        }

        @Test
        @DisplayName("INACTIVE의 표시 이름은 '판매중지'이다")
        void inactiveDisplayName() {
            assertThat(ProductStatus.INACTIVE.displayName()).isEqualTo("판매중지");
        }

        @Test
        @DisplayName("SOLDOUT의 표시 이름은 '품절'이다")
        void soldOutDisplayName() {
            assertThat(ProductStatus.SOLDOUT.displayName()).isEqualTo("품절");
        }

        @Test
        @DisplayName("DELETED의 표시 이름은 '삭제'이다")
        void deletedDisplayName() {
            assertThat(ProductStatus.DELETED.displayName()).isEqualTo("삭제");
        }
    }

    @Nested
    @DisplayName("isActive() - 활성 상태 확인")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVE는 isActive()가 true이다")
        void activeIsActive() {
            assertThat(ProductStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 isActive()가 false이다")
        void inactiveIsNotActive() {
            assertThat(ProductStatus.INACTIVE.isActive()).isFalse();
        }

        @Test
        @DisplayName("SOLDOUT는 isActive()가 false이다")
        void soldOutIsNotActive() {
            assertThat(ProductStatus.SOLDOUT.isActive()).isFalse();
        }

        @Test
        @DisplayName("DELETED는 isActive()가 false이다")
        void deletedIsNotActive() {
            assertThat(ProductStatus.DELETED.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("isDeleted() - 삭제 상태 확인")
    class IsDeletedTest {

        @Test
        @DisplayName("DELETED는 isDeleted()가 true이다")
        void deletedIsDeleted() {
            assertThat(ProductStatus.DELETED.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("ACTIVE는 isDeleted()가 false이다")
        void activeIsNotDeleted() {
            assertThat(ProductStatus.ACTIVE.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE는 isDeleted()가 false이다")
        void inactiveIsNotDeleted() {
            assertThat(ProductStatus.INACTIVE.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("SOLDOUT는 isDeleted()가 false이다")
        void soldOutIsNotDeleted() {
            assertThat(ProductStatus.SOLDOUT.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("canActivate() - 활성화 가능 여부")
    class CanActivateTest {

        @Test
        @DisplayName("INACTIVE는 활성화 가능하다")
        void inactiveCanActivate() {
            assertThat(ProductStatus.INACTIVE.canActivate()).isTrue();
        }

        @Test
        @DisplayName("SOLDOUT는 활성화 가능하다")
        void soldOutCanActivate() {
            assertThat(ProductStatus.SOLDOUT.canActivate()).isTrue();
        }

        @Test
        @DisplayName("ACTIVE는 활성화 불가능하다")
        void activeCannotActivate() {
            assertThat(ProductStatus.ACTIVE.canActivate()).isFalse();
        }

        @Test
        @DisplayName("DELETED는 활성화 불가능하다")
        void deletedCannotActivate() {
            assertThat(ProductStatus.DELETED.canActivate()).isFalse();
        }
    }

    @Nested
    @DisplayName("canDelete() - 삭제 가능 여부")
    class CanDeleteTest {

        @Test
        @DisplayName("ACTIVE는 삭제 가능하다")
        void activeCanDelete() {
            assertThat(ProductStatus.ACTIVE.canDelete()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 삭제 가능하다")
        void inactiveCanDelete() {
            assertThat(ProductStatus.INACTIVE.canDelete()).isTrue();
        }

        @Test
        @DisplayName("SOLDOUT는 삭제 가능하다")
        void soldOutCanDelete() {
            assertThat(ProductStatus.SOLDOUT.canDelete()).isTrue();
        }

        @Test
        @DisplayName("DELETED는 삭제 불가능하다")
        void deletedCannotDelete() {
            assertThat(ProductStatus.DELETED.canDelete()).isFalse();
        }
    }

    @Nested
    @DisplayName("상태 전이 규칙 검증")
    class StateTransitionTest {

        @Test
        @DisplayName("모든 상태는 열거형으로 정의되어 있다")
        void allStatesAreDefined() {
            ProductStatus[] values = ProductStatus.values();
            assertThat(values).hasSize(4);
            assertThat(values).containsExactlyInAnyOrder(
                    ProductStatus.ACTIVE,
                    ProductStatus.INACTIVE,
                    ProductStatus.SOLDOUT,
                    ProductStatus.DELETED
            );
        }
    }
}
