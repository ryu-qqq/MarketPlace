package com.ryuqq.marketplace.application.inboundcategorymapping.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundcategorymapping.port.out.query.InboundCategoryMappingQueryPort;
import com.ryuqq.marketplace.domain.inboundcategorymapping.InboundCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.exception.InboundCategoryMappingNotFoundException;
import com.ryuqq.marketplace.domain.inboundcategorymapping.id.InboundCategoryMappingId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("InboundCategoryMappingReadManager 단위 테스트")
class InboundCategoryMappingReadManagerTest {

    @InjectMocks private InboundCategoryMappingReadManager sut;

    @Mock private InboundCategoryMappingQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 InboundCategoryMapping을 조회한다")
        void getById_Exists_ReturnsMapping() {
            // given
            long id = 1L;
            InboundCategoryMappingId mappingId = InboundCategoryMappingId.of(id);
            InboundCategoryMapping expected = InboundCategoryMappingFixtures.activeMapping(id);

            given(queryPort.findById(mappingId)).willReturn(Optional.of(expected));

            // when
            InboundCategoryMapping result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(mappingId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            long id = 999L;
            InboundCategoryMappingId mappingId = InboundCategoryMappingId.of(id);

            given(queryPort.findById(mappingId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(InboundCategoryMappingNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findBySourceIdAndCode() - 소스ID+코드로 조회")
    class FindBySourceIdAndCodeTest {

        @Test
        @DisplayName("존재하는 소스ID와 코드로 매핑을 조회한다")
        void findBySourceIdAndCode_Exists_ReturnsMapping() {
            // given
            long inboundSourceId = 1L;
            String externalCategoryCode = "CAT_SHOES_001";
            InboundCategoryMapping expected = InboundCategoryMappingFixtures.activeMapping();

            given(
                            queryPort.findByInboundSourceIdAndExternalCategoryCode(
                                    inboundSourceId, externalCategoryCode))
                    .willReturn(Optional.of(expected));

            // when
            InboundCategoryMapping result =
                    sut.findBySourceIdAndCode(inboundSourceId, externalCategoryCode);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 소스ID+코드로 조회 시 예외가 발생한다")
        void findBySourceIdAndCode_NotExists_ThrowsException() {
            // given
            long inboundSourceId = 1L;
            String externalCategoryCode = "NOT_EXIST";

            given(
                            queryPort.findByInboundSourceIdAndExternalCategoryCode(
                                    inboundSourceId, externalCategoryCode))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> sut.findBySourceIdAndCode(inboundSourceId, externalCategoryCode))
                    .isInstanceOf(InboundCategoryMappingNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByInboundSourceId() - 외부 소스 ID로 목록 조회")
    class FindByInboundSourceIdTest {

        @Test
        @DisplayName("외부 소스 ID로 매핑 목록을 조회한다")
        void findByInboundSourceId_ReturnsMappings() {
            // given
            long inboundSourceId = 1L;
            List<InboundCategoryMapping> expected =
                    List.of(
                            InboundCategoryMappingFixtures.activeMapping(1L),
                            InboundCategoryMappingFixtures.activeMapping(2L));

            given(queryPort.findByInboundSourceId(inboundSourceId)).willReturn(expected);

            // when
            List<InboundCategoryMapping> result = sut.findByInboundSourceId(inboundSourceId);

            // then
            assertThat(result).hasSize(2);
            then(queryPort).should().findByInboundSourceId(inboundSourceId);
        }
    }

    @Nested
    @DisplayName("findByInboundSourceIdAndCodes() - 소스ID+코드 목록으로 조회")
    class FindByInboundSourceIdAndCodesTest {

        @Test
        @DisplayName("소스ID와 코드 목록으로 매핑 목록을 조회한다")
        void findByInboundSourceIdAndCodes_ReturnsMappings() {
            // given
            long inboundSourceId = 1L;
            List<String> codes = List.of("CAT_SHOES_001", "CAT_BAG_001");
            List<InboundCategoryMapping> expected =
                    List.of(
                            InboundCategoryMappingFixtures.activeMapping(
                                    1L, 1L, "CAT_SHOES_001", 100L),
                            InboundCategoryMappingFixtures.activeMapping(
                                    2L, 1L, "CAT_BAG_001", 200L));

            given(queryPort.findByInboundSourceIdAndExternalCategoryCodes(inboundSourceId, codes))
                    .willReturn(expected);

            // when
            List<InboundCategoryMapping> result =
                    sut.findByInboundSourceIdAndCodes(inboundSourceId, codes);

            // then
            assertThat(result).hasSize(2);
            then(queryPort)
                    .should()
                    .findByInboundSourceIdAndExternalCategoryCodes(inboundSourceId, codes);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 매핑 목록을 조회한다")
        void findByCriteria_ReturnsMappings() {
            // given
            var criteria =
                    Mockito.mock(
                            com.ryuqq.marketplace.domain.inboundcategorymapping.query
                                    .InboundCategoryMappingSearchCriteria.class);
            List<InboundCategoryMapping> expected =
                    List.of(InboundCategoryMappingFixtures.activeMapping());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<InboundCategoryMapping> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 매핑 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            var criteria =
                    Mockito.mock(
                            com.ryuqq.marketplace.domain.inboundcategorymapping.query
                                    .InboundCategoryMappingSearchCriteria.class);
            long expected = 3L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }
    }
}
