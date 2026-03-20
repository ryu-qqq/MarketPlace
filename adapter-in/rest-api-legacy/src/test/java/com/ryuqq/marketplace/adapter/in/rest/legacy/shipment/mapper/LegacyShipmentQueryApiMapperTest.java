package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.LegacyShipmentApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.dto.response.LegacyShipmentCompanyCodeResponse;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyShipmentQueryApiMapper 단위 테스트")
class LegacyShipmentQueryApiMapperTest {

    private LegacyShipmentQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyShipmentQueryApiMapper();
    }

    @Nested
    @DisplayName("toCompanyCodeResponses - 택배사 코드 목록 변환")
    class ToCompanyCodeResponsesTest {

        @Test
        @DisplayName("CommonCode 목록을 LegacyShipmentCompanyCodeResponse 목록으로 변환한다")
        void toCompanyCodeResponses_ConvertsList_ReturnsList() {
            // given
            List<CommonCode> codes = LegacyShipmentApiFixtures.commonCodes();

            // when
            List<LegacyShipmentCompanyCodeResponse> responses =
                    mapper.toCompanyCodeResponses(codes);

            // then
            assertThat(responses).hasSize(3);
        }

        @Test
        @DisplayName("displayName이 shipmentCompanyName으로 매핑된다")
        void toCompanyCodeResponses_MapsCompanyName_Correctly() {
            // given
            List<CommonCode> codes = LegacyShipmentApiFixtures.singleCommonCode();

            // when
            List<LegacyShipmentCompanyCodeResponse> responses =
                    mapper.toCompanyCodeResponses(codes);

            // then
            assertThat(responses.get(0).shipmentCompanyName())
                    .isEqualTo(LegacyShipmentApiFixtures.DEFAULT_COMPANY_NAME_CJ);
        }

        @Test
        @DisplayName("code가 shipmentCompanyCode로 매핑된다")
        void toCompanyCodeResponses_MapsCompanyCode_Correctly() {
            // given
            List<CommonCode> codes = LegacyShipmentApiFixtures.singleCommonCode();

            // when
            List<LegacyShipmentCompanyCodeResponse> responses =
                    mapper.toCompanyCodeResponses(codes);

            // then
            assertThat(responses.get(0).shipmentCompanyCode())
                    .isEqualTo(LegacyShipmentApiFixtures.DEFAULT_COMPANY_CODE_CJ);
        }

        @Test
        @DisplayName("빈 목록은 빈 응답 목록으로 변환된다")
        void toCompanyCodeResponses_EmptyList_ReturnsEmptyList() {
            // when
            List<LegacyShipmentCompanyCodeResponse> responses =
                    mapper.toCompanyCodeResponses(List.of());

            // then
            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("여러 항목이 순서대로 변환된다")
        void toCompanyCodeResponses_MultipleItems_MaintainsOrder() {
            // given
            List<CommonCode> codes = LegacyShipmentApiFixtures.commonCodes();

            // when
            List<LegacyShipmentCompanyCodeResponse> responses =
                    mapper.toCompanyCodeResponses(codes);

            // then
            assertThat(responses.get(0).shipmentCompanyName())
                    .isEqualTo(LegacyShipmentApiFixtures.DEFAULT_COMPANY_NAME_CJ);
            assertThat(responses.get(1).shipmentCompanyName())
                    .isEqualTo(LegacyShipmentApiFixtures.DEFAULT_COMPANY_NAME_LOTTE);
            assertThat(responses.get(2).shipmentCompanyName())
                    .isEqualTo(LegacyShipmentApiFixtures.DEFAULT_COMPANY_NAME_HANJIN);
        }
    }
}
