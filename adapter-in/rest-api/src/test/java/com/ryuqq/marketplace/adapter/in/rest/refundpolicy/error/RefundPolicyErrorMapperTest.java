package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.refundpolicy.exception.RefundPolicyException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("RefundPolicyErrorMapper 단위 테스트")
class RefundPolicyErrorMapperTest {

    private final RefundPolicyErrorMapper sut = new RefundPolicyErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("RefundPolicyException을 지원한다")
        void supports_RefundPolicyException_ReturnsTrue() {
            // given
            DomainException ex = RefundPolicyException.policyNotFound();

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("다른 도메인 예외는 지원하지 않는다")
        void supports_OtherDomainException_ReturnsFalse() {
            // given
            DomainException ex =
                    new DomainException(
                            new com.ryuqq.marketplace.domain.common.exception.ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "OTHER-001";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 400;
                                }

                                @Override
                                public String getMessage() {
                                    return "Other error";
                                }
                            }) {};

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("map() - 예외를 MappedError로 변환")
    class MapTest {

        @Test
        @DisplayName("policyNotFound를 404 MappedError로 변환한다")
        void map_PolicyNotFound_Returns404() {
            // given
            RefundPolicyException ex = RefundPolicyException.policyNotFound();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Refund Policy Error");
            assertThat(result.type().toString()).startsWith("/errors/refund-policy/");
        }

        @Test
        @DisplayName("policyInactive를 400 MappedError로 변환한다")
        void map_PolicyInactive_Returns400() {
            // given
            RefundPolicyException ex = RefundPolicyException.policyInactive();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Refund Policy Error");
            assertThat(result.type().toString()).contains("/errors/refund-policy/");
        }

        @Test
        @DisplayName("DefaultRefundPolicyNotFoundException(RFP-015)를 400 MappedError로 변환한다")
        void map_DefaultRefundPolicyNotFound_Returns400() {
            // given
            var ex =
                    new com.ryuqq.marketplace.domain.refundpolicy.exception
                            .DefaultRefundPolicyNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Refund Policy Error");
            assertThat(result.detail()).contains("기본 환불 정책이 없습니다");
            assertThat(result.type().toString()).contains("rfp-015");
        }
    }
}
