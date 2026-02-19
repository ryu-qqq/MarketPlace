package com.ryuqq.marketplace.application.externalbrandmapping;

import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingCommand.MappingEntry;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.RegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.UpdateExternalBrandMappingCommand;
import java.util.List;

/**
 * ExternalBrandMapping Application Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExternalBrandMappingCommandFixtures {

    private ExternalBrandMappingCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "BR001";
    public static final String DEFAULT_EXTERNAL_BRAND_NAME = "외부 브랜드 A";
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 100L;

    // ===== RegisterExternalBrandMappingCommand Fixtures =====

    public static RegisterExternalBrandMappingCommand registerCommand() {
        return new RegisterExternalBrandMappingCommand(
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID);
    }

    public static RegisterExternalBrandMappingCommand registerCommand(
            long externalSourceId,
            String externalBrandCode,
            String externalBrandName,
            long internalBrandId) {
        return new RegisterExternalBrandMappingCommand(
                externalSourceId, externalBrandCode, externalBrandName, internalBrandId);
    }

    // ===== UpdateExternalBrandMappingCommand Fixtures =====

    public static UpdateExternalBrandMappingCommand updateCommand() {
        return new UpdateExternalBrandMappingCommand(1L, "수정된 외부 브랜드 A", 200L, "ACTIVE");
    }

    public static UpdateExternalBrandMappingCommand updateCommand(long id) {
        return new UpdateExternalBrandMappingCommand(id, "수정된 외부 브랜드 A", 200L, "ACTIVE");
    }

    public static UpdateExternalBrandMappingCommand updateCommandWithInactive(long id) {
        return new UpdateExternalBrandMappingCommand(id, "비활성 외부 브랜드", 200L, "INACTIVE");
    }

    // ===== BatchRegisterExternalBrandMappingCommand Fixtures =====

    public static BatchRegisterExternalBrandMappingCommand batchRegisterCommand() {
        return new BatchRegisterExternalBrandMappingCommand(
                DEFAULT_EXTERNAL_SOURCE_ID,
                List.of(
                        new MappingEntry("BR001", "외부 브랜드 A", 100L),
                        new MappingEntry("BR002", "외부 브랜드 B", 200L),
                        new MappingEntry("BR003", "외부 브랜드 C", 300L)));
    }

    public static BatchRegisterExternalBrandMappingCommand batchRegisterCommand(
            long externalSourceId, List<MappingEntry> entries) {
        return new BatchRegisterExternalBrandMappingCommand(externalSourceId, entries);
    }

    public static MappingEntry mappingEntry(String code, String name, long internalBrandId) {
        return new MappingEntry(code, name, internalBrandId);
    }
}
