package invmod.common.util;

import codechicken.nei.api.IConfigureNEI;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Optional;
import invmod.common.mod_Invasion;
import codechicken.nei.api.API;

// Doenerstyle

@Optional.Interface(iface = "codechicken.nei.api.API", modid = "NotEnoughItems")
public class NEIInvasionModConfig implements IConfigureNEI {
	
	@Optional.Method(modid="NotEnoughItems")
	@Override
	public String getName() {
		return "RiftFlux: Invasion";
	}
	
	@Optional.Method(modid="NotEnoughItems")
	@Override
	public String getVersion() {
		return "1.2.1";
	}
	
	@Optional.Method(modid="NotEnoughItems")
	@Override
	public void loadConfig() {
		if (ModConfig.enableInvasionModule && mod_Invasion.itemEngyHammer != null) {
			API.hideItem(new ItemStack(mod_Invasion.itemEngyHammer));
		}
	}
	
}
