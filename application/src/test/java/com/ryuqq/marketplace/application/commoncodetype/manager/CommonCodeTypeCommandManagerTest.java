package com.ryuqq.marketplace.application.commoncodetype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.commoncodetype.port.out.command.CommonCodeTypeCommandPort;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
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
@DisplayName("CommonCodeTypeCommandManager 단위 테스트")
class CommonCodeTypeCommandManagerTest {

    @InjectMocks private CommonCodeTypeCommandManager sut;

    @Mock private CommonCodeTypeCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 공통 코드 타입 저장")
    class PersistTest {

        @Test
        @DisplayName("공통 코드 타입을 저장하고 ID를 반환한다")
        void persist_Success() {
            // given
            CommonCodeType commonCodeType = CommonCodeTypeFixtures.newCommonCodeType();
            Long expectedId = 1L;

            given(commandPort.persist(commonCodeType)).willReturn(expectedId);

            // when
            Long result = sut.persist(commonCodeType);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(commonCodeType);
        }
    }

    @Nested
    @DisplayName("persistAll() - 공통 코드 타입 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("여러 공통 코드 타입을 일괄 저장한다")
        void persistAll_Success() {
            // given
            List<CommonCodeType> commonCodeTypes =
                    List.of(
                            CommonCodeTypeFixtures.newCommonCodeType("CODE1", "이름1"),
                            CommonCodeTypeFixtures.newCommonCodeType("CODE2", "이름2"));

            // when
            sut.persistAll(commonCodeTypes);

            // then
            then(commandPort).should().persistAll(commonCodeTypes);
        }

        @Test
        @DisplayName("빈 목록을 저장해도 예외가 발생하지 않는다")
        void persistAll_EmptyList_NoException() {
            // given
            List<CommonCodeType> commonCodeTypes = List.of();

            // when
            sut.persistAll(commonCodeTypes);

            // then
            then(commandPort).should().persistAll(commonCodeTypes);
        }
    }
}
