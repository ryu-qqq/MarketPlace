package com.ryuqq.marketplace.domain.productintelligence.exception;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisStatus;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductIntelligence 예외 단위 테스트")
class ProductIntelligenceExceptionTest {

    @Nested
    @DisplayName("ProductIntelligenceErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("PROFILE_NOT_FOUND 에러 코드가 올바르다")
        void profileNotFound() {
            ProductIntelligenceErrorCode code = ProductIntelligenceErrorCode.PROFILE_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("PI-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("상품 프로파일");
        }

        @Test
        @DisplayName("INVALID_PROFILE_STATE 에러 코드가 올바르다")
        void invalidProfileState() {
            ProductIntelligenceErrorCode code = ProductIntelligenceErrorCode.INVALID_PROFILE_STATE;

            assertThat(code.getCode()).isEqualTo("PI-002");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("프로파일 상태");
        }

        @Test
        @DisplayName("ANALYSIS_ALREADY_COMPLETED 에러 코드가 올바르다")
        void analysisAlreadyCompleted() {
            ProductIntelligenceErrorCode code =
                    ProductIntelligenceErrorCode.ANALYSIS_ALREADY_COMPLETED;

            assertThat(code.getCode()).isEqualTo("PI-003");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("완료된 분석");
        }

        @Test
        @DisplayName("ANALYSIS_NOT_ALL_COMPLETED 에러 코드가 올바르다")
        void analysisNotAllCompleted() {
            ProductIntelligenceErrorCode code =
                    ProductIntelligenceErrorCode.ANALYSIS_NOT_ALL_COMPLETED;

            assertThat(code.getCode()).isEqualTo("PI-004");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("완료되지 않았습니다");
        }

        @Test
        @DisplayName("INVALID_OUTBOX_STATE 에러 코드가 올바르다")
        void invalidOutboxState() {
            ProductIntelligenceErrorCode code = ProductIntelligenceErrorCode.INVALID_OUTBOX_STATE;

            assertThat(code.getCode()).isEqualTo("PI-100");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("아웃박스 상태");
        }
    }

    @Nested
    @DisplayName("ProductIntelligenceException 예외 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("ErrorCode만으로 예외를 생성한다")
        void createExceptionWithErrorCode() {
            ProductIntelligenceException exception =
                    new ProductIntelligenceException(
                            ProductIntelligenceErrorCode.PROFILE_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("PI-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("커스텀 메시지로 예외를 생성한다")
        void createExceptionWithCustomMessage() {
            ProductIntelligenceException exception =
                    new ProductIntelligenceException(
                            ProductIntelligenceErrorCode.INVALID_PROFILE_STATE,
                            "PENDING 상태에서 COMPLETED로 직접 전환 불가");

            assertThat(exception.getMessage()).isEqualTo("PENDING 상태에서 COMPLETED로 직접 전환 불가");
            assertThat(exception.code()).isEqualTo("PI-002");
        }

        @Test
        @DisplayName("원인 예외로 예외를 생성한다")
        void createExceptionWithCause() {
            RuntimeException cause = new RuntimeException("원인 예외");

            ProductIntelligenceException exception =
                    new ProductIntelligenceException(
                            ProductIntelligenceErrorCode.PROFILE_NOT_FOUND, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("PI-001");
        }
    }

    @Nested
    @DisplayName("InvalidProfileStateException 테스트")
    class InvalidProfileStateExceptionTest {

        @Test
        @DisplayName("현재 상태와 액션 정보로 예외 메시지를 생성한다")
        void createWithStatusAndAction() {
            InvalidProfileStateException exception =
                    new InvalidProfileStateException(AnalysisStatus.PENDING, "COMPLETED로 전환");

            assertThat(exception.getMessage()).contains("COMPLETED로 전환").contains("PENDING");
            assertThat(exception.code()).isEqualTo("PI-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("InvalidProfileStateException은 ProductIntelligenceException이다")
        void isProductIntelligenceException() {
            InvalidProfileStateException exception =
                    new InvalidProfileStateException(AnalysisStatus.ANALYZING, "액션");

            assertThat(exception).isInstanceOf(ProductIntelligenceException.class);
        }
    }

    @Nested
    @DisplayName("AnalysisAlreadyCompletedException 테스트")
    class AnalysisAlreadyCompletedExceptionTest {

        @Test
        @DisplayName("분석 타입 정보로 예외 메시지를 생성한다")
        void createWithAnalysisType() {
            AnalysisAlreadyCompletedException exception =
                    new AnalysisAlreadyCompletedException(AnalysisType.DESCRIPTION);

            assertThat(exception.getMessage()).contains("DESCRIPTION").contains("완료");
            assertThat(exception.code()).isEqualTo("PI-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("OPTION 타입 완료 예외를 생성한다")
        void createWithOptionType() {
            AnalysisAlreadyCompletedException exception =
                    new AnalysisAlreadyCompletedException(AnalysisType.OPTION);

            assertThat(exception.getMessage()).contains("OPTION");
        }

        @Test
        @DisplayName("AnalysisAlreadyCompletedException은 ProductIntelligenceException이다")
        void isProductIntelligenceException() {
            AnalysisAlreadyCompletedException exception =
                    new AnalysisAlreadyCompletedException(AnalysisType.NOTICE);

            assertThat(exception).isInstanceOf(ProductIntelligenceException.class);
        }
    }

    @Nested
    @DisplayName("AnalysisNotAllCompletedException 테스트")
    class AnalysisNotAllCompletedExceptionTest {

        @Test
        @DisplayName("완료/기대 카운트 정보로 예외 메시지를 생성한다")
        void createWithCounts() {
            AnalysisNotAllCompletedException exception = new AnalysisNotAllCompletedException(2, 3);

            assertThat(exception.getMessage()).contains("2").contains("3");
            assertThat(exception.code()).isEqualTo("PI-004");
            assertThat(exception.httpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("AnalysisNotAllCompletedException은 ProductIntelligenceException이다")
        void isProductIntelligenceException() {
            AnalysisNotAllCompletedException exception = new AnalysisNotAllCompletedException(0, 3);

            assertThat(exception).isInstanceOf(ProductIntelligenceException.class);
        }
    }

    @Nested
    @DisplayName("InvalidOutboxStateException 테스트")
    class InvalidOutboxStateExceptionTest {

        @Test
        @DisplayName("현재 상태와 액션 정보로 예외 메시지를 생성한다")
        void createWithStatusAndAction() {
            InvalidOutboxStateException exception =
                    new InvalidOutboxStateException("COMPLETED", "SENT로 전환");

            assertThat(exception.getMessage()).contains("SENT로 전환").contains("COMPLETED");
            assertThat(exception.code()).isEqualTo("PI-100");
            assertThat(exception.httpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("InvalidOutboxStateException은 ProductIntelligenceException이다")
        void isProductIntelligenceException() {
            InvalidOutboxStateException exception =
                    new InvalidOutboxStateException("PENDING", "액션");

            assertThat(exception).isInstanceOf(ProductIntelligenceException.class);
        }
    }

    @Nested
    @DisplayName("ProductProfileNotFoundException 테스트")
    class ProductProfileNotFoundExceptionTest {

        @Test
        @DisplayName("ProfileId 정보로 예외 메시지를 생성한다")
        void createWithProfileId() {
            ProductProfileNotFoundException exception = new ProductProfileNotFoundException(999L);

            assertThat(exception.getMessage()).contains("999");
            assertThat(exception.code()).isEqualTo("PI-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ProductProfileNotFoundException은 ProductIntelligenceException이다")
        void isProductIntelligenceException() {
            ProductProfileNotFoundException exception = new ProductProfileNotFoundException(1L);

            assertThat(exception).isInstanceOf(ProductIntelligenceException.class);
        }
    }
}
