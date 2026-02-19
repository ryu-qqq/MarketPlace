package com.ryuqq.marketplace.application.selleroption.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.selleroption.port.out.command.SellerOptionValueCommandPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
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
@DisplayName("SellerOptionValueCommandManager 단위 테스트")
class SellerOptionValueCommandManagerTest {

    @InjectMocks private SellerOptionValueCommandManager sut;

    @Mock private SellerOptionValueCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 옵션 값 저장")
    class PersistTest {

        @Test
        @DisplayName("옵션 값을 저장하고 생성된 ID를 반환한다")
        void persist_ValidValue_ReturnsValueId() {
            // given
            SellerOptionValue value = ProductGroupFixtures.defaultSellerOptionValue();
            Long expectedId = 1L;

            given(commandPort.persist(value)).willReturn(expectedId);

            // when
            Long result = sut.persist(value);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(value);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다건 옵션 값 저장")
    class PersistAllTest {

        @Test
        @DisplayName("옵션 값 목록을 저장하고 생성된 ID 목록을 반환한다")
        void persistAll_MultipleValues_ReturnsValueIds() {
            // given
            SellerOptionValue value1 = ProductGroupFixtures.defaultSellerOptionValue();
            SellerOptionValue value2 = ProductGroupFixtures.mappedSellerOptionValue();
            List<SellerOptionValue> values = List.of(value1, value2);
            List<Long> expectedIds = List.of(10L, 11L);

            given(commandPort.persistAll(values)).willReturn(expectedIds);

            // when
            List<Long> result = sut.persistAll(values);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(commandPort).should().persistAll(values);
        }

        @Test
        @DisplayName("빈 옵션 값 목록은 빈 ID 목록을 반환한다")
        void persistAll_EmptyList_ReturnsEmptyList() {
            // given
            List<SellerOptionValue> emptyValues = List.of();
            given(commandPort.persistAll(emptyValues)).willReturn(List.of());

            // when
            List<Long> result = sut.persistAll(emptyValues);

            // then
            assertThat(result).isEmpty();
        }
    }
}
