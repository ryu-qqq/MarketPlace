package com.ryuqq.marketplace.application.brandpreset.manager;

import com.ryuqq.marketplace.application.brandpreset.port.out.command.BrandPresetCommandPort;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** BrandPreset Command Manager. */
@Component
public class BrandPresetCommandManager {

    private final BrandPresetCommandPort commandPort;

    public BrandPresetCommandManager(BrandPresetCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(BrandPreset brandPreset) {
        return commandPort.persist(brandPreset);
    }

    @Transactional
    public List<Long> persistAll(List<BrandPreset> brandPresets) {
        return commandPort.persistAll(brandPresets);
    }
}
