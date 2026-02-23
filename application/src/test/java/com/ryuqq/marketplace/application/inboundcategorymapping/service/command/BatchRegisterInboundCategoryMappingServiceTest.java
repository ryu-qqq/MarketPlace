package com.ryuqq.marketplace.application.inboundcategorymapping.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.inboundcategorymapping.InboundCategoryMappingCommandFixtures;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.factory.InboundCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.inboundcategorymapping.validator.InboundCategoryMappingValidator;
import com.ryuqq.marketplace.domain.inboundcategorymapping.InboundCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
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
@DisplayName("BatchRegisterInboundCategoryMappingService 단위 테스트")
class BatchRegisterInboundCategoryMappingServiceTest {

    @InjectMocks private BatchRegisterInboundCategoryMappingService sut;

    @Mock private InboundCategoryMappingValidator validator;
    @Mock private InboundCategoryMappingCommandFactory commandFactory;
    @Mock private InboundCategoryMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 카테고리 매핑 일괄 등록")
    class ExecuteTest {

        @Test
        @DisplayName("배치 커맨드로 외부 카테고리 매핑들을 일괄 등록하고 ID 목록을 반환한다")
        void execute_ValidBatchCommand_ReturnsMappingIds() {
            // given
            BatchRegisterInboundCategoryMappingCommand command =
                    InboundCategoryMappingCommandFixtures.batchRegisterCommand();
            List<InboundCategoryMapping> mappings =
                    List.of(
                            InboundCategoryMappingFixtures.newMapping(1L, "CAT_SHOES_001", 100L),
                            InboundCategoryMappingFixtures.newMapping(1L, "CAT_BAG_001", 200L),
                            InboundCategoryMappingFixtures.newMapping(1L, "CAT_CLOTHES_001", 300L));
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
