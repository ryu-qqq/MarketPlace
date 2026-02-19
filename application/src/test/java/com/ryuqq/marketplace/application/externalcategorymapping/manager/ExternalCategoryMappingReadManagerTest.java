package com.ryuqq.marketplace.application.externalcategorymapping.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.externalcategorymapping.port.out.query.ExternalCategoryMappingQueryPort;
import com.ryuqq.marketplace.domain.externalcategorymapping.ExternalCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.exception.ExternalCategoryMappingNotFoundException;
import com.ryuqq.marketplace.domain.externalcategorymapping.id.ExternalCategoryMappingId;
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
@DisplayName("ExternalCategoryMappingReadManager 단위 테스트")
class ExternalCategoryMappingReadManagerTest {

    @InjectMocks private ExternalCategoryMappingReadManager sut;

    @Mock private ExternalCategoryMappingQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 ExternalCategoryMapping을 조회한다")
        void getById_Exists_ReturnsMapping() {
            // given
            long id = 1L;
            ExternalCategoryMappingId mappingId = ExternalCategoryMappingId.of(id);
            ExternalCategoryMapping expected = ExternalCategoryMappingFixtures.activeMapping(id);

            given(queryPort.findById(mappingId)).willReturn(Optional.of(expected));

            // when
            ExternalCategoryMapping result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(mappingId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            long id = 999L;
            ExternalCategoryMappingId mappingId = ExternalCategoryMappingId.of(id);

            given(queryPort.findById(mappingId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(ExternalCategoryMappingNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findBySourceIdAndCode() - 소스ID+코드로 조회")
    class FindBySourceIdAndCodeTest {

        @Test
        @DisplayName("존재하는 소스ID와 코드로 매핑을 조회한다")
        void findBySourceIdAndCode_Exists_ReturnsMapping() {
            // given
            long externalSourceId = 1L;
            String externalCategoryCode = "CAT_SHOES_001";
            ExternalCategoryMapping expected = ExternalCategoryMappingFixtures.activeMapping();

            given(
                            queryPort.findByExternalSourceIdAndExternalCategoryCode(
                                    externalSourceId, externalCategoryCode))
                    .willReturn(Optional.of(expected));

            // when
            ExternalCategoryMapping result =
                    sut.findBySourceIdAndCode(externalSourceId, externalCategoryCode);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 소스ID+코드로 조회 시 예외가 발생한다")
        void findBySourceIdAndCode_NotExists_ThrowsException() {
            // given
            long externalSourceId = 1L;
            String externalCategoryCode = "NOT_EXIST";

            given(
                            queryPort.findByExternalSourceIdAndExternalCategoryCode(
                                    externalSourceId, externalCategoryCode))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                            () -> sut.findBySourceIdAndCode(externalSourceId, externalCategoryCode))
                    .isInstanceOf(ExternalCategoryMappingNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByExternalSourceId() - 외부 소스 ID로 목록 조회")
    class FindByExternalSourceIdTest {

        @Test
        @DisplayName("외부 소스 ID로 매핑 목록을 조회한다")
        void findByExternalSourceId_ReturnsMappings() {
            // given
            long externalSourceId = 1L;
            List<ExternalCategoryMapping> expected =
                    List.of(
                            ExternalCategoryMappingFixtures.activeMapping(1L),
                            ExternalCategoryMappingFixtures.activeMapping(2L));

            given(queryPort.findByExternalSourceId(externalSourceId)).willReturn(expected);

            // when
            List<ExternalCategoryMapping> result = sut.findByExternalSourceId(externalSourceId);

            // then
            assertThat(result).hasSize(2);
            then(queryPort).should().findByExternalSourceId(externalSourceId);
        }
    }

    @Nested
    @DisplayName("findByExternalSourceIdAndCodes() - 소스ID+코드 목록으로 조회")
    class FindByExternalSourceIdAndCodesTest {

        @Test
        @DisplayName("소스ID와 코드 목록으로 매핑 목록을 조회한다")
        void findByExternalSourceIdAndCodes_ReturnsMappings() {
            // given
            long externalSourceId = 1L;
            List<String> codes = List.of("CAT_SHOES_001", "CAT_BAG_001");
            List<ExternalCategoryMapping> expected =
                    List.of(
                            ExternalCategoryMappingFixtures.activeMapping(
                                    1L, 1L, "CAT_SHOES_001", 100L),
                            ExternalCategoryMappingFixtures.activeMapping(
                                    2L, 1L, "CAT_BAG_001", 200L));

            given(queryPort.findByExternalSourceIdAndExternalCategoryCodes(externalSourceId, codes))
                    .willReturn(expected);

            // when
            List<ExternalCategoryMapping> result =
                    sut.findByExternalSourceIdAndCodes(externalSourceId, codes);

            // then
            assertThat(result).hasSize(2);
            then(queryPort)
                    .should()
                    .findByExternalSourceIdAndExternalCategoryCodes(externalSourceId, codes);
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
                            com.ryuqq.marketplace.domain.externalcategorymapping.query
                                    .ExternalCategoryMappingSearchCriteria.class);
            List<ExternalCategoryMapping> expected =
                    List.of(ExternalCategoryMappingFixtures.activeMapping());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<ExternalCategoryMapping> result = sut.findByCriteria(criteria);

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
                            com.ryuqq.marketplace.domain.externalcategorymapping.query
                                    .ExternalCategoryMappingSearchCriteria.class);
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
