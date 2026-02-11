package com.ryuqq.marketplace.domain.notice.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.notice.NoticeFixtures;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeField Entity 테스트")
class NoticeFieldTest {

    @Nested
    @DisplayName("forNew() - 신규 고시정보 필드 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 고시정보 필드를 생성한다")
        void createNewNoticeFieldWithRequiredFields() {
            // given
            NoticeFieldCode code = NoticeFixtures.defaultNoticeFieldCode();
            NoticeFieldName name = NoticeFixtures.defaultNoticeFieldName();
            boolean required = true;
            int sortOrder = 1;

            // when
            NoticeField field = NoticeField.forNew(code, name, required, sortOrder);

            // then
            assertThat(field.id().isNew()).isTrue();
            assertThat(field.fieldCode()).isEqualTo(code);
            assertThat(field.fieldName()).isEqualTo(name);
            assertThat(field.isRequired()).isTrue();
            assertThat(field.sortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("선택 필드로 생성할 수 있다")
        void createOptionalField() {
            // when
            NoticeField field =
                    NoticeField.forNew(
                            NoticeFixtures.defaultNoticeFieldCode(),
                            NoticeFixtures.defaultNoticeFieldName(),
                            false,
                            2);

            // then
            assertThat(field.isRequired()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 필수 필드를 복원한다")
        void reconstituteRequiredField() {
            // given
            NoticeFieldId id = NoticeFieldId.of(1L);
            NoticeFieldCode code = NoticeFixtures.defaultNoticeFieldCode();
            NoticeFieldName name = NoticeFixtures.defaultNoticeFieldName();

            // when
            NoticeField field = NoticeField.reconstitute(id, code, name, true, 1);

            // then
            assertThat(field.id().isNew()).isFalse();
            assertThat(field.id()).isEqualTo(id);
            assertThat(field.isRequired()).isTrue();
        }

        @Test
        @DisplayName("영속성에서 선택 필드를 복원한다")
        void reconstituteOptionalField() {
            // given
            NoticeFieldId id = NoticeFieldId.of(2L);

            // when
            NoticeField field =
                    NoticeField.reconstitute(
                            id,
                            NoticeFixtures.defaultNoticeFieldCode(),
                            NoticeFixtures.defaultNoticeFieldName(),
                            false,
                            2);

            // then
            assertThat(field.isRequired()).isFalse();
        }
    }

    @Nested
    @DisplayName("update() - 필드 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("필드 정보를 수정한다")
        void updateFieldInfo() {
            // given
            NoticeField field = NoticeFixtures.activeNoticeField();
            NoticeFieldName newName = NoticeFieldName.of("제조국");
            boolean newRequired = false;
            int newSortOrder = 10;

            // when
            field.update(newName, newRequired, newSortOrder);

            // then
            assertThat(field.fieldName()).isEqualTo(newName);
            assertThat(field.fieldNameValue()).isEqualTo("제조국");
            assertThat(field.isRequired()).isFalse();
            assertThat(field.sortOrder()).isEqualTo(10);
        }

        @Test
        @DisplayName("필수 여부를 변경할 수 있다")
        void updateRequiredFlag() {
            // given
            NoticeField field = NoticeFixtures.activeNoticeField();
            assertThat(field.isRequired()).isTrue();

            // when
            field.update(field.fieldName(), false, field.sortOrder());

            // then
            assertThat(field.isRequired()).isFalse();
        }

        @Test
        @DisplayName("정렬 순서를 변경할 수 있다")
        void updateSortOrder() {
            // given
            NoticeField field = NoticeFixtures.activeNoticeField();
            assertThat(field.sortOrder()).isEqualTo(1);

            // when
            field.update(field.fieldName(), field.isRequired(), 99);

            // then
            assertThat(field.sortOrder()).isEqualTo(99);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            NoticeField field = NoticeFixtures.activeNoticeField(100L);

            // when
            Long idValue = field.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("fieldCodeValue()는 코드의 값을 반환한다")
        void fieldCodeValueReturnsCodeValue() {
            // given
            NoticeField field = NoticeFixtures.activeNoticeField(1L);

            // when
            String codeValue = field.fieldCodeValue();

            // then
            assertThat(codeValue).isEqualTo("FIELD_1");
        }

        @Test
        @DisplayName("fieldNameValue()는 이름의 값을 반환한다")
        void fieldNameValueReturnsNameValue() {
            // given
            NoticeField field = NoticeFixtures.activeNoticeField(1L);

            // when
            String nameValue = field.fieldNameValue();

            // then
            assertThat(nameValue).isEqualTo("필드 1");
        }
    }
}
