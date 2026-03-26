package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CursorPageRequest Value Object 단위 테스트")
class CursorPageRequestTest {

    @Nested
    @DisplayName("생성 및 정규화 테스트")
    class CreationTest {

        @Test
        @DisplayName("커서와 size로 CursorPageRequest를 생성한다")
        void createWithCursorAndSize() {
            CursorPageRequest<Long> request = CursorPageRequest.of(100L, 20);

            assertThat(request.cursor()).isEqualTo(100L);
            assertThat(request.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("first()는 cursor=null인 첫 페이지 요청을 반환한다")
        void firstReturnsNullCursor() {
            CursorPageRequest<Long> request = CursorPageRequest.first(20);

            assertThat(request.cursor()).isNull();
            assertThat(request.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("defaultPage()는 cursor=null, size=DEFAULT_SIZE를 반환한다")
        void defaultPageReturnsDefaultValues() {
            CursorPageRequest<Long> request = CursorPageRequest.defaultPage();

            assertThat(request.cursor()).isNull();
            assertThat(request.size()).isEqualTo(CursorPageRequest.DEFAULT_SIZE);
        }

        @Test
        @DisplayName("0 이하 size는 DEFAULT_SIZE로 정규화된다")
        void invalidSizeIsNormalized() {
            CursorPageRequest<Long> request = CursorPageRequest.of(null, 0);

            assertThat(request.size()).isEqualTo(CursorPageRequest.DEFAULT_SIZE);
        }

        @Test
        @DisplayName("MAX_SIZE를 초과하는 size는 MAX_SIZE로 정규화된다")
        void oversizedSizeIsNormalized() {
            CursorPageRequest<Long> request = CursorPageRequest.of(null, 200);

            assertThat(request.size()).isEqualTo(CursorPageRequest.MAX_SIZE);
        }

        @Test
        @DisplayName("String 커서의 빈 문자열은 null로 정규화된다")
        void blankStringCursorIsNormalized() {
            CursorPageRequest<String> request = CursorPageRequest.ofString("  ", 20);

            assertThat(request.cursor()).isNull();
        }

        @Test
        @DisplayName("afterId()는 Long ID 기반 커서 요청을 생성한다")
        void afterIdCreatesLongCursorRequest() {
            CursorPageRequest<Long> request = CursorPageRequest.afterId(50L, 20);

            assertThat(request.cursor()).isEqualTo(50L);
            assertThat(request.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("상태 확인 테스트")
    class StatusTest {

        @Test
        @DisplayName("cursor가 null이면 첫 페이지이다")
        void isFirstPageWhenCursorIsNull() {
            CursorPageRequest<Long> request = CursorPageRequest.first(20);

            assertThat(request.isFirstPage()).isTrue();
            assertThat(request.hasCursor()).isFalse();
        }

        @Test
        @DisplayName("cursor가 있으면 첫 페이지가 아니다")
        void isNotFirstPageWhenCursorIsPresent() {
            CursorPageRequest<Long> request = CursorPageRequest.afterId(100L, 20);

            assertThat(request.isFirstPage()).isFalse();
            assertThat(request.hasCursor()).isTrue();
        }
    }

    @Nested
    @DisplayName("fetchSize 테스트")
    class FetchSizeTest {

        @Test
        @DisplayName("fetchSize는 size + 1이다")
        void fetchSizeIsSizePlusOne() {
            CursorPageRequest<Long> request = CursorPageRequest.of(null, 20);

            assertThat(request.fetchSize()).isEqualTo(21);
        }
    }

    @Nested
    @DisplayName("next() 테스트")
    class NextTest {

        @Test
        @DisplayName("next()는 새로운 커서로 다음 페이지 요청을 생성한다")
        void nextCreatesNewRequestWithNewCursor() {
            CursorPageRequest<Long> request = CursorPageRequest.of(100L, 20);

            CursorPageRequest<Long> next = request.next(200L);

            assertThat(next.cursor()).isEqualTo(200L);
            assertThat(next.size()).isEqualTo(20);
        }
    }
}
