package com.ryuqq.marketplace.domain.outboundproductimage.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundProductImageDiff VO 단위 테스트")
class OutboundProductImageDiffTest {

    @Nested
    @DisplayName("of 팩토리 메서드 테스트")
    class OfTest {

        @Test
        @DisplayName("added, removed, retained, occurredAt으로 생성한다")
        void createWithAllFields() {
            // given
            OutboundProductImage added = OutboundProductImageFixtures.newThumbnailImage();
            OutboundProductImage removed = OutboundProductImageFixtures.activeThumbnailImage();
            OutboundProductImage retained = OutboundProductImageFixtures.activeDetailImage(2L, 1);
            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff =
                    OutboundProductImageDiff.of(
                            List.of(added), List.of(removed), List.of(retained), now);

            // then
            assertThat(diff.added()).hasSize(1);
            assertThat(diff.removed()).hasSize(1);
            assertThat(diff.retained()).hasSize(1);
            assertThat(diff.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("모두 빈 리스트로 생성할 수 있다")
        void createWithEmptyLists() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff =
                    OutboundProductImageDiff.of(List.of(), List.of(), List.of(), now);

            // then
            assertThat(diff.added()).isEmpty();
            assertThat(diff.removed()).isEmpty();
            assertThat(diff.retained()).isEmpty();
            assertThat(diff.hasNoChanges()).isTrue();
        }
    }

    @Nested
    @DisplayName("hasNoChanges 메서드 테스트")
    class HasNoChangesTest {

        @Test
        @DisplayName("added와 removed가 모두 비어있으면 true를 반환한다")
        void returnsTrueWhenNoAddedAndNoRemoved() {
            // given
            OutboundProductImage retained = OutboundProductImageFixtures.activeThumbnailImage();
            Instant now = CommonVoFixtures.now();
            OutboundProductImageDiff diff =
                    OutboundProductImageDiff.of(List.of(), List.of(), List.of(retained), now);

            // when & then
            assertThat(diff.hasNoChanges()).isTrue();
        }

        @Test
        @DisplayName("added가 있으면 false를 반환한다")
        void returnsFalseWhenHasAdded() {
            // given
            OutboundProductImage added = OutboundProductImageFixtures.newThumbnailImage();
            Instant now = CommonVoFixtures.now();
            OutboundProductImageDiff diff =
                    OutboundProductImageDiff.of(List.of(added), List.of(), List.of(), now);

            // when & then
            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("removed가 있으면 false를 반환한다")
        void returnsFalseWhenHasRemoved() {
            // given
            OutboundProductImage removed = OutboundProductImageFixtures.activeThumbnailImage();
            Instant now = CommonVoFixtures.now();
            OutboundProductImageDiff diff =
                    OutboundProductImageDiff.of(List.of(), List.of(removed), List.of(), now);

            // when & then
            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("added와 removed가 모두 있으면 false를 반환한다")
        void returnsFalseWhenHasBothAddedAndRemoved() {
            // given
            Instant now = CommonVoFixtures.now();
            OutboundProductImageDiff diff =
                    OutboundProductImageDiff.of(
                            List.of(OutboundProductImageFixtures.newThumbnailImage()),
                            List.of(OutboundProductImageFixtures.activeThumbnailImage()),
                            List.of(),
                            now);

            // when & then
            assertThat(diff.hasNoChanges()).isFalse();
        }
    }

    @Nested
    @DisplayName("방어적 복사 테스트")
    class DefensiveCopyTest {

        @Test
        @DisplayName("added 리스트는 방어적 복사가 적용된다")
        void addedListIsDefensivelyCopied() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();
            java.util.List<OutboundProductImage> mutableAdded =
                    new java.util.ArrayList<>(List.of(image));
            Instant now = CommonVoFixtures.now();

            OutboundProductImageDiff diff =
                    OutboundProductImageDiff.of(mutableAdded, List.of(), List.of(), now);

            // when - 원본 리스트를 변경해도
            mutableAdded.add(OutboundProductImageFixtures.newDetailImage(1));

            // then - diff 내부 리스트는 영향을 받지 않는다
            assertThat(diff.added()).hasSize(1);
        }

        @Test
        @DisplayName("removed 리스트는 방어적 복사가 적용된다")
        void removedListIsDefensivelyCopied() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.activeThumbnailImage();
            java.util.List<OutboundProductImage> mutableRemoved =
                    new java.util.ArrayList<>(List.of(image));
            Instant now = CommonVoFixtures.now();

            OutboundProductImageDiff diff =
                    OutboundProductImageDiff.of(List.of(), mutableRemoved, List.of(), now);

            // when
            mutableRemoved.add(OutboundProductImageFixtures.activeDetailImage(2L, 1));

            // then
            assertThat(diff.removed()).hasSize(1);
        }

        @Test
        @DisplayName("retained 리스트는 방어적 복사가 적용된다")
        void retainedListIsDefensivelyCopied() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.activeThumbnailImage();
            java.util.List<OutboundProductImage> mutableRetained =
                    new java.util.ArrayList<>(List.of(image));
            Instant now = CommonVoFixtures.now();

            OutboundProductImageDiff diff =
                    OutboundProductImageDiff.of(List.of(), List.of(), mutableRetained, now);

            // when
            mutableRetained.add(OutboundProductImageFixtures.activeDetailImage(2L, 1));

            // then
            assertThat(diff.retained()).hasSize(1);
        }

        @Test
        @DisplayName("diff의 added 리스트는 불변이다")
        void addedListIsImmutable() {
            // given
            Instant now = CommonVoFixtures.now();
            OutboundProductImageDiff diff = OutboundProductImageFixtures.addedOnlyDiff(now);

            // when & then
            assertThatThrownBy(
                            () -> diff.added().add(OutboundProductImageFixtures.newDetailImage(1)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
