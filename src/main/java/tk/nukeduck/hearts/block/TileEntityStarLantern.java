package tk.nukeduck.hearts.block;

import java.util.Collections;
import java.util.List;
import net.nmccoy.legendgear.LegendGear2;
import tk.nukeduck.hearts.HeartsConfig;

public class TileEntityStarLantern
extends TileEntityHeartLantern {
    @Override
    protected List<HeartsConfig.LanternAuraEffect> getBaseAuraEffects() {
        if (LegendGear2.manaRegenPotion == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new HeartsConfig.LanternAuraEffect(LegendGear2.manaRegenPotion.id, 0));
    }
}
