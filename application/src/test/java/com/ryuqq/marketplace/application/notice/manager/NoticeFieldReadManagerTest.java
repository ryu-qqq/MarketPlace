package com.ryuqq.marketplace.application.notice.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.notice.port.out.query.NoticeFieldQueryPort;
import com.ryuqq.marketplace.domain.notice.NoticeFixtures;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
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
@DisplayName("NoticeFieldReadManager 단위 테스트")
class NoticeFieldReadManagerTest {

    @InjectMocks private NoticeFieldReadManager sut;

    @Mock private NoticeFieldQueryPort queryPort;

    @Nested
    @DisplayName("getByNoticeCategoryId() - 카테고리 ID로 필드 조회")
    class GetByNoticeCategoryIdTest {

        @Test
        @DisplayName("카테고리 ID로 고시정보 필드 리스트를 조회한다")
        void getByNoticeCategoryId_ExistsId_ReturnsFieldList() {
            // given
            Long noticeCategoryId = 1L;
            List<NoticeField> expected =
                    List.of(
                            NoticeFixtures.activeNoticeField(1L),
                            NoticeFixtures.activeNoticeField(2L));

            given(queryPort.findByNoticeCategoryId(noticeCategoryId)).willReturn(expected);

            // when
            List<NoticeField> result = sut.getByNoticeCategoryId(noticeCategoryId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByNoticeCategoryId(noticeCategoryId);
        }

        @Test
        @DisplayName("필드가 없는 카테고리는 빈 리스트를 반환한다")
        void getByNoticeCategoryId_NoFields_ReturnsEmptyList() {
            // given
            Long noticeCategoryId = 999L;
            List<NoticeField> emptyList = List.of();

            given(queryPort.findByNoticeCategoryId(noticeCategoryId)).willReturn(emptyList);

            // when
            List<NoticeField> result = sut.getByNoticeCategoryId(noticeCategoryId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getGroupedByNoticeCategoryIds() - 여러 카테고리 ID로 그룹핑 조회")
    class GetGroupedByNoticeCategoryIdsTest {

        @Test
        @DisplayName("여러 카테고리 ID로 필드를 그룹핑하여 조회한다")
        void getGroupedByNoticeCategoryIds_ValidIds_ReturnsGroupedMap() {
            // given
            List<Long> categoryIds = List.of(1L, 2L);
            Map<Long, List<NoticeField>> expected =
                    Map.of(
                            1L, List.of(NoticeFixtures.activeNoticeField(1L)),
                            2L, List.of(NoticeFixtures.activeNoticeField(2L)));

            given(queryPort.findGroupedByNoticeCategoryIds(categoryIds)).willReturn(expected);

            // when
            Map<Long, List<NoticeField>> result = sut.getGroupedByNoticeCategoryIds(categoryIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(1L)).hasSize(1);
            assertThat(result.get(2L)).hasSize(1);
            then(queryPort).should().findGroupedByNoticeCategoryIds(categoryIds);
        }

        @Test
        @DisplayName("빈 ID 리스트로 조회하면 빈 Map을 반환한다")
        void getGroupedByNoticeCategoryIds_EmptyIds_ReturnsEmptyMap() {
            // given
            List<Long> emptyIds = List.of();

            // when
            Map<Long, List<NoticeField>> result = sut.getGroupedByNoticeCategoryIds(emptyIds);

            // then
            assertThat(result).isEmpty();
            then(queryPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("일부 카테고리만 필드를 가진 경우 해당 카테고리만 반환한다")
        void getGroupedByNoticeCategoryIds_PartialFields_ReturnsPartialMap() {
            // given
            List<Long> categoryIds = List.of(1L, 2L, 3L);
            Map<Long, List<NoticeField>> expected =
                    Map.of(1L, List.of(NoticeFixtures.activeNoticeField(1L)));

            given(queryPort.findGroupedByNoticeCategoryIds(categoryIds)).willReturn(expected);

            // when
            Map<Long, List<NoticeField>> result = sut.getGroupedByNoticeCategoryIds(categoryIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).containsKey(1L);
            assertThat(result).doesNotContainKey(2L);
            assertThat(result).doesNotContainKey(3L);
        }
    }
}
