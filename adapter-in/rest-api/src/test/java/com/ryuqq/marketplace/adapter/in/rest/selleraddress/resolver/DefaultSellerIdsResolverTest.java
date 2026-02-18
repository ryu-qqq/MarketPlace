package com.ryuqq.marketplace.adapter.in.rest.selleraddress.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("DefaultSellerIdsResolver 단위 테스트")
class DefaultSellerIdsResolverTest {

    private final DefaultSellerIdsResolver sut = new DefaultSellerIdsResolver();

    @Nested
    @DisplayName("resolve() - sellerIds 결정")
    class ResolveTest {

        @Test
        @DisplayName("requestSellerIds가 비어있지 않으면 그대로 반환한다")
        void resolve_WithRequestSellerIds_ReturnsRequestIds() {
            // given
            List<Long> requestSellerIds = List.of(1L, 2L, 3L);

            // when
            List<Long> result = sut.resolve(requestSellerIds, 99L);

            // then
            assertThat(result).containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("requestSellerIds가 null이면 pathSellerId로 단건 리스트를 반환한다")
        void resolve_NullRequestIds_ReturnsPathSellerId() {
            // given & when
            List<Long> result = sut.resolve(null, 10L);

            // then
            assertThat(result).containsExactly(10L);
        }

        @Test
        @DisplayName("requestSellerIds가 빈 리스트이면 pathSellerId로 단건 리스트를 반환한다")
        void resolve_EmptyRequestIds_ReturnsPathSellerId() {
            // given & when
            List<Long> result = sut.resolve(Collections.emptyList(), 10L);

            // then
            assertThat(result).containsExactly(10L);
        }

        @Test
        @DisplayName("requestSellerIds와 pathSellerId 모두 없으면 빈 리스트를 반환한다")
        void resolve_NullBoth_ReturnsEmptyList() {
            // given & when
            List<Long> result = sut.resolve(null, null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("requestSellerIds가 빈 리스트이고 pathSellerId도 null이면 빈 리스트를 반환한다")
        void resolve_EmptyListAndNullPath_ReturnsEmptyList() {
            // given & when
            List<Long> result = sut.resolve(Collections.emptyList(), null);

            // then
            assertThat(result).isEmpty();
        }
    }
}
