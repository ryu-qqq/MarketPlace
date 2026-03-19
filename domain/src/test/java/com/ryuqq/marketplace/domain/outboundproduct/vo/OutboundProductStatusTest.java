package com.ryuqq.marketplace.domain.outboundproduct.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundProductStatus enum 단위 테스트")
class OutboundProductStatusTest {

    @Nested
    @DisplayName("상태 판별 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("PENDING_REGISTRATION은 isPendingRegistration이 true이다")
        void pendingRegistrationCheck() {
            assertThat(OutboundProductStatus.PENDING_REGISTRATION.isPendingRegistration()).isTrue();
            assertThat(OutboundProductStatus.PENDING_REGISTRATION.isRegistered()).isFalse();
            assertThat(OutboundProductStatus.PENDING_REGISTRATION.isRegistrationFailed()).isFalse();
            assertThat(OutboundProductStatus.PENDING_REGISTRATION.isDeregistered()).isFalse();
        }

        @Test
        @DisplayName("REGISTERED는 isRegistered가 true이다")
        void registeredCheck() {
            assertThat(OutboundProductStatus.REGISTERED.isRegistered()).isTrue();
            assertThat(OutboundProductStatus.REGISTERED.isPendingRegistration()).isFalse();
            assertThat(OutboundProductStatus.REGISTERED.isRegistrationFailed()).isFalse();
            assertThat(OutboundProductStatus.REGISTERED.isDeregistered()).isFalse();
        }

        @Test
        @DisplayName("REGISTRATION_FAILED는 isRegistrationFailed가 true이다")
        void registrationFailedCheck() {
            assertThat(OutboundProductStatus.REGISTRATION_FAILED.isRegistrationFailed()).isTrue();
            assertThat(OutboundProductStatus.REGISTRATION_FAILED.isRegistered()).isFalse();
        }

        @Test
        @DisplayName("DEREGISTERED는 isDeregistered가 true이다")
        void deregisteredCheck() {
            assertThat(OutboundProductStatus.DEREGISTERED.isDeregistered()).isTrue();
            assertThat(OutboundProductStatus.DEREGISTERED.isRegistered()).isFalse();
        }
    }

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("PENDING_REGISTRATION의 설명은 등록 대기이다")
        void pendingRegistrationDescription() {
            assertThat(OutboundProductStatus.PENDING_REGISTRATION.description()).isEqualTo("등록 대기");
        }

        @Test
        @DisplayName("REGISTERED의 설명은 등록 완료이다")
        void registeredDescription() {
            assertThat(OutboundProductStatus.REGISTERED.description()).isEqualTo("등록 완료");
        }

        @Test
        @DisplayName("REGISTRATION_FAILED의 설명은 등록 실패이다")
        void registrationFailedDescription() {
            assertThat(OutboundProductStatus.REGISTRATION_FAILED.description()).isEqualTo("등록 실패");
        }

        @Test
        @DisplayName("DEREGISTERED의 설명은 등록 해제이다")
        void deregisteredDescription() {
            assertThat(OutboundProductStatus.DEREGISTERED.description()).isEqualTo("등록 해제");
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("유효한 문자열로 enum을 변환한다")
        void fromValidString() {
            assertThat(OutboundProductStatus.fromString("REGISTERED"))
                    .isEqualTo(OutboundProductStatus.REGISTERED);
        }

        @Test
        @DisplayName("유효하지 않은 문자열이면 예외가 발생한다")
        void fromInvalidStringThrowsException() {
            assertThatThrownBy(() -> OutboundProductStatus.fromString("INVALID_STATUS"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValuesTest {

        @Test
        @DisplayName("OutboundProductStatus는 4가지 값을 가진다")
        void hasCorrectNumberOfValues() {
            assertThat(OutboundProductStatus.values()).hasSize(4);
        }

        @Test
        @DisplayName("모든 상태는 description을 가진다")
        void allStatusesHaveDescription() {
            for (OutboundProductStatus status : OutboundProductStatus.values()) {
                assertThat(status.description()).isNotNull().isNotBlank();
            }
        }
    }
}
