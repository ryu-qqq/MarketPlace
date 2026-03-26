package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacySellerIdMappingQueryDslRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacySellerIdMappingQueryAdapterTest - 셀러 ID 매핑 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacySellerIdMappingQueryAdapter 단위 테스트")
class LegacySellerIdMappingQueryAdapterTest {

    @Mock private LegacySellerIdMappingQueryDslRepository queryDslRepository;

    @InjectMocks private LegacySellerIdMappingQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findInternalSellerIdByLegacySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findInternalSellerIdByLegacySellerId 메서드 테스트")
    class FindInternalSellerIdByLegacySellerIdTest {

        @Test
        @DisplayName("존재하는 legacySellerId로 조회 시 internalSellerId를 반환합니다")
        void findInternalSellerIdByLegacySellerId_WithExistingId_ReturnsInternalId() {
            // given
            long legacySellerId = 5001L;
            long internalSellerId = 1001L;
            given(queryDslRepository.findInternalSellerIdByLegacySellerId(legacySellerId))
                    .willReturn(Optional.of(internalSellerId));

            // when
            Optional<Long> result =
                    queryAdapter.findInternalSellerIdByLegacySellerId(legacySellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(internalSellerId);
            then(queryDslRepository).should().findInternalSellerIdByLegacySellerId(legacySellerId);
        }

        @Test
        @DisplayName("존재하지 않는 legacySellerId로 조회 시 빈 Optional을 반환합니다")
        void findInternalSellerIdByLegacySellerId_WithNonExistingId_ReturnsEmpty() {
            // given
            long legacySellerId = 99999L;
            given(queryDslRepository.findInternalSellerIdByLegacySellerId(legacySellerId))
                    .willReturn(Optional.empty());

            // when
            Optional<Long> result =
                    queryAdapter.findInternalSellerIdByLegacySellerId(legacySellerId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findSellerNameByLegacySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findSellerNameByLegacySellerId 메서드 테스트")
    class FindSellerNameByLegacySellerIdTest {

        @Test
        @DisplayName("존재하는 legacySellerId로 조회 시 셀러명을 반환합니다")
        void findSellerNameByLegacySellerId_WithExistingId_ReturnsSellerName() {
            // given
            long legacySellerId = 5001L;
            String sellerName = "테스트셀러";
            given(queryDslRepository.findSellerNameByLegacySellerId(legacySellerId))
                    .willReturn(Optional.of(sellerName));

            // when
            Optional<String> result = queryAdapter.findSellerNameByLegacySellerId(legacySellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(sellerName);
            then(queryDslRepository).should().findSellerNameByLegacySellerId(legacySellerId);
        }

        @Test
        @DisplayName("존재하지 않는 legacySellerId로 조회 시 빈 Optional을 반환합니다")
        void findSellerNameByLegacySellerId_WithNonExistingId_ReturnsEmpty() {
            // given
            long legacySellerId = 99999L;
            given(queryDslRepository.findSellerNameByLegacySellerId(legacySellerId))
                    .willReturn(Optional.empty());

            // when
            Optional<String> result = queryAdapter.findSellerNameByLegacySellerId(legacySellerId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
