/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package zairus.worldexplorer.equipment;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zairus.worldexplorer.core.IWEAddonEntityManager;
import zairus.worldexplorer.core.IWEAddonMod;
import zairus.worldexplorer.core.IWEAddonMonsterManager;
import zairus.worldexplorer.core.IWEAddonRenderManager;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.equipment.client.renderer.EquipmentRenderManager;
import zairus.worldexplorer.equipment.entity.EquipmentEntityManager;
import zairus.worldexplorer.equipment.entity.monster.EquipmentMonsterManager;
import zairus.worldexplorer.equipment.items.WEEquipmentItems;

public class Equipment
implements IWEAddonMod {
    public static Equipment instance;

    public void preInit(FMLPreInitializationEvent event) {
        WorldExplorer.registerWEAddonMod(this);
        WEEquipmentItems.init();
    }

    public void init(FMLInitializationEvent event) {
        WEEquipmentItems.register();
        this.addRecipes();
    }

    private void addRecipes() {
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)WEEquipmentItems.spyglass), (Object[])new Object[]{"gig", "prp", "gig", Character.valueOf('g'), Blocks.light_weighted_pressure_plate, Character.valueOf('p'), Blocks.glass_pane, Character.valueOf('i'), Blocks.heavy_weighted_pressure_plate, Character.valueOf('r'), Items.redstone});
    }

    @Override
    public IWEAddonEntityManager getEntityManager() {
        return new EquipmentEntityManager();
    }

    @Override
    public IWEAddonMonsterManager getMonsterManager() {
        return new EquipmentMonsterManager();
    }

    @Override
    public IWEAddonRenderManager getRenderManager() {
        return new EquipmentRenderManager();
    }
}
