package com.ryuqq.marketplace.application.commoncodetype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.commoncodetype.port.out.query.CommonCodeTypeQueryPort;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeException;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import java.util.List;
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
@DisplayName("CommonCodeTypeReadManager 단위 테스트")
class CommonCodeTypeReadManagerTest {

    @InjectMocks private CommonCodeTypeReadManager sut;

    @Mock private CommonCodeTypeQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 공통 코드 타입을 조회한다")
        void getById_Exists_ReturnsCommonCodeType() {
            // given
            CommonCodeTypeId id = CommonCodeTypeId.of(1L);
            CommonCodeType expected = CommonCodeTypeFixtures.activeCommonCodeType();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            CommonCodeType result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            CommonCodeTypeId id = CommonCodeTypeId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(CommonCodeTypeException.class);
        }
    }

    @Nested
    @DisplayName("getByIds() - ID 목록으로 조회")
    class GetByIdsTest {

        @Test
        @DisplayName("ID 목록으로 공통 코드 타입들을 조회한다")
        void getByIds_ReturnsCommonCodeTypes() {
            // given
            List<CommonCodeTypeId> ids = List.of(CommonCodeTypeId.of(1L), CommonCodeTypeId.of(2L));
            List<CommonCodeType> expected =
                    List.of(
                            CommonCodeTypeFixtures.activeCommonCodeType(1L),
                            CommonCodeTypeFixtures.activeCommonCodeType(2L));

            given(queryPort.findByIds(ids)).willReturn(expected);

            // when
            List<CommonCodeType> result = sut.getByIds(ids);

            // then
            assertThat(result).hasSize(2);
            then(queryPort).should().findByIds(ids);
        }
    }

    @Nested
    @DisplayName("existsByCode() - 코드 존재 여부 확인")
    class ExistsByCodeTest {

        @Test
        @DisplayName("존재하는 코드면 true를 반환한다")
        void existsByCode_Exists_ReturnsTrue() {
            // given
            String code = "PAYMENT_METHOD";

            given(queryPort.existsByCode(code)).willReturn(true);

            // when
            boolean result = sut.existsByCode(code);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 코드면 false를 반환한다")
        void existsByCode_NotExists_ReturnsFalse() {
            // given
            String code = "NOT_EXISTS";

            given(queryPort.existsByCode(code)).willReturn(false);

            // when
            boolean result = sut.existsByCode(code);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByDisplayOrderExcludingId() - 표시 순서 중복 확인")
    class ExistsByDisplayOrderExcludingIdTest {

        @Test
        @DisplayName("중복된 표시 순서가 존재하면 true를 반환한다")
        void existsByDisplayOrder_Duplicate_ReturnsTrue() {
            // given
            int displayOrder = 1;
            Long excludeId = 2L;

            given(queryPort.existsByDisplayOrderExcludingId(displayOrder, excludeId))
                    .willReturn(true);

            // when
            boolean result = sut.existsByDisplayOrderExcludingId(displayOrder, excludeId);

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 공통 코드 타입 목록을 조회한다")
        void findByCriteria_ReturnsCommonCodeTypes() {
            // given
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();
            List<CommonCodeType> expected = List.of(CommonCodeTypeFixtures.activeCommonCodeType());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<CommonCodeType> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 공통 코드 타입 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            CommonCodeTypeSearchCriteria criteria = CommonCodeTypeSearchCriteria.defaultCriteria();
            long expected = 10L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }
    }
}
