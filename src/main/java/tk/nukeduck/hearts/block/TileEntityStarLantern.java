package tk.nukeduck.hearts.block;

import java.util.Collections;
import java.util.List;
import tk.nukeduck.hearts.HeartsConfig;

public class TileEntityStarLantern
extends TileEntityHeartLantern {
    @Override
    protected boolean isAuraEnabled() {
        HeartsConfig config = this.getLanternConfig();
        return config != null && config.isStarLanternAuraEnabled();
    }

    @Override
    protected float getAuraRadius() {
        HeartsConfig config = this.getLanternConfig();
        return config != null ? config.getStarLanternAuraRadius() : 0.0f;
    }

    @Override
    protected List<HeartsConfig.LanternAuraEffect> getBaseAuraEffects() {
        HeartsConfig config = this.getLanternConfig();
        if (config == null) {
            return Collections.emptyList();
        }
        List<HeartsConfig.LanternAuraEffect> effects = config.getStarLanternAuraEffects();
        return effects != null ? effects : Collections.<HeartsConfig.LanternAuraEffect>emptyList();
    }
}
