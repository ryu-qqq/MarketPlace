package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.dto.LegacySellerCompositeQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.mapper.LegacySellerCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.repository.LegacySellerCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import java.math.BigDecimal;
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
 * LegacySellerCompositionQueryAdapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacySellerCompositionQueryAdapter 단위 테스트")
class LegacySellerCompositionQueryAdapterTest {

    @Mock private LegacySellerCompositeQueryDslRepository repository;
    @Mock private LegacySellerCompositeMapper mapper;

    @InjectMocks private LegacySellerCompositionQueryAdapter adapter;

    private LegacySellerCompositeQueryDto buildDto(long sellerId) {
        return new LegacySellerCompositeQueryDto(
                sellerId, "테스트 셀러", "logo.png", "설명", 10.0,
                "123-45-67890", "테스트 회사", "홍길동", "2025-001",
                "06123", "서울시 강남구", "4층",
                "국민은행", "1234567890", "홍길동",
                "02-1234-5678", "010-1234-5678", "cs@test.com");
    }

    private SellerAdminCompositeResult buildResult(long sellerId) {
        return new SellerAdminCompositeResult(
                new SellerAdminCompositeResult.SellerInfo(
                        sellerId, "테스트 셀러", "테스트 셀러", "logo.png", "설명", true, null, null),
                new SellerAdminCompositeResult.BusinessInfo(
                        sellerId, "123-45-67890", "테스트 회사", "홍길동", "2025-001",
                        "06123", "서울시 강남구", "4층"),
                new SellerAdminCompositeResult.CsInfo(
                        sellerId, "02-1234-5678", "010-1234-5678", "cs@test.com", "", "", "", ""),
                new SellerAdminCompositeResult.ContractInfo(
                        sellerId, BigDecimal.TEN, null, null, "", "", null, null),
                new SellerAdminCompositeResult.SettlementInfo(
                        sellerId, "", "국민은행", "1234567890", "홍길동", "", null, false, null, null, null));
    }

    @Nested
    @DisplayName("findAdminCompositeById 메서드 테스트")
    class FindAdminCompositeByIdTest {

        @Test
        @DisplayName("존재하는 셀러 ID로 조회 시 Composite 결과를 반환합니다")
        void findAdminCompositeById_WithExistingId_ReturnsResult() {
            // given
            long sellerId = 10L;
            LegacySellerCompositeQueryDto dto = buildDto(sellerId);
            SellerAdminCompositeResult expectedResult = buildResult(sellerId);

            given(repository.findById(sellerId)).willReturn(Optional.of(dto));
            given(mapper.toResult(dto)).willReturn(expectedResult);

            // when
            Optional<SellerAdminCompositeResult> result =
                    adapter.findAdminCompositeById(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().seller().id()).isEqualTo(sellerId);
            then(repository).should().findById(sellerId);
            then(mapper).should().toResult(dto);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID로 조회 시 빈 Optional을 반환합니다")
        void findAdminCompositeById_WithNonExistingId_ReturnsEmpty() {
            // given
            given(repository.findById(99L)).willReturn(Optional.empty());

            // when
            Optional<SellerAdminCompositeResult> result = adapter.findAdminCompositeById(99L);

            // then
            assertThat(result).isEmpty();
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
