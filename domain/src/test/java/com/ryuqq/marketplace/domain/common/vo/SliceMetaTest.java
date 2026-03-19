package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SliceMeta Value Object 단위 테스트")
class SliceMetaTest {

    @Nested
    @DisplayName("팩토리 메서드 테스트")
    class FactoryTest {

        @Test
        @DisplayName("of(size, hasNext)로 커서 없는 SliceMeta를 생성한다")
        void createWithoutCursor() {
            SliceMeta meta = SliceMeta.of(20, true);

            assertThat(meta.size()).isEqualTo(20);
            assertThat(meta.hasNext()).isTrue();
            assertThat(meta.cursor()).isNull();
        }

        @Test
        @DisplayName("of(size, hasNext, count)로 count 포함 SliceMeta를 생성한다")
        void createWithCount() {
            SliceMeta meta = SliceMeta.of(20, true, 20);

            assertThat(meta.count()).isEqualTo(20);
        }

        @Test
        @DisplayName("withCursor(String, size, hasNext)로 String 커서 SliceMeta를 생성한다")
        void createWithStringCursor() {
            SliceMeta meta = SliceMeta.withCursor("cursor-xyz", 20, true);

            assertThat(meta.cursor()).isEqualTo("cursor-xyz");
            assertThat(meta.hasCursor()).isTrue();
        }

        @Test
        @DisplayName("withCursor(Long, size, hasNext)로 Long ID 커서 SliceMeta를 생성한다")
        void createWithLongCursor() {
            SliceMeta meta = SliceMeta.withCursor(100L, 20, true);

            assertThat(meta.cursor()).isEqualTo("100");
            assertThat(meta.hasCursor()).isTrue();
        }

        @Test
        @DisplayName("withCursor(null Long, size, hasNext)는 커서가 없다")
        void createWithNullLongCursor() {
            SliceMeta meta = SliceMeta.withCursor((Long) null, 20, false);

            assertThat(meta.cursor()).isNull();
            assertThat(meta.hasCursor()).isFalse();
        }

        @Test
        @DisplayName("empty()는 빈 SliceMeta를 반환한다")
        void emptyReturnsEmptyMeta() {
            SliceMeta meta = SliceMeta.empty();

            assertThat(meta.hasNext()).isFalse();
            assertThat(meta.cursor()).isNull();
            assertThat(meta.count()).isEqualTo(0);
            assertThat(meta.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("size가 0 이하이면 DEFAULT_SIZE로 정규화된다")
        void invalidSizeIsNormalized() {
            SliceMeta meta = SliceMeta.of(0, false);

            assertThat(meta.size()).isEqualTo(SliceMeta.DEFAULT_SIZE);
        }
    }

    @Nested
    @DisplayName("상태 확인 테스트")
    class StatusTest {

        @Test
        @DisplayName("hasNext가 false이면 마지막 슬라이스이다")
        void isLastWhenHasNextIsFalse() {
            SliceMeta meta = SliceMeta.of(20, false);

            assertThat(meta.isLast()).isTrue();
        }

        @Test
        @DisplayName("hasNext가 true이면 마지막 슬라이스가 아니다")
        void isNotLastWhenHasNextIsTrue() {
            SliceMeta meta = SliceMeta.of(20, true);

            assertThat(meta.isLast()).isFalse();
        }

        @Test
        @DisplayName("count가 0이면 비어있다")
        void isEmptyWhenCountIsZero() {
            SliceMeta meta = SliceMeta.of(20, false, 0);

            assertThat(meta.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("cursorAsLong() 테스트")
    class CursorAsLongTest {

        @Test
        @DisplayName("Long으로 변환 가능한 커서를 Long으로 반환한다")
        void cursorAsLongReturnsLong() {
            SliceMeta meta = SliceMeta.withCursor("12345", 20, true);

            assertThat(meta.cursorAsLong()).isEqualTo(12345L);
        }

        @Test
        @DisplayName("커서가 없으면 null을 반환한다")
        void cursorAsLongReturnsNullWhenNoCursor() {
            SliceMeta meta = SliceMeta.empty();

            assertThat(meta.cursorAsLong()).isNull();
        }

        @Test
        @DisplayName("Long으로 변환할 수 없는 커서이면 null을 반환한다")
        void cursorAsLongReturnsNullWhenNotParseable() {
            SliceMeta meta = SliceMeta.withCursor("not-a-number", 20, true);

            assertThat(meta.cursorAsLong()).isNull();
        }
    }

    @Nested
    @DisplayName("next() 테스트")
    class NextTest {

        @Test
        @DisplayName("String 커서로 다음 SliceMeta를 생성한다")
        void nextWithStringCursor() {
            SliceMeta meta = SliceMeta.of(20, true);

            SliceMeta next = meta.next("next-cursor", true);

            assertThat(next.cursor()).isEqualTo("next-cursor");
            assertThat(next.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("Long 커서로 다음 SliceMeta를 생성한다")
        void nextWithLongCursor() {
            SliceMeta meta = SliceMeta.of(20, true);

            SliceMeta next = meta.next(200L, true);

            assertThat(next.cursor()).isEqualTo("200");
        }
    }
}
