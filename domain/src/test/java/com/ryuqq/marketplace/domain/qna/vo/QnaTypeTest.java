package com.ryuqq.marketplace.domain.qna.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaType 단위 테스트")
class QnaTypeTest {

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("8개의 문의 유형이 정의되어 있다")
        void hasEightTypes() {
            assertThat(QnaType.values()).hasSize(8);
        }

        @Test
        @DisplayName("모든 문의 유형이 존재한다")
        void allTypesExist() {
            assertThat(QnaType.values())
                    .containsExactly(
                            QnaType.PRODUCT,
                            QnaType.SHIPPING,
                            QnaType.ORDER,
                            QnaType.EXCHANGE,
                            QnaType.REFUND,
                            QnaType.RESTOCK,
                            QnaType.PRICE,
                            QnaType.ETC);
        }
    }
}
