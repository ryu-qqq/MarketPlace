package com.ryuqq.marketplace.application.notice.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.notice.NoticeQueryFixtures;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.internal.NoticeCategoryReadFacade;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetNoticeCategoryService 단위 테스트")
class GetNoticeCategoryServiceTest {

    @InjectMocks private GetNoticeCategoryService sut;

    @Mock private NoticeCategoryReadFacade readFacade;

    @Nested
    @DisplayName("execute() - CategoryGroup으로 조회")
    class ExecuteTest {

        @Test
        @DisplayName("CategoryGroup으로 고시정보 카테고리를 조회한다")
        void execute_ValidCategoryGroup_ReturnsNoticeCategoryResult() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.CLOTHING;
            NoticeCategoryResult expectedResult = NoticeQueryFixtures.noticeCategoryResult(1L);

            given(readFacade.getByCategoryGroup(categoryGroup)).willReturn(expectedResult);

            // when
            NoticeCategoryResult result = sut.execute(categoryGroup);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.code()).isEqualTo("CLOTHING");
            then(readFacade).should().getByCategoryGroup(categoryGroup);
        }

        @Test
        @DisplayName("필드가 포함된 고시정보 카테고리를 조회한다")
        void execute_WithFields_ReturnsResultWithFields() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.CLOTHING;
            NoticeCategoryResult expectedResult =
                    NoticeQueryFixtures.noticeCategoryResultWithFields(1L);

            given(readFacade.getByCategoryGroup(categoryGroup)).willReturn(expectedResult);

            // when
            NoticeCategoryResult result = sut.execute(categoryGroup);

            // then
            assertThat(result.fields()).hasSize(2);
            assertThat(result.fields().get(0).fieldCode()).isEqualTo("MATERIAL");
            assertThat(result.fields().get(1).fieldCode()).isEqualTo("ORIGIN");
        }

        @Test
        @DisplayName("DIGITAL CategoryGroup으로 고시정보 카테고리를 조회한다")
        void execute_DigitalCategoryGroup_ReturnsResult() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.DIGITAL;
            NoticeCategoryResult expectedResult =
                    NoticeQueryFixtures.noticeCategoryResult(2L, "DIGITAL");

            given(readFacade.getByCategoryGroup(categoryGroup)).willReturn(expectedResult);

            // when
            NoticeCategoryResult result = sut.execute(categoryGroup);

            // then
            assertThat(result.code()).isEqualTo("DIGITAL");
            then(readFacade).should().getByCategoryGroup(categoryGroup);
        }
    }
}
