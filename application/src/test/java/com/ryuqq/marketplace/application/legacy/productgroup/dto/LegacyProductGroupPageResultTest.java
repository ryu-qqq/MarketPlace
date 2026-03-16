package com.ryuqq.marketplace.application.legacy.productgroup.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.legacy.productgroup.LegacyProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyProductGroupPageResult 단위 테스트")
class LegacyProductGroupPageResultTest {

    @Nested
    @DisplayName("of() - 팩토리 메서드 생성")
    class OfTest {

        @Test
        @DisplayName("items, totalElements, page, size로 PageResult를 생성한다")
        void of_ValidParams_CreatesPageResult() {
            // given
            List<LegacyProductGroupDetailResult> items =
                    List.of(LegacyProductGroupQueryFixtures.detailResult(1L));
            long totalElements = 1L;
            int page = 0;
            int size = 20;

            // when
            LegacyProductGroupPageResult result =
                    LegacyProductGroupPageResult.of(items, totalElements, page, size);

            // then
            assertThat(result.items()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("빈 목록으로 PageResult를 생성하면 isEmpty()가 true를 반환한다")
        void of_EmptyItems_IsEmptyReturnsTrue() {
            // when
            LegacyProductGroupPageResult result =
                    LegacyProductGroupPageResult.of(List.of(), 0L, 0, 20);

            // then
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.items()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }

        @Test
        @DisplayName("items가 있으면 isEmpty()가 false를 반환한다")
        void of_WithItems_IsEmptyReturnsFalse() {
            // when
            LegacyProductGroupPageResult result = LegacyProductGroupQueryFixtures.pageResult();

            // then
            assertThat(result.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("여러 아이템과 totalElements를 정상적으로 보유한다")
        void of_MultipleItems_ReturnsCorrectTotalElements() {
            // given
            List<LegacyProductGroupDetailResult> items = List.of(
                    LegacyProductGroupQueryFixtures.detailResult(1L),
                    LegacyProductGroupQueryFixtures.detailResult(2L),
                    LegacyProductGroupQueryFixtures.detailResult(3L));

            // when
            LegacyProductGroupPageResult result =
                    LegacyProductGroupPageResult.of(items, 100L, 1, 20);

            // then
            assertThat(result.items()).hasSize(3);
            assertThat(result.totalElements()).isEqualTo(100L);
            assertThat(result.page()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("empty() - 빈 결과 생성")
    class EmptyTest {

        @Test
        @DisplayName("empty()는 빈 items와 0 totalElements로 PageResult를 생성한다")
        void empty_ReturnsEmptyPageResult() {
            // when
            LegacyProductGroupPageResult result = LegacyProductGroupPageResult.empty(0, 20);

            // then
            assertThat(result.items()).isEmpty();
            assertThat(result.totalElements()).isZero();
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("empty()가 반환한 결과는 isEmpty()가 true이다")
        void empty_IsEmptyReturnsTrue() {
            // when
            LegacyProductGroupPageResult result = LegacyProductGroupPageResult.empty(2, 50);

            // then
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("empty()는 전달한 page와 size를 보존한다")
        void empty_PreservesPageAndSize() {
            // when
            LegacyProductGroupPageResult result = LegacyProductGroupPageResult.empty(3, 50);

            // then
            assertThat(result.page()).isEqualTo(3);
            assertThat(result.size()).isEqualTo(50);
        }
    }
}
