package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.LegacySellerApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.application.legacyseller.dto.response.LegacySellerResult;
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
        @DisplayName("LegacySellerResult를 LegacySellerResponse로 변환한다")
        void toSellerResponse_ConvertsResult_ReturnsResponse() {
            // given
            LegacySellerResult result = LegacySellerApiFixtures.sellerResult();

            // when
            LegacySellerResponse response = mapper.toSellerResponse(result);

            // then
            assertThat(response.sellerId()).isEqualTo(LegacySellerApiFixtures.DEFAULT_SELLER_ID);
            assertThat(response.sellerName())
                    .isEqualTo(LegacySellerApiFixtures.DEFAULT_SELLER_NAME);
            assertThat(response.bizNo()).isEqualTo(LegacySellerApiFixtures.DEFAULT_BIZ_NO);
        }

        @Test
        @DisplayName("sellerId가 Response에 올바르게 매핑된다")
        void toSellerResponse_MapsSellerIdCorrectly() {
            // given
            LegacySellerResult result =
                    LegacySellerApiFixtures.sellerResult(99L, "다른 셀러", "999-99-99999");

            // when
            LegacySellerResponse response = mapper.toSellerResponse(result);

            // then
            assertThat(response.sellerId()).isEqualTo(99L);
        }

        @Test
        @DisplayName("sellerName이 Response에 올바르게 매핑된다")
        void toSellerResponse_MapsSellerNameCorrectly() {
            // given
            LegacySellerResult result =
                    LegacySellerApiFixtures.sellerResult(1L, "특별 셀러", "000-00-00000");

            // when
            LegacySellerResponse response = mapper.toSellerResponse(result);

            // then
            assertThat(response.sellerName()).isEqualTo("특별 셀러");
        }

        @Test
        @DisplayName("bizNo가 Response에 올바르게 매핑된다")
        void toSellerResponse_MapsBizNoCorrectly() {
            // given
            LegacySellerResult result =
                    LegacySellerApiFixtures.sellerResult(1L, "셀러", "321-54-98765");

            // when
            LegacySellerResponse response = mapper.toSellerResponse(result);

            // then
            assertThat(response.bizNo()).isEqualTo("321-54-98765");
        }
    }
}
