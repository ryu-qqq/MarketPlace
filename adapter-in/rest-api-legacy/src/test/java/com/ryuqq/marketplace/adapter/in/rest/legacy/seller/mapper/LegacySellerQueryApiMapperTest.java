package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.LegacySellerApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacySellerQueryApiMapper 단위 테스트")
class LegacySellerQueryApiMapperTest {

    private LegacySellerQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacySellerQueryApiMapper();
    }

    @Nested
    @DisplayName("toSellerResponse - 셀러 조회 결과 변환")
    class ToSellerResponseTest {

        @Test
        @DisplayName("LegacySellerAuthResult를 LegacySellerResponse로 변환한다")
        void toSellerResponse_ConvertsResult_ReturnsResponse() {
            // given
            LegacySellerAuthResult result = LegacySellerApiFixtures.legacySellerAuthResult();

            // when
            LegacySellerResponse response = mapper.toSellerResponse(result);

            // then
            assertThat(response.sellerId()).isEqualTo(LegacySellerApiFixtures.DEFAULT_SELLER_ID);
            assertThat(response.email()).isEqualTo(LegacySellerApiFixtures.DEFAULT_EMAIL);
            assertThat(response.approvalStatus())
                    .isEqualTo(LegacySellerApiFixtures.DEFAULT_APPROVAL_STATUS);
        }

        @Test
        @DisplayName("sellerId가 Response에 올바르게 매핑된다")
        void toSellerResponse_MapsSellerIdCorrectly() {
            // given
            LegacySellerAuthResult result =
                    LegacySellerApiFixtures.legacySellerAuthResult(
                            99L, "other@test.com", "hashed", "ADMIN", "APPROVED");

            // when
            LegacySellerResponse response = mapper.toSellerResponse(result);

            // then
            assertThat(response.sellerId()).isEqualTo(99L);
        }

        @Test
        @DisplayName("email이 Response에 올바르게 매핑된다")
        void toSellerResponse_MapsEmailCorrectly() {
            // given
            LegacySellerAuthResult result =
                    LegacySellerApiFixtures.legacySellerAuthResult(
                            1L, "special@test.com", "hashed", "ADMIN", "APPROVED");

            // when
            LegacySellerResponse response = mapper.toSellerResponse(result);

            // then
            assertThat(response.email()).isEqualTo("special@test.com");
        }

        @Test
        @DisplayName("roleType이 Response에 올바르게 매핑된다")
        void toSellerResponse_MapsRoleTypeCorrectly() {
            // given
            LegacySellerAuthResult result =
                    LegacySellerApiFixtures.legacySellerAuthResult(
                            1L, "test@test.com", "hashed", "SELLER", "APPROVED");

            // when
            LegacySellerResponse response = mapper.toSellerResponse(result);

            // then
            assertThat(response.roleType()).isEqualTo("SELLER");
        }
    }
}
