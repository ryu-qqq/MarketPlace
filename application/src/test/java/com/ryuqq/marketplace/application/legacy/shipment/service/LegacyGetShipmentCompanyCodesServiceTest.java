package com.ryuqq.marketplace.application.legacy.shipment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeReadManager;
import com.ryuqq.marketplace.application.legacy.shipment.LegacyShipmentQueryFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyGetShipmentCompanyCodesService 단위 테스트")
class LegacyGetShipmentCompanyCodesServiceTest {

    @InjectMocks private LegacyGetShipmentCompanyCodesService sut;

    @Mock private CommonCodeReadManager commonCodeReadManager;

    @Captor private ArgumentCaptor<CommonCodeSearchCriteria> criteriaCaptor;

    @Nested
    @DisplayName("execute() - 택배사 코드 목록 조회")
    class ExecuteTest {

        @Test
        @DisplayName("택배사 코드 목록을 조회하면 CommonCode 목록을 반환한다")
        void execute_ReturnsCommonCodeList() {
            // given
            List<CommonCode> commonCodes = LegacyShipmentQueryFixtures.commonCodeList();
            given(commonCodeReadManager.findByCriteria(any(CommonCodeSearchCriteria.class)))
                    .willReturn(commonCodes);

            // when
            List<CommonCode> result = sut.execute();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).codeValue()).isEqualTo("CJ");
            assertThat(result.get(0).displayNameValue()).isEqualTo("CJ대한통운");
            assertThat(result.get(1).codeValue()).isEqualTo("HANJIN");
            assertThat(result.get(1).displayNameValue()).isEqualTo("한진택배");
        }

        @Test
        @DisplayName("COURIER_CODE_LEGACY 타입으로 CommonCodeReadManager를 호출한다")
        void execute_CallsReadManagerWithCourierCodeLegacy() {
            // given
            given(commonCodeReadManager.findByCriteria(any(CommonCodeSearchCriteria.class)))
                    .willReturn(LegacyShipmentQueryFixtures.commonCodeList());

            // when
            sut.execute();

            // then
            then(commonCodeReadManager).should().findByCriteria(criteriaCaptor.capture());
            assertThat(criteriaCaptor.getValue().commonCodeTypeCode())
                    .isEqualTo("COURIER_CODE_LEGACY");
        }

        @Test
        @DisplayName("택배사 코드가 없는 경우 빈 목록을 반환한다")
        void execute_NoShipmentCodes_ReturnsEmptyList() {
            // given
            given(commonCodeReadManager.findByCriteria(any(CommonCodeSearchCriteria.class)))
                    .willReturn(List.of());

            // when
            List<CommonCode> result = sut.execute();

            // then
            assertThat(result).isEmpty();
        }
    }
}
