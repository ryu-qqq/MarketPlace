package com.ryuqq.marketplace.domain.productnotice.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeEntryId;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductNoticeEntry Entity 테스트")
class ProductNoticeEntryTest {

    @Nested
    @DisplayName("forNew() - 신규 고시정보 항목 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 고시정보 항목을 생성한다")
        void createNewProductNoticeEntry() {
            // given
            NoticeFieldId fieldId = NoticeFieldId.of(ProductNoticeFixtures.DEFAULT_NOTICE_FIELD_ID);
            NoticeFieldValue value = ProductNoticeFixtures.defaultNoticeFieldValue();

            // when
            ProductNoticeEntry entry = ProductNoticeEntry.forNew(fieldId, value);

            // then
            assertThat(entry.id().isNew()).isTrue();
            assertThat(entry.noticeFieldId()).isEqualTo(fieldId);
            assertThat(entry.noticeFieldIdValue())
                    .isEqualTo(ProductNoticeFixtures.DEFAULT_NOTICE_FIELD_ID);
            assertThat(entry.fieldValue()).isEqualTo(value);
            assertThat(entry.fieldValueValue())
                    .isEqualTo(ProductNoticeFixtures.DEFAULT_FIELD_VALUE);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 고시정보 항목을 복원한다")
        void reconstituteProductNoticeEntry() {
            // given
            ProductNoticeEntryId id = ProductNoticeEntryId.of(1L);
            NoticeFieldId fieldId = NoticeFieldId.of(100L);
            NoticeFieldValue value = NoticeFieldValue.of("복원된 값");

            // when
            ProductNoticeEntry entry = ProductNoticeEntry.reconstitute(id, fieldId, value);

            // then
            assertThat(entry.id()).isEqualTo(id);
            assertThat(entry.idValue()).isEqualTo(1L);
            assertThat(entry.id().isNew()).isFalse();
            assertThat(entry.noticeFieldId()).isEqualTo(fieldId);
            assertThat(entry.fieldValue()).isEqualTo(value);
        }

        @Test
        @DisplayName("Fixtures를 사용하여 고시정보 항목을 복원한다")
        void reconstituteUsingFixtures() {
            // when
            ProductNoticeEntry entry = ProductNoticeFixtures.existingEntry(123L, 200L, "테스트 값");

            // then
            assertThat(entry.idValue()).isEqualTo(123L);
            assertThat(entry.noticeFieldIdValue()).isEqualTo(200L);
            assertThat(entry.fieldValueValue()).isEqualTo("테스트 값");
        }
    }

    @Nested
    @DisplayName("updateValue() - 값 수정")
    class UpdateValueTest {

        @Test
        @DisplayName("고시정보 값을 수정한다")
        void updateFieldValue() {
            // given
            ProductNoticeEntry entry = ProductNoticeFixtures.defaultEntry();
            NoticeFieldValue originalValue = entry.fieldValue();
            NoticeFieldValue newValue = NoticeFieldValue.of("수정된 값");

            // when
            entry.updateValue(newValue);

            // then
            assertThat(entry.fieldValue()).isEqualTo(newValue);
            assertThat(entry.fieldValue()).isNotEqualTo(originalValue);
            assertThat(entry.fieldValueValue()).isEqualTo("수정된 값");
        }

        @Test
        @DisplayName("값을 여러 번 수정할 수 있다")
        void updateValueMultipleTimes() {
            // given
            ProductNoticeEntry entry = ProductNoticeFixtures.defaultEntry();

            // when
            entry.updateValue(NoticeFieldValue.of("첫 번째 수정"));
            entry.updateValue(NoticeFieldValue.of("두 번째 수정"));
            entry.updateValue(NoticeFieldValue.of("세 번째 수정"));

            // then
            assertThat(entry.fieldValueValue()).isEqualTo("세 번째 수정");
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID 값을 반환한다")
        void idValueReturnsValue() {
            // given
            ProductNoticeEntry entry = ProductNoticeFixtures.existingEntry(999L, 100L, "값");

            // when & then
            assertThat(entry.idValue()).isEqualTo(999L);
        }

        @Test
        @DisplayName("noticeFieldIdValue()는 NoticeField ID 값을 반환한다")
        void noticeFieldIdValueReturnsValue() {
            // given
            ProductNoticeEntry entry = ProductNoticeFixtures.existingEntry(1L, 555L, "값");

            // when & then
            assertThat(entry.noticeFieldIdValue()).isEqualTo(555L);
        }

        @Test
        @DisplayName("fieldValueValue()는 필드 값의 문자열을 반환한다")
        void fieldValueValueReturnsString() {
            // given
            ProductNoticeEntry entry = ProductNoticeFixtures.existingEntry(1L, 100L, "테스트 문자열");

            // when & then
            assertThat(entry.fieldValueValue()).isEqualTo("테스트 문자열");
        }
    }
}
