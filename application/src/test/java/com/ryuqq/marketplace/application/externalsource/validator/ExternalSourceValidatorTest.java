package com.ryuqq.marketplace.application.externalsource.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.application.externalsource.port.out.query.ExternalSourceQueryPort;
import com.ryuqq.marketplace.domain.externalsource.ExternalSourceFixtures;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.exception.ExternalSourceDuplicateException;
import com.ryuqq.marketplace.domain.externalsource.exception.ExternalSourceNotFoundException;
import java.util.Optional;
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
@DisplayName("ExternalSourceValidator 단위 테스트")
class ExternalSourceValidatorTest {

    @InjectMocks private ExternalSourceValidator sut;

    @Mock private ExternalSourceReadManager readManager;
    @Mock private ExternalSourceQueryPort queryPort;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재하는 소스 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 ID로 ExternalSource를 반환한다")
        void findExistingOrThrow_Exists_ReturnsExternalSource() {
            // given
            long id = 1L;
            ExternalSource expected = ExternalSourceFixtures.activeExternalSource(id);

            given(readManager.getById(id)).willReturn(expected);

            // when
            ExternalSource result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            long id = 999L;

            given(readManager.getById(id)).willThrow(new ExternalSourceNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(ExternalSourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateCodeNotDuplicate() - 코드 중복 검증")
    class ValidateCodeNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 코드는 예외 없이 통과한다")
        void validateCodeNotDuplicate_NoDuplicate_NoException() {
            // given
            String code = "NEW_SOURCE";

            given(queryPort.findByCode(code)).willReturn(Optional.empty());

            // when & then (no exception)
            sut.validateCodeNotDuplicate(code);
        }

        @Test
        @DisplayName("중복된 코드가 있으면 예외가 발생한다")
        void validateCodeNotDuplicate_Duplicate_ThrowsException() {
            // given
            String code = "SETOF";

            given(queryPort.findByCode(code))
                    .willReturn(Optional.of(ExternalSourceFixtures.activeExternalSource()));

            // when & then
            assertThatThrownBy(() -> sut.validateCodeNotDuplicate(code))
                    .isInstanceOf(ExternalSourceDuplicateException.class);
        }
    }
}
