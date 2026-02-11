package com.ryuqq.marketplace.domain.notice.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.notice.NoticeFixtures;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryName;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeCategory Aggregate 테스트")
class NoticeCategoryTest {

    @Nested
    @DisplayName("forNew() - 신규 고시정보 카테고리 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 고시정보 카테고리를 생성한다")
        void createNewNoticeCategoryWithRequiredFields() {
            // given
            NoticeCategoryCode code = NoticeFixtures.defaultNoticeCategoryCode();
            NoticeCategoryName name = NoticeFixtures.defaultNoticeCategoryName();
            CategoryGroup group = NoticeFixtures.defaultCategoryGroup();
            Instant now = CommonVoFixtures.now();

            // when
            NoticeCategory category = NoticeCategory.forNew(code, name, group, now);

            // then
            assertThat(category.id().isNew()).isTrue();
            assertThat(category.code()).isEqualTo(code);
            assertThat(category.categoryName()).isEqualTo(name);
            assertThat(category.targetCategoryGroup()).isEqualTo(group);
            assertThat(category.isActive()).isTrue();
            assertThat(category.fields()).isEmpty();
            assertThat(category.createdAt()).isEqualTo(now);
            assertThat(category.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 카테고리는 기본적으로 활성 상태다")
        void newNoticeCategoryIsActiveByDefault() {
            // when
            NoticeCategory category = NoticeFixtures.newNoticeCategory();

            // then
            assertThat(category.isActive()).isTrue();
        }

        @Test
        @DisplayName("신규 카테고리는 필드 목록이 비어있다")
        void newNoticeCategoryHasEmptyFields() {
            // when
            NoticeCategory category = NoticeFixtures.newNoticeCategory();

            // then
            assertThat(category.fields()).isEmpty();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 카테고리를 복원한다")
        void reconstituteActiveNoticeCategory() {
            // given
            NoticeCategoryId id = NoticeCategoryId.of(1L);
            NoticeCategoryCode code = NoticeFixtures.defaultNoticeCategoryCode();
            NoticeCategoryName name = NoticeFixtures.defaultNoticeCategoryName();
            CategoryGroup group = NoticeFixtures.defaultCategoryGroup();
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            NoticeCategory category =
                    NoticeCategory.reconstitute(
                            id, code, name, group, true, java.util.List.of(), createdAt, updatedAt);

            // then
            assertThat(category.id().isNew()).isFalse();
            assertThat(category.id()).isEqualTo(id);
            assertThat(category.isActive()).isTrue();
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 카테고리를 복원한다")
        void reconstituteInactiveNoticeCategory() {
            // given
            NoticeCategoryId id = NoticeCategoryId.of(2L);

            // when
            NoticeCategory category =
                    NoticeCategory.reconstitute(
                            id,
                            NoticeFixtures.defaultNoticeCategoryCode(),
                            NoticeFixtures.defaultNoticeCategoryName(),
                            NoticeFixtures.defaultCategoryGroup(),
                            false,
                            java.util.List.of(),
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            // then
            assertThat(category.isActive()).isFalse();
        }

        @Test
        @DisplayName("영속성에서 필드를 포함한 카테고리를 복원한다")
        void reconstituteCategoryWithFields() {
            // given
            NoticeField field1 = NoticeFixtures.activeNoticeField(1L);
            NoticeField field2 = NoticeFixtures.activeNoticeField(2L);

            // when
            NoticeCategory category =
                    NoticeCategory.reconstitute(
                            NoticeCategoryId.of(1L),
                            NoticeFixtures.defaultNoticeCategoryCode(),
                            NoticeFixtures.defaultNoticeCategoryName(),
                            NoticeFixtures.defaultCategoryGroup(),
                            true,
                            java.util.List.of(field1, field2),
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            // then
            assertThat(category.fields()).hasSize(2);
            assertThat(category.fields()).containsExactly(field1, field2);
        }
    }

    @Nested
    @DisplayName("updateName() - 카테고리 이름 수정")
    class UpdateNameTest {

        @Test
        @DisplayName("카테고리 이름을 수정한다")
        void updateCategoryName() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory();
            NoticeCategoryName newName = NoticeCategoryName.of("식품", "Food");
            Instant now = CommonVoFixtures.now();

            // when
            category.updateName(newName, now);

            // then
            assertThat(category.categoryName()).isEqualTo(newName);
            assertThat(category.nameKo()).isEqualTo("식품");
            assertThat(category.nameEn()).isEqualTo("Food");
            assertThat(category.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("addField() - 필드 추가")
    class AddFieldTest {

        @Test
        @DisplayName("카테고리에 필드를 추가한다")
        void addFieldToCategory() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory();
            NoticeField field = NoticeFixtures.newNoticeField();

            // when
            category.addField(field);

            // then
            assertThat(category.fields()).hasSize(1);
            assertThat(category.fields()).contains(field);
        }

        @Test
        @DisplayName("카테고리에 여러 필드를 추가한다")
        void addMultipleFieldsToCategory() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory();
            NoticeField field1 = NoticeFixtures.activeNoticeField(1L);
            NoticeField field2 = NoticeFixtures.activeNoticeField(2L);

            // when
            category.addField(field1);
            category.addField(field2);

            // then
            assertThat(category.fields()).hasSize(2);
            assertThat(category.fields()).containsExactly(field1, field2);
        }
    }

    @Nested
    @DisplayName("activate() / deactivate() - 활성화 상태 변경")
    class ActivationTest {

        @Test
        @DisplayName("비활성 카테고리를 활성화한다")
        void activateInactiveCategory() {
            // given
            NoticeCategory category = NoticeFixtures.inactiveNoticeCategory();
            Instant now = CommonVoFixtures.now();

            // when
            category.activate(now);

            // then
            assertThat(category.isActive()).isTrue();
            assertThat(category.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성 카테고리를 비활성화한다")
        void deactivateActiveCategory() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory();
            Instant now = CommonVoFixtures.now();

            // when
            category.deactivate(now);

            // then
            assertThat(category.isActive()).isFalse();
            assertThat(category.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory(100L);

            // when
            Long idValue = category.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("codeValue()는 코드의 값을 반환한다")
        void codeValueReturnsCodeValue() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory();

            // when
            String codeValue = category.codeValue();

            // then
            assertThat(codeValue).isEqualTo("CLOTHING");
        }

        @Test
        @DisplayName("nameKo()는 한국어 이름을 반환한다")
        void nameKoReturnsKoreanName() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory();

            // when
            String nameKo = category.nameKo();

            // then
            assertThat(nameKo).isEqualTo("의류");
        }

        @Test
        @DisplayName("nameEn()은 영어 이름을 반환한다")
        void nameEnReturnsEnglishName() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory();

            // when
            String nameEn = category.nameEn();

            // then
            assertThat(nameEn).isEqualTo("Clothing");
        }

        @Test
        @DisplayName("fields()는 불변 리스트를 반환한다")
        void fieldsReturnsUnmodifiableList() {
            // given
            NoticeCategory category = NoticeFixtures.noticeCategoryWithFields();

            // when & then
            assertThatThrownBy(() -> category.fields().add(NoticeFixtures.newNoticeField()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
