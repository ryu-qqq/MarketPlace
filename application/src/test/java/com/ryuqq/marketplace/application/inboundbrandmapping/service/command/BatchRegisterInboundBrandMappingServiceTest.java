package com.ryuqq.marketplace.application.inboundbrandmapping.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.inboundbrandmapping.InboundBrandMappingCommandFixtures;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.factory.InboundBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingCommandManager;
import com.ryuqq.marketplace.application.inboundbrandmapping.validator.InboundBrandMappingValidator;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("BatchRegisterInboundBrandMappingService 단위 테스트")
class BatchRegisterInboundBrandMappingServiceTest {

    @InjectMocks private BatchRegisterInboundBrandMappingService sut;

    @Mock private InboundBrandMappingValidator validator;
    @Mock private InboundBrandMappingCommandFactory commandFactory;
    @Mock private InboundBrandMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 브랜드 매핑 일괄 등록")
    class ExecuteTest {

        @Test
        @DisplayName("배치 커맨드로 외부 브랜드 매핑들을 일괄 등록하고 ID 목록을 반환한다")
        void execute_ValidBatchCommand_ReturnsMappingIds() {
            // given
            BatchRegisterInboundBrandMappingCommand command =
                    InboundBrandMappingCommandFixtures.batchRegisterCommand();
            List<InboundBrandMapping> mappings =
                    List.of(
                            InboundBrandMappingFixtures.newMapping(1L, "BR001", 100L),
                            InboundBrandMappingFixtures.newMapping(1L, "BR002", 200L),
                            InboundBrandMappingFixtures.newMapping(1L, "BR003", 300L));
            List<Long> expectedIds = List.of(1L, 2L, 3L);

            willDoNothing()
                    .given(validator)
                    .validateNotDuplicateBulk(
                            ArgumentMatchers.eq(command.inboundSourceId()),
                            ArgumentMatchers.anyList());
            given(commandFactory.createAll(command)).willReturn(mappings);
            given(commandManager.persistAll(mappings)).willReturn(expectedIds);

            // when
            List<Long> result = sut.execute(command);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).isEqualTo(expectedIds);
            then(commandFactory).should().createAll(command);
            then(commandManager).should().persistAll(mappings);
        }
    }
}
