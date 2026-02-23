package com.ryuqq.marketplace.application.inboundbrandmapping.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingReadManager;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingDuplicateException;
import com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingNotFoundException;
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
@DisplayName("InboundBrandMappingValidator 단위 테스트")
class InboundBrandMappingValidatorTest {

    @InjectMocks private InboundBrandMappingValidator sut;

    @Mock private InboundBrandMappingReadManager readManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재하는 매핑 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 ID로 매핑을 반환한다")
        void findExistingOrThrow_Exists_ReturnsMapping() {
            // given
            long id = 1L;
            InboundBrandMapping expected = InboundBrandMappingFixtures.activeMapping(id);

            given(readManager.getById(id)).willReturn(expected);

            // when
            InboundBrandMapping result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            long id = 999L;

            given(readManager.getById(id)).willThrow(new InboundBrandMappingNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(InboundBrandMappingNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicate() - 단건 중복 검증")
    class ValidateNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 코드는 예외 없이 통과한다")
        void validateNotDuplicate_NoDuplicate_NoException() {
            // given
            long inboundSourceId = 1L;
            String externalBrandCode = "BR_NEW";

            given(
                            readManager.findByInboundSourceIdAndCodes(
                                    inboundSourceId, List.of(externalBrandCode)))
                    .willReturn(List.of());

            // when & then (no exception)
            sut.validateNotDuplicate(inboundSourceId, externalBrandCode);
        }

        @Test
        @DisplayName("중복된 코드가 있으면 예외가 발생한다")
        void validateNotDuplicate_Duplicate_ThrowsException() {
            // given
            long inboundSourceId = 1L;
            String externalBrandCode = "BR001";

            given(
                            readManager.findByInboundSourceIdAndCodes(
                                    inboundSourceId, List.of(externalBrandCode)))
                    .willReturn(List.of(InboundBrandMappingFixtures.activeMapping()));

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicate(inboundSourceId, externalBrandCode))
                    .isInstanceOf(InboundBrandMappingDuplicateException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicateBulk() - 벌크 중복 검증")
    class ValidateNotDuplicateBulkTest {

        @Test
        @DisplayName("중복되지 않은 코드 목록은 예외 없이 통과한다")
        void validateNotDuplicateBulk_NoDuplicate_NoException() {
            // given
            long inboundSourceId = 1L;
            List<String> codes = List.of("BR_NEW_1", "BR_NEW_2");

            given(readManager.findByInboundSourceIdAndCodes(inboundSourceId, codes))
                    .willReturn(List.of());

            // when & then (no exception)
            sut.validateNotDuplicateBulk(inboundSourceId, codes);
        }

        @Test
        @DisplayName("중복된 코드가 포함된 경우 예외가 발생한다")
        void validateNotDuplicateBulk_WithDuplicates_ThrowsException() {
            // given
            long inboundSourceId = 1L;
            List<String> codes = List.of("BR001", "BR002");

            given(readManager.findByInboundSourceIdAndCodes(inboundSourceId, codes))
                    .willReturn(
                            List.of(
                                    InboundBrandMappingFixtures.activeMapping(
                                            1L, 1L, "BR001", 100L)));

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicateBulk(inboundSourceId, codes))
                    .isInstanceOf(InboundBrandMappingDuplicateException.class);
        }
    }
}
