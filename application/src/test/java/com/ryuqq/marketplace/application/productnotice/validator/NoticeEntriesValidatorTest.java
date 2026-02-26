package com.ryuqq.marketplace.application.productnotice.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("NoticeEntriesValidator 단위 테스트")
class NoticeEntriesValidatorTest {

    @InjectMocks private NoticeEntriesValidator sut;

    @Mock private NoticeCategoryReadManager noticeCategoryReadManager;

    @Nested
    @DisplayName("validate(ProductNotice) - 고시정보 전체 검증")
    class ValidateProductNoticeTest {

        @Test
        @DisplayName("유효한 고시정보 항목은 검증을 통과한다")
        void validate_ValidProductNotice_DoesNotThrow() {
            // given
            ProductNotice notice = ProductNoticeFixtures.newProductNotice();

            NoticeField field100 = mockNoticeField(100L, false);
            NoticeField field101 = mockNoticeField(101L, false);
            NoticeField field102 = mockNoticeField(102L, false);
            NoticeCategory category = mockNoticeCategory(List.of(field100, field101, field102));

            given(
                            noticeCategoryReadManager.getById(
                                    NoticeCategoryId.of(
                                            ProductNoticeFixtures.DEFAULT_NOTICE_CATEGORY_ID)))
                    .willReturn(category);

            // when & then
            assertThatCode(() -> sut.validate(notice)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("유효하지 않은 fieldId가 포함되면 예외를 던진다")
        void validate_InvalidFieldId_ThrowsException() {
            // given
            ProductNotice notice = ProductNoticeFixtures.newProductNotice();

            NoticeField field999 = mockNoticeField(999L, false);
            NoticeCategory category = mockNoticeCategory(List.of(field999));

            given(
                            noticeCategoryReadManager.getById(
                                    NoticeCategoryId.of(
                                            ProductNoticeFixtures.DEFAULT_NOTICE_CATEGORY_ID)))
                    .willReturn(category);

            // when & then
            assertThatThrownBy(() -> sut.validate(notice))
                    .isInstanceOf(
                            com.ryuqq.marketplace.domain.notice.exception
                                    .NoticeInvalidFieldException.class);
        }

        @Test
        @DisplayName("필수 필드가 누락되면 예외를 던진다")
        void validate_MissingRequiredField_ThrowsException() {
            // given
            // defaultEntries()는 fieldId 100, 101, 102를 포함
            // 필드 100, 101, 102는 모두 유효하게 설정하고, 추가 필수 필드(200)를 누락
            ProductNotice notice = ProductNoticeFixtures.newProductNotice();

            NoticeField field100 = mockNoticeField(100L, false);
            NoticeField field101 = mockNoticeField(101L, false);
            NoticeField field102 = mockNoticeField(102L, false);
            NoticeField requiredField200 = mockNoticeField(200L, true);
            // 200L이 필수이지만 entries에 포함되지 않아 RequiredFieldMissing 예외가 발생해야 함
            NoticeCategory category =
                    mockNoticeCategory(List.of(field100, field101, field102, requiredField200));

            given(
                            noticeCategoryReadManager.getById(
                                    NoticeCategoryId.of(
                                            ProductNoticeFixtures.DEFAULT_NOTICE_CATEGORY_ID)))
                    .willReturn(category);

            // when & then
            assertThatThrownBy(() -> sut.validate(notice))
                    .isInstanceOf(
                            com.ryuqq.marketplace.domain.notice.exception
                                    .NoticeRequiredFieldMissingException.class);
        }
    }

    // Helper methods

    private NoticeField mockNoticeField(Long fieldId, boolean required) {
        NoticeField field = Mockito.mock(NoticeField.class);
        given(field.idValue()).willReturn(fieldId);
        given(field.isRequired()).willReturn(required);
        return field;
    }

    private NoticeCategory mockNoticeCategory(List<NoticeField> fields) {
        NoticeCategory category = Mockito.mock(NoticeCategory.class);
        given(category.fields()).willReturn(fields);
        return category;
    }
}
