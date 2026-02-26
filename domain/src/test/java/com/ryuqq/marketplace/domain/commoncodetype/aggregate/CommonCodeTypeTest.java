package com.ryuqq.marketplace.domain.commoncodetype.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeType Aggregate 테스트")
class CommonCodeTypeTest {

    @Nested
    @DisplayName("forNew() - 신규 공통 코드 타입 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 공통 코드 타입을 생성한다")
        void createNewCommonCodeType() {
            // when
            var commonCodeType = CommonCodeTypeFixtures.newCommonCodeType();

            // then
            assertThat(commonCodeType.isNew()).isTrue();
            assertThat(commonCodeType.code()).isEqualTo(CommonCodeTypeFixtures.DEFAULT_CODE);
            assertThat(commonCodeType.name()).isEqualTo(CommonCodeTypeFixtures.DEFAULT_NAME);
            assertThat(commonCodeType.description())
                    .isEqualTo(CommonCodeTypeFixtures.DEFAULT_DESCRIPTION);
            assertThat(commonCodeType.displayOrder())
                    .isEqualTo(CommonCodeTypeFixtures.DEFAULT_DISPLAY_ORDER);
            assertThat(commonCodeType.isActive()).isTrue();
            assertThat(commonCodeType.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("커스텀 코드로 공통 코드 타입을 생성한다")
        void createWithCustomCode() {
            // when
            var commonCodeType = CommonCodeTypeFixtures.newCommonCodeType("COURIER", "배송사");

            // then
            assertThat(commonCodeType.code()).isEqualTo("COURIER");
            assertThat(commonCodeType.name()).isEqualTo("배송사");
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("활성 상태의 공통 코드 타입을 복원한다")
        void reconstituteActiveCommonCodeType() {
            // when
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();

            // then
            assertThat(commonCodeType.isNew()).isFalse();
            assertThat(commonCodeType.idValue()).isEqualTo(1L);
            assertThat(commonCodeType.isActive()).isTrue();
            assertThat(commonCodeType.isDeleted()).isFalse();
            assertThat(commonCodeType.deletedAt()).isNull();
        }

        @Test
        @DisplayName("비활성 상태의 공통 코드 타입을 복원한다")
        void reconstituteInactiveCommonCodeType() {
            // when
            var commonCodeType = CommonCodeTypeFixtures.inactiveCommonCodeType();

            // then
            assertThat(commonCodeType.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 공통 코드 타입을 복원한다")
        void reconstituteDeletedCommonCodeType() {
            // when
            var commonCodeType = CommonCodeTypeFixtures.deletedCommonCodeType();

            // then
            assertThat(commonCodeType.isDeleted()).isTrue();
            assertThat(commonCodeType.deletedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("update() - 공통 코드 타입 수정")
    class UpdateTest {

        @Test
        @DisplayName("공통 코드 타입 정보를 수정한다")
        void updateCommonCodeType() {
            // given
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            var updateData =
                    CommonCodeTypeFixtures.commonCodeTypeUpdateData("수정된 이름", "수정된 설명", 10);
            Instant now = CommonVoFixtures.now();

            // when
            commonCodeType.update(updateData, now);

            // then
            assertThat(commonCodeType.name()).isEqualTo("수정된 이름");
            assertThat(commonCodeType.description()).isEqualTo("수정된 설명");
            assertThat(commonCodeType.displayOrder()).isEqualTo(10);
            assertThat(commonCodeType.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("changeActiveStatus() - 활성화 상태 변경")
    class ActivationTest {

        @Test
        @DisplayName("비활성 상태의 공통 코드 타입을 활성화한다")
        void activateCommonCodeType() {
            // given
            var commonCodeType = CommonCodeTypeFixtures.inactiveCommonCodeType();
            Instant now = CommonVoFixtures.now();

            // when
            commonCodeType.changeActiveStatus(true, now);

            // then
            assertThat(commonCodeType.isActive()).isTrue();
            assertThat(commonCodeType.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성 상태의 공통 코드 타입을 비활성화한다")
        void deactivateCommonCodeType() {
            // given
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            Instant now = CommonVoFixtures.now();

            // when
            commonCodeType.changeActiveStatus(false, now);

            // then
            assertThat(commonCodeType.isActive()).isFalse();
            assertThat(commonCodeType.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("delete() / restore() - 삭제 및 복원")
    class DeletionTest {

        @Test
        @DisplayName("공통 코드 타입을 삭제(Soft Delete)한다")
        void deleteCommonCodeType() {
            // given
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            Instant now = CommonVoFixtures.now();

            // when
            commonCodeType.delete(now);

            // then
            assertThat(commonCodeType.isDeleted()).isTrue();
            assertThat(commonCodeType.deletedAt()).isEqualTo(now);
            assertThat(commonCodeType.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("삭제된 공통 코드 타입을 복원한다")
        void restoreCommonCodeType() {
            // given
            var commonCodeType = CommonCodeTypeFixtures.deletedCommonCodeType();
            Instant now = CommonVoFixtures.now();

            // when
            commonCodeType.restore(now);

            // then
            assertThat(commonCodeType.isDeleted()).isFalse();
            assertThat(commonCodeType.deletedAt()).isNull();
            assertThat(commonCodeType.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("id()는 CommonCodeTypeId를 반환한다")
        void returnsId() {
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            assertThat(commonCodeType.id()).isNotNull();
            assertThat(commonCodeType.idValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("code()는 코드 값을 반환한다")
        void returnsCode() {
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            assertThat(commonCodeType.code()).isEqualTo(CommonCodeTypeFixtures.DEFAULT_CODE);
        }

        @Test
        @DisplayName("name()은 이름을 반환한다")
        void returnsName() {
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            assertThat(commonCodeType.name()).isEqualTo(CommonCodeTypeFixtures.DEFAULT_NAME);
        }

        @Test
        @DisplayName("description()은 설명을 반환한다")
        void returnsDescription() {
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            assertThat(commonCodeType.description())
                    .isEqualTo(CommonCodeTypeFixtures.DEFAULT_DESCRIPTION);
        }

        @Test
        @DisplayName("displayOrder()는 표시 순서를 반환한다")
        void returnsDisplayOrder() {
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            assertThat(commonCodeType.displayOrder())
                    .isEqualTo(CommonCodeTypeFixtures.DEFAULT_DISPLAY_ORDER);
        }

        @Test
        @DisplayName("deletionStatus()는 DeletionStatus를 반환한다")
        void returnsDeletionStatus() {
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            assertThat(commonCodeType.deletionStatus()).isNotNull();
            assertThat(commonCodeType.deletionStatus().isDeleted()).isFalse();
        }

        @Test
        @DisplayName("createdAt()은 생성 시각을 반환한다")
        void returnsCreatedAt() {
            var commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType();
            assertThat(commonCodeType.createdAt()).isNotNull();
        }
    }
}
