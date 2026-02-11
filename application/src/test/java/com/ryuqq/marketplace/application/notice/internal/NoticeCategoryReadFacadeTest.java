package com.ryuqq.marketplace.application.notice.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.notice.assembler.NoticeCategoryAssembler;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.application.notice.manager.NoticeFieldReadManager;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.NoticeFixtures;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import java.util.List;
import java.util.Map;
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
@DisplayName("NoticeCategoryReadFacade 단위 테스트")
class NoticeCategoryReadFacadeTest {

    @InjectMocks private NoticeCategoryReadFacade sut;

    @Mock private NoticeCategoryReadManager categoryReadManager;
    @Mock private NoticeFieldReadManager fieldReadManager;
    @Mock private NoticeCategoryAssembler assembler;

    @Nested
    @DisplayName("getByCategoryGroup() - CategoryGroup으로 조회")
    class GetByCategoryGroupTest {

        @Test
        @DisplayName("CategoryGroup으로 카테고리와 필드를 함께 조회한다")
        void getByCategoryGroup_ValidCategoryGroup_ReturnsResultWithFields() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.CLOTHING;
            NoticeCategory category = NoticeFixtures.activeNoticeCategory(1L);
            List<NoticeField> fields =
                    List.of(
                            NoticeFixtures.activeNoticeField(1L),
                            NoticeFixtures.activeNoticeField(2L));
            NoticeCategoryResult expectedResult =
                    new NoticeCategoryResult(
                            1L,
                            "CLOTHING",
                            "의류",
                            "Clothing",
                            "CLOTHING",
                            true,
                            List.of(),
                            category.createdAt());

            given(categoryReadManager.getByCategoryGroup(categoryGroup)).willReturn(category);
            given(fieldReadManager.getByNoticeCategoryId(1L)).willReturn(fields);
            given(assembler.toResult(category, fields)).willReturn(expectedResult);

            // when
            NoticeCategoryResult result = sut.getByCategoryGroup(categoryGroup);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(categoryReadManager).should().getByCategoryGroup(categoryGroup);
            then(fieldReadManager).should().getByNoticeCategoryId(1L);
            then(assembler).should().toResult(category, fields);
        }

        @Test
        @DisplayName("필드가 없는 카테고리를 조회한다")
        void getByCategoryGroup_CategoryWithoutFields_ReturnsResultWithEmptyFields() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.DIGITAL;
            NoticeCategory category = NoticeFixtures.activeNoticeCategory(2L);
            List<NoticeField> emptyFields = List.of();
            NoticeCategoryResult expectedResult =
                    new NoticeCategoryResult(
                            2L,
                            "DIGITAL",
                            "디지털/가전",
                            "Digital",
                            "DIGITAL",
                            true,
                            List.of(),
                            category.createdAt());

            given(categoryReadManager.getByCategoryGroup(categoryGroup)).willReturn(category);
            given(fieldReadManager.getByNoticeCategoryId(2L)).willReturn(emptyFields);
            given(assembler.toResult(category, emptyFields)).willReturn(expectedResult);

            // when
            NoticeCategoryResult result = sut.getByCategoryGroup(categoryGroup);

            // then
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 카테고리와 필드를 함께 조회한다")
        void findByCriteria_ValidCriteria_ReturnsResultsWithFields() {
            // given
            NoticeCategorySearchCriteria criteria = null; // mock 대상
            List<NoticeCategory> categories =
                    List.of(
                            NoticeFixtures.activeNoticeCategory(1L),
                            NoticeFixtures.activeNoticeCategory(2L));
            Map<Long, List<NoticeField>> fieldsMap =
                    Map.of(
                            1L, List.of(NoticeFixtures.activeNoticeField(1L)),
                            2L, List.of(NoticeFixtures.activeNoticeField(2L)));

            NoticeCategoryResult result1 =
                    new NoticeCategoryResult(
                            1L, "CLOTHING", "의류", "Clothing", "CLOTHING", true, List.of(), null);
            NoticeCategoryResult result2 =
                    new NoticeCategoryResult(
                            2L, "DIGITAL", "디지털/가전", "Digital", "DIGITAL", true, List.of(), null);

            given(categoryReadManager.findByCriteria(criteria)).willReturn(categories);
            given(fieldReadManager.getGroupedByNoticeCategoryIds(List.of(1L, 2L)))
                    .willReturn(fieldsMap);
            given(assembler.toResult(categories.get(0), fieldsMap.get(1L))).willReturn(result1);
            given(assembler.toResult(categories.get(1), fieldsMap.get(2L))).willReturn(result2);

            // when
            List<NoticeCategoryResult> results = sut.findByCriteria(criteria);

            // then
            assertThat(results).hasSize(2);
            then(categoryReadManager).should().findByCriteria(criteria);
            then(fieldReadManager).should().getGroupedByNoticeCategoryIds(List.of(1L, 2L));
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환한다")
        void findByCriteria_NoResults_ReturnsEmptyList() {
            // given
            NoticeCategorySearchCriteria criteria = null;
            List<NoticeCategory> emptyCategories = List.of();

            given(categoryReadManager.findByCriteria(criteria)).willReturn(emptyCategories);

            // when
            List<NoticeCategoryResult> results = sut.findByCriteria(criteria);

            // then
            assertThat(results).isEmpty();
            then(fieldReadManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("일부 카테고리만 필드를 가진 경우 올바르게 조회한다")
        void findByCriteria_PartialFields_ReturnsResultsWithPartialFields() {
            // given
            NoticeCategorySearchCriteria criteria = null;
            List<NoticeCategory> categories =
                    List.of(
                            NoticeFixtures.activeNoticeCategory(1L),
                            NoticeFixtures.activeNoticeCategory(2L));
            Map<Long, List<NoticeField>> fieldsMap =
                    Map.of(1L, List.of(NoticeFixtures.activeNoticeField(1L)));

            NoticeCategoryResult result1 =
                    new NoticeCategoryResult(
                            1L, "CLOTHING", "의류", "Clothing", "CLOTHING", true, List.of(), null);
            NoticeCategoryResult result2 =
                    new NoticeCategoryResult(
                            2L, "DIGITAL", "디지털/가전", "Digital", "DIGITAL", true, List.of(), null);

            given(categoryReadManager.findByCriteria(criteria)).willReturn(categories);
            given(fieldReadManager.getGroupedByNoticeCategoryIds(List.of(1L, 2L)))
                    .willReturn(fieldsMap);
            given(assembler.toResult(categories.get(0), fieldsMap.get(1L))).willReturn(result1);
            given(assembler.toResult(categories.get(1), List.of())).willReturn(result2);

            // when
            List<NoticeCategoryResult> results = sut.findByCriteria(criteria);

            // then
            assertThat(results).hasSize(2);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 카테고리 개수를 조회한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            NoticeCategorySearchCriteria criteria = null;
            long expectedCount = 10L;

            given(categoryReadManager.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(categoryReadManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환한다")
        void countByCriteria_NoResults_ReturnsZero() {
            // given
            NoticeCategorySearchCriteria criteria = null;

            given(categoryReadManager.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
