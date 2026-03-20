package com.ryuqq.marketplace.application.legacy.shipment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.commoncode.manager.LegacyCommonCodeReadManager;
import com.ryuqq.marketplace.application.legacy.shipment.LegacyShipmentQueryFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyGetShipmentCompanyCodesService 단위 테스트")
class LegacyGetShipmentCompanyCodesServiceTest {

    private static final Long SHIPMENT_COMPANY_CODE_GROUP_ID = 2L;

    @InjectMocks private LegacyGetShipmentCompanyCodesService sut;

    @Mock private LegacyCommonCodeReadManager legacyCommonCodeReadManager;

    @Nested
    @DisplayName("execute() - 택배사 코드 목록 조회")
    class ExecuteTest {

        @Test
        @DisplayName("택배사 코드 목록을 조회하면 CommonCode 목록으로 변환하여 반환한다")
        void execute_ReturnsCommonCodeList() {
            // given
            List<LegacyCommonCode> legacyCommonCodes =
                    LegacyShipmentQueryFixtures.legacyCommonCodeList();

            given(legacyCommonCodeReadManager.getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID))
                    .willReturn(legacyCommonCodes);

            // when
            List<CommonCode> result = sut.execute();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).codeValue()).isEqualTo("CJ");
            assertThat(result.get(0).displayNameValue()).isEqualTo("CJ대한통운");
            assertThat(result.get(1).codeValue()).isEqualTo("HANJIN");
            assertThat(result.get(1).displayNameValue()).isEqualTo("한진택배");
            then(legacyCommonCodeReadManager)
                    .should()
                    .getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID);
        }

        @Test
        @DisplayName("코드그룹 ID 2로 LegacyCommonCodeReadManager를 호출한다")
        void execute_CallsReadManagerWithCorrectCodeGroupId() {
            // given
            List<LegacyCommonCode> legacyCommonCodes =
                    LegacyShipmentQueryFixtures.legacyCommonCodeList();

            given(legacyCommonCodeReadManager.getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID))
                    .willReturn(legacyCommonCodes);

            // when
            sut.execute();

            // then
            then(legacyCommonCodeReadManager)
                    .should()
                    .getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID);
            then(legacyCommonCodeReadManager).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("택배사 코드가 없는 경우 빈 목록을 반환한다")
        void execute_NoShipmentCodes_ReturnsEmptyList() {
            // given
            given(legacyCommonCodeReadManager.getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID))
                    .willReturn(LegacyShipmentQueryFixtures.emptyLegacyCommonCodeList());

            // when
            List<CommonCode> result = sut.execute();

            // then
            assertThat(result).isEmpty();
            then(legacyCommonCodeReadManager)
                    .should()
                    .getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID);
        }

        @Test
        @DisplayName("displayOrder가 null인 레거시 코드는 0으로 변환된다")
        void execute_LegacyCodeWithNullDisplayOrder_MapsToZero() {
            // given
            List<LegacyCommonCode> legacyCodes =
                    List.of(
                            LegacyShipmentQueryFixtures.legacyCommonCodeWithNullDisplayOrder(
                                    10L, "EMS", "EMS국제우편"));

            given(legacyCommonCodeReadManager.getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID))
                    .willReturn(legacyCodes);

            // when
            List<CommonCode> result = sut.execute();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).displayOrderValue()).isZero();
            assertThat(result.get(0).codeValue()).isEqualTo("EMS");
            then(legacyCommonCodeReadManager)
                    .should()
                    .getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID);
        }

        @Test
        @DisplayName("변환된 CommonCode는 codeGroupId를 CommonCodeTypeId로 올바르게 매핑한다")
        void execute_MapsCodeGroupIdToCommonCodeTypeId() {
            // given
            List<LegacyCommonCode> legacyCodes =
                    List.of(LegacyShipmentQueryFixtures.legacyCommonCode(1L, "CJ", "CJ대한통운"));

            given(legacyCommonCodeReadManager.getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID))
                    .willReturn(legacyCodes);

            // when
            List<CommonCode> result = sut.execute();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).commonCodeTypeIdValue())
                    .isEqualTo(SHIPMENT_COMPANY_CODE_GROUP_ID);
            assertThat(result.get(0).isActive()).isTrue();
            assertThat(result.get(0).isDeleted()).isFalse();
        }
    }
}
