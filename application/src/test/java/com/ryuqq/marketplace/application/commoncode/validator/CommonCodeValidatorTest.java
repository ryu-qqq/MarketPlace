package com.ryuqq.marketplace.application.commoncode.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeReadManager;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeReadManager;
import com.ryuqq.marketplace.domain.commoncode.CommonCodeFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.exception.CommonCodeDuplicateException;
import com.ryuqq.marketplace.domain.commoncode.exception.CommonCodeNotFoundException;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.util.Collections;
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
@DisplayName("CommonCodeValidator 단위 테스트")
class CommonCodeValidatorTest {

    @InjectMocks private CommonCodeValidator sut;

    @Mock private CommonCodeReadManager readManager;
    @Mock private CommonCodeTypeReadManager commonCodeTypeReadManager;

    @Nested
    @DisplayName("findExistingOrThrow() - ID로 공통 코드 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 공통 코드를 반환한다")
        void findExistingOrThrow_ReturnsCommonCode() {
            // given
            CommonCodeId id = CommonCodeId.of(1L);
            CommonCode expected = CommonCodeFixtures.activeCommonCode();

            given(readManager.getById(id)).willReturn(expected);

            // when
            CommonCode result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("validateNotDuplicate() - 중복 검증")
    class ValidateNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않으면 검증을 통과한다")
        void validateNotDuplicate_NotDuplicate_Passes() {
            // given
            Long commonCodeTypeId = 1L;
            String code = "NEW_CODE";

            given(readManager.existsByCommonCodeTypeIdAndCode(commonCodeTypeId, code))
                    .willReturn(false);

            // when & then (no exception)
            sut.validateNotDuplicate(commonCodeTypeId, code);
        }

        @Test
        @DisplayName("중복되면 예외를 발생시킨다")
        void validateNotDuplicate_Duplicate_ThrowsException() {
            // given
            Long commonCodeTypeId = 1L;
            String code = "EXISTING_CODE";

            given(readManager.existsByCommonCodeTypeIdAndCode(commonCodeTypeId, code))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNotDuplicate(commonCodeTypeId, code))
                    .isInstanceOf(CommonCodeDuplicateException.class);
        }
    }

    @Nested
    @DisplayName("validateCommonCodeTypeExists() - 타입 존재 검증")
    class ValidateCommonCodeTypeExistsTest {

        @Test
        @DisplayName("타입이 존재하면 검증을 통과한다")
        void validateCommonCodeTypeExists_Exists_Passes() {
            // given
            Long commonCodeTypeId = 1L;
            CommonCodeTypeId typeId = CommonCodeTypeId.of(commonCodeTypeId);

            // when & then (no exception - getById returns if exists)
            sut.validateCommonCodeTypeExists(commonCodeTypeId);

            then(commonCodeTypeReadManager).should().getById(typeId);
        }
    }

    @Nested
    @DisplayName("findAllExistingOrThrow() - ID 목록으로 공통 코드 목록 조회")
    class FindAllExistingOrThrowTest {

        @Test
        @DisplayName("모든 공통 코드를 반환한다")
        void findAllExistingOrThrow_ReturnsAllCommonCodes() {
            // given
            List<CommonCodeId> ids = List.of(CommonCodeId.of(1L), CommonCodeId.of(2L));
            List<CommonCode> codes =
                    List.of(
                            CommonCodeFixtures.activeCommonCode(),
                            CommonCodeFixtures.inactiveCommonCode());

            given(readManager.getByIds(ids)).willReturn(codes);

            // when
            List<CommonCode> result = sut.findAllExistingOrThrow(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("빈 ID 목록이면 빈 결과를 반환한다")
        void findAllExistingOrThrow_EmptyIds_ReturnsEmptyList() {
            // given
            List<CommonCodeId> ids = Collections.emptyList();

            // when
            List<CommonCode> result = sut.findAllExistingOrThrow(ids);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID가 포함되면 예외를 발생시킨다")
        void findAllExistingOrThrow_NotFound_ThrowsException() {
            // given
            List<CommonCodeId> ids = List.of(CommonCodeId.of(1L), CommonCodeId.of(999L));
            List<CommonCode> codes = List.of(CommonCodeFixtures.activeCommonCode());

            given(readManager.getByIds(ids)).willReturn(codes);

            // when & then
            assertThatThrownBy(() -> sut.findAllExistingOrThrow(ids))
                    .isInstanceOf(CommonCodeNotFoundException.class);
        }
    }
}
