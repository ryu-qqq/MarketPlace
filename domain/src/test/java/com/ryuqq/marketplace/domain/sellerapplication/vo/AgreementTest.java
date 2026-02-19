package com.ryuqq.marketplace.domain.sellerapplication.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Agreement VO 테스트")
class AgreementTest {

    @Nested
    @DisplayName("of() - 동의 정보 생성")
    class OfTest {

        @Test
        @DisplayName("모든 동의가 완료되면 생성된다")
        void createWithAllAgreed() {
            Instant now = CommonVoFixtures.now();
            Agreement agreement = Agreement.of(true, true, now);

            assertThat(agreement.privacyAgreed()).isTrue();
            assertThat(agreement.termsAgreed()).isTrue();
            assertThat(agreement.agreedAt()).isEqualTo(now);
            assertThat(agreement.agreedAtValue()).isEqualTo(now);
        }

        @Test
        @DisplayName("개인정보 처리방침에 동의하지 않으면 예외가 발생한다")
        void privacyNotAgreed_ThrowsException() {
            assertThatThrownBy(() -> Agreement.of(false, true, Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("모두 동의");
        }

        @Test
        @DisplayName("이용약관에 동의하지 않으면 예외가 발생한다")
        void termsNotAgreed_ThrowsException() {
            assertThatThrownBy(() -> Agreement.of(true, false, Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("모두 동의");
        }

        @Test
        @DisplayName("모두 동의하지 않으면 예외가 발생한다")
        void noneAgreed_ThrowsException() {
            assertThatThrownBy(() -> Agreement.of(false, false, Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("모두 동의");
        }

        @Test
        @DisplayName("동의 시각이 null이면 예외가 발생한다")
        void nullAgreedAt_ThrowsException() {
            assertThatThrownBy(() -> Agreement.of(true, true, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("동의 시각은 필수");
        }
    }

    @Nested
    @DisplayName("agreedAt() - 팩토리 메서드")
    class AgreedAtFactoryTest {

        @Test
        @DisplayName("현재 시각으로 모든 동의를 생성한다")
        void createWithNow() {
            Instant now = CommonVoFixtures.now();
            Agreement agreement = Agreement.agreedAt(now);

            assertThat(agreement.isPrivacyAgreed()).isTrue();
            assertThat(agreement.isTermsAgreed()).isTrue();
            assertThat(agreement.agreedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("동의 시각으로 재구성한다")
        void reconstituteFromDb() {
            Instant agreedAt = CommonVoFixtures.yesterday();
            Agreement agreement = Agreement.reconstitute(agreedAt);

            assertThat(agreement.isPrivacyAgreed()).isTrue();
            assertThat(agreement.isTermsAgreed()).isTrue();
            assertThat(agreement.agreedAt()).isEqualTo(agreedAt);
        }
    }
}
