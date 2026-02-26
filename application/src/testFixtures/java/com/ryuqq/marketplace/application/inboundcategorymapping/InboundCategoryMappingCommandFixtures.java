package com.ryuqq.marketplace.application.inboundcategorymapping;

import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand.MappingEntry;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingCommand;
import java.util.List;

/**
 * InboundCategoryMapping Application Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class InboundCategoryMappingCommandFixtures {

    private InboundCategoryMappingCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "CAT_SHOES_001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_NAME = "외부 카테고리 신발";
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 100L;

    // ===== RegisterInboundCategoryMappingCommand Fixtures =====

    public static RegisterInboundCategoryMappingCommand registerCommand() {
        return new RegisterInboundCategoryMappingCommand(
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID);
    }

    public static RegisterInboundCategoryMappingCommand registerCommand(
            long inboundSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            long internalCategoryId) {
        return new RegisterInboundCategoryMappingCommand(
                inboundSourceId, externalCategoryCode, externalCategoryName, internalCategoryId);
    }

    // ===== UpdateInboundCategoryMappingCommand Fixtures =====

    public static UpdateInboundCategoryMappingCommand updateCommand() {
        return new UpdateInboundCategoryMappingCommand(1L, "수정된 외부 카테고리 신발", 200L, "ACTIVE");
    }

    public static UpdateInboundCategoryMappingCommand updateCommand(long id) {
        return new UpdateInboundCategoryMappingCommand(id, "수정된 외부 카테고리 신발", 200L, "ACTIVE");
    }

    public static UpdateInboundCategoryMappingCommand updateCommandWithInactive(long id) {
        return new UpdateInboundCategoryMappingCommand(id, "비활성 외부 카테고리", 200L, "INACTIVE");
    }

    // ===== BatchRegisterInboundCategoryMappingCommand Fixtures =====

    public static BatchRegisterInboundCategoryMappingCommand batchRegisterCommand() {
        return new BatchRegisterInboundCategoryMappingCommand(
                DEFAULT_EXTERNAL_SOURCE_ID,
                List.of(
                        new MappingEntry("CAT_SHOES_001", "외부 카테고리 신발", 100L),
                        new MappingEntry("CAT_BAG_001", "외부 카테고리 가방", 200L),
                        new MappingEntry("CAT_CLOTHES_001", "외부 카테고리 의류", 300L)));
    }

    public static BatchRegisterInboundCategoryMappingCommand batchRegisterCommand(
            long inboundSourceId, List<MappingEntry> entries) {
        return new BatchRegisterInboundCategoryMappingCommand(inboundSourceId, entries);
    }

    public static MappingEntry mappingEntry(String code, String name, long internalCategoryId) {
        return new MappingEntry(code, name, internalCategoryId);
    }
}
