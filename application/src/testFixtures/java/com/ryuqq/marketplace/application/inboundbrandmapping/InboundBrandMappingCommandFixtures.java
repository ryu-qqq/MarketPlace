package com.ryuqq.marketplace.application.inboundbrandmapping;

import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand.MappingEntry;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.RegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.UpdateInboundBrandMappingCommand;
import java.util.List;

/**
 * InboundBrandMapping Application Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class InboundBrandMappingCommandFixtures {

    private InboundBrandMappingCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_EXTERNAL_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "BR001";
    public static final String DEFAULT_EXTERNAL_BRAND_NAME = "외부 브랜드 A";
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 100L;

    // ===== RegisterInboundBrandMappingCommand Fixtures =====

    public static RegisterInboundBrandMappingCommand registerCommand() {
        return new RegisterInboundBrandMappingCommand(
                DEFAULT_EXTERNAL_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID);
    }

    public static RegisterInboundBrandMappingCommand registerCommand(
            long inboundSourceId,
            String externalBrandCode,
            String externalBrandName,
            long internalBrandId) {
        return new RegisterInboundBrandMappingCommand(
                inboundSourceId, externalBrandCode, externalBrandName, internalBrandId);
    }

    // ===== UpdateInboundBrandMappingCommand Fixtures =====

    public static UpdateInboundBrandMappingCommand updateCommand() {
        return new UpdateInboundBrandMappingCommand(1L, "수정된 외부 브랜드 A", 200L, "ACTIVE");
    }

    public static UpdateInboundBrandMappingCommand updateCommand(long id) {
        return new UpdateInboundBrandMappingCommand(id, "수정된 외부 브랜드 A", 200L, "ACTIVE");
    }

    public static UpdateInboundBrandMappingCommand updateCommandWithInactive(long id) {
        return new UpdateInboundBrandMappingCommand(id, "비활성 외부 브랜드", 200L, "INACTIVE");
    }

    // ===== BatchRegisterInboundBrandMappingCommand Fixtures =====

    public static BatchRegisterInboundBrandMappingCommand batchRegisterCommand() {
        return new BatchRegisterInboundBrandMappingCommand(
                DEFAULT_EXTERNAL_SOURCE_ID,
                List.of(
                        new MappingEntry("BR001", "외부 브랜드 A", 100L),
                        new MappingEntry("BR002", "외부 브랜드 B", 200L),
                        new MappingEntry("BR003", "외부 브랜드 C", 300L)));
    }

    public static BatchRegisterInboundBrandMappingCommand batchRegisterCommand(
            long inboundSourceId, List<MappingEntry> entries) {
        return new BatchRegisterInboundBrandMappingCommand(inboundSourceId, entries);
    }

    public static MappingEntry mappingEntry(String code, String name, long internalBrandId) {
        return new MappingEntry(code, name, internalBrandId);
    }
}
