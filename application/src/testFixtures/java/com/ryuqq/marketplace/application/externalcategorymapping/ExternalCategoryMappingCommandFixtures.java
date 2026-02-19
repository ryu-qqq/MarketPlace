package com.ryuqq.marketplace.application.externalcategorymapping;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingCommand.MappingEntry;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.RegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.UpdateExternalCategoryMappingCommand;
import java.util.List;

/**
 * ExternalCategoryMapping Application Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExternalCategoryMappingCommandFixtures {

    private ExternalCategoryMappingCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "CAT_SHOES_001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_NAME = "외부 카테고리 신발";
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 100L;

    // ===== RegisterExternalCategoryMappingCommand Fixtures =====

    public static RegisterExternalCategoryMappingCommand registerCommand() {
        return new RegisterExternalCategoryMappingCommand(
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID);
    }

    public static RegisterExternalCategoryMappingCommand registerCommand(
            long externalSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            long internalCategoryId) {
        return new RegisterExternalCategoryMappingCommand(
                externalSourceId, externalCategoryCode, externalCategoryName, internalCategoryId);
    }

    // ===== UpdateExternalCategoryMappingCommand Fixtures =====

    public static UpdateExternalCategoryMappingCommand updateCommand() {
        return new UpdateExternalCategoryMappingCommand(1L, "수정된 외부 카테고리 신발", 200L, "ACTIVE");
    }

    public static UpdateExternalCategoryMappingCommand updateCommand(long id) {
        return new UpdateExternalCategoryMappingCommand(id, "수정된 외부 카테고리 신발", 200L, "ACTIVE");
    }

    public static UpdateExternalCategoryMappingCommand updateCommandWithInactive(long id) {
        return new UpdateExternalCategoryMappingCommand(id, "비활성 외부 카테고리", 200L, "INACTIVE");
    }

    // ===== BatchRegisterExternalCategoryMappingCommand Fixtures =====

    public static BatchRegisterExternalCategoryMappingCommand batchRegisterCommand() {
        return new BatchRegisterExternalCategoryMappingCommand(
                DEFAULT_EXTERNAL_SOURCE_ID,
                List.of(
                        new MappingEntry("CAT_SHOES_001", "외부 카테고리 신발", 100L),
                        new MappingEntry("CAT_BAG_001", "외부 카테고리 가방", 200L),
                        new MappingEntry("CAT_CLOTHES_001", "외부 카테고리 의류", 300L)));
    }

    public static BatchRegisterExternalCategoryMappingCommand batchRegisterCommand(
            long externalSourceId, List<MappingEntry> entries) {
        return new BatchRegisterExternalCategoryMappingCommand(externalSourceId, entries);
    }

    public static MappingEntry mappingEntry(String code, String name, long internalCategoryId) {
        return new MappingEntry(code, name, internalCategoryId);
    }
}
