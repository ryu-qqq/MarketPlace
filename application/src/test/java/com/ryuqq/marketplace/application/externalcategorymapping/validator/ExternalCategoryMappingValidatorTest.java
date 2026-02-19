package com.ryuqq.marketplace.application.externalcategorymapping.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingReadManager;
import com.ryuqq.marketplace.domain.externalcategorymapping.ExternalCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.exception.ExternalCategoryMappingDuplicateException;
import com.ryuqq.marketplace.domain.externalcategorymapping.exception.ExternalCategoryMappingNotFoundException;
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
@DisplayName("ExternalCategoryMappingValidator 단위 테스트")
class ExternalCategoryMappingValidatorTest {

    @InjectMocks private ExternalCategoryMappingValidator sut;

    @Mock private ExternalCategoryMappingReadManager readManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재하는 매핑 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 ID로 매핑을 반환한다")
        void findExistingOrThrow_Exists_ReturnsMapping() {
            // given
            long id = 1L;
            ExternalCategoryMapping expected = ExternalCategoryMappingFixtures.activeMapping(id);

            given(readManager.getById(id)).willReturn(expected);

            // when
            ExternalCategoryMapping result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            long id = 999L;

            given(readManager.getById(id))
                    .willThrow(new ExternalCategoryMappingNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(ExternalCategoryMappingNotFoundException.class);
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
            String externalCategoryCode = "CAT_NEW";

            given(
                            readManager.findByExternalSourceIdAndCodes(
                                    externalSourceId, List.of(externalCategoryCode)))
                    .willReturn(List.of());

            // when & then (no exception)
            sut.validateNotDuplicate(externalSourceId, externalCategoryCode);
        }

        @Test
        @DisplayName("중복된 코드가 있으면 예외가 발생한다")
        void validateNotDuplicate_Duplicate_ThrowsException() {
            // given
            long externalSourceId = 1L;
            String externalCategoryCode = "CAT_SHOES_001";

            given(
                            readManager.findByExternalSourceIdAndCodes(
                                    externalSourceId, List.of(externalCategoryCode)))
                    .willReturn(List.of(ExternalCategoryMappingFixtures.activeMapping()));

            // when & then
            assertThatThrownBy(
                            () -> sut.validateNotDuplicate(externalSourceId, externalCategoryCode))
                    .isInstanceOf(ExternalCategoryMappingDuplicateException.class);
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
            List<String> codes = List.of("CAT_NEW_1", "CAT_NEW_2");

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
            List<String> codes = List.of("CAT_SHOES_001", "CAT_BAG_001");

            given(readManager.findByExternalSourceIdAndCodes(externalSourceId, codes))
                    .willReturn(
                            List.of(
                                    ExternalCategoryMappingFixtures.activeMapping(
                                            1L, 1L, "CAT_SHOES_001", 100L)));

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicateBulk(externalSourceId, codes))
                    .isInstanceOf(ExternalCategoryMappingDuplicateException.class);
        }
    }
}
