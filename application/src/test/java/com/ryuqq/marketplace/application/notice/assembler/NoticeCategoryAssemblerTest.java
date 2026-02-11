package com.ryuqq.marketplace.application.notice.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeFieldResult;
import com.ryuqq.marketplace.domain.notice.NoticeFixtures;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeCategoryAssembler 단위 테스트")
class NoticeCategoryAssemblerTest {

    private NoticeCategoryAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new NoticeCategoryAssembler();
    }

    @Nested
    @DisplayName("toResult() - 단일 Result 변환")
    class ToResultTest {

        @Test
        @DisplayName("고시정보 카테고리를 Result로 변환한다")
        void toResult_ValidNoticeCategory_ReturnsResult() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory(1L);
            List<NoticeField> fields = List.of();

            // when
            NoticeCategoryResult result = sut.toResult(category, fields);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(category.idValue());
            assertThat(result.code()).isEqualTo(category.codeValue());
            assertThat(result.nameKo()).isEqualTo(category.nameKo());
            assertThat(result.nameEn()).isEqualTo(category.nameEn());
            assertThat(result.targetCategoryGroup())
                    .isEqualTo(category.targetCategoryGroup().name());
            assertThat(result.active()).isEqualTo(category.isActive());
            assertThat(result.fields()).isEmpty();
            assertThat(result.createdAt()).isEqualTo(category.createdAt());
        }

        @Test
        @DisplayName("필드가 포함된 고시정보 카테고리를 Result로 변환한다")
        void toResult_WithFields_ReturnsResultWithFields() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory(1L);
            List<NoticeField> fields =
                    List.of(
                            NoticeFixtures.activeNoticeField(1L),
                            NoticeFixtures.activeNoticeField(2L));

            // when
            NoticeCategoryResult result = sut.toResult(category, fields);

            // then
            assertThat(result.fields()).hasSize(2);
            assertThat(result.fields()).extracting(NoticeFieldResult::id).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("비활성 상태의 고시정보 카테고리를 Result로 변환한다")
        void toResult_InactiveCategory_ReturnsInactiveResult() {
            // given
            NoticeCategory category = NoticeFixtures.inactiveNoticeCategory();
            List<NoticeField> fields = List.of();

            // when
            NoticeCategoryResult result = sut.toResult(category, fields);

            // then
            assertThat(result.active()).isFalse();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 Result 변환")
    class ToPageResultTest {

        @Test
        @DisplayName("Result 리스트를 PageResult로 변환한다")
        void toPageResult_ValidList_ReturnsPageResult() {
            // given
            NoticeCategory category1 = NoticeFixtures.activeNoticeCategory(1L);
            NoticeCategory category2 = NoticeFixtures.activeNoticeCategory(2L);
            List<NoticeCategoryResult> results =
                    List.of(sut.toResult(category1, List.of()), sut.toResult(category2, List.of()));
            int page = 0;
            int size = 20;
            long totalElements = 2L;

            // when
            NoticeCategoryPageResult pageResult =
                    sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(pageResult).isNotNull();
            assertThat(pageResult.results()).hasSize(2);
            assertThat(pageResult.pageMeta().page()).isEqualTo(page);
            assertThat(pageResult.pageMeta().size()).isEqualTo(size);
            assertThat(pageResult.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("빈 리스트로 빈 PageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            List<NoticeCategoryResult> emptyList = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            NoticeCategoryPageResult pageResult =
                    sut.toPageResult(emptyList, page, size, totalElements);

            // then
            assertThat(pageResult.results()).isEmpty();
            assertThat(pageResult.isEmpty()).isTrue();
            assertThat(pageResult.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("다양한 페이징 정보로 PageResult를 생성한다")
        void toPageResult_DifferentPaging_ReturnsPageResult() {
            // given
            NoticeCategory category = NoticeFixtures.activeNoticeCategory(1L);
            List<NoticeCategoryResult> results = List.of(sut.toResult(category, List.of()));
            int page = 2;
            int size = 10;
            long totalElements = 25L;

            // when
            NoticeCategoryPageResult pageResult =
                    sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(pageResult.pageMeta().page()).isEqualTo(2);
            assertThat(pageResult.pageMeta().size()).isEqualTo(10);
            assertThat(pageResult.pageMeta().totalElements()).isEqualTo(25L);
        }
    }
}
