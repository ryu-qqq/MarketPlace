package com.ryuqq.marketplace.application.notice.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.notice.port.out.query.NoticeCategoryQueryPort;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.NoticeFixtures;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.exception.NoticeCategoryNotFoundException;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import java.util.List;
import java.util.Optional;
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
@DisplayName("NoticeCategoryReadManager 단위 테스트")
class NoticeCategoryReadManagerTest {

    @InjectMocks private NoticeCategoryReadManager sut;

    @Mock private NoticeCategoryQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 고시정보 카테고리를 ID로 조회한다")
        void getById_ExistsId_ReturnsNoticeCategory() {
            // given
            NoticeCategoryId id = NoticeCategoryId.of(1L);
            NoticeCategory expected = NoticeFixtures.activeNoticeCategory(1L);

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            NoticeCategory result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        void getById_NotExistsId_ThrowsException() {
            // given
            NoticeCategoryId id = NoticeCategoryId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(NoticeCategoryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByCategoryGroup() - CategoryGroup으로 조회")
    class GetByCategoryGroupTest {

        @Test
        @DisplayName("CategoryGroup으로 고시정보 카테고리를 조회한다")
        void getByCategoryGroup_ExistsCategoryGroup_ReturnsNoticeCategory() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.CLOTHING;
            NoticeCategory expected = NoticeFixtures.activeNoticeCategory();

            given(queryPort.findByCategoryGroup(categoryGroup)).willReturn(Optional.of(expected));

            // when
            NoticeCategory result = sut.getByCategoryGroup(categoryGroup);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByCategoryGroup(categoryGroup);
        }

        @Test
        @DisplayName("존재하지 않는 CategoryGroup으로 조회하면 예외가 발생한다")
        void getByCategoryGroup_NotExists_ThrowsException() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.DIGITAL;

            given(queryPort.findByCategoryGroup(categoryGroup)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getByCategoryGroup(categoryGroup))
                    .isInstanceOf(NoticeCategoryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 고시정보 카테고리 리스트를 조회한다")
        void findByCriteria_ValidCriteria_ReturnsList() {
            // given
            NoticeCategorySearchCriteria criteria = null; // mock 대상
            List<NoticeCategory> expected =
                    List.of(
                            NoticeFixtures.activeNoticeCategory(1L),
                            NoticeFixtures.activeNoticeCategory(2L));

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<NoticeCategory> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 카테고리가 없으면 빈 리스트를 반환한다")
        void findByCriteria_NoMatches_ReturnsEmptyList() {
            // given
            NoticeCategorySearchCriteria criteria = null;
            List<NoticeCategory> emptyList = List.of();

            given(queryPort.findByCriteria(criteria)).willReturn(emptyList);

            // when
            List<NoticeCategory> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 고시정보 카테고리 개수를 조회한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            NoticeCategorySearchCriteria criteria = null;
            long expectedCount = 5L;

            given(queryPort.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(queryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 카테고리가 없으면 0을 반환한다")
        void countByCriteria_NoMatches_ReturnsZero() {
            // given
            NoticeCategorySearchCriteria criteria = null;

            given(queryPort.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
