package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("DeletionStatus Value Object 단위 테스트")
class DeletionStatusTest {

    @Nested
    @DisplayName("active() 팩토리 테스트")
    class ActiveTest {

        @Test
        @DisplayName("active()는 삭제되지 않은 상태를 반환한다")
        void activeIsNotDeleted() {
            DeletionStatus status = DeletionStatus.active();

            assertThat(status.isDeleted()).isFalse();
            assertThat(status.isActive()).isTrue();
            assertThat(status.deletedAt()).isNull();
        }

        @Test
        @DisplayName("active() 호출마다 동일한 싱글톤 인스턴스를 반환한다")
        void activeReturnsSameInstance() {
            DeletionStatus a = DeletionStatus.active();
            DeletionStatus b = DeletionStatus.active();

            assertThat(a).isEqualTo(b);
        }
    }

    @Nested
    @DisplayName("deletedAt() 팩토리 테스트")
    class DeletedAtTest {

        @Test
        @DisplayName("deletedAt()은 삭제된 상태를 반환한다")
        void deletedAtIsDeleted() {
            Instant now = Instant.now();
            DeletionStatus status = DeletionStatus.deletedAt(now);

            assertThat(status.isDeleted()).isTrue();
            assertThat(status.isActive()).isFalse();
            assertThat(status.deletedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("null 시간으로 삭제 상태를 만들면 예외가 발생한다")
        void deletedAtWithNullThrowsException() {
            assertThatThrownBy(() -> DeletionStatus.deletedAt(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("생성자 유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("deleted=true인데 deletedAt=null이면 예외가 발생한다")
        void deletedTrueWithNullDeletedAtThrowsException() {
            assertThatThrownBy(() -> new DeletionStatus(true, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("deleted=false인데 deletedAt이 있으면 예외가 발생한다")
        void deletedFalseWithDeletedAtThrowsException() {
            assertThatThrownBy(() -> new DeletionStatus(false, Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("reconstitute() 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("deleted=false면 active 상태를 복원한다")
        void reconstituteActiveState() {
            DeletionStatus status = DeletionStatus.reconstitute(false, null);

            assertThat(status.isActive()).isTrue();
            assertThat(status.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("deleted=true면 삭제 상태를 복원한다")
        void reconstituteDeletedState() {
            Instant deletedAt = Instant.now();
            DeletionStatus status = DeletionStatus.reconstitute(true, deletedAt);

            assertThat(status.isDeleted()).isTrue();
            assertThat(status.deletedAt()).isEqualTo(deletedAt);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("active 상태끼리 동일하다")
        void twoActiveStatusesAreEqual() {
            DeletionStatus a = DeletionStatus.active();
            DeletionStatus b = DeletionStatus.active();

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("같은 deletedAt을 가진 삭제 상태는 동일하다")
        void twoDeletedStatusesWithSameTimeAreEqual() {
            Instant now = Instant.now();
            DeletionStatus a = DeletionStatus.deletedAt(now);
            DeletionStatus b = DeletionStatus.deletedAt(now);

            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("active와 deleted 상태는 동일하지 않다")
        void activeAndDeletedAreNotEqual() {
            DeletionStatus active = DeletionStatus.active();
            DeletionStatus deleted = DeletionStatus.deletedAt(Instant.now());

            assertThat(active).isNotEqualTo(deleted);
        }
    }
}
