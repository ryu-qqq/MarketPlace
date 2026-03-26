package com.ryuqq.marketplace.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CourierCode enum 단위 테스트")
class CourierCodeTest {

    @Nested
    @DisplayName("주요 택배사 코드 검증")
    class MajorCourierCodeTest {

        @Test
        @DisplayName("우체국택배 코드는 01이다")
        void koreaPostCode() {
            assertThat(CourierCode.KOREA_POST.code()).isEqualTo("01");
            assertThat(CourierCode.KOREA_POST.displayName()).isEqualTo("우체국택배");
        }

        @Test
        @DisplayName("CJ대한통운 코드는 04이다")
        void cjLogisticsCode() {
            assertThat(CourierCode.CJ_LOGISTICS.code()).isEqualTo("04");
            assertThat(CourierCode.CJ_LOGISTICS.displayName()).isEqualTo("CJ대한통운");
        }

        @Test
        @DisplayName("한진택배 코드는 05이다")
        void hanjinCode() {
            assertThat(CourierCode.HANJIN.code()).isEqualTo("05");
            assertThat(CourierCode.HANJIN.displayName()).isEqualTo("한진택배");
        }

        @Test
        @DisplayName("롯데택배 코드는 08이다")
        void lotteCode() {
            assertThat(CourierCode.LOTTE.code()).isEqualTo("08");
            assertThat(CourierCode.LOTTE.displayName()).isEqualTo("롯데택배");
        }

        @Test
        @DisplayName("수동처리 코드는 999이다")
        void manualCode() {
            assertThat(CourierCode.MANUAL.code()).isEqualTo("999");
            assertThat(CourierCode.MANUAL.displayName()).isEqualTo("수동처리(퀵, 방문수령 등)");
        }
    }

    @Nested
    @DisplayName("enum 값 품질 검증")
    class EnumQualityTest {

        @Test
        @DisplayName("모든 CourierCode는 code와 displayName을 가진다")
        void allCourierCodesHaveCodeAndDisplayName() {
            for (CourierCode courierCode : CourierCode.values()) {
                assertThat(courierCode.code()).isNotNull().isNotBlank();
                assertThat(courierCode.displayName()).isNotNull().isNotBlank();
            }
        }

        @Test
        @DisplayName("CourierCode의 code는 모두 고유하다")
        void allCodesAreUnique() {
            long distinctCount =
                    java.util.Arrays.stream(CourierCode.values())
                            .map(CourierCode::code)
                            .distinct()
                            .count();

            assertThat(distinctCount).isEqualTo(CourierCode.values().length);
        }
    }
}
