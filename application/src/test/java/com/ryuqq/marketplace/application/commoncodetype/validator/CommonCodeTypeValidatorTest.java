package com.ryuqq.marketplace.application.commoncodetype.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeReadManager;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeReadManager;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeException;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeNotFoundException;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
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
@DisplayName("CommonCodeTypeValidator 단위 테스트")
class CommonCodeTypeValidatorTest {

    @InjectMocks private CommonCodeTypeValidator sut;

    @Mock private CommonCodeTypeReadManager commonCodeTypeReadManager;

    @Mock private CommonCodeReadManager commonCodeReadManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재 검증 후 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 ID로 조회하면 도메인 객체를 반환한다")
        void findExistingOrThrow_Exists_ReturnsDomain() {
            // given
            CommonCodeTypeId id = CommonCodeTypeId.of(1L);
            CommonCodeType expected = CommonCodeTypeFixtures.activeCommonCodeType();

            given(commonCodeTypeReadManager.getById(id)).willReturn(expected);

            // when
            CommonCodeType result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(commonCodeTypeReadManager).should().getById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            CommonCodeTypeId id = CommonCodeTypeId.of(999L);

            given(commonCodeTypeReadManager.getById(id))
                    .willThrow(new CommonCodeTypeNotFoundException(999L));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(CommonCodeTypeException.class);
        }
    }

    @Nested
    @DisplayName("findAllExistingOrThrow() - 일괄 존재 검증 후 조회")
    class FindAllExistingOrThrowTest {

        @Test
        @DisplayName("빈 ID 목록이면 빈 리스트를 반환한다")
        void findAllExistingOrThrow_EmptyIds_ReturnsEmptyList() {
            // given
            List<CommonCodeTypeId> ids = List.of();

            // when
            List<CommonCodeType> result = sut.findAllExistingOrThrow(ids);

            // then
            assertThat(result).isEmpty();
            then(commonCodeTypeReadManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("모든 ID가 존재하면 도메인 객체 목록을 반환한다")
        void findAllExistingOrThrow_AllExists_ReturnsDomains() {
            // given
            List<CommonCodeTypeId> ids = List.of(CommonCodeTypeId.of(1L), CommonCodeTypeId.of(2L));
            List<CommonCodeType> found =
                    List.of(
                            CommonCodeTypeFixtures.activeCommonCodeType(1L),
                            CommonCodeTypeFixtures.activeCommonCodeType(2L));

            given(commonCodeTypeReadManager.getByIds(ids)).willReturn(found);

            // when
            List<CommonCodeType> result = sut.findAllExistingOrThrow(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("일부 ID가 존재하지 않으면 예외가 발생한다")
        void findAllExistingOrThrow_PartialExists_ThrowsException() {
            // given
            List<CommonCodeTypeId> ids =
                    List.of(CommonCodeTypeId.of(1L), CommonCodeTypeId.of(999L));
            List<CommonCodeType> found =
                    List.of(CommonCodeTypeFixtures.activeCommonCodeType(1L)); // 999L은 없음

            given(commonCodeTypeReadManager.getByIds(ids)).willReturn(found);

            // when & then
            assertThatThrownBy(() -> sut.findAllExistingOrThrow(ids))
                    .isInstanceOf(CommonCodeTypeException.class);
        }
    }

    @Nested
    @DisplayName("validateCodeNotDuplicate() - 코드 중복 검증")
    class ValidateCodeNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 코드면 예외가 발생하지 않는다")
        void validateCodeNotDuplicate_Unique_NoException() {
            // given
            String code = "UNIQUE_CODE";

            given(commonCodeTypeReadManager.existsByCode(code)).willReturn(false);

            // when & then
            sut.validateCodeNotDuplicate(code);
            then(commonCodeTypeReadManager).should().existsByCode(code);
        }

        @Test
        @DisplayName("중복된 코드면 예외가 발생한다")
        void validateCodeNotDuplicate_Duplicate_ThrowsException() {
            // given
            String code = "DUPLICATE_CODE";

            given(commonCodeTypeReadManager.existsByCode(code)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateCodeNotDuplicate(code))
                    .isInstanceOf(CommonCodeTypeException.class);
        }
    }

    @Nested
    @DisplayName("validateDisplayOrderNotDuplicate() - 표시 순서 중복 검증")
    class ValidateDisplayOrderNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 표시 순서면 예외가 발생하지 않는다")
        void validateDisplayOrderNotDuplicate_Unique_NoException() {
            // given
            int displayOrder = 1;
            Long excludeId = 1L;

            given(
                            commonCodeTypeReadManager.existsByDisplayOrderExcludingId(
                                    displayOrder, excludeId))
                    .willReturn(false);

            // when & then
            sut.validateDisplayOrderNotDuplicate(displayOrder, excludeId);
            then(commonCodeTypeReadManager)
                    .should()
                    .existsByDisplayOrderExcludingId(displayOrder, excludeId);
        }

        @Test
        @DisplayName("중복된 표시 순서면 예외가 발생한다")
        void validateDisplayOrderNotDuplicate_Duplicate_ThrowsException() {
            // given
            int displayOrder = 1;
            Long excludeId = 2L;

            given(
                            commonCodeTypeReadManager.existsByDisplayOrderExcludingId(
                                    displayOrder, excludeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateDisplayOrderNotDuplicate(displayOrder, excludeId))
                    .isInstanceOf(CommonCodeTypeException.class);
        }
    }

    @Nested
    @DisplayName("validateNoActiveCommonCodes() - 활성 하위 코드 존재 검증")
    class ValidateNoActiveCommonCodesTest {

        @Test
        @DisplayName("활성화된 하위 코드가 없으면 예외가 발생하지 않는다")
        void validateNoActiveCommonCodes_NoActiveCodes_NoException() {
            // given
            Long commonCodeTypeId = 1L;

            given(commonCodeReadManager.existsActiveByCommonCodeTypeId(commonCodeTypeId))
                    .willReturn(false);

            // when & then
            sut.validateNoActiveCommonCodes(commonCodeTypeId);
            then(commonCodeReadManager).should().existsActiveByCommonCodeTypeId(commonCodeTypeId);
        }

        @Test
        @DisplayName("활성화된 하위 코드가 있으면 예외가 발생한다")
        void validateNoActiveCommonCodes_HasActiveCodes_ThrowsException() {
            // given
            Long commonCodeTypeId = 1L;

            given(commonCodeReadManager.existsActiveByCommonCodeTypeId(commonCodeTypeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNoActiveCommonCodes(commonCodeTypeId))
                    .isInstanceOf(CommonCodeTypeException.class);
        }
    }
}
