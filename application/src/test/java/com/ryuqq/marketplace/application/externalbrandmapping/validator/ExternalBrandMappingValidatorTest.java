package com.ryuqq.marketplace.application.externalbrandmapping.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingReadManager;
import com.ryuqq.marketplace.domain.externalbrandmapping.ExternalBrandMappingFixtures;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.exception.ExternalBrandMappingDuplicateException;
import com.ryuqq.marketplace.domain.externalbrandmapping.exception.ExternalBrandMappingNotFoundException;
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
@DisplayName("ExternalBrandMappingValidator 단위 테스트")
class ExternalBrandMappingValidatorTest {

    @InjectMocks private ExternalBrandMappingValidator sut;

    @Mock private ExternalBrandMappingReadManager readManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재하는 매핑 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 ID로 매핑을 반환한다")
        void findExistingOrThrow_Exists_ReturnsMapping() {
            // given
            long id = 1L;
            ExternalBrandMapping expected = ExternalBrandMappingFixtures.activeMapping(id);

            given(readManager.getById(id)).willReturn(expected);

            // when
            ExternalBrandMapping result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            long id = 999L;

            given(readManager.getById(id)).willThrow(new ExternalBrandMappingNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(ExternalBrandMappingNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicate() - 단건 중복 검증")
    class ValidateNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 코드는 예외 없이 통과한다")
        void validateNotDuplicate_NoDuplicate_NoException() {
            // given
            long externalSourceId = 1L;
            String externalBrandCode = "BR_NEW";

            given(
                            readManager.findByExternalSourceIdAndCodes(
                                    externalSourceId, List.of(externalBrandCode)))
                    .willReturn(List.of());

            // when & then (no exception)
            sut.validateNotDuplicate(externalSourceId, externalBrandCode);
        }

        @Test
        @DisplayName("중복된 코드가 있으면 예외가 발생한다")
        void validateNotDuplicate_Duplicate_ThrowsException() {
            // given
            long externalSourceId = 1L;
            String externalBrandCode = "BR001";

            given(
                            readManager.findByExternalSourceIdAndCodes(
                                    externalSourceId, List.of(externalBrandCode)))
                    .willReturn(List.of(ExternalBrandMappingFixtures.activeMapping()));

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicate(externalSourceId, externalBrandCode))
                    .isInstanceOf(ExternalBrandMappingDuplicateException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicateBulk() - 벌크 중복 검증")
    class ValidateNotDuplicateBulkTest {

        @Test
        @DisplayName("중복되지 않은 코드 목록은 예외 없이 통과한다")
        void validateNotDuplicateBulk_NoDuplicate_NoException() {
            // given
            long externalSourceId = 1L;
            List<String> codes = List.of("BR_NEW_1", "BR_NEW_2");

            given(readManager.findByExternalSourceIdAndCodes(externalSourceId, codes))
                    .willReturn(List.of());

            // when & then (no exception)
            sut.validateNotDuplicateBulk(externalSourceId, codes);
        }

        @Test
        @DisplayName("중복된 코드가 포함된 경우 예외가 발생한다")
        void validateNotDuplicateBulk_WithDuplicates_ThrowsException() {
            // given
            long externalSourceId = 1L;
            List<String> codes = List.of("BR001", "BR002");

            given(readManager.findByExternalSourceIdAndCodes(externalSourceId, codes))
                    .willReturn(
                            List.of(
                                    ExternalBrandMappingFixtures.activeMapping(
                                            1L, 1L, "BR001", 100L)));

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicateBulk(externalSourceId, codes))
                    .isInstanceOf(ExternalBrandMappingDuplicateException.class);
        }
    }
}
