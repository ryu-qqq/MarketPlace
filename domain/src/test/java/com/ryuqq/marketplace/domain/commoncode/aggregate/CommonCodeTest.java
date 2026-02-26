package com.ryuqq.marketplace.domain.commoncode.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.commoncode.CommonCodeFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCode Aggregate 테스트")
class CommonCodeTest {

    @Nested
    @DisplayName("forNew() - 신규 공통 코드 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 공통 코드를 생성한다")
        void createNewCommonCode() {
            // when
            var commonCode = CommonCodeFixtures.newCommonCode();

            // then
            assertThat(commonCode.isNew()).isTrue();
            assertThat(commonCode.codeValue()).isEqualTo(CommonCodeFixtures.DEFAULT_CODE);
            assertThat(commonCode.displayNameValue())
                    .isEqualTo(CommonCodeFixtures.DEFAULT_DISPLAY_NAME);
            assertThat(commonCode.displayOrderValue())
                    .isEqualTo(CommonCodeFixtures.DEFAULT_DISPLAY_ORDER);
            assertThat(commonCode.isActive()).isTrue();
            assertThat(commonCode.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("커스텀 코드로 공통 코드를 생성한다")
        void createWithCustomCode() {
            // when
            var commonCode = CommonCodeFixtures.newCommonCode("BANK_TRANSFER", "계좌이체");

            // then
            assertThat(commonCode.codeValue()).isEqualTo("BANK_TRANSFER");
            assertThat(commonCode.displayNameValue()).isEqualTo("계좌이체");
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("활성 상태의 공통 코드를 복원한다")
        void reconstituteActiveCommonCode() {
            // when
            var commonCode = CommonCodeFixtures.activeCommonCode();

            // then
            assertThat(commonCode.isNew()).isFalse();
            assertThat(commonCode.idValue()).isEqualTo(1L);
            assertThat(commonCode.isActive()).isTrue();
            assertThat(commonCode.isDeleted()).isFalse();
            assertThat(commonCode.deletedAt()).isNull();
        }

        @Test
        @DisplayName("삭제된 공통 코드를 복원한다")
        void reconstituteDeletedCommonCode() {
            // when
            var commonCode = CommonCodeFixtures.deletedCommonCode();

            // then
            assertThat(commonCode.isDeleted()).isTrue();
            assertThat(commonCode.deletedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("update() - 공통 코드 수정")
    class UpdateTest {

        @Test
        @DisplayName("공통 코드 정보를 수정한다")
        void updateCommonCode() {
            // given
            var commonCode = CommonCodeFixtures.activeCommonCode();
            var updateData = CommonCodeFixtures.commonCodeUpdateData("수정된 표시명", 5);
            Instant now = CommonVoFixtures.now();

            // when
            commonCode.update(updateData, now);

            // then
            assertThat(commonCode.displayNameValue()).isEqualTo("수정된 표시명");
            assertThat(commonCode.displayOrderValue()).isEqualTo(5);
            assertThat(commonCode.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("activate() / deactivate() - 활성화 상태 변경")
    class ActivationTest {

        @Test
        @DisplayName("비활성 상태의 공통 코드를 활성화한다")
        void activateCommonCode() {
            // given
            var commonCode = CommonCodeFixtures.inactiveCommonCode();
            Instant now = CommonVoFixtures.now();

            // when
            commonCode.activate(now);

            // then
            assertThat(commonCode.isActive()).isTrue();
            assertThat(commonCode.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성 상태의 공통 코드를 비활성화한다")
        void deactivateCommonCode() {
            // given
            var commonCode = CommonCodeFixtures.activeCommonCode();
            Instant now = CommonVoFixtures.now();

            // when
            commonCode.deactivate(now);

            // then
            assertThat(commonCode.isActive()).isFalse();
            assertThat(commonCode.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("delete() / restore() - 삭제 및 복원")
    class DeletionTest {

        @Test
        @DisplayName("공통 코드를 삭제(Soft Delete)한다")
        void deleteCommonCode() {
            // given
            var commonCode = CommonCodeFixtures.activeCommonCode();
            Instant now = CommonVoFixtures.now();

            // when
            commonCode.delete(now);

            // then
            assertThat(commonCode.isDeleted()).isTrue();
            assertThat(commonCode.deletedAt()).isEqualTo(now);
            assertThat(commonCode.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("삭제된 공통 코드를 복원한다")
        void restoreCommonCode() {
            // given
            var commonCode = CommonCodeFixtures.deletedCommonCode();
            Instant now = CommonVoFixtures.now();

            // when
            commonCode.restore(now);

            // then
            assertThat(commonCode.isDeleted()).isFalse();
            assertThat(commonCode.deletedAt()).isNull();
            assertThat(commonCode.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("id()는 CommonCodeId를 반환한다")
        void returnsId() {
            var commonCode = CommonCodeFixtures.activeCommonCode();
            assertThat(commonCode.id()).isNotNull();
            assertThat(commonCode.idValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("commonCodeTypeId()는 CommonCodeTypeId를 반환한다")
        void returnsCommonCodeTypeId() {
            var commonCode = CommonCodeFixtures.activeCommonCode();
            assertThat(commonCode.commonCodeTypeId()).isNotNull();
            assertThat(commonCode.commonCodeTypeIdValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("code()는 CommonCodeValue를 반환한다")
        void returnsCode() {
            var commonCode = CommonCodeFixtures.activeCommonCode();
            assertThat(commonCode.code()).isNotNull();
            assertThat(commonCode.codeValue()).isEqualTo(CommonCodeFixtures.DEFAULT_CODE);
        }

        @Test
        @DisplayName("displayName()은 CommonCodeDisplayName을 반환한다")
        void returnsDisplayName() {
            var commonCode = CommonCodeFixtures.activeCommonCode();
            assertThat(commonCode.displayName()).isNotNull();
            assertThat(commonCode.displayNameValue())
                    .isEqualTo(CommonCodeFixtures.DEFAULT_DISPLAY_NAME);
        }

        @Test
        @DisplayName("displayOrder()는 DisplayOrder를 반환한다")
        void returnsDisplayOrder() {
            var commonCode = CommonCodeFixtures.activeCommonCode();
            assertThat(commonCode.displayOrder()).isNotNull();
            assertThat(commonCode.displayOrderValue())
                    .isEqualTo(CommonCodeFixtures.DEFAULT_DISPLAY_ORDER);
        }

        @Test
        @DisplayName("deletionStatus()는 DeletionStatus를 반환한다")
        void returnsDeletionStatus() {
            var commonCode = CommonCodeFixtures.activeCommonCode();
            assertThat(commonCode.deletionStatus()).isNotNull();
            assertThat(commonCode.deletionStatus().isDeleted()).isFalse();
        }

        @Test
        @DisplayName("createdAt()은 생성 시각을 반환한다")
        void returnsCreatedAt() {
            var commonCode = CommonCodeFixtures.activeCommonCode();
            assertThat(commonCode.createdAt()).isNotNull();
        }
    }
}
