package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.order.OrderFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExternalProductSnapshot Value Object 테스트")
class ExternalProductSnapshotTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 ExternalProductSnapshot을 생성한다")
        void createWithValidValues() {
            // when
            ExternalProductSnapshot snapshot = OrderFixtures.defaultExternalProductSnapshot();

            // then
            assertThat(snapshot.externalProductId()).isEqualTo("EXT-PROD-001");
            assertThat(snapshot.externalOptionId()).isEqualTo("EXT-OPT-001");
            assertThat(snapshot.externalProductName()).isEqualTo("테스트 상품명");
            assertThat(snapshot.externalOptionName()).isEqualTo("블랙 / L");
            assertThat(snapshot.externalImageUrl()).isNotBlank();
        }

        @Test
        @DisplayName("외부 상품 ID가 null이면 예외가 발생한다")
        void createWithNullExternalProductId_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalProductSnapshot.of(
                                            null,
                                            "EXT-OPT-001",
                                            "상품명",
                                            "옵션명",
                                            "https://example.com/image.jpg"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부 상품 ID는 필수");
        }

        @Test
        @DisplayName("외부 상품 ID가 빈 문자열이면 예외가 발생한다")
        void createWithBlankExternalProductId_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalProductSnapshot.of(
                                            "   ",
                                            "EXT-OPT-001",
                                            "상품명",
                                            "옵션명",
                                            "https://example.com/image.jpg"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부 상품 ID는 필수");
        }

        @Test
        @DisplayName("선택 필드(옵션ID, 상품명, 옵션명, 이미지URL)는 null을 허용한다")
        void createWithNullOptionalFields() {
            // when & then
            assertThatCode(() -> ExternalProductSnapshot.of("EXT-PROD-001", null, null, null, null))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 ExternalProductSnapshot은 동일하다")
        void sameValuesAreEqual() {
            // when
            ExternalProductSnapshot snapshot1 = OrderFixtures.defaultExternalProductSnapshot();
            ExternalProductSnapshot snapshot2 = OrderFixtures.defaultExternalProductSnapshot();

            // then
            assertThat(snapshot1).isEqualTo(snapshot2);
            assertThat(snapshot1.hashCode()).isEqualTo(snapshot2.hashCode());
        }
    }
}
