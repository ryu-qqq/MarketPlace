package com.ryuqq.marketplace.application.inboundbrandmapping.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundbrandmapping.port.out.query.InboundBrandMappingQueryPort;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingNotFoundException;
import com.ryuqq.marketplace.domain.inboundbrandmapping.id.InboundBrandMappingId;
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
@DisplayName("InboundBrandMappingReadManager 단위 테스트")
class InboundBrandMappingReadManagerTest {

    @InjectMocks private InboundBrandMappingReadManager sut;

    @Mock private InboundBrandMappingQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 InboundBrandMapping을 조회한다")
        void getById_Exists_ReturnsMapping() {
            // given
            long id = 1L;
            InboundBrandMappingId mappingId = InboundBrandMappingId.of(id);
            InboundBrandMapping expected = InboundBrandMappingFixtures.activeMapping(id);

            given(queryPort.findById(mappingId)).willReturn(Optional.of(expected));

            // when
            InboundBrandMapping result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(mappingId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            long id = 999L;
            InboundBrandMappingId mappingId = InboundBrandMappingId.of(id);

            given(queryPort.findById(mappingId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(InboundBrandMappingNotFoundException.class);
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
            String externalBrandCode = "BR001";
            InboundBrandMapping expected = InboundBrandMappingFixtures.activeMapping();

            given(
                            queryPort.findByInboundSourceIdAndExternalBrandCode(
                                    inboundSourceId, externalBrandCode))
                    .willReturn(Optional.of(expected));

            // when
            InboundBrandMapping result =
                    sut.findBySourceIdAndCode(inboundSourceId, externalBrandCode);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 소스ID+코드로 조회 시 예외가 발생한다")
        void findBySourceIdAndCode_NotExists_ThrowsException() {
            // given
            long inboundSourceId = 1L;
            String externalBrandCode = "NOTEXIST";

            given(
                            queryPort.findByInboundSourceIdAndExternalBrandCode(
                                    inboundSourceId, externalBrandCode))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findBySourceIdAndCode(inboundSourceId, externalBrandCode))
                    .isInstanceOf(InboundBrandMappingNotFoundException.class);
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
            List<InboundBrandMapping> expected =
                    List.of(
                            InboundBrandMappingFixtures.activeMapping(1L),
                            InboundBrandMappingFixtures.activeMapping(2L));

            given(queryPort.findByInboundSourceId(inboundSourceId)).willReturn(expected);

            // when
            List<InboundBrandMapping> result = sut.findByInboundSourceId(inboundSourceId);

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
            List<String> codes = List.of("BR001", "BR002");
            List<InboundBrandMapping> expected =
                    List.of(
                            InboundBrandMappingFixtures.activeMapping(1L, 1L, "BR001", 100L),
                            InboundBrandMappingFixtures.activeMapping(2L, 1L, "BR002", 200L));

            given(queryPort.findByInboundSourceIdAndExternalBrandCodes(inboundSourceId, codes))
                    .willReturn(expected);

            // when
            List<InboundBrandMapping> result =
                    sut.findByInboundSourceIdAndCodes(inboundSourceId, codes);

            // then
            assertThat(result).hasSize(2);
            then(queryPort)
                    .should()
                    .findByInboundSourceIdAndExternalBrandCodes(inboundSourceId, codes);
        }

        @Test
        @DisplayName("매핑이 없으면 빈 목록을 반환한다")
        void findByInboundSourceIdAndCodes_NotExists_ReturnsEmptyList() {
            // given
            long inboundSourceId = 1L;
            List<String> codes = List.of("NOTEXIST");

            given(queryPort.findByInboundSourceIdAndExternalBrandCodes(inboundSourceId, codes))
                    .willReturn(List.of());

            // when
            List<InboundBrandMapping> result =
                    sut.findByInboundSourceIdAndCodes(inboundSourceId, codes);

            // then
            assertThat(result).isEmpty();
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
                            com.ryuqq.marketplace.domain.inboundbrandmapping.query
                                    .InboundBrandMappingSearchCriteria.class);
            List<InboundBrandMapping> expected =
                    List.of(InboundBrandMappingFixtures.activeMapping());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<InboundBrandMapping> result = sut.findByCriteria(criteria);

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
                            com.ryuqq.marketplace.domain.inboundbrandmapping.query
                                    .InboundBrandMappingSearchCriteria.class);
            long expected = 5L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }
    }
}
